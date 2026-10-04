package dev.whisperlyric.carpet_hfut_addition.mixins.rule.mapLeak;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import carpet.patches.EntityPlayerMPFake;

/**
 * Source-side fix of the map recipient leak: carpet fake players
 * have no client to deliver updates to, so their registrations are skipped.
 */
@Mixin(ServerEntity.class)
public abstract class ServerEntityMixin {

    //#if MC >= 260102
    //$$ @WrapOperation(
    //$$         method = "sendChanges",
    //$$         at = @At(value = "INVOKE",
    //$$                  target = "Lnet/minecraft/world/level/saveddata/maps/MapItemSavedData;tickCarriedBy"
    //$$                           + "(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;"
    //$$                           + "Lnet/minecraft/world/entity/decoration/ItemFrame;)V")
    //$$ )
    //$$ private void hfut$skipFakePlayerRegistration(MapItemSavedData data, Player player, ItemStack stack,
    //$$                                               ItemFrame frame, Operation<Void> original) {
    //$$     if (player instanceof EntityPlayerMPFake) {
    //$$         return;
    //$$     }
    //$$     original.call(data, player, stack, frame);
    //$$ }
    //#else
    @WrapOperation(
            method = "sendChanges",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/world/level/saveddata/maps/MapItemSavedData;tickCarriedBy"
                              + "(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)V")
    )
    private void hfut$skipFakePlayerRegistration(MapItemSavedData data, Player player, ItemStack stack,
                                                 Operation<Void> original) {
        if (player instanceof EntityPlayerMPFake) {
            return;
        }
        original.call(data, player, stack);
    }
    //#endif
}
