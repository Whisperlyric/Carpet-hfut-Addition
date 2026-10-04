package dev.whisperlyric.carpet_hfut_addition.mixins.rule.mapLeak;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.Map;

/**
 * Field access for the map "update recipient" registries. The
 * holder type is a private nested class, so it is carried as {@code Object}.
 */
@Mixin(MapItemSavedData.class)
public interface MapItemSavedDataAccessor {

    @Accessor("carriedByPlayers")
    Map<Player, Object> hfut$getCarriedByPlayers();

    @Accessor("carriedBy")
    List<Object> hfut$getCarriedBy();

    @Invoker("removeDecoration")
    void hfut$callRemoveDecoration(String name);
}
