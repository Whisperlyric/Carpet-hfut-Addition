package dev.whisperlyric.carpet_hfut_addition.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import dev.whisperlyric.carpet_hfut_addition.helpers.rule.simpleLazyChunk.SimpleLazyChunkManager;
import dev.whisperlyric.carpet_hfut_addition.mixins.ServerPlayerLanguageAccessor;
import dev.whisperlyric.carpet_hfut_addition.utils.CommandUtil;
import dev.whisperlyric.carpet_hfut_addition.utils.HFUTChatPage;
import dev.whisperlyric.carpet_hfut_addition.utils.HFUTText;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.ColumnPosArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;

import java.util.List;

/**
 * {@code /hfut lazychunk}: pin chunks at lazy strength (block ticking, no
 * entities) even without players, and hold them there against promotion.
 * Marks are in-memory per dimension, gated by {@code commandSimpleLazyChunk};
 * {@code pos} takes a column and folds it into chunk space so callers never
 * divide by 16, {@code query} pages the marks, and {@code delete all} clears
 * the dimension.
 */
public class SimpleLazyChunkCommand {

    private static final int PAGE_SIZE = 10;
    private static final int MAX_LISTED_OCCUPANTS = 8;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var addChunk = Commands.literal("chunk")
                .then(Commands.argument("x", IntegerArgumentType.integer())
                        .then(Commands.argument("z", IntegerArgumentType.integer())
                                .executes(ctx -> hfut$add(ctx.getSource(),
                                        IntegerArgumentType.getInteger(ctx, "x"),
                                        IntegerArgumentType.getInteger(ctx, "z")))));
        var addPos = Commands.literal("pos")
                .then(Commands.argument("pos", ColumnPosArgument.columnPos())
                        .executes(ctx -> hfut$add(ctx.getSource(), ColumnPosArgument.getColumnPos(ctx, "pos"))));
        var addAreaChunk = Commands.literal("chunk")
                .then(Commands.argument("x1", IntegerArgumentType.integer())
                        .then(Commands.argument("z1", IntegerArgumentType.integer())
                                .then(Commands.argument("x2", IntegerArgumentType.integer())
                                        .then(Commands.argument("z2", IntegerArgumentType.integer())
                                                .executes(ctx -> hfut$addRange(ctx.getSource(),
                                                        IntegerArgumentType.getInteger(ctx, "x1"),
                                                        IntegerArgumentType.getInteger(ctx, "z1"),
                                                        IntegerArgumentType.getInteger(ctx, "x2"),
                                                        IntegerArgumentType.getInteger(ctx, "z2")))))));
        var addAreaPos = Commands.literal("pos")
                .then(Commands.argument("from", ColumnPosArgument.columnPos())
                        .then(Commands.argument("to", ColumnPosArgument.columnPos())
                                .executes(ctx -> hfut$addRange(ctx.getSource(),
                                        ColumnPosArgument.getColumnPos(ctx, "from"),
                                        ColumnPosArgument.getColumnPos(ctx, "to")))));
        var add = Commands.literal("add")
                .then(addChunk).then(addPos)
                .then(Commands.literal("area").then(addAreaChunk).then(addAreaPos));

        var deleteChunk = Commands.literal("chunk")
                .then(Commands.argument("x", IntegerArgumentType.integer())
                        .then(Commands.argument("z", IntegerArgumentType.integer())
                                .executes(ctx -> hfut$delete(ctx.getSource(),
                                        IntegerArgumentType.getInteger(ctx, "x"),
                                        IntegerArgumentType.getInteger(ctx, "z")))));
        var deleteArea = Commands.literal("area")
                .then(Commands.argument("x1", IntegerArgumentType.integer())
                        .then(Commands.argument("z1", IntegerArgumentType.integer())
                                .then(Commands.argument("x2", IntegerArgumentType.integer())
                                        .then(Commands.argument("z2", IntegerArgumentType.integer())
                                                .executes(ctx -> hfut$deleteRange(ctx.getSource(),
                                                        IntegerArgumentType.getInteger(ctx, "x1"),
                                                        IntegerArgumentType.getInteger(ctx, "z1"),
                                                        IntegerArgumentType.getInteger(ctx, "x2"),
                                                        IntegerArgumentType.getInteger(ctx, "z2")))))));
        var delete = Commands.literal("delete").then(deleteChunk).then(deleteArea)
                .then(Commands.literal("all").executes(ctx -> hfut$deleteAll(ctx.getSource())));

        var query = Commands.literal("query")
                .executes(ctx -> query(ctx.getSource(), 1));

        var lazychunk = Commands.literal("lazychunk")
                .requires(source -> CommandUtil.canUseCommand(source, HFUTSettings.commandSimpleLazyChunk))
                .then(add).then(delete).then(query);
        dispatcher.register(Commands.literal("hfut").then(lazychunk));
    }

    private SimpleLazyChunkCommand() {
    }

    private static int hfut$add(CommandSourceStack source, int chunkX, int chunkZ) {
        if (!hfut$checkBounds(source, chunkX, chunkZ)) {
            return 0;
        }
        ServerLevel level = source.getLevel();
        if (SimpleLazyChunkManager.add(level, new ChunkPos(chunkX, chunkZ))) {
            source.sendSuccess(() -> HFUTText.forViewer(source, "hfut.lazychunk.added", chunkX, chunkZ), true);
            hfut$noteOccupants(source, chunkX, chunkZ, chunkX, chunkZ);
        } else {
            source.sendFailure(HFUTText.forViewer(source, "hfut.lazychunk.already_marked", chunkX, chunkZ));
            return 0;
        }
        return 1;
    }

    /** {@code pos} is only ever a chunk, so a column {@code <x> <z>} (~ supported) folds straight into chunk space. */
    private static int hfut$add(CommandSourceStack source, ColumnPos column) {
        long packed = SimpleLazyChunkManager.pack(column.toChunkPos());
        return hfut$add(source, SimpleLazyChunkManager.x(packed), SimpleLazyChunkManager.z(packed));
    }

    private static int hfut$delete(CommandSourceStack source, int chunkX, int chunkZ) {
        if (!hfut$checkBounds(source, chunkX, chunkZ)) {
            return 0;
        }
        ServerLevel level = source.getLevel();
        if (SimpleLazyChunkManager.remove(level, new ChunkPos(chunkX, chunkZ))) {
            source.sendSuccess(() -> HFUTText.forViewer(source, "hfut.lazychunk.removed", chunkX, chunkZ), true);
        } else {
            source.sendFailure(HFUTText.forViewer(source, "hfut.lazychunk.not_marked", chunkX, chunkZ));
            return 0;
        }
        return 1;
    }

    private static int hfut$addRange(CommandSourceStack source, int x1, int z1, int x2, int z2) {
        if (!hfut$checkBounds(source, x1, z1) || !hfut$checkBounds(source, x2, z2)) {
            return 0;
        }
        ServerLevel level = source.getLevel();
        int changed = SimpleLazyChunkManager.addRange(level, new ChunkPos(x1, z1), new ChunkPos(x2, z2));
        int result = hfut$rangeResult(source, changed, true);
        if (result > 0) {
            hfut$noteOccupants(source, Math.min(x1, x2), Math.min(z1, z2), Math.max(x1, x2), Math.max(z1, z2));
        }
        return result;
    }

    private static int hfut$addRange(CommandSourceStack source, ColumnPos from, ColumnPos to) {
        long packedFrom = SimpleLazyChunkManager.pack(from.toChunkPos());
        long packedTo = SimpleLazyChunkManager.pack(to.toChunkPos());
        return hfut$addRange(source,
                SimpleLazyChunkManager.x(packedFrom), SimpleLazyChunkManager.z(packedFrom),
                SimpleLazyChunkManager.x(packedTo), SimpleLazyChunkManager.z(packedTo));
    }

    private static int hfut$deleteRange(CommandSourceStack source, int x1, int z1, int x2, int z2) {
        if (!hfut$checkBounds(source, x1, z1) || !hfut$checkBounds(source, x2, z2)) {
            return 0;
        }
        ServerLevel level = source.getLevel();
        int changed = SimpleLazyChunkManager.removeRange(level, new ChunkPos(x1, z1), new ChunkPos(x2, z2));
        return hfut$rangeResult(source, changed, false);
    }

    /** Chunk-coordinate sanity: reject positions that can never exist inside the world border. */
    private static boolean hfut$checkBounds(CommandSourceStack source, int chunkX, int chunkZ) {
        if (!SimpleLazyChunkManager.inWorldBound(chunkX, chunkZ)) {
            source.sendFailure(HFUTText.forViewer(source, "hfut.lazychunk.coord_out_of_world",
                    SimpleLazyChunkManager.MAX_CHUNK_COORD));
            return false;
        }
        return true;
    }

    /**
     * Player-occupied chunks are exempt from the clamp, so a mark there looks
     * inert until they leave - say so instead of a silent no-op. Lists every
     * occupant.
     */
    private static void hfut$noteOccupants(CommandSourceStack source, int minX, int minZ, int maxX, int maxZ) {
        List<ServerPlayer> occupants = SimpleLazyChunkManager.playersIn(source.getLevel(), minX, minZ, maxX, maxZ);
        if (occupants.isEmpty()) {
            return;
        }
        String lang = hfut$lang(source);
        StringBuilder names = new StringBuilder();
        for (int i = 0; i < occupants.size() && i < MAX_LISTED_OCCUPANTS; i++) {
            ServerPlayer occupant = occupants.get(i);
            if (i > 0) {
                names.append(", ");
            }
            names.append(occupant.getName().getString()).append('[')
                    .append(occupant.blockPosition().getX() >> 4).append(", ")
                    .append(occupant.blockPosition().getZ() >> 4).append(']');
        }
        if (occupants.size() > MAX_LISTED_OCCUPANTS) {
            names.append(", ").append(HFUTText.template(lang, "hfut.lazychunk.player_more")
                    .formatted(occupants.size() - MAX_LISTED_OCCUPANTS));
        }
        source.sendSuccess(() -> HFUTText.forViewer(source, "hfut.lazychunk.player_in_range", names.toString()), false);
    }

    private static int hfut$rangeResult(CommandSourceStack source, int changed, boolean adding) {
        if (changed < 0) {
            source.sendFailure(HFUTText.forViewer(source, "hfut.lazychunk.range_too_large",
                    SimpleLazyChunkManager.MAX_RANGE_CHUNKS));
            return 0;
        }
        if (changed == 0) {
            source.sendSuccess(() -> HFUTText.forViewer(source, adding
                    ? "hfut.lazychunk.range_added_none" : "hfut.lazychunk.range_removed_none"), false);
            return 0;
        }
        String key = adding ? "hfut.lazychunk.range_added" : "hfut.lazychunk.range_removed";
        source.sendSuccess(() -> HFUTText.forViewer(source, key, changed), true);
        return changed;
    }

    private static int hfut$deleteAll(CommandSourceStack source) {
        int removed = SimpleLazyChunkManager.clearDimension(source.getLevel());
        if (removed == 0) {
            source.sendSuccess(() -> HFUTText.forViewer(source, "hfut.lazychunk.query_none"), false);
            return 0;
        }
        source.sendSuccess(() -> HFUTText.forViewer(source, "hfut.lazychunk.cleared", removed), true);
        return removed;
    }

    /** Pages the marks (10 rows per page, out-of-range clamps to the last); each row click-fills its delete command. */
    public static int query(CommandSourceStack source, int page) {
        List<Long> marks = SimpleLazyChunkManager.marksIn(source.getLevel());
        if (marks.isEmpty()) {
            source.sendSuccess(() -> HFUTText.forViewer(source, "hfut.lazychunk.query_none"), false);
            return 0;
        }
        int pages = (marks.size() + PAGE_SIZE - 1) / PAGE_SIZE;
        int currentPage = Math.max(1, Math.min(page, pages));
        int from = (currentPage - 1) * PAGE_SIZE;
        int to = Math.min(marks.size(), from + PAGE_SIZE);
        List<Long> pageMarks = marks.subList(from, to);
        long batch = HFUTChatPage.nextBatch();
        MutableComponent header = HFUTChatPage.mark(HFUTText.forViewer(source, "hfut.lazychunk.query_header",
                marks.size(), currentPage, pages), HFUTChatPage.LAZYCHUNK, batch);
        source.sendSuccess(() -> header, false);
        for (long packed : pageMarks) {
            int chunkX = SimpleLazyChunkManager.x(packed);
            int chunkZ = SimpleLazyChunkManager.z(packed);
            MutableComponent row = HFUTChatPage.mark(clickable(
                    Component.literal("[" + chunkX + ", " + chunkZ + "]=" + hfut$status(source, packed)),
                    "/hfut lazychunk delete chunk " + chunkX + " " + chunkZ), HFUTChatPage.LAZYCHUNK, batch);
            source.sendSuccess(() -> row, false);
        }
        if (pages > 1) {
            MutableComponent nav = HFUTChatPage.mark(hfut$pageNav(source, currentPage, pages),
                    HFUTChatPage.LAZYCHUNK, batch);
            source.sendSuccess(() -> nav, false);
        }
        return marks.size();
    }

    private static MutableComponent hfut$pageNav(CommandSourceStack source, int page, int pages) {
        String lang = hfut$lang(source);
        MutableComponent nav = Component.empty();
        if (page > 1) {
            nav.append(runnable(Component.literal(HFUTText.template(lang, "hfut.lazychunk.page_prev"))
                            .withStyle(ChatFormatting.GRAY), "/hfutclient lazychunk " + (page - 1)));
            if (page < pages) {
                nav.append("  ");
            }
        }
        if (page < pages) {
            nav.append(runnable(Component.literal(HFUTText.template(lang, "hfut.lazychunk.page_next"))
                    .withStyle(ChatFormatting.GRAY), "/hfutclient lazychunk " + (page + 1)));
        }
        return nav;
    }

    /** Row clicks only fill the chat input, keeping the delete deliberate. */
    private static MutableComponent clickable(MutableComponent line, String suggestCommand) {
        //#if MC >= 12105
        //$$ return line.withStyle(style -> style.withClickEvent(new ClickEvent.SuggestCommand(suggestCommand)));
        //#else
        return line.withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggestCommand)));
        //#endif
    }

    /** Page arrows run on click - harmless, unlike a row's destructive delete. */
    private static MutableComponent runnable(MutableComponent line, String command) {
        //#if MC >= 12105
        //$$ return line.withStyle(style -> style.withClickEvent(new ClickEvent.RunCommand(command)));
        //#else
        return line.withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command)));
        //#endif
    }

    private static String hfut$status(CommandSourceStack source, long packed) {
        ServerLevel level = source.getLevel();
        String raw = "NOT_LOADED";
        if (level.getChunk(SimpleLazyChunkManager.x(packed), SimpleLazyChunkManager.z(packed),
                ChunkStatus.FULL, false) instanceof LevelChunk loaded) {
            FullChunkStatus status = loaded.getFullStatus();
            raw = status != null ? status.name() : "UNKNOWN";
        }
        String lang = hfut$lang(source);
        return HFUTText.template(lang, "hfut.lazychunk.status." + raw);
    }

    private static String hfut$lang(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        return player == null ? "en_us" : ((ServerPlayerLanguageAccessor) player).hfut$language();
    }
}
