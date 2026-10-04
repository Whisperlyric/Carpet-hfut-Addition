# 规则

**提示：可以使用`Ctrl+F`快速查找自己想要的规则**

## 修改假人 tick 阶段权限 (commandPlayerTickingStage)

设置谁可以使用 `/player <假人> tickingStage` 调整假人的 tick 阶段（`ops` = 权限等级 2 及以上）。

- 类型: `string`
- 默认值: `ops`
- 参考选项: `false`, `true`, `ops`, `0`, `1`, `2`, `3`, `4`
- 分类: `HFUT`, `COMMAND`, `FEATURE`

## 幽灵珍珠反查命令权限 (commandPearlTrace)

设置谁可以使用 `/pearltrace list`/`show` 查看幽灵珍珠残留。

- 类型: `string`
- 默认值: `ops`
- 参考选项: `false`, `true`, `ops`, `0`, `1`, `2`, `3`, `4`
- 分类: `HFUT`, `COMMAND`, `FEATURE`

## 幽灵珍珠清除命令权限 (commandPearlTracePurge)

设置谁可以使用 `/pearltrace purge` 从在线玩家身上清除幽灵珍珠（`ops` = 权限等级 2 及以上）。

- 类型: `string`
- 默认值: `ops`
- 参考选项: `false`, `true`, `ops`, `0`, `1`, `2`, `3`, `4`
- 分类: `HFUT`, `COMMAND`, `FEATURE`

> 只读的 `list`/`show` 由 `commandPearlTrace` 控制；清除会改写玩家数据，因此单独用一个更严的权限。

## 绊线行为修复 (tripwireIgnoreDepartures) `MC>=1.21.9`

用较为激进的方式修复绊线在实体穿过时多次触发上升沿的问题（MC-305475）。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `BUGFIX`

> 26.2 及以上需要先开启 `tripwireRestore=true` 作为前置。

## 恢复绊线行为 (tripwireRestore) `MC>=26.2`

把 26.2 及以上的绊线恢复成 26.2 之前的行为。

- 类型: `string`
- 默认值: `false`
- 参考选项: `false`, `true`, `26.3`
- 分类: `HFUT`, `BUGFIX`

- `false`：当前版本的原版行为
- `true`：退回 26.2 之前的绊线行为（也是开启 `tripwireIgnoreDepartures` 的前提）
- `26.3`：仅 26.2 有效，把绊线断电后的「立即重新检测」延后到 1t（26.3 的官方修法）

## 假人像真人一样 tick (fakePlayerTicksLikeRealPlayer)

让假人像真人玩家一样 tick：假人的 tick 移到网络阶段，动作包移到异步任务阶段。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `EXPERIMENTAL`

> 可只对单个假人生效：`/player <假人> tickingStage`（global / invert / origin / likeReal），或 GCA 假人菜单里的按钮；仅在本次开服期间有效。

## 幽灵末影珍珠修复 (ghostEnderPearlFix) `MC>=1.21.2`

幽灵末影珍珠修复（MC-306936）：玩家重登后珍珠消失、已被用掉的珍珠仍然传送玩家、或珍珠数量越重登越多。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `BUGFIX`

> 三层修复：登录时丢弃重复生成的珍珠副本、关服时防止珍珠被区块重复保存、存档时只写入仍存在于世界中的珍珠。
>
> 装了 carpet-igny-addition 时本规则隐藏，由其同名规则接管。

## 幽灵末影珍珠反查 (ghostEnderPearlTrace) `MC>=1.21.2`

记录每次珍珠传送及其来源、按珍珠 UUID 累计次数，可用 `/pearltrace` 查看或清除（见[命令](commands.md)）。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `BUGFIX`, `COMMAND`

> 同一 UUID 传送 2 次及以上即为幽灵回放；可独立使用，也可与修复规则叠加。

## 清除过门潜影弹 (shulkerBulletPortalCleanup) `MC>=1.21.2`

让潜影弹在穿过传送门切换维度时被清除。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `FEATURE`

> 潜影弹的制导目标是跨维度解析的，过门后会朝切换维度前的坐标飞行。
>
> 在传送发生前清除，同时免去目的侧的区块加载。

## 清除弱加载区块潜影弹 (shulkerBulletWeakChunkCleanup)

触发式清除处于弱加载区块的潜影弹。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `FEATURE`

## 清除游荡凋灵头 (witherSkullCleanup)

触发式清除处于弱加载区块的凋灵头和蓝头。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `FEATURE`

## 矿车加速 (minecartAcceleration) `MC>=26.3`

重新引入 1.21.5-26.2 的矿车加速：假人可以链式换乘矿车，一游戏刻内被多次 tick。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `EXPERIMENTAL`, `FEATURE`

> 26.3 修复了矿车交互的返回值，假人骑矿车开始有 4gt 使用间隔；开启后成功上矿车的返回值改回 PASS，等价 26.2- 行为。
>
> 只影响假人，真人玩家不受影响。

## 地图玩家记录泄漏修复 (mapRegistryLeakFix)

原版会清除地图里的玩家记录，但 Lithium 等模组改写了这条清除路径，边缘情况没清掉，导致记录堆积、内存泄漏。

- 类型: `boolean`
- 默认值: `true`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `REMOTE_BUGFIX`

> 玩家一下线，就从所有已加载地图的名单中移除；假人不再被记入名单。
>
> 默认开启，开销很小：只在玩家下线时清理一次。

## 重登泄漏修复 (reloginAvatarLeakFix) `1.21.2~1.21.11`

修复 Carpet-Org-Addition 1.21.x 上的重登泄漏：relogin 下线的旧假人没有真正断开，会一直残留在内存中。

- 类型: `boolean`
- 默认值: `true`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `REMOTE_BUGFIX`

> 下线任务会跳过已标记移除的假人，旧假人的断开流程因此永远不会执行。修复方式：强制执行断开，同时跳过已移除假人的重复存档（保留 ORG 对骑乘实体数据的保护）。
>
> 需要安装 carpet-org-addition；仅 1.21.2～1.21.11 存在此问题（新版 ORG v1.46.0+ 已自行修复）。
