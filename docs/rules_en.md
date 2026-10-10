# Rules

**Tip: use `Ctrl+F` to find a rule**

## commandPlayerTickingStage

Who may use `/player <name> tickingStage` to adjust a fake player's tick stage.

- Type: `string`
- Default value: `ops`
- Suggested options: `false`, `true`, `ops`, `0`, `1`, `2`, `3`, `4`
- Categories: `HFUT`, `COMMAND`, `FEATURE`

## commandSimpleLazyChunk `🐛Beta`

Controls who may use `/hfut lazychunk`, which pins chunks at lazy (weakly loaded) strength (block ticks only, entities frozen) even with no player nearby, and holds them there against promotion to fully loaded (see [commands](commands_en.md)). Marks live in memory only and are lost on restart.

- Type: `string`
- Default value: `ops`
- Suggested options: `false`, `true`, `ops`, `0`, `1`, `2`, `3`, `4`
- Categories: `HFUT`, `COMMAND`, `FEATURE`

## commandFakePlayerOpenStorage `Requires GCA`

Controls who may use `/player <name> open inventory|enderchest` to open a fake player's inventory or ender chest for editing while it is offline (see [commands](commands_en.md)). Requires Carpet GugleCarpetAddition; without it the rule does not exist.

- Type: `string`
- Default value: `ops`
- Suggested options: `false`, `true`, `ops`, `0`, `1`, `2`, `3`, `4`
- Categories: `HFUT`, `COMMAND`, `FEATURE`

## commandTradeSeq `🐛Beta` `MC>=26.1`

Controls who may use `/tradeseq`, which reads and moves the named random sequence a villager's trade refresh draws from (see [commands](commands_en.md)).

- Type: `string`
- Default value: `ops`
- Suggested options: `false`, `true`, `ops`, `0`, `1`, `2`, `3`, `4`
- Categories: `HFUT`, `COMMAND`, `FEATURE`

## tripwireIgnoreDepartures `🐛Beta` `MC>=1.21.9`

Fixes, in a fairly aggressive way, the tripwire firing its rising edge multiple times as entities pass through it (MC-305475).

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `HFUT`, `BUGFIX`

> On 26.2 and above, `tripwireRestore=true` is a prerequisite.

## tripwireRestore `MC>=26.2`

Rolls back the tripwire behaviour change on 26.2 and above.

- Type: `string`
- Default value: `false`
- Suggested options: `false`, `true`, `26.3`
- Categories: `HFUT`, `BUGFIX`

- `false`: vanilla behaviour of the current version
- `true`: roll back to the pre-26.2 tripwire behaviour
- `26.3`: on 26.2 only, changes the tripwire's 0t scheduled tick to 1t

## fakePlayerTicksLikeRealPlayer

Makes fake players tick like real players: their tick moves to the network phase and their action pack to the async task phase.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `HFUT`, `EXPERIMENTAL`

> Can be set for a single fake player: `/player <name> tickingStage` (global / invert / origin / likeReal), or the button in GCA's fake player menu; it lasts for the current server run only.

## ghostEnderPearlFix `MC>=1.21.2`

Fixes several main symptoms of the ghost ender pearl bug (MC-306936).

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `HFUT`, `BUGFIX`

> Hidden when carpet-igny-addition is installed; its same-name rule takes over.

## ghostEnderPearlTrace `🐛Beta` `MC>=1.21.2`

When on, records every pearl teleport and its origin and counts teleports per pearl UUID; inspect or purge with `/pearltrace` (see [commands](commands_en.md)). This rule only decides **whether to record**; who may use the command is governed by `commandPearlTrace` / `commandPearlTracePurge` below.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `HFUT`, `BUGFIX`

> A pearl can only teleport once, so 2+ teleports of the same UUID is basically proven ghost replay.
> Works standalone and stacks on vanilla, carpet-igny-addition or ghostEnderPearlFix alike.

## commandPearlTrace

Who may use the read-only `/pearltrace list` and `show` to inspect ghost pearl leftovers.

- Type: `string`
- Default value: `ops`
- Suggested options: `false`, `true`, `ops`, `0`, `1`, `2`, `3`, `4`
- Categories: `HFUT`, `COMMAND`, `FEATURE`

> Recording must be on first (`ghostEnderPearlTrace=true`) for there to be anything to inspect; this rule only governs command access.
> `false` here disables the command, not the recording.

## commandPearlTracePurge

Who may use `/pearltrace purge` to remove ghost pearls from online players.

- Type: `string`
- Default value: `ops`
- Suggested options: `false`, `true`, `ops`, `0`, `1`, `2`, `3`, `4`
- Categories: `HFUT`, `COMMAND`, `FEATURE`

> Purging rewrites player data, so it gets its own, tighter permission.
> Recording must be on first (`ghostEnderPearlTrace=true`) for there to be anything to purge; `false` here disables the command, not the recording.

## shulkerBulletPortalCleanup `MC>=1.21.2`

Discards a shulker bullet when it crosses a portal into another dimension.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `HFUT`, `FEATURE`

> A bullet's guidance target resolves across dimensions, so after crossing it flies towards its pre-crossing coordinates.
>
> Clearing it before the teleport also spares the destination-side chunk loading.

## shulkerBulletWeakChunkCleanup

Trigger-style cleanup of shulker bullets sitting in weakly loaded chunks.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `HFUT`, `FEATURE`

## witherSkullCleanup

Trigger-style cleanup of wither skulls (including charged/blue) sitting in weakly loaded chunks.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `HFUT`, `FEATURE`

## minecartAcceleration `🐛Beta` `MC>=26.3`

Reintroduces the 1.21.5-26.2 minecart acceleration: bots can chain-mount carts and get ticked multiple times per tick.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `HFUT`, `EXPERIMENTAL`, `FEATURE`

> 26.3 fixed the minecart interaction return value, giving bots a 4t use cooldown on mounting; when on, a successful mount returns PASS again, matching pre-26.2 behaviour.

## attributeModifierRemovalDelay `MC>=26.3`

Reintroduces MC-311022: when swapping equipment quickly, attribute modifiers are cleared a tick late.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `HFUT`, `EXPERIMENTAL`, `FEATURE`

## mapRegistryLeakFix

Vanilla clears a map's player records, but map optimizations such as Lithium rewrite that path and miss an edge case, so records pile up and leak.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `HFUT`, `REMOTE_BUGFIX`

> On disconnect the player is removed from every loaded map's list; fake players are never added.
>
> Off by default; enable it when needed. Cheap: one pass over the loaded maps per disconnect.

## reloginAvatarLeakFix `1.21.2~1.21.11`

Fixes the relogin leak on Carpet-Org-Addition's 1.21.x line: the old fake player being logged out by relogin is never properly disconnected and lingers in memory.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `HFUT`, `REMOTE_BUGFIX`

> The logout task skips fake players already marked removed, so the old player's disconnect never runs and it lingers. Fix: force the disconnect through while skipping the duplicate save of an already-removed fake player.
>
> Requires carpet-org-addition; only 1.21.2 - 1.21.11 is affected (newer ORG fixed itself).

## miningFatigueDigSpeedFix `MC<=26.2`

Fixes the mining fatigue dig-speed table: 0.027 was typed as 0.0027 and 0.0081 as 8.1E-4, so dig speed under fatigue III+ is 10x slower than intended.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `HFUT`, `BUGFIX`

> 26.3 fixed this upstream with 0.3^(amplifier+1); this rule only exists on 26.2 and below and keeps 26.2-'s behaviour of capping above-IV dig speed at IV.

## enderDragonVerticalVelocityFix

Fixes the ender dragon's vertical velocity when flying to a target node (MC-272431): since 19w08b (1.14) the vertical acceleration scale is 0.01 instead of 0.1, so the dragon can barely climb or dive and circles its target nodes.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `HFUT`, `BUGFIX`

> Restores the 1.13-and-earlier ender dragon flight behaviour, and also mitigates MC-271336, MC-271337 and MC-197201.

## villagerInstantLevelUp `MC<=26.2`

Port of the 26.3 villager trading: a qualifying trade levels the villager up and unlocks the next tier instantly, without closing and reopening the screen.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `HFUT`, `FEATURE`

> Up to 26.2 vanilla defers the level-up by 40 ticks and requires the villager not to be trading; 26.3 removes that restriction.
>
> Also ports computing and showing the new tier's trade discount immediately for the trading player.

## villagerLivePriceSync `MC<=26.2`

Port of the 26.3 instant villager price recompute: while the trade screen is open, restocks, demand catch-up, gossip spread and player reputation all reprice and correct the offers instantly.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `HFUT`, `FEATURE`

## babyMobAvoidGoldenDandelion `MC>=26.1`

Babies whose growth golden dandelion suppresses steer away from it (incl. potted, or held by a player), piglin-and-soul-fire style.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `HFUT`, `FEATURE`

> Armadillo, axolotl, bee, camel, cat, chicken, cow, dolphin, donkey, fox, glowing squid, goat, small ghast (happy ghast baby), hoglin, horse, llama, mooshroom, mule, nautilus, ocelot, panda, pig, polar bear, rabbit, sheep, sniffer, squid, strider, sulfur cube, tadpole, trader llama, turtle, wolf.
>
> Mobs outside the list (villager children included) and adults are unaffected.

## babyMobAvoidGoldenDandelionIgnoreAgeLocked `MC>=26.1`

Babies whose growth is age-locked by a golden dandelion no longer steer away from golden dandelions.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `HFUT`, `FEATURE`

> Requires `babyMobAvoidGoldenDandelion` to be on first.

## bonemealGrowMelons

Bone meal on a mature (age 7) melon/pumpkin stem grows the fruit block.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `HFUT`, `FEATURE`

> Vanilla rejects bone meal on mature stems outright, leaving the fruit to random ticks; when on, a mature stem becomes a valid target and fruit placement fully reuses vanilla random-tick logic.

## mapPlayerIconRotation `MC<=26.2`

Port of the 26.3 map change: a player outside the map bounds gets their edge-clamped marker rotated by their facing.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `HFUT`, `FEATURE`

> Player markers in the map always show facing. Up to 26.2 an off-map player's marker shows no direction; from 26.3 it rotates by facing too.
>
> The rotation byte travels in the map data packet, so vanilla clients render it without this mod installed.

## creativeNoEntityCollision

Creative players no longer take part in entity collision.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `HFUT`, `FEATURE`

> It applies while not flying too, but non-collision interactions such as projectile hits and pressure plates are unaffected.

## windChargeNoRedstoneActivation

Wind charge explosions no longer trigger buttons, levers, doors, trapdoors and fence gates.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `HFUT`, `FEATURE`

## windChargeNoBeehiveAnger `MC>=1.21.2`

Wind charge explosions no longer anger nearby bees through beehives.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `HFUT`, `FEATURE`

## windChargeNoNonLivingKnockback

Wind charge explosions no longer knock back non-living entities.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `HFUT`, `FEATURE`

## soulSpeedNoBootDamage

Soul Speed no longer consumes boot durability when in use.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `HFUT`, `FEATURE`

## durabilityRngFollowsPlayer `🐛Beta`

Unbreaking's durability loss roll uses the player's own random sequence instead of the world's.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `HFUT`, `FEATURE`

## namedWanderingTraderPersistence

A wandering trader named with a matching custom name no longer despawns naturally.

- Type: `string`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `HFUT`, `FEATURE`

- `false`: disabled
- `true`: any custom-named trader persists
- any other value: only traders named with that exact string persist

## beaconDownwardsInfiniteRange

The beacon's effect area extends downwards without limit; the horizontal range is unchanged.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `HFUT`, `FEATURE`

- `false`: disabled (vanilla behaviour — the effect area only grows upwards)
- `true`: players at any depth directly below the beacon receive its effects
