package dev.whisperlyric.carpet_hfut_addition.mixins.rule.tradeSeq;

//#if MC >= 260100
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.gen.Accessor;
//$$ import net.minecraft.world.level.levelgen.Xoroshiro128PlusPlus;
//$$
//$$ /** Reads/writes the 128-bit state behind a named sequence, for /tradeseq. */
//$$ @Mixin(Xoroshiro128PlusPlus.class)
//$$ public interface Xor128Accessor {
//$$
//$$     @Accessor("seedLo")
//$$     long hfut$seedLo();
//$$
//$$     @Accessor("seedHi")
//$$     long hfut$seedHi();
//$$
//$$     @Accessor("seedLo")
//$$     void hfut$seedLo(long lo);
//$$
//$$     @Accessor("seedHi")
//$$     void hfut$seedHi(long hi);
//$$ }
//#endif
