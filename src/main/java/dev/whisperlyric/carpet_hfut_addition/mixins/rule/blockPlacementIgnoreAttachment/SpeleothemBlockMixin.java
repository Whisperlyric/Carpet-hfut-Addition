package dev.whisperlyric.carpet_hfut_addition.mixins.rule.blockPlacementIgnoreAttachment;

//#if MC >= 260200
//$$ import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
//$$ import dev.whisperlyric.carpet_hfut_addition.helpers.rule.blockPlacementIgnoreAttachment.PlacementScope;
//$$ import net.minecraft.core.BlockPos;
//$$ import net.minecraft.core.Direction;
//$$ import net.minecraft.world.level.LevelReader;
//$$ import net.minecraft.world.level.block.SpeleothemBlock;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//$$
//$$ /**
//$$  * blockPlacementIgnoreAttachment, 26.2+ half: Mojang hoisted the dripstone
//$$  * placement check to {@code SpeleothemBlock.isValidSpeleothemPlacement}
//$$  * (removing the {@code PointedDripstoneBlock.isValidPointedDripstonePlacement}
//$$  * override our pre-26.2 mixin targets), so this variant targets the
//$$  * superclass; pointed dripstone inherits it. {@code SpeleothemBlock} itself
//$$  * only exists from 26.2, hence the source gate.
//$$  */
//$$ @Mixin(SpeleothemBlock.class)
//$$ public abstract class SpeleothemBlockMixin {
//$$
//$$     @Inject(method = "isValidSpeleothemPlacement", at = @At("HEAD"), cancellable = true)
//$$     private static void hfut$allowAnyPlacement(LevelReader level, BlockPos pos, Direction direction,
//$$                                                CallbackInfoReturnable<Boolean> cir) {
//$$         if (HFUTSettings.blockPlacementIgnoreAttachment && PlacementScope.active()) {
//$$             cir.setReturnValue(true);
//$$         }
//$$     }
//$$ }
//#endif
