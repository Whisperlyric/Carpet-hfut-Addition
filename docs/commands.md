# 命令

本模组注册了 `/hfut`、`/pearltrace`、`/tradeseq` 三个命令，并向 Carpet 自带的 `/player` 注入了一个子命令。

能否使用由权限规则控制（见[规则](rules.md)）：`commandPlayerTickingStage` 管 `/player ... tickingStage`，`commandPearlTrace` 管 `/pearltrace` 的查验（`list`/`show`），`commandPearlTracePurge` 管清除（`purge`），`commandTradeSeq` 管 `/tradeseq`。取值：`false`（禁用）、`true`（所有人）、`ops`（权限等级 2 及以上）、`0`～`4`（指定的最低权限等级）。

## 调整假人 tick 阶段 (`/player <假人> tickingStage`)

### 语法
- `/player <假人> tickingStage <global|invert|origin|likeReal>`
    - `global` 跟随全局规则（默认，取消单独设置）
    - `invert` 与全局规则相反
    - `origin` 假人的原始行为（实体阶段）
    - `likeReal` 与真人玩家相同（网络阶段）

### 效果
- 修改该假人的 tick 阶段，并提示当前生效值（T/F 及所处阶段）与全局值。
- `<假人>` 支持 Tab 补全（仅在线假人）。
- 仅在本次开服期间有效：重新生成同一个假人仍会保持，重启服务器后重置。

## 幽灵珍珠反查 (`/pearltrace`) `MC>=1.21.2`

### 语法
- `/pearltrace list [<玩家>] [<页码>]`
- `/pearltrace show <uuid>`
- `/pearltrace purge <玩家> <uuid> [confirm]`

`<uuid>` 可以填完整 UUID，也可以只填能唯一确定它的开头几位。

### 效果
- `list` 查看最近的传送记录（新→旧，每页 8 条）：每行显示幽灵标记（`!!` = 同一 UUID 传送 ≥2 次）、完整 UUID、玩家、时间、来源、次数、坐标；点击任意一行会把 `show` 命令填进聊天框。
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

`<职业>` 为村民职业（提供补全），`<等级>` 为 1-5；二者对应 vanilla 命名随机序列 `minecraft:trade_set/<职业>/level_<等级>`（如 `librarian 4` 即 `minecraft:trade_set/librarian/level_4`）。

### 效果
- 同一职业同等级的所有村民**共享同一条序列**，谁先刷新谁先消耗；操纵序列即可控制该批村民后续的刷取结果。
- `status` 显示序列当前的 128 位状态（`lo:hi` 十六进制）。
- `set <数字>` 将序列重置到起始状态后再前进 `<数字>` 步（0 ≤ `<数字>` ≤ 100000；`set 0` 即从头，下一次刷新为第 1 个结果）。
- `rollback <数字>` 沿 Xoroshiro128++ 逆变换回退 `<数字>` 步（1 ≤ `<数字>` ≤ 100000）。
- `skip <数字>` 向前步进 `<数字>` 步，跳过不要的刷取结果（1 ≤ `<数字>` ≤ 100000）。
- 需要 op 权限（受 `commandTradeSeq` 控制）；仅 26.1+ 存在（交易序列机制随数据驱动交易引入）。

## 查看版本 (`/hfut version`)

### 语法
- `/hfut version`

### 效果
- 打印模组名与当前版本。

## GCA 假人菜单按钮（非命令）

装了 [GugleCarpetAddition](https://github.com/Gu-ZT/gugle-carpet-addition)（GCA）时，假人的末影箱 / 控制菜单里、GCA「退出游戏」按钮的左侧（第 25 格）会出现 **HFUT：tick 阶段反转** 按钮。

- 悬停显示当前生效值（T/F 及所处阶段）与全局值。
- 点击在 `invert` 与 `global` 之间切换，等同于 `/player <假人> tickingStage invert`。
- 未装 GCA 时该按钮不存在，其他功能不受影响。
