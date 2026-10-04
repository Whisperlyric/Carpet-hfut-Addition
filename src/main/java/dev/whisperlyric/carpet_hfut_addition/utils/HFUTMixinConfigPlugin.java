package dev.whisperlyric.carpet_hfut_addition.utils;

import me.fallenbreath.conditionalmixin.api.mixin.RestrictiveMixinConfigPlugin;

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

    @Override
    public List<String> getMixins() {
        return null;
    }
}
