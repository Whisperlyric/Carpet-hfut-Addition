package dev.whisperlyric.carpet_hfut_addition.commands;

//#if MC >= 260100
//$$ import com.mojang.brigadier.CommandDispatcher;
//$$ import com.mojang.brigadier.arguments.IntegerArgumentType;
//$$ import com.mojang.brigadier.arguments.StringArgumentType;
//$$ import com.mojang.brigadier.context.CommandContext;
//$$ import com.mojang.brigadier.suggestion.Suggestions;
//$$ import com.mojang.brigadier.suggestion.SuggestionsBuilder;
//$$ import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
//$$ import dev.whisperlyric.carpet_hfut_addition.api.RandomSequenceApi;
//$$ import dev.whisperlyric.carpet_hfut_addition.mixins.rule.tradeSeq.Xor128Accessor;
//$$ import dev.whisperlyric.carpet_hfut_addition.mixins.rule.tradeSeq.XoroshiroRandomSourceAccessor;
//$$ import dev.whisperlyric.carpet_hfut_addition.utils.CommandUtil;
//$$ import dev.whisperlyric.carpet_hfut_addition.utils.HFUTText;
//$$ import net.minecraft.commands.CommandSourceStack;
//$$ import net.minecraft.commands.Commands;
//$$ import net.minecraft.core.Registry;
//$$ import net.minecraft.core.registries.Registries;
//$$ import net.minecraft.resources.Identifier;
//$$ import net.minecraft.resources.ResourceKey;
//$$ import net.minecraft.server.MinecraftServer;
//$$ import net.minecraft.world.item.trading.TradeSet;
//$$ import net.minecraft.world.level.levelgen.Xoroshiro128PlusPlus;
//$$ import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
//$$
//$$ import java.util.List;
//$$ import java.util.Set;
//$$ import java.util.TreeSet;
//$$ import java.util.concurrent.CompletableFuture;
//#endif

/**
 * {@code /tradeseq <profession> <level> status|set|rollback|skip <n>} - drives one
 * named random sequence. 26.1+ trade refresh draws from
 * {@code minecraft:trade_set/<profession>/level_<n>}, shared by every villager of
 * that profession and level, so nudging the sequence steers the whole batch.
 *
 * <p>The vanilla sequence is a XoroshiroRandomSource wrapping an
 * Xoroshiro128PlusPlus (two longs, no draw counter). {@code status}
 * therefore prints the raw 128-bit state - the only exact representation - and
 * {@code set}/{@code rollback} move that generator directly: {@code set n}
 * re-seeds and advances {@code n} draws (n=0 is a fresh sequence, so the next
 * draw is number n+1), {@code rollback n} applies the invertible backward
 * transition n times, and {@code skip n} burns n draws forward.
 */
public class TradeSeqCommand {

    private TradeSeqCommand() {
    }

    //#if MC >= 260100
    //$$ /** Used when the registry cannot be read, or yields no id the command can rebuild. */
    //$$ private static final List<String> FALLBACK_PROFESSIONS = List.of(
    //$$         "armorer", "butcher", "cartographer", "cleric", "farmer", "fisherman",
    //$$         "fletcher", "leatherworker", "librarian", "mason", "shepherd",
    //$$         "toolsmith", "weaponsmith");
    //$$
    //$$ public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
    //$$     var root = Commands.literal("tradeseq")
    //$$             .requires(source -> CommandUtil.canUseCommand(source, HFUTSettings.commandTradeSeq));
    //$$     root.then(Commands.argument("profession", StringArgumentType.word())
    //$$             .suggests(TradeSeqCommand::suggestProfessions)
    //$$             .then(Commands.argument("level", IntegerArgumentType.integer(1, 5))
    //$$                     .then(Commands.literal("status")
    //$$                             .executes(TradeSeqCommand::hfut$status))
    //$$                     .then(Commands.literal("set")
    //$$                             .then(Commands.argument("value", IntegerArgumentType.integer(0, 100000))
    //$$                                     .executes(TradeSeqCommand::hfut$set)))
    //$$                     .then(Commands.literal("rollback")
    //$$                             .then(Commands.argument("steps", IntegerArgumentType.integer(1, 100000))
    //$$                                     .executes(TradeSeqCommand::hfut$rollback)))
    //$$                     .then(Commands.literal("skip")
    //$$                             .then(Commands.argument("steps", IntegerArgumentType.integer(1, 100000))
    //$$                                     .executes(TradeSeqCommand::hfut$skip)))));
    //$$     dispatcher.register(root);
    //$$ }
    //$$
    //$$ private static CompletableFuture<Suggestions> suggestProfessions(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
    //$$     String remaining = builder.getRemainingLowerCase();
    //$$     for (String profession : hfut$professions(ctx.getSource())) {
    //$$         if (profession.startsWith(remaining)) {
    //$$             builder.suggest(profession);
    //$$         }
    //$$     }
    //$$     return builder.buildFuture();
    //$$ }
    //$$
    //$$ /** Professions parsed from the {@code minecraft:trade_set} registry; falls back to the static list when it cannot be read. */
    //$$ private static List<String> hfut$professions(CommandSourceStack source) {
    //$$     try {
    //$$         Registry<TradeSet> sets = source.getServer().registryAccess().lookup(Registries.TRADE_SET).orElse(null);
    //$$         if (sets == null) {
    //$$             return FALLBACK_PROFESSIONS;
    //$$         }
    //$$         Set<String> professions = new TreeSet<>();
    //$$         for (ResourceKey<TradeSet> key : sets.registryKeySet()) {
    //$$             String profession = hfut$professionOf(sets.getValue(key).randomSequence().orElse(null));
    //$$             if (profession != null) {
    //$$                 professions.add(profession);
    //$$             }
    //$$         }
    //$$         return professions.isEmpty() ? FALLBACK_PROFESSIONS : List.copyOf(professions);
    //$$     } catch (RuntimeException ignored) {
    //$$         return FALLBACK_PROFESSIONS;
    //$$     }
    //$$ }
    //$$
    //$$ /** Only the ids {@code hfut$id} can rebuild byte-for-byte are addressable, so anything else is skipped. */
    //$$ private static String hfut$professionOf(Identifier sequence) {
    //$$     if (sequence == null || !"minecraft".equals(sequence.getNamespace()) || !sequence.getPath().startsWith("trade_set/")) {
    //$$         return null;
    //$$     }
    //$$     String rest = sequence.getPath().substring("trade_set/".length());
    //$$     int slash = rest.lastIndexOf('/');
    //$$     if (slash <= 0) {
    //$$         return null;
    //$$     }
    //$$     String profession = rest.substring(0, slash);
    //$$     return rest.substring(slash + 1).startsWith("level_") && profession.indexOf('/') < 0 ? profession : null;
    //$$ }
    //$$
    //$$ private static Identifier hfut$id(CommandContext<CommandSourceStack> ctx) {
    //$$     String profession = StringArgumentType.getString(ctx, "profession");
    //$$     int level = IntegerArgumentType.getInteger(ctx, "level");
    //$$     return Identifier.fromNamespaceAndPath("minecraft", "trade_set/" + profession + "/level_" + level);
    //$$ }
    //$$
    //$$ private static long[] hfut$state(XoroshiroRandomSource rng) {
    //$$     Xor128Accessor acc = (Xor128Accessor) ((XoroshiroRandomSourceAccessor) rng).hfut$generator();
    //$$     return new long[]{acc.hfut$seedLo(), acc.hfut$seedHi()};
    //$$ }
    //$$
    //$$ private static int hfut$status(CommandContext<CommandSourceStack> ctx) {
    //$$     CommandSourceStack source = ctx.getSource();
    //$$     Identifier id = hfut$id(ctx);
    //$$     long[] state = hfut$state(RandomSequenceApi.sequence(source.getServer(), id));
    //$$     source.sendSuccess(() -> HFUTText.forViewer(source, "hfut.tradeseq.status",
    //$$             id, Long.toHexString(state[0]), Long.toHexString(state[1])), false);
    //$$     return 1;
    //$$ }
    //$$
    //$$ /** set n = re-seed to the fresh state, then advance n draws; n=0 is the start, so the next draw is number n+1. */
    //$$ private static int hfut$set(CommandContext<CommandSourceStack> ctx) {
    //$$     CommandSourceStack source = ctx.getSource();
    //$$     MinecraftServer server = source.getServer();
    //$$     Identifier id = hfut$id(ctx);
    //$$     int value = IntegerArgumentType.getInteger(ctx, "value");
    //$$     server.getRandomSequences().reset(id, server.getWorldGenSettings().options().seed());
    //$$     XoroshiroRandomSource rng = RandomSequenceApi.sequence(server, id);
    //$$     if (value > 0) {
    //$$         rng.consumeCount(value);
    //$$     }
    //$$     long[] state = hfut$state(rng);
    //$$     source.sendSuccess(() -> HFUTText.forViewer(source, "hfut.tradeseq.set",
    //$$             id, value, Long.toHexString(state[0]), Long.toHexString(state[1])), false);
    //$$     return 1;
    //$$ }
    //$$
    //$$ /** rollback n = apply the invertible Xoroshiro128++ backward transition n times. */
    //$$ private static int hfut$rollback(CommandContext<CommandSourceStack> ctx) {
    //$$     CommandSourceStack source = ctx.getSource();
    //$$     Identifier id = hfut$id(ctx);
    //$$     int steps = IntegerArgumentType.getInteger(ctx, "steps");
    //$$     Xor128Accessor acc = (Xor128Accessor) ((XoroshiroRandomSourceAccessor) RandomSequenceApi.sequence(source.getServer(), id)).hfut$generator();
    //$$     long lo = acc.hfut$seedLo();
    //$$     long hi = acc.hfut$seedHi();
    //$$     for (int i = 0; i < steps; i++) {
    //$$         long prevB = Long.rotateRight(hi, 28);
    //$$         long prevA = Long.rotateRight(lo ^ prevB ^ (prevB << 21), 49);
    //$$         lo = prevA;
    //$$         hi = prevB ^ prevA;
    //$$     }
    //$$     acc.hfut$seedLo(lo);
    //$$     acc.hfut$seedHi(hi);
    //$$     long newLo = lo;
    //$$     long newHi = hi;
    //$$     source.sendSuccess(() -> HFUTText.forViewer(source, "hfut.tradeseq.rolled_back",
    //$$             id, steps, Long.toHexString(newLo), Long.toHexString(newHi)), false);
    //$$     return 1;
    //$$ }
    //$$
    //$$ private static int hfut$skip(CommandContext<CommandSourceStack> ctx) {
    //$$     CommandSourceStack source = ctx.getSource();
    //$$     Identifier id = hfut$id(ctx);
    //$$     int steps = IntegerArgumentType.getInteger(ctx, "steps");
    //$$     RandomSequenceApi.sequence(source.getServer(), id).consumeCount(steps);
    //$$     source.sendSuccess(() -> HFUTText.forViewer(source, "hfut.tradeseq.skipped", id, steps), false);
    //$$     return 1;
    //$$ }
    //#endif
}
