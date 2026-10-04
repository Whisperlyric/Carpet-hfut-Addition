package dev.whisperlyric.carpet_hfut_addition.helpers.rule.fakePlayerTickStage;

import carpet.helpers.EntityPlayerActionPack;
import dev.whisperlyric.carpet_hfut_addition.HFUTServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Defers selected fake players' ticks out of the entity stage: doTick to the
 * network phase and the action pack to the async task phase
 * ({@code MinecraftServer#waitUntilNextTick}), mirroring carpet-tis-addition.
 */
public class FakePlayerTicker {
    private static final FakePlayerTicker INSTANCE = new FakePlayerTicker();

    private final List<ServerPlayer> pendingEntityTicks = new ArrayList<>();
    private final Queue<ActionPackTickTask> pendingActionPackTicks = new ConcurrentLinkedQueue<>();

    public static FakePlayerTicker getInstance() {
        return INSTANCE;
    }

    public synchronized void addPlayerEntityTick(ServerPlayer player) {
        this.pendingEntityTicks.add(player);
    }

    public void addActionPackTick(ServerPlayer player, EntityPlayerActionPack actionPack) {
        // Level.getServer survives the 1.21.10 de-gettering; Entity.getServer does not
        MinecraftServer server = player.level().getServer();
        if (server != null) {
            this.pendingActionPackTicks.add(new ActionPackTickTask(player, actionPack::onUpdate));
        }
    }

    /**
     * Runs at the network phase (start of {@code ServerConnectionListener#tick}),
     * where real players' doTick happens.
     */
    public synchronized void networkPhaseTick() {
        List<ServerPlayer> players = new ArrayList<>(this.pendingEntityTicks);
        this.pendingEntityTicks.clear();
        for (ServerPlayer player : players) {
            try {
                player.doTick();
            } catch (Exception e) {
                HFUTServer.LOGGER.warn("[HFUT] Failed to entity-tick fake player {} at the player phase", player.getName(), e);
            }
        }
    }

    /**
     * Runs at the async task phase ({@code MinecraftServer#waitUntilNextTick}).
     * The real executor is not used because it may delay tasks past the next tick.
     */
    public void asyncTaskPhaseTick() {
        ActionPackTickTask task;
        while ((task = this.pendingActionPackTicks.poll()) != null) {
            try {
                task.runnable().run();
            } catch (Exception e) {
                HFUTServer.LOGGER.warn("[HFUT] Failed to tick action pack for player {}", task.player().getName(), e);
            }
        }
    }

    private record ActionPackTickTask(ServerPlayer player, Runnable runnable) {
    }
}
