package dev.whisperlyric.carpet_hfut_addition.api;

//#if MC >= 260100
//$$ import dev.whisperlyric.carpet_hfut_addition.mixins.rule.tradeSeq.Xor128Accessor;
//$$ import dev.whisperlyric.carpet_hfut_addition.mixins.rule.tradeSeq.XoroshiroRandomSourceAccessor;
//$$ import net.minecraft.resources.Identifier;
//$$ import net.minecraft.server.MinecraftServer;
//$$ import net.minecraft.world.level.levelgen.Xoroshiro128PlusPlus;
//$$ import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
//#endif

/**
 * Public query/manipulation API for named random sequences: trade prediction mods
 * pass in the owning server and the sequence id to read or move the Xoroshiro
 * state of {@code minecraft:trade_set/<profession>/level_<n>}.
 *
 * <p>26.x only; the backing accessor mixins are registered conditionally.
 */
public final class RandomSequenceApi {

    private RandomSequenceApi() {
    }

    //#if MC >= 260100
    //$$ /**
    //$$  * Reads the current 128-bit state as {@code [seedLo, seedHi]}; with the
    //$$  * world seed this reproduces the next trade refresh.
    //$$  */
    //$$ public static long[] getState(MinecraftServer server, Identifier id) {
    //$$     Xor128Accessor acc = (Xor128Accessor) ((XoroshiroRandomSourceAccessor) sequence(server, id)).hfut$generator();
    //$$     return new long[]{acc.hfut$seedLo(), acc.hfut$seedHi()};
    //$$ }
    //$$
    //$$ /**
    //$$  * Overwrites the 128-bit state; call {@link #getState} first to snapshot it.
    //$$  */
    //$$ public static void setState(MinecraftServer server, Identifier id, long seedLo, long seedHi) {
    //$$     ((XoroshiroRandomSourceAccessor) sequence(server, id)).hfut$generator(new Xoroshiro128PlusPlus(seedLo, seedHi));
    //$$ }
    //$$
    //$$ /** Advances the sequence by {@code count} rolls (official {@code consumeCount}). */
    //$$ public static void skip(MinecraftServer server, Identifier id, int count) {
    //$$     sequence(server, id).consumeCount(count);
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
