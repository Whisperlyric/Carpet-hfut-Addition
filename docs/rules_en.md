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
