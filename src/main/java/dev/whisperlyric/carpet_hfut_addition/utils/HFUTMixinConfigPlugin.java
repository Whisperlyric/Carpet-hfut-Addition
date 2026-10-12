package dev.whisperlyric.carpet_hfut_addition.utils;

import me.fallenbreath.conditionalmixin.api.mixin.RestrictiveMixinConfigPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Mixin config plugin. Extending {@link RestrictiveMixinConfigPlugin} enables
 * the {@code @Restriction} annotation for conditionally-applied mixins, e.g.
 * only when another mod is loaded.
 */
public class HFUTMixinConfigPlugin extends RestrictiveMixinConfigPlugin {
    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    /**
     * Mixins that only exist on some versions, so they cannot sit in the
     * unconditional list of the mixin config. The 26.x trade sequence accessors
     * are source-gated behind {@code MC >= 260100} (absent from 1.21.x builds),
     * the stone cutter recipe mixin is source-gated behind {@code MC < 260100}
     * (absent from 26.x builds), and the RecipeMap accessor is source-gated
     * behind {@code MC >= 260300} (absent from earlier builds); register each
     * only where its class was compiled in.
     */
    @Override
    public List<String> getMixins() {
        List<String> out = new ArrayList<>();
        String mc = net.fabricmc.loader.api.FabricLoader.getInstance()
                .getModContainer("minecraft")
                .map(c -> c.getMetadata().getVersion().getFriendlyString())
                .orElse("");
        if (mc.startsWith("26.")) {
            out.add("rule.tradeSeq.XoroshiroRandomSourceAccessor");
            out.add("rule.tradeSeq.Xor128Accessor");
            if (minorAtLeast(mc, 3)) {
                out.add("rule.easyDispenserCrafting.RecipeMapInvoker");
            }
        } else {
            out.add("rule.stoneCutCobblestoneVariants.RecipeManagerMixin");
        }
        return out;
    }

    /** Whether a "26.x..." style version string has minor version >= {@code bound}. */
    private static boolean minorAtLeast(String mcVersion, int bound) {
        String[] parts = mcVersion.split("[.\\-]");
        try {
            return parts.length > 1 && Integer.parseInt(parts[1]) >= bound;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
