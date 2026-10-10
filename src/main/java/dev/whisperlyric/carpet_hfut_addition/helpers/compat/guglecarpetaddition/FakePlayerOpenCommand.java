package dev.whisperlyric.carpet_hfut_addition.helpers.compat.guglecarpetaddition;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.dubhe.gugle.carpet.tools.player.PlayerEnderChestContainer;
import dev.dubhe.gugle.carpet.tools.player.PlayerInventoryContainer;
import dev.dubhe.gugle.carpet.tools.player.PlayerInventoryMenu;
import dev.whisperlyric.carpet_hfut_addition.FakePlayerOpenStorageSettings;
import dev.whisperlyric.carpet_hfut_addition.helpers.rule.fakePlayerOpen.FakePlayerStorageEditor;
import dev.whisperlyric.carpet_hfut_addition.utils.CommandUtil;
import dev.whisperlyric.carpet_hfut_addition.utils.HFUTText;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;

import static net.minecraft.commands.Commands.literal;

/**
 * {@code /player <player> open inventory|enderchest}: opens GCA's fake player
 * interface over an OFFLINE fake player. Loaded only behind
 * {@link GcaGuard#present()} because it references GCA types; without GCA
 * neither this node nor the rule is registered. The shadow's GCA control
 * buttons are suppressed (see {@code PlayerEnderChestContainerMixin}).
 */
public final class FakePlayerOpenCommand {
    private FakePlayerOpenCommand() {
    }

    public static LiteralArgumentBuilder<CommandSourceStack> openNode() {
        return literal("open")
                .requires(source -> CommandUtil.canUseCommand(
                        source, FakePlayerOpenStorageSettings.commandFakePlayerOpenStorage))
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
        FakePlayerStorageEditor.BeginResult result = FakePlayerStorageEditor.begin(viewer, name);
        if (result.errorKey() != null) {
            source.sendFailure(HFUTText.forViewer(source, result.errorKey(), result.errorArgs()));
            return 0;
        }
        ServerPlayer shadow = result.session().shadow();
        boolean ender = kind == FakePlayerStorageEditor.Kind.ENDER_CHEST;
        String titleKey = ender ? "hfut.fakePlayerOpen.title.enderchest" : "hfut.fakePlayerOpen.title.inventory";
        Component title = HFUTText.forViewer(source, titleKey, name);
        MenuProvider provider = new SimpleMenuProvider(
                (id, inv, p) -> buildMenu(ender, id, viewer.getInventory(), shadow), title);
        viewer.openMenu(provider);
        FakePlayerStorageEditor.attach(result.session(), viewer, viewer.containerMenu);
        String openedKey = ender
                ? "hfut.fakePlayerOpen.opened.enderchest" : "hfut.fakePlayerOpen.opened.inventory";
        source.sendSuccess(() -> HFUTText.forViewer(source, openedKey, name), false);
        return 1;
    }

    private static AbstractContainerMenu buildMenu(boolean ender, int id,
                                                   net.minecraft.world.entity.player.Inventory viewerInv,
                                                   ServerPlayer shadow) {
        if (ender) {
            // Three rows = the 27 ender slots; GCA's control-button compartment stays
            // out of reach, and refreshButtons is cancelled for shadows anyway.
            PlayerEnderChestContainer container = new PlayerEnderChestContainer(shadow);
            return new ChestMenu(MenuType.GENERIC_9x3, id, viewerInv, container, 3);
        }
        PlayerInventoryContainer container = new PlayerInventoryContainer(shadow);
        return new PlayerInventoryMenu(id, viewerInv, container);
    }
}
