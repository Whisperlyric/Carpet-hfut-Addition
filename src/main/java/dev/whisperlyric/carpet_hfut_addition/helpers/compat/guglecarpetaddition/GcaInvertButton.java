package dev.whisperlyric.carpet_hfut_addition.helpers.compat.guglecarpetaddition;

import carpet.patches.EntityPlayerMPFake;
import dev.dubhe.gugle.carpet.api.menu.control.ButtonBuilder;
import dev.whisperlyric.carpet_hfut_addition.helpers.rule.fakePlayerTickStage.FakePlayerTickStage;
import dev.whisperlyric.carpet_hfut_addition.helpers.rule.fakePlayerTickStage.FakePlayerTickStage.StageMode;
import dev.whisperlyric.carpet_hfut_addition.mixins.ServerPlayerLanguageAccessor;
import dev.whisperlyric.carpet_hfut_addition.utils.HFUTText;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.ChestMenu;

/**
 * The invert-tick-stage button in GCA's fake player menu, at slot 25 next to
 * GCA's "quit" button at slot 26. Clicking it flips that fake player's
 * tick-stage inversion and reports the resulting value and phase.
 */
public final class GcaInvertButton {
    public static final int SLOT_INDEX = 25;

    private GcaInvertButton() {
    }

    public static ButtonBuilder builder(EntityPlayerMPFake fake, Container menuContainer) {
        String lang = ((ServerPlayerLanguageAccessor) fake).hfut$language();
        ButtonBuilder builder = ButtonBuilder.ofName(
                HFUTText.forLang(lang, "hfut.fakePlayerTickStage.button.name").getString());
        builder.appendTooltip(currentLine(fake, lang));
        builder.appendTooltip(HFUTText.forLang(lang, "hfut.fakePlayerTickStage.button.global",
                boolText(FakePlayerTickStage.globalTicksLikeRealPlayer())));
        builder.appendTooltip(HFUTText.forLang(lang, "hfut.fakePlayerTickStage.button.click"));
        builder.addTurnOnCallback(button -> {
            StageMode newMode = FakePlayerTickStage.modeOf(fake) == StageMode.INVERT
                    ? StageMode.GLOBAL
                    : StageMode.INVERT;
            FakePlayerTickStage.setMode(fake, newMode);
            boolean newEffective = FakePlayerTickStage.effectiveTicksLikeRealPlayer(fake);
            notifyViewers(menuContainer, fake, newEffective);
        });
        return builder;
    }

    private static Component currentLine(EntityPlayerMPFake fake, String lang) {
        boolean effective = FakePlayerTickStage.effectiveTicksLikeRealPlayer(fake);
        String stageKey = effective ? "hfut.fakePlayerTickStage.stage.t" : "hfut.fakePlayerTickStage.stage.f";
        return HFUTText.forLang(lang, "hfut.fakePlayerTickStage.button.current",
                boolText(effective), HFUTText.forLang(lang, stageKey).getString());
    }

    /** Feedback goes to everyone currently viewing this menu (normally the single clicker). */
    private static void notifyViewers(Container menuContainer, EntityPlayerMPFake fake, boolean newEffective) {
        String stageKey = newEffective ? "hfut.fakePlayerTickStage.stage.t" : "hfut.fakePlayerTickStage.stage.f";
        for (ServerPlayer viewer : fake.level().getServer().getPlayerList().getPlayers()) {
            if (!(viewer.containerMenu instanceof ChestMenu chestMenu)
                    || chestMenu.getContainer() != menuContainer) {
                continue;
            }
            viewer.sendSystemMessage(HFUTText.forPlayer(
                    viewer, "hfut.fakePlayerTickStage.toggled",
                    fake.getName().getString(), boolText(newEffective),
                    HFUTText.forPlayer(viewer, stageKey).getString(),
                    boolText(FakePlayerTickStage.globalTicksLikeRealPlayer())
            ));
        }
    }

    public static String boolText(boolean value) {
        return value ? "T" : "F";
    }
}
