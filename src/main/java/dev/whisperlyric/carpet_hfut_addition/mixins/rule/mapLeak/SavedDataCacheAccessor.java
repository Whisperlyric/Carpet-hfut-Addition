package dev.whisperlyric.carpet_hfut_addition.mixins.rule.mapLeak;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

/**
 * Exposes the loaded-saved-data cache of the per-level storage (class renamed
 * on 26.1). Typed as a raw map because the cache value shape varies across
 * versions; the caller disambiguates with instanceof.
 */
@Mixin({
        //#if MC >= 260102
        //$$ net.minecraft.world.level.storage.SavedDataStorage.class
        //#else
        net.minecraft.world.level.storage.DimensionDataStorage.class
        //#endif
})
public interface SavedDataCacheAccessor {

    @Accessor("cache")
    Map<?, ?> hfut$getCache();
}
