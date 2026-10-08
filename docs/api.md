# API

供其他模组（如交易预测类）在编译期引用调用的 Java API。

## `RandomSequenceApi`

`dev.whisperlyric.carpet_hfut_addition.api.RandomSequenceApi`

读写、推进某条命名随机序列的 Xoroshiro 状态。`<id>` 为序列标识符，例如村民交易的 `Identifier.fromNamespaceAndPath("minecraft", "trade_set/<职业>/level_<等级>")`（`<等级>` 为 1-5）。

### 方法
- `static long[] getState(MinecraftServer server, Identifier id)`
    读取当前 128 位状态，返回 `[seedLo, seedHi]`；配合世界种子即可确定性地推算出下一次交易刷新的结果。
- `static void setState(MinecraftServer server, Identifier id, long seedLo, long seedHi)`
    覆盖 128 位状态；需要还原时先用 `getState` 存一份快照。
- `static void skip(MinecraftServer server, Identifier id, int count)`
    向前推进 `count` 步（对应官方的 `consumeCount`），跳过不想要的刷新结果。
- `static XoroshiroRandomSource sequence(MinecraftServer server, Identifier id)`
    解析出 `<id>` 底层的原始生成器（序列不存在则自动创建）；需要直接持有序列（如自行调用 `nextLong` / 反复查询）时用它，避免每个方法各自解析一次。

### 说明
- 同一职业同等级的所有村民**共享同一条序列**，因此写入/推进会影响该批村民后续的刷新结果。
- `<id>` 对应的序列不存在时会自动创建（等价于先执行一次 `server.getRandomSequence(id)`）。
- 序列在 vanilla 中被一层「脏标记」包装类（`RandomSequences$DirtyMarkingRandomSource`）包裹，直接拿 `server.getRandomSequence(id)` 的结果取不到底层状态；API 内部（`sequence`）会绕过该包装，调用方只需传服务器与 `<id>`。
