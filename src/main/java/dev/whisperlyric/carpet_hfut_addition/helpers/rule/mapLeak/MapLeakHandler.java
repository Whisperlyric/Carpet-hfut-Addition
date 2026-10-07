package dev.whisperlyric.carpet_hfut_addition.helpers.rule.mapLeak;

import dev.whisperlyric.carpet_hfut_addition.utils.PlayerNames;
import dev.whisperlyric.carpet_hfut_addition.mixins.rule.mapLeak.MapItemSavedDataAccessor;
import dev.whisperlyric.carpet_hfut_addition.mixins.rule.mapLeak.SavedDataCacheAccessor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

import java.util.Map;
import java.util.Optional;

/**
 * Purges a disconnecting player from every loaded map's recipient registries
 * and removes their map decorations. The saved-data cache shape differs across
 * versions, so its values are disambiguated with instanceof.
 */
public final class MapLeakHandler {
    private MapLeakHandler() {
    }

    public static void purgePlayer(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();
        String playerName = playerName(player);
        for (ServerLevel level : server.getAllLevels()) {
            Map<?, ?> cache = ((SavedDataCacheAccessor) (Object) level.getDataStorage()).hfut$getCache();
            for (Object value : cache.values()) {
                SavedData data = unwrap(value);
                if (data instanceof MapItemSavedData mapData) {
                    purgeFromMap(mapData, player, playerName);
                }
            }
        }
    }

    private static void purgeFromMap(MapItemSavedData data, Player player, String playerName) {
        Map<Player, Object> carriedByPlayers = ((MapItemSavedDataAccessor) (Object) data).hfut$getCarriedByPlayers();
        Object holder = carriedByPlayers.get(player);
        if (holder != null) {
            carriedByPlayers.remove(player);
            ((MapItemSavedDataAccessor) (Object) data).hfut$getCarriedBy().remove(holder);
            ((MapItemSavedDataAccessor) (Object) data).hfut$callRemoveDecoration(playerName);
        }
    }

    private static SavedData unwrap(Object cacheValue) {
        if (cacheValue instanceof Optional<?> optional) {
            return optional.orElse(null) instanceof SavedData saved ? saved : null;
        }
        return cacheValue instanceof SavedData saved ? saved : null;
    }

    private static String playerName(ServerPlayer player) {
        return PlayerNames.of(player);
    }
}
