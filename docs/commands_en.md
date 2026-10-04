# Commands

This mod registers `/hfut` and `/pearltrace`, and injects one subcommand into Carpet's own `/player`.

Who may use them is controlled by permission rules (see [rules](rules_en.md)): `commandPlayerTickingStage` for `/player ... tickingStage`, `commandPearlTrace` for the read-only `/pearltrace` `list`/`show`, and `commandPearlTracePurge` for `purge`. Values: `false` (disabled), `true` (everyone), `ops` (permission level 2 or above), `0`-`4` (a minimum permission level).

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
