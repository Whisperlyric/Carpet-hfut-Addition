package dev.whisperlyric.carpet_hfut_addition.mixins.rule.tradeSeq;

//#if MC >= 260100
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.gen.Accessor;
//$$ import net.minecraft.world.RandomSequences;
//$$
//$$ /** Reads the per-world fresh-seed flags, so /tradeseq can compute a sequence's fresh state. */
//$$ @Mixin(RandomSequences.class)
//$$ public interface RandomSequencesAccessor {
//$$
//$$     @Accessor("salt")
//$$     int hfut$salt();
//$$
//$$     @Accessor("includeWorldSeed")
//$$     boolean hfut$includeWorldSeed();
//$$
//$$     @Accessor("includeSequenceId")
//$$     boolean hfut$includeSequenceId();
//$$ }
//#endif
