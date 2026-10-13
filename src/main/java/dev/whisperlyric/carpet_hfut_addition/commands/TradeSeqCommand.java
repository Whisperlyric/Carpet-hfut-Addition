package dev.whisperlyric.carpet_hfut_addition.commands;

//#if MC >= 260100
//$$ import com.google.common.collect.Lists;
//$$ import com.mojang.brigadier.CommandDispatcher;
//$$ import com.mojang.brigadier.arguments.BoolArgumentType;
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
//$$ import net.minecraft.core.Holder;
//$$ import net.minecraft.core.HolderSet;
//$$ import net.minecraft.core.Registry;
//$$ import net.minecraft.core.registries.Registries;
//$$ import net.minecraft.resources.Identifier;
//$$ import net.minecraft.resources.ResourceKey;
//$$ import net.minecraft.server.MinecraftServer;
//$$ import net.minecraft.server.level.ServerLevel;
//$$ import net.minecraft.util.Unit;
//$$ import net.minecraft.world.entity.EntitySpawnReason;
//#if MC >= 260200
//$$ import net.minecraft.world.entity.EntityTypes;
//#else
//$$ import net.minecraft.world.entity.EntityType;
//#endif
//$$ import net.minecraft.world.entity.npc.villager.Villager;
//$$ import net.minecraft.world.item.trading.MerchantOffer;
//$$ import net.minecraft.world.item.trading.TradeSet;
//$$ import net.minecraft.world.item.trading.VillagerTrade;
//$$ import net.minecraft.world.level.levelgen.Xoroshiro128PlusPlus;
//$$ import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
//$$ import net.minecraft.world.level.storage.loot.LootContext;
//$$ import net.minecraft.world.level.storage.loot.LootParams;
//$$ import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
//$$ import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
//$$ import net.minecraft.world.phys.Vec3;
//$$
//$$ import java.util.List;
//$$ import java.util.Optional;
//$$ import java.util.Set;
//$$ import java.util.TreeSet;
//$$ import java.util.concurrent.CompletableFuture;
//$$ import java.util.concurrent.CompletionException;
//$$ import java.util.concurrent.ExecutorService;
//$$ import java.util.concurrent.Executors;
//$$ import java.util.function.Consumer;
//$$ import java.util.function.Supplier;
//#endif

/**
 * {@code /tradeseq <profession> <level> status|set|rollback|skip <n>}, plus
 * {@code /tradeseq cancel}, {@code /tradeseq confirm} and {@code /tradeseq debug <bool>},
 * drives one named random sequence. 26.1+ trade refresh draws from
 * {@code minecraft:trade_set/<profession>/level_<n>}, shared by every villager of
 * that profession and level, so nudging the sequence steers the whole batch.
 *
 * <p>Counts are whole <em>refreshes</em> (one complete trade generation), matching the
 * Python reference tool's offset: {@code set n} leaves the sequence exactly where a
 * fresh one would be after n refreshes, so the next refresh yields result n+1.
 * A refresh is replayed by walking vanilla's own path - {@code calculateNumberOfTrades}
 * then the allowDuplicates-dependent pick loop, with every {@code ctx.getRandom()} draw
 * served by the sequence under control - so the real trade table has to be read from the
 * registry to know how many draws a generation costs.
 *
 * <p>{@code status} replays from the fresh state and reports which generation the live
 * state sits on; a state caught mid-refresh reports the two neighbouring generations,
 * and beyond it reports unknown. {@code set}/{@code skip} advance by
 * replay; {@code rollback} locates the current generation, then replays to
 * {@code max(0, generation - n)} (an unknown position aborts it). Every mutation flags the
 * sequence store for saving - the writes bypass vanilla's dirty marking.
 *
 * <p>Replays run on one daemon worker ({@link #REPLAY_EXECUTOR}), so a long one never
 * stalls the tick; only one may run at a time - a second command is rejected until it
 * finishes or is stopped with {@code /tradeseq cancel}. A {@code <n>} above
 * {@link #CONFIRM_THRESHOLD} does not run at once: it is parked, and the issuing source
 * must clear it with a bare {@code /tradeseq confirm}.
 */
public class TradeSeqCommand {

    private TradeSeqCommand() {
    }

    //#if MC >= 260100
    //$$ /**
    //$$  * Replay bound for locating the live state and for set/skip/rollback. Opened up to the full
    //$$  * Brigadier/Java int range - the most a command argument can carry. Replays run on {@link
    //$$  * #REPLAY_EXECUTOR}, so a large value no longer stalls a tick, but it still occupies that single
    //$$  * worker until done; huge values are the caller's responsibility.
    //$$  */
    //$$ private static final int REFRESH_CAP = Integer.MAX_VALUE;
    //$$
    //$$ /**
    //$$  * Single daemon worker that all replays run on. One thread keeps commands serial and off the
    //$$  * server thread; the sequence state is only ever written back on the server thread (see {@link
    //$$  * #hfut$async}), so nothing else shares these scratch objects.
    //$$  */
    //$$ private static final ExecutorService REPLAY_EXECUTOR = Executors.newSingleThreadExecutor(runnable -> {
    //$$     Thread thread = new Thread(runnable, "carpet-hfut-tradeseq");
    //$$     thread.setDaemon(true);
    //$$     return thread;
    //$$ });
    //$$
    //$$ /** Backward-walk bound for spotting a state that sits in the middle of a refresh. */
    //$$ private static final long INSIDE_REFRESH_CAP = 1L << 16;
    //$$
    //$$ /** Values above this refresh count are parked until the source clears them with {@code /tradeseq confirm}. */
    //$$ private static final int CONFIRM_THRESHOLD = 100_000;
    //$$
    //$$ /** The too-large action parked for {@code /tradeseq confirm}, with the source that may clear it. */
    //$$ private static volatile Pending PENDING;
    //$$
    //$$ /** The replay in flight; a second one is rejected until it finishes or is cancelled. */
    //$$ private static volatile CompletableFuture<?> CURRENT;
    //$$
    //$$ /** The worker thread running {@code CURRENT}, for {@code /tradeseq cancel} to interrupt. */
    //$$ private static volatile Thread REPLAY_THREAD;
    //$$
    //$$ /** Cooperative-cancellation marker; stack traces disabled, it is pure control flow. */
    //$$ private static final class ReplayCancelled extends RuntimeException {
    //$$     private ReplayCancelled() {
    //$$         super(null, null, false, false);
    //$$     }
    //$$ }
    //$$
    //$$ /** Thrown from the replay loops when {@code /tradeseq cancel} interrupted the worker. */
    //$$ private static void hfut$checkCancelled() {
    //$$     if (Thread.currentThread().isInterrupted()) {
    //$$         throw new ReplayCancelled();
    //$$     }
    //$$ }
    //$$
    //$$ /**
    //$$  * When on (via {@code /tradeseq debug true}), the replay commands append the elapsed time and the
    //$$  * resulting state as extra lines; the state is otherwise omitted from their main messages. Off by
    //$$  * default. Toggled and read only on the server thread, so no synchronisation is needed.
    //$$  */
    //$$ private static boolean DEBUG = false;
    //$$
    //$$ /** Used when the registry cannot be read, or yields no id the command can rebuild. */
    //$$ private static final List<String> FALLBACK_PROFESSIONS = List.of(
    //$$         "armorer", "butcher", "cartographer", "cleric", "farmer", "fisherman",
    //$$         "fletcher", "leatherworker", "librarian", "mason", "shepherd",
    //$$         "toolsmith", "weaponsmith");
    //$$
    //$$ /** A resolved trade set plus the reusable context that a refresh replays against. */
    //$$ private record RefreshSim(TradeSet tradeSet, LootContext.Builder builder) {
    //$$ }
    //$$
    //$$ /**
    //$$  * Exact generation, or the two generations a mid-refresh state sits between
    //$$  * ({@code exact} is -1 then); both are -1 when the state is out of search range.
    //$$  */
    //$$ private record RefreshPosition(int exact, int floor, int ceil) {
    //$$     static final RefreshPosition UNKNOWN = new RefreshPosition(-1, -1, -1);
    //$$ }
    //$$
    //$$ /**
    //$$  * One replay's scratch, built on the server thread (so the throwaway villager and the registry
    //$$  * reads stay there) and then handed to {@link #REPLAY_EXECUTOR}. The context holds the scratch
    //$$  * random by reference, so re-seeding {@code rng} also moves what the context draws from.
    //$$  */
    //$$ private record Replay(TradeSet tradeSet, XoroshiroRandomSource rng, LootContext ctx,
    //$$                       Xor128Accessor state, List<Holder<VillagerTrade>> pool) {
    //$$ }
    //$$
    //$$ /** Outcome of an off-thread rollback: {@code target} is -1 when the live state was out of range. */
    //$$ private record Rollback(int amount, int target, long[] state) {
    //$$ }
    //$$
    //$$ /** A parked action: {@code initiator} is the source allowed to confirm it, {@code run} the replay it starts. */
    //$$ private record Pending(String initiator, Supplier<Integer> run) {
    //$$ }
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
    //$$                             .then(Commands.argument("value", IntegerArgumentType.integer(0, REFRESH_CAP))
    //$$                                     .executes(TradeSeqCommand::hfut$set)))
    //$$                     .then(Commands.literal("rollback")
    //$$                             .then(Commands.argument("steps", IntegerArgumentType.integer(1, REFRESH_CAP))
    //$$                                     .executes(TradeSeqCommand::hfut$rollback)))
    //$$                     .then(Commands.literal("skip")
    //$$                             .then(Commands.argument("steps", IntegerArgumentType.integer(1, REFRESH_CAP))
    //$$                                     .executes(TradeSeqCommand::hfut$skip)))));
    //$$     root.then(Commands.literal("confirm")
    //$$             .executes(TradeSeqCommand::hfut$confirm));
    //$$     root.then(Commands.literal("cancel")
    //$$             .executes(TradeSeqCommand::hfut$cancel));
    //$$     root.then(Commands.literal("debug")
    //$$             .then(Commands.argument("value", BoolArgumentType.bool())
    //$$                     .executes(TradeSeqCommand::hfut$debug)));
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
    //$$ /** {@code /tradeseq debug <true|false>} - toggles the extra timing/state lines on the replay commands. */
    //$$ private static int hfut$debug(CommandContext<CommandSourceStack> ctx) {
    //$$     CommandSourceStack source = ctx.getSource();
    //$$     DEBUG = BoolArgumentType.getBool(ctx, "value");
    //$$     source.sendSuccess(() -> HFUTText.forViewer(source,
    //$$             DEBUG ? "hfut.tradeseq.debug.on" : "hfut.tradeseq.debug.off"), false);
    //$$     return 1;
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
    //$$ /** The random sequence id - {@code minecraft:trade_set/<profession>/level_<n>}, what the trade set seeds. */
    //$$ private static Identifier hfut$id(CommandContext<CommandSourceStack> ctx) {
    //$$     String profession = StringArgumentType.getString(ctx, "profession");
    //$$     int level = IntegerArgumentType.getInteger(ctx, "level");
    //$$     return Identifier.fromNamespaceAndPath("minecraft", "trade_set/" + profession + "/level_" + level);
    //$$ }
    //$$
    //$$ /**
    //$$  * The datapack registry entry id of the trade set - {@code minecraft:<profession>/level_<n>}, i.e. the file
    //$$  * path under {@code data/minecraft/trade_set/} without that registry folder and without the {@code .json}
    //$$  * suffix. This is <em>not</em> the random sequence id; the two differ by the {@code trade_set/} segment.
    //$$  */
    //$$ private static Identifier hfut$tradeSetId(CommandContext<CommandSourceStack> ctx) {
    //$$     String profession = StringArgumentType.getString(ctx, "profession");
    //$$     int level = IntegerArgumentType.getInteger(ctx, "level");
    //$$     return Identifier.fromNamespaceAndPath("minecraft", profession + "/level_" + level);
    //$$ }
    //$$
    //$$ /**
    //$$  * A {@code VILLAGER_TRADE} context needs an entity; a throwaway villager is built at
    //$$  * the command position and never added to the level.
    //$$  */
    //$$ private static LootContext.Builder hfut$context(ServerLevel level, Vec3 origin) {
    //#if MC >= 260200
    //$$     Villager villager = EntityTypes.VILLAGER.create(level, EntitySpawnReason.COMMAND);
    //#else
    //$$     Villager villager = EntityType.VILLAGER.create(level, EntitySpawnReason.COMMAND);
    //#endif
    //$$     if (villager == null) {
    //$$         return null;
    //$$     }
    //$$     villager.setPos(origin.x, origin.y, origin.z);
    //$$     LootParams params = new LootParams.Builder(level)
    //$$             .withParameter(LootContextParams.ORIGIN, origin)
    //$$             .withParameter(LootContextParams.THIS_ENTITY, villager)
    //$$             .withParameter(LootContextParams.ADDITIONAL_COST_COMPONENT_ALLOWED, Unit.INSTANCE)
    //$$             .create(LootContextParamSets.VILLAGER_TRADE);
    //$$     return new LootContext.Builder(params);
    //$$ }
    //$$
    //$$ /** Resolves the trade set (by its registry entry id) and the replay context, or null when either is unavailable. */
    //$$ private static RefreshSim hfut$sim(CommandSourceStack source, Identifier tradeSetId) {
    //$$     ServerLevel level = source.getLevel();
    //$$     Registry<TradeSet> registry = level.registryAccess().lookup(Registries.TRADE_SET).orElse(null);
    //$$     TradeSet tradeSet = registry != null ? registry.getOptional(tradeSetId).orElse(null) : null;
    //$$     if (tradeSet == null) {
    //$$         return null;
    //$$     }
    //$$     LootContext.Builder builder = hfut$context(level, source.getPosition());
    //$$     return builder != null ? new RefreshSim(tradeSet, builder) : null;
    //$$ }
    //$$
    //$$ /**
    //$$  * Replays one refresh - {@code AbstractVillager.addOffersFromTradeSet} - consuming draws
    //$$  * from {@code ctx}'s random. The offers are discarded; only how many draws are eaten
    //$$  * matters. The count is bumped only for a non-null offer, exactly as the vanilla loops do.
    //$$  *
    //$$  * <p>{@code ctx} and {@code pool} are caller-owned scratch reused across a whole replay: the
    //$$  * context merely holds the random source by reference, so it stays live as draws are consumed,
    //$$  * and the pick pool is refilled rather than reallocated. Skipping the per-refresh
    //$$  * {@code LootContext} and {@code Lists.newArrayList} allocation is where most of the garbage
    //$$  * used to come from.
    //$$  */
    //$$ private static void hfut$refresh(TradeSet tradeSet, LootContext ctx, List<Holder<VillagerTrade>> pool) {
    //$$     int amount = tradeSet.calculateNumberOfTrades(ctx);
    //#if MC >= 260300
    //$$     HolderSet<VillagerTrade> trades = tradeSet.trades();
    //#else
    //$$     HolderSet<VillagerTrade> trades = tradeSet.getTrades();
    //#endif
    //$$     int found = 0;
    //$$     if (tradeSet.allowDuplicates()) {
    //$$         while (found < amount) {
    //$$             hfut$checkCancelled(); // a datapack amount can be huge; never spin past a cancel
    //$$             Optional<Holder<VillagerTrade>> holder = trades.getRandomElement(ctx.getRandom());
    //$$             if (holder.isEmpty()) {
    //$$                 break;
    //$$             }
    //$$             MerchantOffer offer = holder.get().value().getOffer(ctx);
    //$$             if (offer != null) {
    //$$                 found++;
    //$$             }
    //$$         }
    //$$     } else {
    //$$         pool.clear();
    //$$         for (Holder<VillagerTrade> trade : trades) {
    //$$             pool.add(trade);
    //$$         }
    //$$         while (found < amount && !pool.isEmpty()) {
    //$$             hfut$checkCancelled();
    //$$             Holder<VillagerTrade> holder = pool.remove(ctx.getRandom().nextInt(pool.size()));
    //$$             if (holder.value().getOffer(ctx) != null) {
    //$$                 found++;
    //$$             }
    //$$         }
    //$$     }
    //$$ }
    //$$
    //$$ /** Builds the scratch for a replay seeded at {@code (seedLo, seedHi)}; server thread only. */
    //$$ private static Replay hfut$scratch(RefreshSim sim, long seedLo, long seedHi) {
    //$$     XoroshiroRandomSource rng = new XoroshiroRandomSource(seedLo, seedHi);
    //$$     LootContext ctx = sim.builder().withOptionalRandomSource(rng).create(Optional.empty());
    //$$     Xor128Accessor state = (Xor128Accessor) ((XoroshiroRandomSourceAccessor) rng).hfut$generator();
    //$$     return new Replay(sim.tradeSet(), rng, ctx, state, Lists.newArrayList());
    //$$ }
    //$$
    //$$ /** Replays {@code count} refreshes on the scratch. Pure arithmetic, safe off the server thread. */
    //$$ private static void hfut$replay(Replay replay, int count) {
    //$$     for (int i = 0; i < count; i++) {
    //$$         if ((i & 0xFF) == 0) {
    //$$             hfut$checkCancelled();
    //$$         }
    //$$         hfut$refresh(replay.tradeSet(), replay.ctx(), replay.pool());
    //$$     }
    //$$ }
    //$$
    //$$ /** Rewinds the scratch to {@code (seedLo, seedHi)}; its context keeps drawing from the same wrapper. */
    //$$ private static void hfut$reseed(Replay replay, long seedLo, long seedHi) {
    //$$     ((XoroshiroRandomSourceAccessor) replay.rng()).hfut$generator(new Xoroshiro128PlusPlus(seedLo, seedHi));
    //$$ }
    //$$
    //$$ private static long[] hfut$seed(Replay replay) {
    //$$     return new long[]{replay.state().hfut$seedLo(), replay.state().hfut$seedHi()};
    //$$ }
    //$$
    //$$ /** True when the live sequence moved since {@code snapshot} - a villager traded mid-computation. */
    //$$ private static boolean hfut$changed(MinecraftServer server, Identifier id, long[] snapshot) {
    //$$     long[] live = RandomSequenceApi.getState(server, id);
    //$$     return live[0] != snapshot[0] || live[1] != snapshot[1];
    //$$ }
    //$$
    //$$ /**
    //$$  * Rejects while another replay is still running (cancel it first via
    //$$  * {@code /tradeseq cancel}); otherwise runs {@code compute} on the replay
    //$$  * worker and {@code done} back on the server thread. The task is registered
    //$$  * in {@code CURRENT} for the busy check and {@code cancel}, and deregistered
    //$$  * on completion so neither it nor its scratch outlives its result.
    //$$  */
    //$$ private static <T> void hfut$async(CommandSourceStack source, Supplier<T> compute, Consumer<T> done) {
    //$$     MinecraftServer server = source.getServer();
    //$$     if (CURRENT != null && !CURRENT.isDone()) {
    //$$         source.sendFailure(HFUTText.forViewer(source, "hfut.tradeseq.busy"));
    //$$         return;
    //$$     }
    //$$     CompletableFuture<T> future = CompletableFuture.supplyAsync(() -> {
    //$$         REPLAY_THREAD = Thread.currentThread();
    //$$         try {
    //$$             return compute.get();
    //$$         } finally {
    //$$             Thread.interrupted(); // a cancelled replay leaves the flag set; clear it for the next task
    //$$         }
    //$$     }, REPLAY_EXECUTOR);
    //$$     CURRENT = future;
    //$$     future.whenComplete((value, error) -> server.execute(() -> {
    //$$         if (CURRENT == future) {
    //$$             CURRENT = null;
    //$$             REPLAY_THREAD = null;
    //$$         }
    //$$         // supplyAsync wraps a thrown exception in CompletionException, so unwrap before matching
    //$$         Throwable cause = error instanceof CompletionException && error.getCause() != null
    //$$                 ? error.getCause() : error;
    //$$         if (cause instanceof ReplayCancelled) {
    //$$             source.sendSuccess(() -> HFUTText.forViewer(source, "hfut.tradeseq.cancelled"), false);
    //$$         } else if (error != null) {
    //$$             source.sendFailure(HFUTText.forViewer(source, "hfut.tradeseq.failed"));
    //$$         } else {
    //$$             done.accept(value);
    //$$         }
    //$$     }));
    //$$ }
    //$$
    //$$ /** Runs {@code run} at once; above {@link #CONFIRM_THRESHOLD} parks it for {@code /tradeseq confirm} instead. */
    //$$ private static int hfut$guarded(CommandSourceStack source, int value, Supplier<Integer> run) {
    //$$     if (value <= CONFIRM_THRESHOLD) {
    //$$         return run.get();
    //$$     }
    //$$     PENDING = new Pending(source.getTextName(), run);
    //$$     source.sendFailure(HFUTText.forViewer(source, "hfut.tradeseq.confirm", value, CONFIRM_THRESHOLD));
    //$$     return 0;
    //$$ }
    //$$
    //$$ /**
    //$$  * Drops the parked action and the task bookkeeping, and stops a still-running
    //$$  * replay; called on server close so no source or server is retained across a
    //$$  * restart (or a singleplayer world switch).
    //$$  */
    //$$ public static void clearState() {
    //$$     PENDING = null;
    //$$     CURRENT = null;
    //$$     Thread worker = REPLAY_THREAD;
    //$$     REPLAY_THREAD = null;
    //$$     if (worker != null) {
    //$$         worker.interrupt();
    //$$     }
    //$$ }
    //$$
    //$$ /** {@code /tradeseq confirm} - starts the action parked by the same source, if any. */
    //$$ private static int hfut$confirm(CommandContext<CommandSourceStack> ctx) {
    //$$     CommandSourceStack source = ctx.getSource();
    //$$     Pending pending = PENDING;
    //$$     if (pending == null || !pending.initiator().equals(source.getTextName())) {
    //$$         source.sendFailure(HFUTText.forViewer(source, "hfut.tradeseq.confirm.nothing"));
    //$$         return 0;
    //$$     }
    //$$     PENDING = null;
    //$$     return pending.run().get();
    //$$ }
    //$$
    //$$ /** {@code /tradeseq cancel} - cooperatively stops the running replay by interrupting the worker. */
    //$$ private static int hfut$cancel(CommandContext<CommandSourceStack> ctx) {
    //$$     CommandSourceStack source = ctx.getSource();
    //$$     CompletableFuture<?> future = CURRENT;
    //$$     Thread worker = REPLAY_THREAD;
    //$$     if (future == null || future.isDone() || worker == null) {
    //$$         source.sendSuccess(() -> HFUTText.forViewer(source, "hfut.tradeseq.cancel.nothing"), false);
    //$$         return 1;
    //$$     }
    //$$     worker.interrupt();
    //$$     source.sendSuccess(() -> HFUTText.forViewer(source, "hfut.tradeseq.cancel.requested"), false);
    //$$     return 1;
    //$$ }
    //$$
    //$$ /**
    //$$  * With {@link #DEBUG} on, appends the elapsed time and the resulting 128-bit state, each on its own
    //$$  * line; a no-op otherwise. Called on the server thread, after the command's main feedback.
    //$$  */
    //$$ private static void hfut$trace(CommandSourceStack source, long startNanos, long[] state) {
    //$$     if (!DEBUG) {
    //$$         return;
    //$$     }
    //$$     long millis = (System.nanoTime() - startNanos) / 1_000_000L;
    //$$     source.sendSuccess(() -> HFUTText.forViewer(source, "hfut.tradeseq.debug.time", millis), false);
    //$$     source.sendSuccess(() -> HFUTText.forViewer(source, "hfut.tradeseq.debug.state",
    //$$             Long.toHexString(state[0]), Long.toHexString(state[1])), false);
    //$$ }
    //$$
    //$$ /**
    //$$  * True when {@code live} sits strictly between the previous and the current refresh boundary -
    //$$  * i.e. partway through a generation. Steps the Xoroshiro128++ state backwards one transition at
    //$$  * a time (the inverse verified against the forward step) until it reaches either boundary.
    //$$  * Scalars, not arrays, because this runs once per draw of every refresh.
    //$$  */
    //$$ private static boolean hfut$insideRefresh(long liveLo, long liveHi, long afterLo, long afterHi, long beforeLo, long beforeHi) {
    //$$     long lo = afterLo;
    //$$     long hi = afterHi;
    //$$     for (long k = 0; k < INSIDE_REFRESH_CAP; k++) {
    //$$         if (lo == beforeLo && hi == beforeHi) {
    //$$             return false;
    //$$         }
    //$$         if (lo == liveLo && hi == liveHi) {
    //$$             return true;
    //$$         }
    //$$         long prevB = Long.rotateRight(hi, 28);
    //$$         long prevA = Long.rotateRight(lo ^ prevB ^ (prevB << 21), 49);
    //$$         lo = prevA;
    //$$         hi = prevB ^ prevA;
    //$$     }
    //$$     return false;
    //$$ }
    //$$
    //$$ /**
    //$$  * Replays the fresh-seeded {@code replay} to place {@code live}; UNKNOWN past {@link #REFRESH_CAP}
    //$$  * refreshes. Off-thread: touches only the scratch and the immutable trade set.
    //$$  */
    //$$ private static RefreshPosition hfut$locate(Replay replay, long[] fresh, long[] live) {
    //$$     if (live[0] == fresh[0] && live[1] == fresh[1]) {
    //$$         return new RefreshPosition(0, 0, 0);
    //$$     }
    //$$     Xor128Accessor state = replay.state();
    //$$     long beforeLo = fresh[0];
    //$$     long beforeHi = fresh[1];
    //$$     for (long r = 1; r <= REFRESH_CAP; r++) {
    //$$         if ((r & 0xFF) == 0) {
    //$$             hfut$checkCancelled();
    //$$         }
    //$$         hfut$refresh(replay.tradeSet(), replay.ctx(), replay.pool());
    //$$         long afterLo = state.hfut$seedLo();
    //$$         long afterHi = state.hfut$seedHi();
    //$$         if (live[0] == afterLo && live[1] == afterHi) {
    //$$             return new RefreshPosition((int) r, (int) r, (int) r);
    //$$         }
    //$$         if (hfut$insideRefresh(live[0], live[1], afterLo, afterHi, beforeLo, beforeHi)) {
    //$$             return new RefreshPosition(-1, (int) (r - 1), (int) r);
    //$$         }
    //$$         beforeLo = afterLo;
    //$$         beforeHi = afterHi;
    //$$     }
    //$$     return RefreshPosition.UNKNOWN;
    //$$ }
    //$$
    //$$ private static int hfut$status(CommandContext<CommandSourceStack> ctx) {
    //$$     CommandSourceStack source = ctx.getSource();
    //$$     MinecraftServer server = source.getServer();
    //$$     Identifier id = hfut$id(ctx);
    //$$     Identifier tradeSetId = hfut$tradeSetId(ctx);
    //$$     RefreshSim sim = hfut$sim(source, tradeSetId);
    //$$     if (sim == null) {
    //$$         source.sendFailure(HFUTText.forViewer(source, "hfut.tradeseq.no_trade_set", tradeSetId));
    //$$         return 0;
    //$$     }
    //$$     long[] live = RandomSequenceApi.getState(server, id);
    //$$     long[] fresh = RandomSequenceApi.freshState(server, id);
    //$$     Replay replay = hfut$scratch(sim, fresh[0], fresh[1]);
    //$$     long start = System.nanoTime();
    //$$     hfut$async(source, () -> hfut$locate(replay, fresh, live), position -> {
    //$$         hfut$report(source, id, position);
    //$$         hfut$trace(source, start, live);
    //$$     });
    //$$     return 1;
    //$$ }
    //$$
    //$$ /** Reports a located position on the server thread. */
    //$$ private static void hfut$report(CommandSourceStack source, Identifier id, RefreshPosition position) {
    //$$     if (position.exact() >= 0) {
    //$$         int generation = position.exact();
    //$$         source.sendSuccess(() -> HFUTText.forViewer(source, "hfut.tradeseq.status", id, generation), false);
    //$$     } else if (position.floor() >= 0) {
    //$$         int floor = position.floor();
    //$$         int ceil = position.ceil();
    //$$         source.sendSuccess(() -> HFUTText.forViewer(source, "hfut.tradeseq.status.between", id, floor, ceil), false);
    //$$     } else {
    //$$         source.sendSuccess(() -> HFUTText.forViewer(source, "hfut.tradeseq.status.unknown", id, REFRESH_CAP), false);
    //$$     }
    //$$ }
    //$$
    //$$ /** {@code set n} entry: parks the replay for confirmation when n is above the threshold. */
    //$$ private static int hfut$set(CommandContext<CommandSourceStack> ctx) {
    //$$     CommandSourceStack source = ctx.getSource();
    //$$     Identifier id = hfut$id(ctx);
    //$$     int value = IntegerArgumentType.getInteger(ctx, "value");
    //$$     Identifier tradeSetId = hfut$tradeSetId(ctx);
    //$$     return hfut$guarded(source, value, () -> hfut$runSet(source, id, tradeSetId, value));
    //$$ }
    //$$
    //$$ /** set n = re-seed, then replay n refreshes, so the next refresh is result n+1. */
    //$$ private static int hfut$runSet(CommandSourceStack source, Identifier id, Identifier tradeSetId, int value) {
    //$$     MinecraftServer server = source.getServer();
    //$$     RefreshSim sim = hfut$sim(source, tradeSetId);
    //$$     if (sim == null) {
    //$$         source.sendFailure(HFUTText.forViewer(source, "hfut.tradeseq.no_trade_set", tradeSetId));
    //$$         return 0;
    //$$     }
    //$$     long[] fresh = RandomSequenceApi.freshState(server, id);
    //$$     Replay replay = hfut$scratch(sim, fresh[0], fresh[1]);
    //$$     long start = System.nanoTime();
    //$$     hfut$async(source, () -> {
    //$$         hfut$replay(replay, value);
    //$$         return hfut$seed(replay);
    //$$     }, state -> {
    //$$         RandomSequenceApi.setState(server, id, state[0], state[1]);
    //$$         source.sendSuccess(() -> HFUTText.forViewer(source, "hfut.tradeseq.set", id, value), false);
    //$$         hfut$trace(source, start, state);
    //$$     });
    //$$     return 1;
    //$$ }
    //$$
    //$$ /** {@code rollback n} entry: parks the replay for confirmation when n is above the threshold. */
    //$$ private static int hfut$rollback(CommandContext<CommandSourceStack> ctx) {
    //$$     CommandSourceStack source = ctx.getSource();
    //$$     Identifier id = hfut$id(ctx);
    //$$     int steps = IntegerArgumentType.getInteger(ctx, "steps");
    //$$     Identifier tradeSetId = hfut$tradeSetId(ctx);
    //$$     return hfut$guarded(source, steps, () -> hfut$runRollback(source, id, tradeSetId, steps));
    //$$ }
    //$$
    //$$ /** rollback n = locate the current generation, then replay to max(0, generation - n). */
    //$$ private static int hfut$runRollback(CommandSourceStack source, Identifier id, Identifier tradeSetId, int steps) {
    //$$     MinecraftServer server = source.getServer();
    //$$     RefreshSim sim = hfut$sim(source, tradeSetId);
    //$$     if (sim == null) {
    //$$         source.sendFailure(HFUTText.forViewer(source, "hfut.tradeseq.no_trade_set", tradeSetId));
    //$$         return 0;
    //$$     }
    //$$     long[] live = RandomSequenceApi.getState(server, id);
    //$$     long[] fresh = RandomSequenceApi.freshState(server, id);
    //$$     Replay replay = hfut$scratch(sim, fresh[0], fresh[1]);
    //$$     long start = System.nanoTime();
    //$$     hfut$async(source, () -> {
    //$$         RefreshPosition position = hfut$locate(replay, fresh, live);
    //$$         if (position.floor() < 0) {
    //$$             return new Rollback(-1, -1, null);
    //$$         }
    //$$         int target = Math.max(0, position.floor() - steps);
    //$$         int amount = position.floor() - target;
    //$$         hfut$reseed(replay, fresh[0], fresh[1]);
    //$$         hfut$replay(replay, target);
    //$$         return new Rollback(amount, target, hfut$seed(replay));
    //$$     }, rollback -> {
    //$$         if (rollback.target() < 0) {
    //$$             source.sendFailure(HFUTText.forViewer(source, "hfut.tradeseq.rollback_unknown", id, REFRESH_CAP));
    //$$             hfut$trace(source, start, live);
    //$$             return;
    //$$         }
    //$$         if (hfut$changed(server, id, live)) {
    //$$             source.sendFailure(HFUTText.forViewer(source, "hfut.tradeseq.concurrent", id));
    //$$             return;
    //$$         }
    //$$         long[] state = rollback.state();
    //$$         RandomSequenceApi.setState(server, id, state[0], state[1]);
    //$$         source.sendSuccess(() -> HFUTText.forViewer(source, "hfut.tradeseq.rolled_back",
    //$$                 id, rollback.amount(), rollback.target()), false);
    //$$         hfut$trace(source, start, state);
    //$$     });
    //$$     return 1;
    //$$ }
    //$$
    //$$ /** {@code skip n} entry: parks the replay for confirmation when n is above the threshold. */
    //$$ private static int hfut$skip(CommandContext<CommandSourceStack> ctx) {
    //$$     CommandSourceStack source = ctx.getSource();
    //$$     Identifier id = hfut$id(ctx);
    //$$     int steps = IntegerArgumentType.getInteger(ctx, "steps");
    //$$     Identifier tradeSetId = hfut$tradeSetId(ctx);
    //$$     return hfut$guarded(source, steps, () -> hfut$runSkip(source, id, tradeSetId, steps));
    //$$ }
    //$$
    //$$ /** skip n = replay n refreshes on the live sequence, jumping past unwanted results. */
    //$$ private static int hfut$runSkip(CommandSourceStack source, Identifier id, Identifier tradeSetId, int steps) {
    //$$     MinecraftServer server = source.getServer();
    //$$     RefreshSim sim = hfut$sim(source, tradeSetId);
    //$$     if (sim == null) {
    //$$         source.sendFailure(HFUTText.forViewer(source, "hfut.tradeseq.no_trade_set", tradeSetId));
    //$$         return 0;
    //$$     }
    //$$     long[] live = RandomSequenceApi.getState(server, id);
    //$$     Replay replay = hfut$scratch(sim, live[0], live[1]);
    //$$     long start = System.nanoTime();
    //$$     hfut$async(source, () -> {
    //$$         hfut$replay(replay, steps);
    //$$         return hfut$seed(replay);
    //$$     }, state -> {
    //$$         if (hfut$changed(server, id, live)) {
    //$$             source.sendFailure(HFUTText.forViewer(source, "hfut.tradeseq.concurrent", id));
    //$$             return;
    //$$         }
    //$$         RandomSequenceApi.setState(server, id, state[0], state[1]);
    //$$         source.sendSuccess(() -> HFUTText.forViewer(source, "hfut.tradeseq.skipped", id, steps), false);
    //$$         hfut$trace(source, start, state);
    //$$     });
    //$$     return 1;
    //$$ }
    //#endif
}
