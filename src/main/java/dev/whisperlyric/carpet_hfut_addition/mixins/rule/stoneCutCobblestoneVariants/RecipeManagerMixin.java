package dev.whisperlyric.carpet_hfut_addition.mixins.rule.stoneCutCobblestoneVariants;

//#if MC < 260100
import dev.whisperlyric.carpet_hfut_addition.HFUTServerMod;
import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.ItemLike;

//#if MC < 12102
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import java.util.Map;
//#else
//$$ import net.minecraft.core.registries.Registries;
//$$ import net.minecraft.resources.ResourceKey;
//$$ import net.minecraft.world.flag.FeatureFlagSet;
//$$ import net.minecraft.world.item.crafting.Recipe;
//$$ import net.minecraft.world.item.crafting.RecipeMap;
//#endif

import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;

/**
 * stoneCutCobblestoneVariants (below 26.1): registers the eight stonecutter
 * recipes 26.1 added natively (stone/deepslate into cobblestone and cobbled
 * deepslate variants) into the loaded recipe map, so UI, crafting checks and
 * client sync all run on vanilla paths. The reconcile re-fires on every recipe
 * load, and the class is compiled out on 26.1+.
 */
@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {

    //#if MC < 12102
    @Shadow
    private Multimap<RecipeType<?>, RecipeHolder<?>> byType;

    @Shadow
    private Map<ResourceLocation, RecipeHolder<?>> byName;

    /**
     * Tail of the recipe map load: 1.21.1 rebuilds both fields as immutable
     * maps on every apply, so ours are appended by rebuilding them once more.
     */
    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
            at = @At("TAIL"))
    private void hfut$reconcileStoneCutVariants(CallbackInfo ci) {
        if (!HFUTSettings.stoneCutCobblestoneVariants) {
            return;
        }
        List<RecipeHolder<?>> holders = hfut$holders();
        Set<ResourceLocation> ourIds = new HashSet<>();
        for (RecipeHolder<?> holder : holders) {
            ourIds.add(holder.id());
        }
        ImmutableMultimap.Builder<RecipeType<?>, RecipeHolder<?>> byTypeBuilder = ImmutableMultimap.builder();
        byTypeBuilder.putAll(this.byType);
        for (RecipeHolder<?> holder : holders) {
            byTypeBuilder.put(RecipeType.STONECUTTING, holder);
        }
        this.byType = byTypeBuilder.build();

        ImmutableMap.Builder<ResourceLocation, RecipeHolder<?>> byNameBuilder = ImmutableMap.builder();
        for (Map.Entry<ResourceLocation, RecipeHolder<?>> entry : this.byName.entrySet()) {
            if (!ourIds.contains(entry.getKey())) {
                byNameBuilder.put(entry.getKey(), entry.getValue());
            }
        }
        for (RecipeHolder<?> holder : holders) {
            byNameBuilder.put(holder.id(), holder);
        }
        this.byName = byNameBuilder.build();
    }
    //#else
    //$$ @Shadow
    //$$ private RecipeMap recipes;
    //$$
    //$$ /**
    //$$  * Head of finalizeRecipeLoading: rebuild the recipe map with our entries
    //$$  * merged in; the stonecutter display set, property sets and recipe book
    //$$  * data are derived from it right after.
    //$$  */
    //$$ @Inject(method = "finalizeRecipeLoading", at = @At("HEAD"))
    //$$ private void hfut$reconcileStoneCutVariants(FeatureFlagSet features, CallbackInfo ci) {
    //$$     List<RecipeHolder<?>> ours = hfut$holders();
    //$$     Set<ResourceKey<Recipe<?>>> ourKeys = new HashSet<>();
    //$$     for (RecipeHolder<?> holder : ours) {
    //$$         ourKeys.add(holder.id());
    //$$     }
    //$$     List<RecipeHolder<?>> merged = new ArrayList<>(this.recipes.values());
    //$$     merged.removeIf(h -> ourKeys.contains(h.id()));
    //$$     if (HFUTSettings.stoneCutCobblestoneVariants) {
    //$$         merged.addAll(ours);
    //$$     }
    //$$     this.recipes = RecipeMap.create(merged);
    //$$ }
    //#endif

    @Unique
    private static List<RecipeHolder<?>> hfut$holders() {
        List<RecipeHolder<?>> out = new ArrayList<>();
        out.add(hfut$holder("cobblestone_from_stone_stonecutting", Items.STONE, Items.COBBLESTONE, 1));
        out.add(hfut$holder("cobblestone_slab_from_stone_stonecutting", Items.STONE, Items.COBBLESTONE_SLAB, 2));
        out.add(hfut$holder("cobblestone_stairs_from_stone_stonecutting", Items.STONE, Items.COBBLESTONE_STAIRS, 1));
        out.add(hfut$holder("cobblestone_wall_from_stone_stonecutting", Items.STONE, Items.COBBLESTONE_WALL, 1));
        out.add(hfut$holder("cobbled_deepslate_from_deepslate_stonecutting", Items.DEEPSLATE, Items.COBBLED_DEEPSLATE, 1));
        out.add(hfut$holder("cobbled_deepslate_slab_from_deepslate_stonecutting", Items.DEEPSLATE, Items.COBBLED_DEEPSLATE_SLAB, 2));
        out.add(hfut$holder("cobbled_deepslate_stairs_from_deepslate_stonecutting", Items.DEEPSLATE, Items.COBBLED_DEEPSLATE_STAIRS, 1));
        out.add(hfut$holder("cobbled_deepslate_wall_from_deepslate_stonecutting", Items.DEEPSLATE, Items.COBBLED_DEEPSLATE_WALL, 1));
        return out;
    }

    @Unique
    private static RecipeHolder<?> hfut$holder(String path, ItemLike input, ItemLike output, int count) {
        StonecutterRecipe recipe = new StonecutterRecipe("", Ingredient.of(input), new ItemStack(output, count));
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(HFUTServerMod.getModId(), path);
        //#if MC < 12102
        return new RecipeHolder<>(id, recipe);
        //#else
        //$$ return new RecipeHolder<>(ResourceKey.create(Registries.RECIPE, id), recipe);
        //#endif
    }
}
//#endif
