# Commands

This mod registers `/hfut`, `/pearltrace` and `/tradeseq`, and injects one subcommand into Carpet's own `/player`.

Who may use them is controlled by permission rules (see [rules](rules_en.md)): `commandPlayerTickingStage` for `/player ... tickingStage`, `commandPearlTrace` for the read-only `/pearltrace` `list`/`show`, `commandPearlTracePurge` for `purge`, and `commandTradeSeq` for `/tradeseq`. Values: `false` (disabled), `true` (everyone), `ops` (permission level 2 or above), `0`-`4` (a minimum permission level).

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

### Syntax
- `/pearltrace list [<player>] [<page>]`
- `/pearltrace show <uuid>`
- `/pearltrace purge <player> <uuid> [confirm]`

`<uuid>` may be a full UUID or just enough leading characters to identify it uniquely.

### Effect
- `list` shows recent teleport events (newest first, 8 per page): each row has the ghost mark (`!!` = the same UUID teleported 2+ times), full UUID, player, time, origin, count and coordinates; clicking a row puts the `show` command into the chat input.
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

## Version (`/hfut version`)

### Syntax
- `/hfut version`

### Effect
- Prints the mod name and current version.

## GCA fake player menu button (not a command)

With [GugleCarpetAddition](https://github.com/Gu-ZT/gugle-carpet-addition) (GCA) installed, a **HFUT: tick stage invert** button appears in the fake player's ender chest / controller menu, to the left of GCA's "quit game" button (slot 25).

- The tooltip shows the current value (T/F and its phase) and the global value.
- Clicking toggles between `invert` and `global`, the same as `/player <name> tickingStage invert`.
- Without GCA the button simply does not exist; nothing else is affected.

