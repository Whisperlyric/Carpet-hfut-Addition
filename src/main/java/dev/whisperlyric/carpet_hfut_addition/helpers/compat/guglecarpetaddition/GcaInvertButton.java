package dev.whisperlyric.carpet_hfut_addition.helpers.compat.guglecarpetaddition;

import carpet.patches.EntityPlayerMPFake;
import dev.dubhe.gugle.carpet.api.menu.control.ButtonBuilder;
import dev.whisperlyric.carpet_hfut_addition.helpers.rule.fakePlayerTickStage.FakePlayerTickStage;
import dev.whisperlyric.carpet_hfut_addition.helpers.rule.fakePlayerTickStage.FakePlayerTickStage.StageMode;
import dev.whisperlyric.carpet_hfut_addition.mixins.ServerPlayerLanguageAccessor;
import dev.whisperlyric.carpet_hfut_addition.utils.HFUTText;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;

import java.util.List;

/**
 * The tick-stage button in GCA's fake player menu, at slot 25 next to GCA's
 * "quit" button at slot 26.
 *
 * <p>State is shown in a fixed, differentiated way: {@code T} (ticks like a real
 * player / network stage) always uses one icon and {@code F} (entity-only stage)
 * always uses another, and each side carries its own lore. GCA re-writes the
 * slot from the current state every tick and vanilla broadcasts the change, so
 * both the icon and the tooltip follow a click within a tick.
 */
public final class GcaInvertButton {
    public static final int SLOT_INDEX = 25;

    /** One fixed icon per state, so T and F are told apart at a glance. */
    private static final Item T_ICON = Items.BARRIER;
    private static final Item F_ICON = Items.STRUCTURE_VOID;

    private GcaInvertButton() {
    }

    public static ButtonBuilder builder(EntityPlayerMPFake fake, Container menuContainer) {
        String lang = ((ServerPlayerLanguageAccessor) fake).hfut$language();
        boolean global = FakePlayerTickStage.globalTicksLikeRealPlayer();
        ButtonBuilder builder = ButtonBuilder.ofName(
                HFUTText.forLang(lang, "hfut.fakePlayerTickStage.button.name").getString());
        builder.setOnItem(icon(T_ICON, fake, lang, true, global));
        builder.setOffItem(icon(F_ICON, fake, lang, false, global));
        builder.setDefaultState(FakePlayerTickStage.effectiveTicksLikeRealPlayer(fake));
        builder.addTurnOnCallback(button -> apply(menuContainer, fake, true));
        builder.addTurnOffCallback(button -> apply(menuContainer, fake, false));
        return builder;
    }

    /**
     * Both states live in separate stacks with their own lore (we deliberately do
     * not use {@code appendTooltip}, which would bake one shared list into both).
     */
    private static ItemStack icon(Item item, EntityPlayerMPFake fake, String lang,
                                  boolean effective, boolean global) {
        ItemStack stack = new ItemStack(item);
        stack.set(DataComponents.LORE, new ItemLore(List.<Component>of(
                HFUTText.forLang(lang, "hfut.fakePlayerTickStage.button.current",
                        boolText(effective), FakePlayerTickStage.stageDisplayFor(fake, effective)),
                HFUTText.forLang(lang, "hfut.fakePlayerTickStage.button.global", boolText(global)),
                HFUTText.forLang(lang, "hfut.fakePlayerTickStage.button.click"))));
        return stack;
    }

    private static void apply(Container menuContainer, EntityPlayerMPFake fake, boolean ticksLikeRealPlayer) {
        FakePlayerTickStage.setMode(fake, ticksLikeRealPlayer ? StageMode.LIKE_REAL : StageMode.ORIGIN);
        notifyViewers(menuContainer, fake, ticksLikeRealPlayer);
    }

    /** Feedback goes to everyone currently viewing this menu (normally the single clicker). */
    private static void notifyViewers(Container menuContainer, EntityPlayerMPFake fake, boolean newEffective) {
        for (ServerPlayer viewer : fake.level().getServer().getPlayerList().getPlayers()) {
            if (!(viewer.containerMenu instanceof ChestMenu chestMenu)
                    || chestMenu.getContainer() != menuContainer) {
                continue;
            }
            viewer.sendSystemMessage(HFUTText.forPlayer(
                    viewer, "hfut.fakePlayerTickStage.query",
                    fake.getName().getString(),
                    FakePlayerTickStage.stageDisplayFor(viewer, newEffective)
            ).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }
    }

    public static String boolText(boolean value) {
        return value ? "T" : "F";
    }
}
