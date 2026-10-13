package dev.whisperlyric.carpet_hfut_addition.api;

//#if MC >= 260100
//$$ import dev.whisperlyric.carpet_hfut_addition.mixins.rule.tradeSeq.RandomSequencesAccessor;
//$$ import dev.whisperlyric.carpet_hfut_addition.mixins.rule.tradeSeq.Xor128Accessor;
//$$ import dev.whisperlyric.carpet_hfut_addition.mixins.rule.tradeSeq.XoroshiroRandomSourceAccessor;
//$$ import net.minecraft.resources.Identifier;
//$$ import net.minecraft.server.MinecraftServer;
//$$ import net.minecraft.world.RandomSequence;
//$$ import net.minecraft.world.level.levelgen.Xoroshiro128PlusPlus;
//$$ import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
//$$
//$$ import java.util.Optional;
//#endif

/**
 * Public query/manipulation API for named random sequences: trade prediction mods
 * pass in the owning server and the sequence id to read or move the Xoroshiro
 * state of {@code minecraft:trade_set/<profession>/level_<n>}.
 *
 * <p>26.x only; the backing accessor mixins are registered conditionally. All
 * counts are raw draws (one {@code nextLong} step each) - one trade refresh
 * consumes a variable chunk of 2+ draws (one pick per offer plus any draws the
 * offer's price/enchantment components make), never exactly one.
 */
public final class RandomSequenceApi {

    private RandomSequenceApi() {
    }

    //#if MC >= 260100
    //$$ /**
    //$$  * Reads the current 128-bit state as {@code [seedLo, seedHi]}; the next
    //$$  * draw of every villager refresh on this sequence continues from here.
    //$$  */
    //$$ public static long[] getState(MinecraftServer server, Identifier id) {
    //$$     Xor128Accessor acc = (Xor128Accessor) ((XoroshiroRandomSourceAccessor) sequence(server, id)).hfut$generator();
    //$$     return new long[]{acc.hfut$seedLo(), acc.hfut$seedHi()};
    //$$ }
    //$$
    //$$ /**
    //$$  * The 128-bit state of {@code id} freshly seeded, computed from the
    //$$  * world's stored seed flags without touching the live sequence.
    //$$  */
    //$$ public static long[] freshState(MinecraftServer server, Identifier id) {
    //$$     RandomSequencesAccessor seqs = (RandomSequencesAccessor) server.getRandomSequences();
    //$$     long worldSeed = server.getWorldGenSettings().options().seed();
    //$$     long seed = (seqs.hfut$includeWorldSeed() ? worldSeed : 0L) ^ seqs.hfut$salt();
    //$$     RandomSequence fresh = new RandomSequence(seed, seqs.hfut$includeSequenceId() ? Optional.of(id) : Optional.empty());
    //$$     Xor128Accessor acc = (Xor128Accessor) ((XoroshiroRandomSourceAccessor) fresh.random()).hfut$generator();
    //$$     return new long[]{acc.hfut$seedLo(), acc.hfut$seedHi()};
    //$$ }
    //$$
    //$$ /**
    //$$  * Overwrites the 128-bit state; call {@link #getState} first to snapshot it.
    //$$  * Direct writes bypass vanilla's dirty marking, so call {@link #markDirty}.
    //$$  */
    //$$ public static void setState(MinecraftServer server, Identifier id, long seedLo, long seedHi) {
    //$$     ((XoroshiroRandomSourceAccessor) sequence(server, id)).hfut$generator(new Xoroshiro128PlusPlus(seedLo, seedHi));
    //$$     markDirty(server);
    //$$ }
    //$$
    //$$ /** Advances the sequence by {@code count} rolls (official {@code consumeCount}). */
    //$$ public static void skip(MinecraftServer server, Identifier id, int count) {
    //$$     sequence(server, id).consumeCount(count);
    //$$     markDirty(server);
    //$$ }
    //$$
    //$$ /** Flags the per-world sequence store for saving after a direct state write. */
    //$$ public static void markDirty(MinecraftServer server) {
    //$$     server.getRandomSequences().setDirty();
    //$$ }
    //$$
    //$$ /**
    //$$  * Resolves the raw generator behind {@code id}, creating the sequence if absent.
    //$$  * Vanilla wraps it in a dirty-marking delegate that hides the state, so the
    //$$  * unwrapped instance has to be read back from {@code forAllSequences}.
    //$$  */
    //$$ public static XoroshiroRandomSource sequence(MinecraftServer server, Identifier id) {
    //$$     server.getRandomSequence(id); // make sure the sequence exists before reaching into the map
    //$$     XoroshiroRandomSource[] found = new XoroshiroRandomSource[1];
    //$$     server.getRandomSequences().forAllSequences((key, seq) -> {
    //$$         if (key.equals(id)) {
    //$$             found[0] = (XoroshiroRandomSource) seq.random();
    //$$         }
    //$$     });
    //$$     return found[0];
    //$$ }
    //#endif
}
