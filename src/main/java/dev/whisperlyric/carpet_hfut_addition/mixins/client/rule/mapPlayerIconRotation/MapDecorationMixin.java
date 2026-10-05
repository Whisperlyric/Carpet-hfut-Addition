package dev.whisperlyric.carpet_hfut_addition.mixins.client.rule.mapPlayerIconRotation;

//#if MC < 260300
import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import dev.whisperlyric.carpet_hfut_addition.HFUTServerMod;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//#endif
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Set;

/**
 * mapPlayerIconRotation, client half: 26.3 swapped the off-map player markers'
 * circular textures for rotating arrows, so the rotation the server half sends
 * needs those textures - shipped here and reachable through the decoration
 * atlas, which stitches every namespace. Only the three player sprite ids are
 * renamed to ours (off = vanilla ids); ResourceLocation renames stay in the
 * preprocessor.
 */
@Mixin(MapDecoration.class)
public abstract class MapDecorationMixin {

    //#if MC < 260300
    @Unique
    private static final Set<String> HFUT$PLAYER_ICON_PATHS = Set.of("player", "player_off_map", "player_off_limits");

    @Inject(method = "getSpriteLocation", at = @At("RETURN"), cancellable = true)
    private void hfut$useSyncedPlayerIcons(CallbackInfoReturnable<ResourceLocation> cir) {
        ResourceLocation id = cir.getReturnValue();
        if (HFUTSettings.mapPlayerIconRotation && id != null && "minecraft".equals(id.getNamespace())
                && HFUT$PLAYER_ICON_PATHS.contains(id.getPath())) {
            cir.setReturnValue(ResourceLocation.fromNamespaceAndPath(HFUTServerMod.MOD_ID, id.getPath()));
        }
    }
    //#endif
}
