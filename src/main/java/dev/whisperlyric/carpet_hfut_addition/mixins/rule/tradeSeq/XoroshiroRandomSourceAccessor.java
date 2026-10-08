package dev.whisperlyric.carpet_hfut_addition.mixins.rule.tradeSeq;

//#if MC >= 260100
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.gen.Accessor;
//$$ import net.minecraft.world.level.levelgen.Xoroshiro128PlusPlus;
//$$ import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
//$$
//$$ /** Reads/writes the Xoroshiro128PlusPlus wrapped by a named sequence, for /tradeseq. */
//$$ @Mixin(XoroshiroRandomSource.class)
//$$ public interface XoroshiroRandomSourceAccessor {
//$$
//$$     @Accessor("randomNumberGenerator")
//$$     Xoroshiro128PlusPlus hfut$generator();
//$$
//$$     @Accessor("randomNumberGenerator")
//$$     void hfut$generator(Xoroshiro128PlusPlus generator);
//$$ }
//#endif
