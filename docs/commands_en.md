# Commands

This mod registers `/hfut`, `/pearltrace` and `/tradeseq`, and injects one subcommand into Carpet's own `/player`. All of them are registered server-side except `/hfutclient`, which is client-only.

Who may use them is controlled by permission rules (see [rules](rules_en.md)).

## Change a fake player's tick stage (`/player <name> tickingStage`)

### Syntax
- `/player <name> tickingStage <global|invert|origin|likeReal>`
    - `global` follow the global rule (default, clears the per-player setting)
    - `invert` the opposite of the global rule
    - `origin` vanilla fake player behaviour (entity phase)
    - `likeReal` same as a real player (network phase)

### Effect
- Changes that fake player's tick stage and reports the current value (T/F and its phase) plus the global value.
- `<name>` supports Tab completion (online fake players only).
- Lasts for the current server run only: re-spawning the same fake player keeps it, restarting the server resets it.

## Ghost pearl trace (`/pearltrace`) `MC>=1.21.2`

> Recording requires the `ghostEnderPearlTrace` rule to be on first; access to `/pearltrace` is governed by `commandPearlTrace` (`list`/`show`) and `commandPearlTracePurge` (`purge`).

### Syntax
- `/pearltrace list [<player>]`
- `/pearltrace show <uuid>`
- `/pearltrace purge <player> <uuid> [confirm]`

`<uuid>` may be a full UUID or just enough leading characters to identify it uniquely.

### Effect
- `list` shows recent teleport events (newest first, 8 per page): each row has the ghost mark (`!!` = the same UUID teleported 2+ times), full UUID, player, time, origin, count and coordinates; clicking a row puts the `show` command into the chat input; when there are multiple pages, gray `[< Prev]` / `[Next >]` arrows at the bottom turn the page on click. On a client that has this mod, a new page replaces the previous page in chat instead of stacking; this only affects the display, with everything still kept in `logs/latest.log`.
- `show` prints the pearl's details: total teleport count, the latest teleport (time / owner / dimension / coordinates / origin / birth spot), and which online players still hold leftover copies.
- `purge` removes entries of that UUID from an online player's collection (gated by `commandPearlTracePurge`, tighter than inspection), in two steps to avoid mistakes:
    - Without `confirm`: preview only, nothing is removed; not confirming means cancel.
    - With `confirm`: performs the removal.
    - A preview expires if it is not confirmed within 60 seconds, if the pearl teleported again meanwhile, or if you previewed another pearl in between; preview again to continue.
    - Console / command blocks must preview first too.
    - Online players only; leftovers of offline players are cleaned up automatically on their next login.
    - There is no "clear everything" form.

## `/tradeseq` `🐛Beta` `MC>=26.1`

### Syntax
- `/tradeseq <profession> <level> status`
- `/tradeseq <profession> <level> set <number>`
- `/tradeseq <profession> <level> rollback <number>`
- `/tradeseq <profession> <level> skip <number>`

`<profession>` is a villager profession (with suggestions), `<level>` is 1-5; together they map to the vanilla named random sequence `minecraft:trade_set/<profession>/level_<level>` (e.g. `librarian 4` is `minecraft:trade_set/librarian/level_4`).

### Effect
- All villagers of the same profession and level **share one sequence**, first refresh first served; controlling it controls the next refresh results of that whole batch.
- `status` shows the current 128-bit state (`lo:hi`, hex).
- `set <number>` re-seeds the sequence to its fresh state and then advances it `<number>` draws (`<number>` >= 0, up to 100000; `set 0` is the start, so the next refresh is result #1).
- `rollback <number>` applies the invertible Xoroshiro128++ backward transition `<number>` times (`<number>` >= 1, up to 100000).
- `skip <number>` advances forward by `<number>` draws to jump past unwanted results (`<number>` >= 1, up to 100000).
- Requires op (gated by `commandTradeSeq`); only exists on 26.1+ (the sequence mechanism came with data-driven trades).

## Simple lazy chunks (`/hfut lazychunk`) `🐛Beta`

Pins chunks at "lazy" (weakly loaded) strength: kept loaded, only block ticks run (crop growth, scheduled ticks, etc.), entities do not tick (mobs stand still, no spawning), and they stay that way even with no player nearby; at the same time nothing can promote them back to fully loaded (walking players, `/forceload`, portals are all clamped). Available on all versions; marks live in memory only, are tracked per dimension, and are cleared on server restart.

### Syntax
- `/hfut lazychunk add chunk <x> <z>`: mark a single chunk by **chunk coordinates**
- `/hfut lazychunk add pos <x> <z>`: mark the chunk a **column `<x> <z>`** falls in (supports `~`; converted to chunk coordinates for you, no need to divide by 16)
- `/hfut lazychunk add area chunk <x1> <z1> <x2> <z2>`: mark a rectangular range by chunk coordinates
- `/hfut lazychunk add area pos <x1> <z1> <x2> <z2>`: mark a range by columns (each end is converted to a chunk, then the rectangle is taken)
- `/hfut lazychunk delete chunk <x> <z>`: unmark a single chunk
- `/hfut lazychunk delete area <x1> <z1> <x2> <z2>`: unmark a rectangular range (chunk coordinates only - what `query` prints is what you use here)
- `/hfut lazychunk delete all`: unmark **every** mark in this dimension
- `/hfut lazychunk query`: page through all marked chunks in this dimension with their **current load state** (a localized status name)

### Effect
- After `add` a chunk turns lazy on the next chunk-tick update; after `delete` it returns to the normal unload flow immediately.
- `add` / `add area` refuses a range that exceeds 256 chunks after conversion (same cap as vanilla `/forceload`); already-lazy chunks in the range are skipped and only the new ones are counted.
- Chunk coordinates outside the world border (±1,874,999, i.e. the ±29,999,984-block world border ÷ 16) are refused outright, so no permanently-unreachable tickets are created.
- **Players are never frozen**: the player's own chunk is exempt from the clamp and stays entity-ticking while they are present, turning lazy the same tick they leave (removing the player ticket itself triggers that level update). So running `add` on a range with a player inside is accepted as usual, with a note that it only takes effect once they leave.
- `delete all` unmarks every mark in this dimension and returns how many were removed.
- `query` shows 10 rows per page. Clicking a row fills the chat input with its `delete chunk` command; when there are multiple pages, gray `[< Prev]` / `[Next >]` arrows at the bottom turn the page on click. On a client that has this mod, turning the page replaces the previous page in chat instead of stacking the pages; this only affects the display, and every line is still kept in `logs/latest.log`.
- Requires op (gated by `commandSimpleLazyChunk`).

## Offline fake player storage (`/player <name> open`)

Opens a fake player's inventory or ender chest for editing **while it is offline**. Gated by `commandFakePlayerOpenStorage`; the menu ships with this mod, so Carpet GugleCarpetAddition is not required.

### Syntax
- `/player <name> open inventory`: opens the inventory
- `/player <name> open enderchest`: opens the ender chest

### Effect
- The target's saved data is loaded into a shadow entity that never joins the world, and edits are written straight back to the playerdata: saved when the menu closes and on server stop.
- Duping is prevented and access is exclusive, and opening is refused when the fake player has never been spawned (no playerdata). The inventory view is 5 rows (including head/chest/legs/feet and off-hand cells); the ender chest is 3 rows.

## Version (`/hfut version`)

### Syntax
- `/hfut version`

### Effect
- Prints the mod name and current version.

## Client commands (`/hfutclient`) `Client only`

### Syntax
- `/hfutclient lazychunk <page>`
- `/hfutclient pearl <page> [<player>]`
- `/hfutclient hidepausesocial` `MC>=26.2`
- `/hfutclient hidepausesocial <true|false>` `MC>=26.2`

### Effect
- `lazychunk` / `pearl`: ask the server to re-send the given page of `/hfut lazychunk query` / `/pearltrace list`; the page arrows at the bottom of `query` and `list` call them on click. The server re-checks permission against the matching rule. On a client without this mod, clicking a page arrow gives "Unknown command".
- `hidepausesocial`: hides the 26.2+ pause menu's social button row (bug report / Friends / reporting) and the Friends button next to Multiplayer on the title screen. If ModMenu is installed, the old default behaviour is restored. Without an argument it toggles; `true`/`false` sets it directly.

## GCA fake player menu button (not a command)

With [GugleCarpetAddition](https://github.com/Gu-ZT/gugle-carpet-addition) (GCA) also installed, a **HFUT: tick stage invert** button appears in the fake player's ender chest / controller menu, to the left of GCA's "quit game" button (slot 25).

- The tooltip shows the current value (T/F and its phase) and the global value.
- Clicking toggles between `invert` and `global`, the same as `/player <name> tickingStage invert`.
- Without GCA the button simply does not exist; nothing else is affected.

