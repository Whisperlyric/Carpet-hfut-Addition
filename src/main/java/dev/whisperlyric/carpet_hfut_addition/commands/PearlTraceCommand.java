package dev.whisperlyric.carpet_hfut_addition.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import dev.whisperlyric.carpet_hfut_addition.helpers.rule.ghostPearl.PearlTraceStore;
import dev.whisperlyric.carpet_hfut_addition.helpers.rule.ghostPearl.PearlTraceStore.TeleportEvent;
import dev.whisperlyric.carpet_hfut_addition.utils.CommandUtil;
import dev.whisperlyric.carpet_hfut_addition.utils.HFUTText;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

/**
 * {@code /pearltrace} - forensics for ghost ender pearls (MC-306936): UUIDs may
 * be full or an unambiguous prefix, removal is two-step (preview, then confirm),
 * and there is deliberately no "purge all" - a mistaken mass removal would be an
 * accident. Access is gated by the commandPearlTrace* rules.
 */
public final class PearlTraceCommand {
    private static final int PAGE_SIZE = 8;
    private static final long CONFIRM_TTL_MILLIS = 60_000L;
    private static final String CONSOLE_KEY = "@console";
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneOffset.systemDefault());

    /** A previewed-but-not-yet-confirmed removal. One per source; expires and goes stale. */
    private record PendingPurge(String playerName, UUID pearl, int countAtPreview, long time) {
    }

    private static final Map<String, PendingPurge> PENDING = new ConcurrentHashMap<>();

    private PearlTraceCommand() {
    }

    /** Pending confirmations are keyed per source: the admin's UUID, or a shared key for console-like sources. */
    private static String sourceKey(CommandSourceStack source) {
        ServerPlayer admin = source.getPlayer();
        return admin != null ? admin.getUUID().toString() : CONSOLE_KEY;
    }

    /** Lazily drops pending confirmations older than the TTL. */
    private static void pruneExpired() {
        long now = System.currentTimeMillis();
        PENDING.entrySet().removeIf(e -> now - e.getValue().time() > CONFIRM_TTL_MILLIS);
    }

    /** Called on server stop: pending confirmations are session-only state. */
    public static void clearPending() {
        PENDING.clear();
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // below 1.21.2 there is no pearl persistence, so the store stays empty
        LiteralArgumentBuilder<CommandSourceStack> root = literal("pearltrace")
                .requires(source -> CommandUtil.canUseCommand(source, HFUTSettings.commandPearlTrace)
                        || CommandUtil.canUseCommand(source, HFUTSettings.commandPearlTracePurge))
                .then(literal("list")
                        .requires(source -> CommandUtil.canUseCommand(source, HFUTSettings.commandPearlTrace))
                        .executes(ctx -> list(ctx.getSource(), null, 1))
                        .then(argument("player", StringArgumentType.word())
                                .executes(ctx -> list(ctx.getSource(), StringArgumentType.getString(ctx, "player"), 1))
                                .then(argument("page", StringArgumentType.word())
                                        .executes(ctx -> list(ctx.getSource(),
                                                StringArgumentType.getString(ctx, "player"),
                                                parsePage(StringArgumentType.getString(ctx, "page")))))))
                .then(literal("show")
                        .requires(source -> CommandUtil.canUseCommand(source, HFUTSettings.commandPearlTrace))
                        .then(argument("uuid", StringArgumentType.word())
                                .executes(ctx -> show(ctx.getSource(), StringArgumentType.getString(ctx, "uuid")))))
                .then(literal("purge")
                        .requires(source -> CommandUtil.canUseCommand(source, HFUTSettings.commandPearlTracePurge))
                        .then(argument("player", StringArgumentType.word())
                                .suggests(PearlTraceCommand::suggestOnlinePlayers)
                                .then(argument("uuid", StringArgumentType.word())
                                        .executes(ctx -> purge(ctx.getSource(),
                                                StringArgumentType.getString(ctx, "player"),
                                                StringArgumentType.getString(ctx, "uuid"), false))
                                        .then(literal("confirm")
                                                .executes(ctx -> purge(ctx.getSource(),
                                                        StringArgumentType.getString(ctx, "player"),
                                                        StringArgumentType.getString(ctx, "uuid"), true))))));
        dispatcher.register(root);
    }

    private static CompletableFuture<Suggestions> suggestOnlinePlayers(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        String remaining = builder.getRemainingLowerCase();
        for (ServerPlayer player : context.getSource().getServer().getPlayerList().getPlayers()) {
            String name = playerName(player);
            if (name.toLowerCase().startsWith(remaining)) {
                builder.suggest(name);
            }
        }
        return builder.buildFuture();
    }

    private static int parsePage(String raw) {
        try {
            return Math.max(1, Integer.parseInt(raw));
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    private static int list(CommandSourceStack source, String ownerFilter, int page) {
        List<TeleportEvent> events = PearlTraceStore.get().events(ownerFilter, null);
        if (events.isEmpty()) {
            send(source, "hfut.pearltrace.list.empty");
            return Command.SINGLE_SUCCESS;
        }
        int pages = (events.size() + PAGE_SIZE - 1) / PAGE_SIZE;
        page = Math.min(page, pages);
        send(source, "hfut.pearltrace.list.header", events.size(), page, pages);
        int start = events.size() - 1 - (page - 1) * PAGE_SIZE;
        for (int i = start; i >= 0 && i > start - PAGE_SIZE; i--) {
            TeleportEvent event = events.get(i);
            String ghostMark = event.count() >= 2 ? "!!" : "  ";
            MutableComponent row = HFUTText.forViewer(source, "hfut.pearltrace.list.row",
                    ghostMark, fullUuid(event.pearl()),
                    event.ownerName() == null ? "?" : event.ownerName(),
                    TIME.format(Instant.ofEpochMilli(event.time())),
                    event.origin() == PearlTraceStore.Origin.NBT_LOAD ? "NBT" : "--",
                    event.count(),
                    String.format("%.0f %.0f %.0f", event.x(), event.y(), event.z()));
            Component clickable = clickable(row, "/pearltrace show " + event.pearl());
            source.sendSuccess(() -> clickable, false);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int show(CommandSourceStack source, String uuidRaw) {
        UUID uuid = resolveUuid(source, uuidRaw);
        if (uuid == null) {
            return 0;
        }
        printDetails(source, uuid);
        return Command.SINGLE_SUCCESS;
    }

    /** Two-step removal: the first call only prints the pearl's data for review. */
    private static int purge(CommandSourceStack source, String playerName, String uuidRaw, boolean confirmed) {
        ServerPlayer target = source.getServer().getPlayerList().getPlayerByName(playerName);
        if (target == null) {
            send(source, "hfut.pearltrace.player_offline", playerName);
            return 0;
        }
        UUID uuid = resolveUuid(source, uuidRaw);
        if (uuid == null) {
            return 0;
        }
        if (!confirmed) {
            send(source, "hfut.pearltrace.purge.header", playerName, uuid);
            printDetails(source, uuid);
            send(source, "hfut.pearltrace.purge.confirm", playerName, uuid);
            pruneExpired();
            // one pending per source: a new preview replaces the old one
            PENDING.put(sourceKey(source), new PendingPurge(playerName, uuid,
                    PearlTraceStore.get().countOf(uuid), System.currentTimeMillis()));
            return Command.SINGLE_SUCCESS;
        }
        PendingPurge pending = PENDING.remove(sourceKey(source));
        if (pending == null || !pending.pearl().equals(uuid) || !pending.playerName().equals(playerName)) {
            send(source, "hfut.pearltrace.purge.no_pending");
            return 0;
        }
        if (System.currentTimeMillis() - pending.time() > CONFIRM_TTL_MILLIS) {
            send(source, "hfut.pearltrace.purge.expired");
            return 0;
        }
        if (PearlTraceStore.get().countOf(uuid) != pending.countAtPreview()) {
            // the ghost moved again since the preview - the reviewed data is no longer the data being removed
            send(source, "hfut.pearltrace.purge.stale");
            return 0;
        }
        int removed = 0;
        //#if MC >= 12102
        //$$ removed = PearlTraceStore.purge(target, uuid);
        //#endif
        send(source, "hfut.pearltrace.purge.done", playerName, removed);
        return Command.SINGLE_SUCCESS;
    }

    /** Full UUID, or an unambiguous prefix of a UUID the store knows. */
    private static UUID resolveUuid(CommandSourceStack source, String input) {
        if (input.length() == 36) {
            try {
                return UUID.fromString(input);
            } catch (IllegalArgumentException ignored) {
            }
        }
        List<UUID> matches = PearlTraceStore.get().knownUuids().stream()
                .filter(u -> u.toString().startsWith(input.toLowerCase()))
                .sorted()
                .toList();
        if (matches.isEmpty()) {
            send(source, "hfut.pearltrace.uuid_unknown", input);
            return null;
        }
        if (matches.size() > 1) {
            send(source, "hfut.pearltrace.uuid_ambiguous", input,
                    String.join(", ", matches.stream().map(PearlTraceCommand::fullUuid).limit(5).toList()));
            return null;
        }
        return matches.get(0);
    }

    private static void printDetails(CommandSourceStack source, UUID uuid) {
        send(source, "hfut.pearltrace.show.header", uuid);
        send(source, "hfut.pearltrace.show.count", PearlTraceStore.get().countOf(uuid));
        TeleportEvent event = PearlTraceStore.get().latestFor(uuid);
        if (event != null) {
            send(source, "hfut.pearltrace.show.last",
                    TIME.format(Instant.ofEpochMilli(event.time())),
                    event.ownerName() == null ? "?" : event.ownerName(),
                    event.dimension(),
                    String.format("%.1f %.1f %.1f", event.x(), event.y(), event.z()),
                    event.origin() == PearlTraceStore.Origin.NBT_LOAD ? "NBT_LOAD" : "OTHER",
                    event.originSnapshot() == null ? "-" : event.originSnapshot());
        } else {
            send(source, "hfut.pearltrace.show.no_event");
        }
        List<String> holders = java.util.Collections.emptyList();
        //#if MC >= 12102
        //$$ holders = PearlTraceStore.get().remainingCopies(source.getServer(), uuid);
        //#endif
        if (holders.isEmpty()) {
            send(source, "hfut.pearltrace.show.no_copies");
        } else {
            send(source, "hfut.pearltrace.show.copies", String.join(", ", holders));
        }
    }

    /** Clicking the row puts the inspection command into the chat input (full UUID included). */
    private static MutableComponent clickable(MutableComponent line, String suggestCommand) {
        //#if MC >= 12105
        //$$ return line.withStyle(style -> style.withClickEvent(new ClickEvent.SuggestCommand(suggestCommand)));
        //#else
        return line.withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggestCommand)));
        //#endif
    }

    private static String playerName(ServerPlayer player) {
        //#if MC >= 12110
        //$$ return player.getGameProfile().name();
        //#else
        return player.getGameProfile().getName();
        //#endif
    }

    private static String fullUuid(UUID uuid) {
        return uuid.toString();
    }

    private static void send(CommandSourceStack source, String key, Object... args) {
        source.sendSuccess(() -> HFUTText.forViewer(source, key, args), false);
    }
}
