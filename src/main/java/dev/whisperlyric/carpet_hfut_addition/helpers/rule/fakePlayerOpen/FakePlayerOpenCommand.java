package dev.whisperlyric.carpet_hfut_addition.helpers.rule.fakePlayerOpen;

import carpet.patches.EntityPlayerMPFake;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.whisperlyric.carpet_hfut_addition.FakePlayerOpenStorageSettings;
import dev.whisperlyric.carpet_hfut_addition.helpers.compat.guglecarpetaddition.GcaStorageMenuBridge;
import dev.whisperlyric.carpet_hfut_addition.utils.CommandUtil;
import dev.whisperlyric.carpet_hfut_addition.utils.HFUTText;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import static net.minecraft.commands.Commands.literal;

/**
 * {@code /player <player> open inventory|enderchest}: opens a chest-style view over
 * a fake player's storage for remote editing. An online target is shown through GCA's
 * own menu when GCA and the matching GCA rule are available, and through this mod's
 * own menu otherwise; an offline target is shown from its saved data, loaded into a
 * shadow entity that never joins the world. Non-storage cells of this mod's own
 * inventory view carry a plain-NBT marker so GCA-aware sorters skip them.
 */
public final class FakePlayerOpenCommand {
    /** GCA classes needed for GCA's menus; probed by name so no GCA type is touched when GCA is absent. */
    private static final String[] GCA_MENU_CLASSES = {
            "dev.dubhe.gugle.carpet.api.menu.control.ButtonBuilder",
            "dev.dubhe.gugle.carpet.tools.player.IGcaPlayer",
            "dev.dubhe.gugle.carpet.tools.player.PlayerInventoryMenu",
            "dev.dubhe.gugle.carpet.GcaSetting",
            "dev.dubhe.gugle.carpet.util.SettingUtil",
    };

    private static Boolean gcaMenusUsable;

    private FakePlayerOpenCommand() {
    }

    public static LiteralArgumentBuilder<CommandSourceStack> openNode() {
        return literal("open")
                .requires(source -> CommandUtil.canUseCommand(source,
                                FakePlayerOpenStorageSettings.commandFakePlayerRemoteOpenStorage)
                        || CommandUtil.canUseCommand(source,
                                FakePlayerOpenStorageSettings.commandOfflineFakePlayerOpenStorage))
                .then(literal("inventory")
                        .executes(ctx -> open(ctx, FakePlayerStorageEditor.Kind.INVENTORY)))
                .then(literal("enderchest")
                        .executes(ctx -> open(ctx, FakePlayerStorageEditor.Kind.ENDER_CHEST)));
    }

    private static int open(CommandContext<CommandSourceStack> ctx, FakePlayerStorageEditor.Kind kind) {
        CommandSourceStack source = ctx.getSource();
        ServerPlayer viewer = source.getPlayer();
        if (viewer == null) {
            source.sendFailure(HFUTText.forViewer(source, "hfut.fakePlayerOpen.player_only"));
            return 0;
        }
        String name = StringArgumentType.getString(ctx, "player");
        ServerPlayer target = source.getServer().getPlayerList().getPlayerByName(name);
        return target == null
                ? openOffline(source, viewer, name, kind)
                : openOnline(source, viewer, name, target, kind);
    }

    /** Online target: GCA's own menu when available, this mod's own menu otherwise. */
    private static int openOnline(CommandSourceStack source, ServerPlayer viewer, String name,
                                  ServerPlayer target, FakePlayerStorageEditor.Kind kind) {
        if (!CommandUtil.canUseCommand(source,
                FakePlayerOpenStorageSettings.commandFakePlayerRemoteOpenStorage)) {
            source.sendFailure(HFUTText.forViewer(source, "hfut.fakePlayerOpen.no_permission"));
            return 0;
        }
        if (!(target instanceof EntityPlayerMPFake fake)) {
            source.sendFailure(HFUTText.forViewer(source, "hfut.fakePlayerOpen.not_fake", name));
            return 0;
        }
        MenuProvider provider = new SimpleMenuProvider(
                (id, inv, p) -> onlineMenu(fake, viewer, kind, id), title(source, kind, name));
        viewer.openMenu(provider);
        reportOpened(source, kind, name);
        return 1;
    }

    /** Offline target: edit its saved data through a shadow entity. */
    private static int openOffline(CommandSourceStack source, ServerPlayer viewer, String name,
                                   FakePlayerStorageEditor.Kind kind) {
        if (!CommandUtil.canUseCommand(source,
                FakePlayerOpenStorageSettings.commandOfflineFakePlayerOpenStorage)) {
            source.sendFailure(HFUTText.forViewer(source, "hfut.fakePlayerOpen.no_permission"));
            return 0;
        }
        FakePlayerStorageEditor.BeginResult result = FakePlayerStorageEditor.begin(name, kind);
        if (result.errorKey() != null) {
            source.sendFailure(HFUTText.forViewer(source, result.errorKey(), result.errorArgs()));
            return 0;
        }
        ServerPlayer shadow = result.session().shadow();
        MenuProvider provider = new SimpleMenuProvider(
                (id, inv, p) -> buildMenu(kind, id, viewer.getInventory(), shadow),
                title(source, kind, name));
        viewer.openMenu(provider);
        FakePlayerStorageEditor.attach(result.session(), kind, viewer, viewer.containerMenu);
        reportOpened(source, kind, name);
        return 1;
    }

    private static AbstractContainerMenu onlineMenu(ServerPlayer fake, ServerPlayer viewer,
                                                    FakePlayerStorageEditor.Kind kind, int id) {
        if (gcaMenusUsable()) {
            boolean ender = kind == FakePlayerStorageEditor.Kind.ENDER_CHEST;
            if (ender && GcaStorageMenuBridge.allowsEnderChest(viewer)) {
                return GcaStorageMenuBridge.enderChestMenu(fake, id, viewer.getInventory());
            }
            if (!ender && GcaStorageMenuBridge.allowsInventory()) {
                return GcaStorageMenuBridge.inventoryMenu(fake, id, viewer.getInventory());
            }
        }
        return buildMenu(kind, id, viewer.getInventory(), fake);
    }

    private static AbstractContainerMenu buildMenu(FakePlayerStorageEditor.Kind kind, int id,
                                                   Inventory viewerInv, ServerPlayer target) {
        if (kind == FakePlayerStorageEditor.Kind.ENDER_CHEST) {
            return new FakePlayerStorageMenu(MenuType.GENERIC_9x3, id, viewerInv, kind,
                    new OfflinePlayerEnderChestContainer(target), 3, slot -> false);
        }
        return new FakePlayerStorageMenu(MenuType.GENERIC_9x5, id, viewerInv, kind,
                new OfflinePlayerInventoryContainer(target), 5,
                OfflinePlayerInventoryContainer::isPlaceholder);
    }

    private static Component title(CommandSourceStack source, FakePlayerStorageEditor.Kind kind, String name) {
        String key = kind == FakePlayerStorageEditor.Kind.ENDER_CHEST
                ? "hfut.fakePlayerOpen.title.enderchest" : "hfut.fakePlayerOpen.title.inventory";
        return HFUTText.forViewer(source, key, name);
    }

    private static void reportOpened(CommandSourceStack source, FakePlayerStorageEditor.Kind kind, String name) {
        String key = kind == FakePlayerStorageEditor.Kind.ENDER_CHEST
                ? "hfut.fakePlayerOpen.opened.enderchest" : "hfut.fakePlayerOpen.opened.inventory";
        source.sendSuccess(() -> HFUTText.forViewer(source, key, name), false);
    }

    /**
     * True when GCA's menu API is present. Probed by class name only (never by
     * touching a GCA type) and cached, because the first touch of a GCA class would
     * fail hard when GCA is absent.
     */
    private static boolean gcaMenusUsable() {
        if (gcaMenusUsable == null) {
            gcaMenusUsable = probeGcaMenus();
        }
        return gcaMenusUsable;
    }

    private static boolean probeGcaMenus() {
        if (!FabricLoader.getInstance().isModLoaded("gca")) {
            return false;
        }
        ClassLoader loader = FakePlayerOpenCommand.class.getClassLoader();
        try {
            for (String className : GCA_MENU_CLASSES) {
                Class.forName(className, false, loader);
            }
            return true;
        } catch (ClassNotFoundException | LinkageError e) {
            return false;
        }
    }
}
