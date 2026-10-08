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
     * Mixins that only exist on some versions. The 26.x trade sequence
     * accessors are source-gated behind {@code MC >= 260100}, so 1.21.x
     * builds ship no such classes to register.
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
        }
        return out;
    }
}
