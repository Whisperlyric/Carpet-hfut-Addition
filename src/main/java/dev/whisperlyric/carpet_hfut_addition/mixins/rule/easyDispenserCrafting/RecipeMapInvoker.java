package dev.whisperlyric.carpet_hfut_addition.mixins.rule.easyDispenserCrafting;

/**
 * Exposes RecipeMap's private constructor: since 26.3 the only public factory
 * needs a recipe registry lookup, so RecipeManagerMixin rebuilds the map from
 * builders through this. Compiled only on 26.3+ and registered by
 * HFUTMixinConfigPlugin there.
 */
//#if MC >= 260300
//$$ import com.google.common.collect.ImmutableMap;
//$$ import com.google.common.collect.ImmutableMultimap;
//$$ import com.google.common.collect.Multimap;
//$$ import net.minecraft.resources.ResourceKey;
//$$ import net.minecraft.world.item.crafting.Recipe;
//$$ import net.minecraft.world.item.crafting.RecipeHolder;
//$$ import net.minecraft.world.item.crafting.RecipeMap;
//$$ import net.minecraft.world.item.crafting.RecipeType;
//$$
//$$ import java.util.Map;
//$$
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.gen.Invoker;
//$$
//$$ @Mixin(RecipeMap.class)
//$$ public interface RecipeMapInvoker {
//$$
//$$     @Invoker("<init>")
//$$     static RecipeMap hfut$create(Multimap<RecipeType<?>, RecipeHolder<?>> byType,
//$$             Map<ResourceKey<Recipe<?>>, RecipeHolder<?>> byKey) {
//$$         throw new AssertionError();
//$$     }
//$$ }
//#endif
