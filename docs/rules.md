# 规则

**提示：可以使用`Ctrl+F`快速查找自己想要的规则**

## 修改假人 tick 阶段权限 (commandPlayerTickingStage)

设置谁可以使用 `/player <假人> tickingStage` 调整假人的 tick 阶段。

- 类型: `string`
- 默认值: `ops`
- 参考选项: `false`, `true`, `ops`, `0`, `1`, `2`, `3`, `4`
- 分类: `HFUT`, `COMMAND`, `FEATURE`

## 弱加载指令权限 (commandSimpleLazyChunk) `🐛Beta`

设置谁可以使用 `/hfut lazychunk`：把区块固定在弱加载状态（仅方块刻运行、实体冻结），即使附近没有玩家也保持加载，并阻止其升级为完全加载（见[命令](commands.md)）。标记仅保存在内存中，重启后失效。

- 类型: `string`
- 默认值: `ops`
- 参考选项: `false`, `true`, `ops`, `0`, `1`, `2`, `3`, `4`
- 分类: `HFUT`, `COMMAND`, `FEATURE`

## 假人离线开箱权限 (commandFakePlayerOpenStorage)

设置谁可以使用 `/player <假人> open inventory|enderchest`：在假人不在线时打开它的背包或末影箱进行编辑（见[命令](commands.md)）。

- 类型: `string`
- 默认值: `ops`
- 参考选项: `false`, `true`, `ops`, `0`, `1`, `2`, `3`, `4`
- 分类: `HFUT`, `COMMAND`, `FEATURE`

## 交易序列操纵 (commandTradeSeq) `🐛Beta` `MC>=26.1`

设置谁可以使用 `/tradeseq`：查询与移动村民交易刷新所用的命名随机序列（见[命令](commands.md)）。

- 类型: `string`
- 默认值: `ops`
- 参考选项: `false`, `true`, `ops`, `0`, `1`, `2`, `3`, `4`
- 分类: `HFUT`, `COMMAND`, `FEATURE`

## 绊线行为修复 (tripwireIgnoreDepartures) `MC>=1.21.9`

用较为激进的方式修复绊线在实体穿过时多次触发上升沿的问题（MC-305475）。

26.2+需要规则`tripwireRestore`作为前置。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `BUGFIX`

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

可只对单个假人生效：`/player <假人> tickingStage`，或 GCA 假人菜单里的按钮；未重启服务器时对该假人持续有效。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `EXPERIMENTAL`

## 幽灵末影珍珠修复 (ghostEnderPearlFix) `MC>=1.21.2`

修复幽灵末影珍珠修复（MC-306936）的几个主要表征。

登录时丢弃重复生成的珍珠副本、关服时防止珍珠被区块重复保存、存档时只写入仍存在于世界中的珍珠。

同时安装 Carpet Igny Addition 时本规则隐藏，由其同名规则接管。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `BUGFIX`

## 幽灵末影珍珠反查 (ghostEnderPearlTrace) `MC>=1.21.2`

开启后记录每次珍珠传送及其来源并按珍珠 UUID 累计次数，可用 `/pearltrace` 查看或清除（见[命令](commands.md)）。本规则只管**是否记录**，命令能否使用由下面的 `commandPearlTrace` / `commandPearlTracePurge` 控制。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `BUGFIX`

## 幽灵珍珠反查命令权限 (commandPearlTrace)

设置谁可以使用 `/pearltrace list`/`show` 查看幽灵珍珠残留。

需要规则`ghostEnderPearlTrace=true`作为记录前置，本规则只控制命令权限。

- 类型: `string`
- 默认值: `ops`
- 参考选项: `false`, `true`, `ops`, `0`, `1`, `2`, `3`, `4`
- 分类: `HFUT`, `COMMAND`, `FEATURE`

## 幽灵珍珠清除命令权限 (commandPearlTracePurge)

设置谁可以使用 `/pearltrace purge` 从在线玩家身上清除幽灵珍珠。

需先规则`ghostEnderPearlTrace=true` 作为记录前置，本规则只控制命令权限。

- 类型: `string`
- 默认值: `ops`
- 参考选项: `false`, `true`, `ops`, `0`, `1`, `2`, `3`, `4`
- 分类: `HFUT`, `COMMAND`, `FEATURE`

## 清除过门潜影弹 (shulkerBulletPortalCleanup) `MC>=1.21.2`

让潜影弹在穿过传送门切换维度时被清除。

潜影弹的制导目标是跨维度解析的，过门后会朝切换维度前的坐标飞行。

在传送发生前清除，同时免去目的侧的区块加载。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `FEATURE`

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

将26.3+成功上矿车的返回值改回26.2-的PASS。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `EXPERIMENTAL`, `FEATURE`

## 属性修饰符清除延迟 (attributeModifierRemovalDelay) `MC>=26.3`

重新引入 MC-311022：快速切换装备时，属性修饰符的清除会晚 1 个游戏刻。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `EXPERIMENTAL`, `FEATURE`

## 地图玩家记录泄漏修复 (mapRegistryLeakFix)

原版会清除地图里的玩家记录，但 Lithium 等模组改写了这条清除路径，边缘情况下未能正确清除，导致记录堆积、内存泄漏。

玩家一下线，就从所有已加载地图的名单中移除；假人不再被记入名单。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `REMOTE_BUGFIX`

## [26.x-]Relogin泄漏修复 (reloginAvatarLeakFix) `1.21.2~1.21.11`

修复 Carpet-Org-Addition 1.21.x 上的重登泄漏：relogin 下线的旧假人没有真正断开，会一直残留在内存中。

下线任务会跳过已标记移除的假人，旧假人的断开流程因此永远不会执行。修复方式：强制执行断开，同时跳过已移除假人的重复存档

需要安装 carpet-org-addition；仅 1.21.2～1.21.11 存在此问题（org v1.46.0+ 已包含修复）。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `REMOTE_BUGFIX`

## 挖掘疲劳修复 (miningFatigueDigSpeedFix) `MC<=26.2`

修正挖掘疲劳III及以上的挖掘速度百分比。

26.3+ 原版修正公式为 0.3^(等级+1)，本规则仅存在于26.2-，但保留了26.2-的高于IV的挖掘速度等同于IV这一行为。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `BUGFIX`

## 末影龙垂直速度修复 (enderDragonVerticalVelocityFix)

修复末影龙飞向目标节点时垂直速度过低的问题(MC-272431)

将末影龙的垂直加速度系数从19w08b(1.14)起的0.01改为0.1，从而恢复 1.13 及更早的末影龙飞行行为。
- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `BUGFIX`

## 村民即时升级 (villagerInstantLevelUp) `MC<=26.2`

向下移植 26.3 的村民交易界面行为，达到经验后当立即升级并解锁新等级交易，无需关闭界面再打开。

同时移植了对交易中玩家立即计算并显示新等级的交易折扣的特性。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `FEATURE`

## 村民界面价格即时同步 (villagerLivePriceSync) `MC<=26.2`

向下移植 26.3 的村民价格即时重算行为：交易界面打开时，补货、需求追平、流言传播、玩家声誉四类事件都会即时重算并修正交易价格。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `FEATURE`

## 幼年生物回避金蒲公英 (babyMobAvoidGoldenDandelion) `MC>=26.1`

能被金蒲公英抑制成长的幼年生物会逃离金蒲公英（含盆栽、玩家手持），类似猪灵害怕灵魂火。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `FEATURE`

## 已锁生长的幼年生物不回避金蒲公英 (babyMobAvoidGoldenDandelionIgnoreAgeLocked) `MC>=26.1`

被金蒲公英锁住生长的幼年生物不再逃离金蒲公英。

需要规则 `babyMobAvoidGoldenDandelion` 作为前置。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `FEATURE`

## 骨粉催熟出瓜 (bonemealGrowMelons)

对成熟(age=7)的西瓜/南瓜茎使用骨粉可直接结出瓜。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `FEATURE`

## 地图玩家图标朝向 (mapPlayerIconRotation) `MC<=26.2`

向下移植 26.3 的地图改动：处于地图范围外但在追踪范围内的玩家，其标记显示会随玩家朝向旋转。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `FEATURE`

## 创造模式无实体碰撞 (creativeNoEntityCollision)

创造模式玩家不再参与实体的碰撞计算。

非飞行状态也生效，但投射物命中、压力板触发等非碰撞交互不受影响。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `FEATURE`

## 风弹不触发红石元件 (windChargeNoRedstoneActivation)

风弹爆炸不再触发按钮、拉杆、门、活板门与栅栏门。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `FEATURE`

## 风弹不激怒蜂群 (windChargeNoBeehiveAnger) `MC>=1.21.2`

风弹爆炸不再因蜂箱激怒周围的蜜蜂。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `FEATURE`

## 风弹不影响非生物实体 (windChargeNoNonLivingKnockback)

风弹爆炸不再对非生物实体产生击退。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `FEATURE`

## 灵魂疾行不消耗靴子耐久 (soulSpeedNoBootDamage)

灵魂疾行附魔不再在使用时消耗靴子耐久。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `FEATURE`

## 耐久序列回归玩家 (durabilityRngFollowsPlayer) `🐛Beta`

耐久附魔的耐久损耗判定使用玩家自己的随机序列而非世界随机序列。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `FEATURE`

## 命名流浪商人不消失 (namedWanderingTraderPersistence)

被命名为指定字符串的流浪商人不会自然消失。

- 类型: `string`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `FEATURE`

- `false`：关闭
- `true`：任意自定义命名的流浪商人不消失
- 其他值：仅名称与该值完全相同的流浪商人不消失

## 信标效果向下无限延伸 (beaconDownwardsInfiniteRange)

信标的效果判定区域向下无限延伸，水平范围不变。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `FEATURE`

- `false`：关闭（原版行为，效果区域仅向上方延伸）
- `true`：信标正下方任意深度的玩家同样获得其效果

## [1.21.9+]海豚上船恢复 (dolphinBoatRidingRestore) `MC>=1.21.9`

恢复 1.21.9 前海豚可进入船的行为。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `FEATURE`

- `false`：关闭
- `true`：海豚碰撞船时照旧进入船内

## 坚毅紫水晶母岩 (resilientBuddingAmethyst)

将紫水晶母岩的硬度与爆炸抗性改为 1557。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `FEATURE`

- `false`：关闭（原版行为）
- `true`：紫水晶母岩变为坚毅方块

## 易碎玻璃 (fragileGlass)

将玻璃、染色玻璃、遮光玻璃和玻璃板的合适挖掘工具设为锄头。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `FEATURE`

- `false`：关闭（原版行为，无合适工具）
- `true`：锄头可加速破坏上述方块，效率附魔随之生效；掉落行为不变

## [1.21.x]石头可切圆石变种 (stoneCutCobblestoneVariants) `MC<26.1`

在切石机中，石头和深板岩可切出相应的圆石及其圆石变种建筑方块。

向下移植 26.1 的切石机配方

切换规则时会触发一次数据包重载（与 `/reload` 相同）以重建配方并重发配方包，即时生效。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `FEATURE`

- `false`：关闭
- `true`：注册上述切石机配方

## 便捷发射器合成 (easyDispenserCrafting)

使发射器可以在弓的合成配方中心空位放置一个投掷器来合成。

该配方没有对应的解锁进度，不会出现在配方书中，需手动摆放材料。

切换规则时会触发一次数据包重载（与 `/reload` 相同）以重建配方并重发配方包，即时生效。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `HFUT`, `FEATURE`

- `false`：关闭
- `true`：注册上述合成配方
