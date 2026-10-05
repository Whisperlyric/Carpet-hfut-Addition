# 规则

**提示：可以使用`Ctrl+F`快速查找自己想要的规则**

## 修改假人 tick 阶段权限 (commandPlayerTickingStage)

设置谁可以使用 `/player <假人> tickingStage` 调整假人的 tick 阶段。

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

设置谁可以使用 `/pearltrace purge` 从在线玩家身上清除幽灵珍珠。

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

回退 26.2 及以上的绊线行为改动。

- 类型: `string`
- 默认值: `false`
- 参考选项: `false`, `true`, `26.3`
- 分类: `HFUT`, `BUGFIX`

- `false`：当前版本的原版行为
- `true`：退回 26.2 之前的绊线行为
- `26.3`：仅 26.2 有效，将 0t 计划刻改为 1t

## 假人像真人一样 tick (fakePlayerTicksLikeRealPlayer)

让假人像真人玩家一样 tick：假人的 tick 移到网络阶段，动作包移到异步任务阶段。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `EXPERIMENTAL`

> 可只对单个假人生效：`/player <假人> tickingStage`（global / invert / origin / likeReal），或 GCA 假人菜单里的按钮；未重启服务器时对该假人持续有效。

## 幽灵末影珍珠修复 (ghostEnderPearlFix) `MC>=1.21.2`

修复幽灵末影珍珠修复（MC-306936）的几个主要表征。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `BUGFIX`

> 登录时丢弃重复生成的珍珠副本、关服时防止珍珠被区块重复保存、存档时只写入仍存在于世界中的珍珠。
>
> 装了 carpet-igny-addition 时本规则隐藏，由其同名规则接管。

## 幽灵末影珍珠反查 (ghostEnderPearlTrace) `MC>=1.21.2`

记录每次珍珠传送及其来源并按珍珠 UUID 累计次数，可用 `/pearltrace` 查看或清除（见[命令](commands.md)）。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `BUGFIX`, `COMMAND`

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

> 26.3 修复了矿车交互的返回值，假人骑矿车开始有 4t 使用间隔；开启后成功上矿车的返回值改回 PASS，等价 26.2- 行为。

## 属性修饰符清除延迟 (attributeModifierRemovalDelay) `MC>=26.3`

重新引入 MC-311022：快速切换装备时，属性修饰符的清除会晚 1 个游戏刻。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `EXPERIMENTAL`, `FEATURE`


## 地图玩家记录泄漏修复 (mapRegistryLeakFix)

原版会清除地图里的玩家记录，但 Lithium 等模组改写了这条清除路径，边缘情况没清掉，导致记录堆积、内存泄漏。

- 类型: `boolean`
- 默认值: `true`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `REMOTE_BUGFIX`

> 玩家一下线，就从所有已加载地图的名单中移除；假人不再被记入名单。
>
> 默认开启，开销很小：只在玩家下线时清理一次。

## [26.x-]Relogin泄漏修复 (reloginAvatarLeakFix) `1.21.2~1.21.11`

修复 Carpet-Org-Addition 1.21.x 上的重登泄漏：relogin 下线的旧假人没有真正断开，会一直残留在内存中。

- 类型: `boolean`
- 默认值: `true`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `REMOTE_BUGFIX`

> 下线任务会跳过已标记移除的假人，旧假人的断开流程因此永远不会执行。修复方式：强制执行断开，同时跳过已移除假人的重复存档
>
> 需要安装 carpet-org-addition；仅 1.21.2～1.21.11 存在此问题（org v1.46.0+ 已包含修复）。

## 挖掘疲劳修复 (miningFatigueDigSpeedFix) `MC<=26.2`

修复挖掘疲劳 III+ 的挖掘速度表：0.027 被写成了 0.0027、0.0081 被写成了 8.1E-4，挖掘疲劳 III 起挖掘速度比预期慢 10 倍。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `BUGFIX`

> 26.3 起原版已按 0.3^(等级+1) 计算并自行修复，本规则仅存在于 26.2 及以下，但保留了 26.2- 的高于IV的挖掘速度等同于IV这一行为。

## 末影龙垂直速度修复 (enderDragonVerticalVelocityFix)

修复末影龙飞向目标节点时垂直速度过低的问题（MC-272431）：19w08b（1.14）起垂直加速度系数 0.01 应为 0.1，末影龙难以爬升和俯冲，在目标节点周围来回盘旋。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `BUGFIX`

> 恢复 1.13 及更早的末影龙飞行行为，同时缓解 MC-271336、MC-271337、MC-197201。

## 村民即时升级 (villagerInstantLevelUp) `MC<=26.2`

向下移植 26.3 的村民交易界面行为，达到经验后当立即升级并解锁新等级交易，无需关闭界面再打开。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `FEATURE`

> 26.2- 原版将升级推迟 40 tick 且要求村民不在交易状态，26.3 起原版取消了此限制。
>
> 同时移植对交易中玩家立即计算并显示新等级的交易折扣。

## 村民界面价格即时同步 (villagerLivePriceSync) `MC<=26.2`

向下移植 26.3 的村民价格即时重算行为：交易界面打开时，补货、需求追平、流言传播、玩家声誉四类事件都会即时重算并修正交易价格。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `FEATURE`

## 幼年生物回避金蒲公英 (babyMobAvoidGoldenDandelion) `MC>=26.1.2`

能被金蒲公英抑制成长的幼年生物会逃离金蒲公英（含盆栽），类似猪灵害怕灵魂火。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `FEATURE`

> 犰狳、美西螈、蜜蜂、骆驼、猫、鸡、牛、海豚、驴、狐狸、发光鱿鱼、山羊、小恶魂（快乐恶魂幼体）、疣猪兽、马、羊驼、哞菇、骡、鹦鹉螺、豹猫、熊猫、猪、北极熊、兔子、绵羊、嗅探兽、鱿鱼、炽足兽、硫方怪、蝌蚪、行商羊驼、海龟、狼。
>
> 清单外的生物（含幼年村民）与成年个体不受影响。

## 骨粉催熟出瓜 (bonemealGrowMelons)

对成熟(age=7)的西瓜/南瓜茎使用骨粉可直接结出瓜。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `FEATURE`

> 原版成熟茎会直接拒绝骨粉，瓜只能等待随机刻才会生长；开启后成熟茎成为合法目标，放瓜完全复用原版随机刻逻辑。

## 地图玩家图标朝向 (mapPlayerIconRotation) `MC<=26.2`

向下移植 26.3 的地图改动：处于地图范围外的玩家，其边缘夹紧标记会随玩家朝向旋转。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `FEATURE`

> 地图中玩家的图标一直显示朝向。26.2 及以前图外玩家的标记不可见方向，26.3 起改为同样按朝向旋转。
>
> 旋转字节随地图数据包下发，原版客户端无需安装本模组即可看到效果。

## 创造模式无实体碰撞 (creativeNoEntityCollision)

创造模式玩家不再参与实体的碰撞计算。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `FEATURE`

> 非飞行状态也生效，但投射物命中、压力板触发等非碰撞交互不受影响。
