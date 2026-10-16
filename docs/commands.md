# 命令

本模组注册了 `/hfut`、`/pearltrace`、`/tradeseq` 三个命令，并向 Carpet 自带的 `/player` 注入了一个子命令。除 `/hfutclient` 为纯客户端命令外，其余命令都在服务端注册。

能否使用由权限规则控制(见[规则](rules.md))

## 调整假人 tick 阶段 (`/player <假人> tickingStage`)

### 语法
- `/player <假人> tickingStage`：查询当前状态
- `/player <假人> tickingStage <invert|origin|likeReal>`
    - `invert` 相对当前取反
    - `origin` 实体阶段
    - `likeReal` 网络阶段

### 效果
- 返回为灰色斜体；调整后同时显示假人与全局规则各自所处的阶段（网络阶段NU/实体阶段EU，当前生效者在前）。
- 全局规则选项已移除：全局规则仅作为未单独设置假人的默认值。
- `<假人>` 支持 Tab 补全（仅在线假人）。
- 仅在本次开服期间有效：重新生成同一个假人仍会保持，重启服务器后重置。

## 假人加载范围 (`/player <假人> loadRange`)

用一张本模组自己的票据覆盖单个假人的区块加载范围（受 `commandSingleFakePlayerLoadRange` 控制）。

### 语法
- `/player <假人> loadRange <2-33>`：把该假人的加载半径设为指定格数
- `/player <假人> loadRange reset`：清除设置，恢复为服务默认值

### 效果
- 设置期间原版假人玩家加载票被摘除，只保留本模组的假人玩家加载票。
- 假人因任何原因被移除时，其设置与加载票一并清除。
- `reset` 后该假人会在下一次区块更新（最多 10 tick）时自动恢复原版的加载范围。

## 幽灵珍珠反查 (`/pearltrace`) `MC>=1.21.2`

> 需先开启规则 `ghostEnderPearlTrace` 才会有记录；`/pearltrace` 的使用权限由 `commandPearlTrace`（`list`/`show`）与 `commandPearlTracePurge`（`purge`）控制。

### 语法
- `/pearltrace list [<玩家>]`
- `/pearltrace show <uuid>`
- `/pearltrace purge <玩家> <uuid> [confirm]`

`<uuid>` 可以填完整 UUID，也可以只填能唯一确定它的开头几位。

### 效果
- `list` 查看最近的传送记录（新→旧，每页 8 条）：每行显示幽灵标记（`!!` = 同一 UUID 传送 ≥2 次）、完整 UUID、玩家、时间、来源、次数、坐标；点击任意一行会把 `show` 命令填进聊天框；多于一页时底部有灰色 `[< 上一页]` / `[下一页 >]` 按钮（点击翻页）。安装本模组的客户端翻页时会用新一页替换聊天里的上一页，而不是把上一页顶上去；仅影响显示，所有内容仍完整保留在 `logs/latest.log`。
- `show` 查看该珍珠的详情：累计传送次数、最近一次传送（时间 / 拥有者 / 维度 / 坐标 / 来源 / 出生地）、以及还有哪些在线玩家身上带着它的残留副本。
- `purge` 从在线玩家的珍珠集合中移除该 UUID 的条目（权限由 `commandPearlTracePurge` 控制，比查验更严），分两步执行，避免误删：
    - 不带 `confirm`：只预览，不删除；不确认即等于取消。
    - 加上 `confirm`：执行移除。
    - 预览后 60 秒内未确认、珍珠期间又传送过、或你中途预览了别的珍珠，预览都会失效，需重新预览。
    - 控制台 / 命令方块同样需要先预览再确认。
    - 仅支持在线玩家；离线玩家的残留会在其下次登录时自动清理。
    - 没有「一次清空全部」的用法。

## 交易序列操纵 (`/tradeseq`) `MC>=26.1` `🐛Beta`

### 语法
- `/tradeseq <职业> <等级> status`
- `/tradeseq <职业> <等级> set <数字>`
- `/tradeseq <职业> <等级> rollback <数字>`
- `/tradeseq <职业> <等级> skip <数字>`
- `/tradeseq confirm`
- `/tradeseq cancel`
- `/tradeseq debug <true|false>`

`<职业>` 为村民职业（提供补全），`<等级>` 为 1-5；二者对应 vanilla 命名随机序列 `minecraft:trade_set/<职业>/level_<等级>`（如 `librarian 4` 即 `minecraft:trade_set/librarian/level_4`）。

### 效果
- 同一职业同等级的所有村民**共享同一条序列**，谁先刷新谁先消耗；操纵序列即可控制该批村民后续的刷取结果。
- `status` 显示序列距初态已经历过多少次**刷新**（若状态停在某轮刷新中途，会同时报出相邻的两代；超出搜索范围则报告未知）。
- `debug <true|false>` 开关调试信息（默认 `false`）；开启后，`status`/`set`/`rollback`/`skip` 每次操作会另起两行，分别输出本次耗时（ms）与当前 128 位状态（`lo:hi` 十六进制）。
- `set <数字>` 将序列重置到初态，再重放 `<数字>` 次刷新（`0` ≤ `<数字>` ≤ `2147483647`，即 int 上限）。`set 0` 即初态，下一次刷新为第 1 个结果；`set n` 之后的下一次刷新，其结果与 Python 参考工具 `predict_at_offset(prof, level, n)` 一致。
- `rollback <数字>` 先定位当前次数，再重放到 `max(0, 当前次数 − 数字)`（`1` ≤ `<数字>` ≤ `2147483647`）；`<数字>` 超过当前次数时回退到初态，提示中显示的是**实际回退量**；定位不到（超出搜索范围）则拒绝执行。
- `skip <数字>` 在当前序列上重放 `<数字>` 次刷新，跳过不要的刷取结果（`1` ≤ `<数字>` ≤ `2147483647`）。
- `<数字>` 超过 `100000` 时不会立即执行，而是把它暂存为待确认操作并提示执行 `/tradeseq confirm` 确认（防止误触发超大规模重放）；执行 `/tradeseq confirm` 后才真正执行。无待确认操作时 `/tradeseq confirm` 会提示无操作可确认。
- `cancel` 请求取消正在进行的计算（协作式中断，稍后停止，回发「计算已取消」）。任一时刻只允许一个计算：正在计算时发起其它 `/tradeseq` 命令会被拒绝，并提示先 `cancel`。
- 需要 op 权限（受 `commandTradeSeq` 控制）；仅 26.1+ 存在（交易序列机制随数据驱动交易引入）。

> **计数单位是「刷新次数」，与 py 工具一致**：一次刷新是「抽一个 trade_set 并生成 offer」的完整一轮，底层会消耗可变次数的抽取（1 抽起步——取决于交易表内容、是否允许重复、offer 生成是否失败、价格/附魔组件是否额外抽取）。计数从**初态**起算，`set n` 等价于先把序列复位、再完整重放 n 轮，因此与 Python 参考工具的 offset 一一对应。所有修改都会正确落盘，重启后保留。
>
> **上限与异步**：`<数字>` 与内部搜索上限同为 int 最大值 `2147483647`，命令实参即按此校验。重放已移到**单线程异步 worker** 上执行，不再占用服务端 tick，因此启动看门狗不会因重放超时而崩服；命令会立即返回，结果（代数；调试模式下另含耗时与状态）在计算完成后回发。代价是极大数字仍会长时间占满该 worker，期间其它 `/tradeseq` 命令会被拒绝，需先用 `/tradeseq cancel` 取消或等它完成。`rollback`/`skip` 在应用结果前会校验序列未被并发改动（例如期间有村民交易），若已改动则取消并提示重试。

## 弱加载区块 (`/hfut lazychunk`) `🐛Beta`

把区块固定在「弱加载」状态：即使附近没有玩家也保持；同时阻止任何东西把它升级回完全加载（玩家走近、`/forceload`、传送门都会被钳住）。全部版本可用；标记只存在内存里，按维度区分，重启服务器后清空。

### 语法
- `/hfut lazychunk add chunk <x> <z>`：按**区块坐标**标记单个区块
- `/hfut lazychunk add pos <x> <z>`：按 **x z 列坐标**标记所在区块（支持 `~`，自动换算成区块坐标，不用自己除以 16）
- `/hfut lazychunk add area chunk <x1> <z1> <x2> <z2>`：按区块坐标标记矩形范围
- `/hfut lazychunk add area pos <x1> <z1> <x2> <z2>`：按 x z 列坐标标记范围（两端各自换算成区块，取矩形）
- `/hfut lazychunk delete chunk <x> <z>`：取消单个区块
- `/hfut lazychunk delete area <x1> <z1> <x2> <z2>`：取消矩形范围（只收区块坐标——用 `query` 查到的就是区块坐标）
- `/hfut lazychunk delete all`：取消本维度的**全部**标记
- `/hfut lazychunk query`：分页列出本维度全部标记区块及其**当前加载状态**（随游戏语言显示的本地化状态名）

### 效果
- `add` 后区块立即（在下一次区块刻更新时）进入弱加载；`delete` 后立刻恢复正常卸载流程。
- `add`/`add area` 的范围换算后超过 256 个区块会拒绝执行（与原版 `/forceload` 上限一致）；范围内已是弱加载的区块会跳过，只统计新增数量。
- 区块坐标超出世界边界（±1 874 999，即世界边界 ±29 999 984 方块 ÷ 16）会直接拒绝，不会挂出永远加载不到的废票。
- **玩家永远不会被冻结**：玩家所在区块豁免于钳制，临时保持实体刻，玩家离开的同一刻即降为弱加载。因此对有玩家在场的范围执行 `add` 会照常接受，但会附一条提示说明该区块要等玩家离开才生效。
- `delete all` 一键取消本维度全部标记，返回移除数量。
- `query` 每页 10 条；点击任意一行会把对应的 `delete chunk` 命令填进聊天框；多于一页时底部有灰色 `[< 上一页]` / `[下一页 >]` 按钮（点击翻页）。安装本模组的客户端翻页时会用新一页替换聊天里的上一页，而不是把上一页顶上去；这仅影响显示，每一行仍完整保留在 `logs/latest.log`。
- 需要 op 权限（受 `commandSimpleLazyChunk` 控制）。

## 假人开箱 (`/player <假人> open`)

打开假人的背包或末影箱进行编辑。假人在线时由 `commandFakePlayerRemoteOpenStorage` 控制权限，假人不在线时由 `commandOfflineFakePlayerOpenStorage` 控制权限。

### 语法
- `/player <假人> open inventory`：打开背包
- `/player <假人> open enderchest`：打开末影箱

### 效果
- 假人在线时打开它的存储；目标是真人时返回「只能操作假人」。
- 同时安装 [GugleCarpetAddition](https://github.com/Gu-ZT/gugle-carpet-addition)（GCA）且其 `openFakePlayerInventory` / `openFakePlayerEnderChest` 开启时，在线打开直接复用 GCA 的界面；否则使用本模组自带的界面。
- 假人不在线时，其存档数据被加载进一个不入世界的影子实体，编辑实时写回玩家存档：关闭界面即保存，服务器关闭时也会进行保存。防刷物品、独占访问，假人从未生成过（无玩家存档）时拒绝打开。本模组自带的背包界面为 5 行（含头/胸/腿/脚与副手位），末影箱为 3 行。

## 查看版本 (`/hfut version`)

### 语法
- `/hfut version`

### 效果
- 打印模组名与当前版本。

## 客户端命令 (`/hfutclient`) `仅客户端`

### 语法
- `/hfutclient lazychunk <页码>`
- `/hfutclient pearl <页码> [<玩家>]`
- `/hfutclient hidepausesocial` `MC>=26.2`
- `/hfutclient hidepausesocial <true|false>` `MC>=26.2`

### 效果
- `lazychunk` / `pearl`：请求服务端重新发送 `/hfut lazychunk query` / `/pearltrace list` 的指定页，`query` 与 `list` 底部的翻页按钮点击时即调用它们；服务端会按对应规则复核权限。未安装本模组的客户端点击翻页按钮会得到「未知命令」。
- `hidepausesocial`：隐藏 26.2+ 暂停菜单里的社交按钮一排（问题反馈 / 好友 / 举报等），以及标题界面「多人游戏」旁的好友按钮。如果安装了 ModMenu ，则行为与旧版本的默认界面相同。不带参数为取反，带 `true`/`false` 为直接设置。

## GCA 假人菜单按钮（非命令）

同时安装 [GugleCarpetAddition](https://github.com/Gu-ZT/gugle-carpet-addition)（GCA）时，假人的末影箱 / 控制菜单里、GCA「退出游戏」按钮的左侧（第 25 格）会出现 **HFUT：tick 阶段反转** 按钮。

- 悬停显示当前生效值（T/F 及所处阶段）与全局值。
- 点击在 `invert` 与 `global` 之间切换，等同于 `/player <假人> tickingStage invert`。
- 未装 GCA 时该按钮不存在，其他功能不受影响。
