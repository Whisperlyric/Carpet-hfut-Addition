package dev.whisperlyric.carpet_hfut_addition.mixins.rule.easyDispenserCrafting;

import dev.whisperlyric.carpet_hfut_addition.HFUTServerMod;
import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

//#if MC >= 260100
//$$ import net.minecraft.world.item.ItemStackTemplate;
//$$ import net.minecraft.world.item.crafting.CraftingRecipe;
//#endif
//#if MC < 12102
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
//#elseif MC < 260300
//$$ import net.minecraft.world.flag.FeatureFlagSet;
//$$ import net.minecraft.world.item.crafting.RecipeMap;
//#else
//$$ import com.google.common.collect.ImmutableMap;
//$$ import com.google.common.collect.ImmutableMultimap;
//$$ import com.google.common.collect.Multimap;
//$$ import net.minecraft.world.flag.FeatureFlagSet;
//$$ import net.minecraft.world.item.crafting.RecipeMap;
//#endif

import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;

/**
 * easyDispenserCrafting: registers a shaped recipe - the bow recipe with a
 * dropper in its middle slot - into the loaded recipe map, so crafting and
 * recipe sync run on vanilla paths; it has no advancement, so it never shows in
 * the recipe book. Re-registering on every load makes toggling the rule work.
 */
@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {

    //#if MC < 12102
    @Shadow
    private Multimap<RecipeType<?>, RecipeHolder<?>> byType;

    @Shadow
    private Map<ResourceLocation, RecipeHolder<?>> byName;

    /** Tail of the 1.21.1 load: rebuild both immutable fields with ours appended. */
    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
            at = @At("TAIL"))
    private void hfut$reconcileDispenserCrafting(CallbackInfo ci) {
        if (!HFUTSettings.easyDispenserCrafting) {
            return;
        }
        RecipeHolder<?> ours = hfut$holder();
        Set<ResourceLocation> ourIds = new HashSet<>();
        ourIds.add(ours.id());
        ImmutableMultimap.Builder<RecipeType<?>, RecipeHolder<?>> byTypeBuilder = ImmutableMultimap.builder();
        byTypeBuilder.putAll(this.byType);
        byTypeBuilder.put(RecipeType.CRAFTING, ours);
        this.byType = byTypeBuilder.build();

        ImmutableMap.Builder<ResourceLocation, RecipeHolder<?>> byNameBuilder = ImmutableMap.builder();
        for (Map.Entry<ResourceLocation, RecipeHolder<?>> entry : this.byName.entrySet()) {
            if (!ourIds.contains(entry.getKey())) {
                byNameBuilder.put(entry.getKey(), entry.getValue());
            }
        }
        byNameBuilder.put(ours.id(), ours);
        this.byName = byNameBuilder.build();
    }
    //#elseif MC < 260300
    //$$ @Shadow
    //$$ private RecipeMap recipes;
    //$$
    //$$ /** Head of finalizeRecipeLoading: rebuild the recipe map with ours merged in. */
    //$$ @Inject(method = "finalizeRecipeLoading", at = @At("HEAD"))
    //$$ private void hfut$reconcileDispenserCrafting(FeatureFlagSet features, CallbackInfo ci) {
    //$$     RecipeHolder<?> ours = hfut$holder();
    //$$     Set<ResourceKey<Recipe<?>>> ourKeys = new HashSet<>();
    //$$     ourKeys.add(ours.id());
    //$$     List<RecipeHolder<?>> merged = new ArrayList<>(this.recipes.values());
    //$$     merged.removeIf(h -> ourKeys.contains(h.id()));
    //$$     if (HFUTSettings.easyDispenserCrafting) {
    //$$         merged.add(ours);
    //$$     }
    //$$     this.recipes = RecipeMap.create(merged);
    //$$ }
    //#else
    //$$ @Mutable
    //$$ @Shadow
    //$$ private RecipeMap recipes;
    //$$
    //$$ /**
    //$$  * Head of finalizeRecipeLoading: rebuild the recipe map with ours merged in.
    //$$  * 26.3 made the field final and RecipeMap.create registry-bound, so rebuild
    //$$  * through the private constructor (see RecipeMapInvoker).
    //$$  */
    //$$ @Inject(method = "finalizeRecipeLoading", at = @At("HEAD"))
    //$$ private void hfut$reconcileDispenserCrafting(FeatureFlagSet features, CallbackInfo ci) {
    //$$     RecipeHolder<?> ours = hfut$holder();
    //$$     Set<ResourceKey<Recipe<?>>> ourKeys = new HashSet<>();
    //$$     ourKeys.add(ours.id());
    //$$     ImmutableMultimap.Builder<RecipeType<?>, RecipeHolder<?>> byType = ImmutableMultimap.builder();
    //$$     ImmutableMap.Builder<ResourceKey<Recipe<?>>, RecipeHolder<?>> byKey = ImmutableMap.builder();
    //$$     for (RecipeHolder<?> holder : this.recipes.values()) {
    //$$         if (!ourKeys.contains(holder.id())) {
    //$$             byType.put(holder.value().getType(), holder);
    //$$             byKey.put(holder.id(), holder);
    //$$         }
    //$$     }
    //$$     if (HFUTSettings.easyDispenserCrafting) {
    //$$         byType.put(RecipeType.CRAFTING, ours);
    //$$         byKey.put(ours.id(), ours);
    //$$     }
    //$$     this.recipes = RecipeMapInvoker.hfut$create(byType.build(), byKey.build());
    //$$ }
    //#endif

    @Unique
    private static RecipeHolder<?> hfut$holder() {
        ShapedRecipePattern pattern = ShapedRecipePattern.of(Map.of(
                        '#', Ingredient.of(Items.STICK),
                        'D', Ingredient.of(Items.DROPPER),
                        'X', Ingredient.of(Items.STRING)),
                " #X", "#DX", " #X");
        //#if MC >= 260100
        //$$ ShapedRecipe recipe = new ShapedRecipe(new Recipe.CommonInfo(true),
        //$$         new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.REDSTONE, ""), pattern,
        //$$         new ItemStackTemplate(Items.DISPENSER));
        //#else
        ShapedRecipe recipe = new ShapedRecipe("", CraftingBookCategory.REDSTONE, pattern, new ItemStack(Items.DISPENSER));
        //#endif
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(HFUTServerMod.getModId(), "dispenser_from_dropper");
        //#if MC < 12102
        return new RecipeHolder<>(id, recipe);
        //#else
        //$$ return new RecipeHolder<>(ResourceKey.create(Registries.RECIPE, id), recipe);
        //#endif
    }
}
