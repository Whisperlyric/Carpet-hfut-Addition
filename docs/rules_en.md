# Rules

**Tip: use `Ctrl+F` to find a rule**

## commandPlayerTickingStage

Who may use `/player <name> tickingStage` to change a fake player's tick stage (`ops` = permission level 2 or above).

- Type: `string`
- Default value: `ops`
- Suggested options: `false`, `true`, `ops`, `0`, `1`, `2`, `3`, `4`
- Categories: `HFUT`, `COMMAND`, `FEATURE`

## commandPearlTrace

Who may use the read-only `/pearltrace list` and `show` to inspect ghost pearl leftovers.

- Type: `string`
- Default value: `ops`
- Suggested options: `false`, `true`, `ops`, `0`, `1`, `2`, `3`, `4`
- Categories: `HFUT`, `COMMAND`, `FEATURE`

## commandPearlTracePurge

Who may use `/pearltrace purge` to remove ghost pearls from online players (`ops` = permission level 2 or above).

- Type: `string`
- Default value: `ops`
- Suggested options: `false`, `true`, `ops`, `0`, `1`, `2`, `3`, `4`
- Categories: `HFUT`, `COMMAND`, `FEATURE`

> The read-only `list`/`show` are controlled by `commandPearlTrace`; purging rewrites player data, so it gets its own, tighter permission.

## tripwireIgnoreDepartures `MC>=1.21.9`

Fixes the tripwire firing its rising edge multiple times as entities pass through it (MC-305475).

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `HFUT`, `BUGFIX`

> On 26.2 and above, `tripwireRestore=true` is a prerequisite.

## tripwireRestore `MC>=26.2`

Restores the pre-26.2 tripwire behaviour on 26.2 and above.

- Type: `string`
- Default value: `false`
- Suggested options: `false`, `true`, `26.3`
- Categories: `HFUT`, `BUGFIX`

- `false`: vanilla behaviour of the current version
- `true`: roll back to the pre-26.2 tripwire behaviour (also the prerequisite for `tripwireIgnoreDepartures`)
- `26.3`: on 26.2 only, delays the tripwire's "immediate re-check" after losing power to 1t (the official 26.3 fix)

## fakePlayerTicksLikeRealPlayer

Makes fake players tick like real players: their tick moves to the network phase and their action pack to the async task phase.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `HFUT`, `EXPERIMENTAL`

> Can be set for a single fake player: `/player <name> tickingStage` (global / invert / origin / likeReal), or the button in GCA's fake player menu; it lasts for the current server run only.

## ghostEnderPearlFix `MC>=1.21.2`

Ghost ender pearl fix (MC-306936): pearls vanishing after relogin, already-used pearls still teleporting the player, or pearls multiplying with each relogin.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `HFUT`, `BUGFIX`

> Hidden when carpet-igny-addition is installed; its same-name rule takes over.

## ghostEnderPearlTrace `MC>=1.21.2`

Records every pearl teleport and its origin and counts teleports per pearl UUID; inspect or purge with `/pearltrace` (see [commands](commands_en.md)).

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `HFUT`, `BUGFIX`, `COMMAND`

> The same UUID teleporting 2+ times proves ghost replay; works on its own or together with a fix rule.

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

Discards shulker bullets sitting in a non-entity-ticking (weakly loaded) chunk.

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

## minecartAcceleration `MC>=26.3`

Reintroduces the 1.21.5-26.2 minecart acceleration: bots can chain-mount carts and get ticked multiple times per tick.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `HFUT`, `EXPERIMENTAL`, `FEATURE`

> Only affects bots; real players are unaffected.

## mapRegistryLeakFix

Vanilla clears a map's player records, but map optimizations such as Lithium rewrite that path and miss an edge case, so records pile up and leak.

- Type: `boolean`
- Default value: `true`
- Suggested options: `false`, `true`
- Categories: `HFUT`, `REMOTE_BUGFIX`

> On disconnect the player is removed from every loaded map's list; fake players are never added.
>
> On by default, and cheap: it is one pass over the loaded maps per disconnect.

## reloginAvatarLeakFix `1.21.2~1.21.11`

Fixes the relogin leak on Carpet-Org-Addition's 1.21.x line: the old fake player being logged out by relogin is never properly disconnected and lingers in memory.

- Type: `boolean`
- Default value: `true`
- Suggested options: `false`, `true`
- Categories: `HFUT`, `REMOTE_BUGFIX`

> Requires carpet-org-addition; only 1.21.2 - 1.21.11 is affected (newer ORG fixed itself).

## miningFatigueDigSpeedFix `MC<=26.2`

Fixes the mining fatigue dig-speed table: 0.027 was typed as 0.0027 and 0.0081 as 8.1E-4, so dig speed under fatigue III+ is 10x slower than intended.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `HFUT`, `BUGFIX`

> 26.3 fixed this upstream with 0.3^(amplifier+1); this rule only exists below 26.3 and matches that behaviour (levels V+ are command-only and are treated as IV).

## enderDragonVerticalVelocityFix

Fixes the ender dragon's vertical velocity when flying to a target node (MC-272431): since 19w08b (1.14) the vertical acceleration scale is 0.01 instead of 0.1, so the dragon can barely climb or dive and circles its target nodes.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `HFUT`, `BUGFIX`

> Restores 1.13-and-earlier flight: perching descends without wandering, charges reach the player; unfixed in every supported version.
>
> Also mitigates MC-271336, MC-271337 and MC-197201.

## villagerInstantLevelUp `MC<=26.2`

Port of the 26.3 villager trading: a qualifying trade levels the villager up and unlocks the next tier instantly, without closing and reopening the screen.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `HFUT`, `FEATURE`

> Up to 26.2 the level-up is deferred by 40gt and only runs while the villager is not trading, hence the screen dance; 26.3 does it on the spot.
>
> Also ports the 10s regen I on level-up and instant special prices on the freshly unlocked tier.

## villagerLivePriceSync `MC<=26.2`

Port of the 26.3 instant villager price recompute: while the trade screen is open, restocks, demand catch-up, gossip spread and player reputation all reprice and correct the offers instantly.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `HFUT`, `FEATURE`

> Up to 26.2 vanilla waits for the next screen open to update; this rule makes the new prices apply while the screen is still open.

## babyMobAvoidGoldenDandelion `MC>=26.1.2`

Babies whose growth golden dandelion suppresses steer away from it (incl. potted), piglin-and-soul-fire style; only the 33 growable babies below are affected.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `HFUT`, `FEATURE`

> Armadillo, axolotl, bee, camel, cat, chicken, cow, dolphin, donkey, fox, glowing squid, goat, small ghast (happy ghast baby), hoglin, horse, llama, mooshroom, mule, nautilus, ocelot, panda, pig, polar bear, rabbit, sheep, sniffer, squid, strider, sulfur cube, tadpole, trader llama, turtle, wolf.
>
> Mobs outside the list (villager children included) and adults are unaffected: a 6-block scan every 20gt, then fleeing 8-12 blocks away. The sulfur cube is a 26.2+ mob, so that entry is inert on 26.1.2.

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

> In-map player markers always showed facing; up to 26.2 off-map markers point north, and 26.3 rotates theirs by facing as well.
>
> The rotation byte travels in the map data packet, so vanilla clients render it without this mod installed.

## creativeNoEntityCollision

Creative players are no longer stopped by entity hitboxes nor pushed around - including after landing; standing on a boat, shulker or the like simply falls through.

- Type: `boolean`
- Default value: `false`
- Suggested options: `false`, `true`
- Categories: `HFUT`, `FEATURE`

> Unlike carpet's own `creativeNoClip` (treats flying creative players as spectators, blocks included, and collision comes back the moment they land): this rule only disables entity collision, keeps block collision, and does not care whether the player is flying.
>
> Pushing is inert in both directions (mobs, minecarts and boats neither shove the player nor get shoved); projectile hits, pressure plates and other non-collision interactions are unaffected.
