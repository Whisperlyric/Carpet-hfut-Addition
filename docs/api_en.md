# API

Java API for other mods (e.g. trade prediction mods) to reference at compile time.

## `RandomSequenceApi`

`dev.whisperlyric.carpet_hfut_addition.api.RandomSequenceApi`

Reads, overwrites and advances the Xoroshiro state of one named random sequence. `<id>` is the sequence identifier, e.g. `Identifier.fromNamespaceAndPath("minecraft", "trade_set/<profession>/level_<level>")` for villager trades (`<level>` is 1-5).

### Methods
- `static long[] getState(MinecraftServer server, Identifier id)`
    Reads the current 128-bit state as `[seedLo, seedHi]`; with the world seed this reproduces the next trade refresh.
- `static void setState(MinecraftServer server, Identifier id, long seedLo, long seedHi)`
    Overwrites the 128-bit state; call `getState` first to snapshot it.
- `static void skip(MinecraftServer server, Identifier id, int count)`
    Advances the sequence by `count` rolls (vanilla `consumeCount`), jumping past unwanted refresh results.
- `static XoroshiroRandomSource sequence(MinecraftServer server, Identifier id)`
    Resolves the raw generator behind `<id>` (creating the sequence if absent); use it when you want to hold the sequence yourself (e.g. call `nextLong` directly or query repeatedly), so it is not resolved once per call.

### Notes
- All villagers of the same profession and level **share one sequence**, so writing or advancing it affects the next refresh results of that whole batch.
- A missing `<id>` is created on demand (equivalent to one `server.getRandomSequence(id)` call).
- Vanilla wraps the sequence in a dirty-marking delegate (`RandomSequences$DirtyMarkingRandomSource`), so `server.getRandomSequence(id)` alone does not expose the underlying state; the API (`sequence`) unwraps it for you, so callers only pass the server and the `<id>`.
