package dev.whisperlyric.carpet_hfut_addition.mixins.rule.soulSpeedNoBootDamage;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.effects.EnchantmentLocationBasedEffect;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
//#if MC < 12103
import net.minecraft.world.item.enchantment.effects.DamageItem;
//#else
//$$ import net.minecraft.world.item.enchantment.effects.ChangeItemDamage;
//#endif

/**
 * soulSpeedNoBootDamage: drop soul speed's damage_item effect by wrapping the
 * onChangedBlock dispatch in Enchantment.runLocationChangedEffects. Identity goes
 * through the registry because the enchantment is datapack-driven; only the effect
 * class is versioned (DamageItem -> ChangeItemDamage from 1.21.3).
 */
@Mixin(Enchantment.class)
public abstract class EnchantmentMixin {

    @WrapOperation(method = "runLocationChangedEffects",
                   at = @At(value = "INVOKE",
                            target = "Lnet/minecraft/world/item/enchantment/effects/EnchantmentLocationBasedEffect;onChangedBlock(Lnet/minecraft/server/level/ServerLevel;ILnet/minecraft/world/item/enchantment/EnchantedItemInUse;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Z)V"))
    private void hfut$noBootDamage(EnchantmentLocationBasedEffect effect, ServerLevel level, int enchantLvl,
                                   EnchantedItemInUse item, Entity entity, Vec3 pos, boolean newlyActive,
                                   Operation<Void> original) {
        if (HFUTSettings.soulSpeedNoBootDamage
                && this.hfut$isSoulSpeed(entity)
                && this.hfut$isItemDamageEffect(effect)) {
            return;
        }
        original.call(effect, level, enchantLvl, item, entity, pos, newlyActive);
    }

    @Unique
    private boolean hfut$isSoulSpeed(Entity entity) {
        HolderGetter<Enchantment> lookup = entity.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        ResourceKey<Enchantment> key = Enchantments.SOUL_SPEED;
        return lookup.get(key).map(Holder::value).orElse(null) == (Object) this;
    }

    @Unique
    private boolean hfut$isItemDamageEffect(EnchantmentLocationBasedEffect effect) {
        //#if MC < 12103
        return effect instanceof DamageItem;
        //#else
        //$$ return effect instanceof ChangeItemDamage;
        //#endif
    }
}
