package dev.whisperlyric.carpet_hfut_addition.mixins.rule.mapPlayerIconRotation;

//#if MC < 260300
import com.mojang.datafixers.util.Pair;
import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
//#endif
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

/**
 * mapPlayerIconRotation (below 26.3): port of the 26.3 map change - an
 * off-map player marker carries the player's real rotation instead of a
 * hardcoded 0; in-map markers always rotated and are untouched. On the
 * 1.21.1 node the decision sits inline in addDecoration, so the constructed
 * 0 byte is recomputed; 1.21.2+ re-implements playerDecorationTypeAndRotation.
 */
@Mixin(MapItemSavedData.class)
public abstract class MapItemSavedDataMixin {

    //#if MC < 260300

    //#if MC >= 12102
    //$$ @Shadow
    //$$ static boolean isInsideMap(float x, float z) {
    //$$     throw new AssertionError();
    //$$ }
    //$$
    //$$ @Shadow
    //$$ abstract Holder<MapDecorationType> decorationTypeForPlayerOutsideMap(float x, float z);
    //$$
    //$$ @Shadow
    //$$ abstract byte calculateRotation(LevelAccessor level, double yaw);
    //$$
    //$$ @Inject(method = "playerDecorationTypeAndRotation", at = @At("HEAD"), cancellable = true)
    //$$ private void hfut$rotateOffMapMarkers(Holder<MapDecorationType> type, LevelAccessor level, double yaw,
    //$$                                       float x, float z, CallbackInfoReturnable<Pair<Holder<MapDecorationType>, Byte>> cir) {
    //$$     if (!HFUTSettings.mapPlayerIconRotation || isInsideMap(x, z)) {
    //$$         return;
    //$$     }
    //$$     Holder<MapDecorationType> outsideType = decorationTypeForPlayerOutsideMap(x, z);
    //$$     cir.setReturnValue(outsideType == null ? null : Pair.of(outsideType, calculateRotation(level, yaw)));
    //$$ }
    //#else
    @Final
    @Shadow
    public ResourceKey<Level> dimension;

    @Unique
    private LevelAccessor hfut$decorationLevel;
    @Unique
    private double hfut$decorationYaw;

    @Inject(method = "addDecoration", at = @At("HEAD"))
    private void hfut$captureDecoration(Holder<MapDecorationType> type, LevelAccessor level, String key,
                                        double x, double z, double rotation, Component name, CallbackInfo ci) {
        if (HFUTSettings.mapPlayerIconRotation) {
            this.hfut$decorationLevel = level;
            this.hfut$decorationYaw = rotation;
        } else {
            this.hfut$decorationLevel = null;
        }
    }

    @Redirect(method = "addDecoration",
              at = @At(value = "NEW", target = "net/minecraft/world/level/saveddata/maps/MapDecoration"))
    private MapDecoration hfut$rotateOffMapMarkers(Holder<MapDecorationType> type, byte x, byte y, byte rotation,
                                                   Optional<Component> name) {
        if (rotation == 0 && this.hfut$decorationLevel != null) {
            rotation = this.hfut$recomputeRotation();
        }
        return new MapDecoration(type, x, y, rotation, name);
    }

    /** Vanilla 1.21.1's own in-bounds formula, verbatim. */
    @Unique
    private byte hfut$recomputeRotation() {
        LevelAccessor level = this.hfut$decorationLevel;
        double yaw = this.hfut$decorationYaw;
        if (this.dimension == Level.NETHER) {
            int i = (int) (level.getLevelData().getDayTime() / 10L);
            return (byte) ((i * i * 34187121 + 121 * i) >> 15 & 15);
        }
        double adjusted = yaw + (yaw < 0.0 ? -8.0 : 8.0);
        return (byte) (int) (adjusted * 16.0 / 360.0);
    }
    //#endif
    //#endif
}
