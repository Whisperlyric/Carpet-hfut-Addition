# Carpet-Hfut-Addition

[![License](https://img.shields.io/badge/License-LGPL%203.0-blue.svg)](https://choosealicense.com/licenses/lgpl-3.0/)

**中文** | [English](README_en.md)

基于 [Fabric Carpet](https://github.com/gnembon/fabric-carpet) 的扩展模组，由 HFUT Minecraft 社区开发。

## 依赖

| 名称                | 类型 | 链接                                                                                                                   |
|-------------------|----|----------------------------------------------------------------------------------------------------------------------|
| Carpet            | 必须 | [Modrinth](https://modrinth.com/mod/carpet) &#124; [CurseForge](https://www.curseforge.com/minecraft/mc-mods/carpet) |
| Fabric API        | 必须 | [FabricMC](https://fabricmc.net/)                                                                                    |
| MixinExtras       | 内置 | [GitHub](https://github.com/SpongePowered/MixinExtras)                                                               |
| conditional-mixin | 内置 | [GitHub](https://github.com/Fallen-Breath/conditional-mixin)                                                         |
| GCA               | 可选 | [GitHub](https://github.com/Gu-ZT/gugle-carpet-addition)（提供假人菜单里的 tick 阶段反转按钮）                                       |

## 版本支持

| 游戏版本                 | 开发状态 |
|----------------------|------|
| 1.21(.1)（主版本）        | 维护中  |
| 1.21.3 ~ 1.21.11     | 维护中  |
| 26.1.2 / 26.2 / 26.3 | 维护中  |

## 文档

- [规则](docs/rules.md)
- [命令](docs/commands.md)
- [API](docs/api.md)

## 下载

- [GitHub Releases](https://github.com/Whisperlyric/Carpet-Hfut-Addition/releases/latest)

## 构建

本项目使用 [ReplayMod Preprocessor](https://github.com/ReplayMod/preprocessor)（上游版，非 fork）实现多版本共源，主版本为 `1.21.1`（见 `versions/mainProject`），其余版本源码由预处理器自动生成。`versions/mapping-*.txt` 以严格 ExtraMapping 格式参与预处理（`strictExtraMappings = true`），仅在自动推导的映射不够用（如类/成员改名无法从 MC jar 推导）时才需要手写条目，语法见各文件头部注释。

```bash
# 构建所有版本并把 jar 收集到 build/libs/
./gradlew buildAndGather

# 构建单个版本
./gradlew :1.21.1:build
./gradlew :26.2:build

# 运行客户端 / 服务端
./gradlew :1.21.1:runClient
./gradlew :1.21.1:runServer
```

> 26.x 版本为 unobfuscated（命名映射直连），需要 JDK 25；1.21.x 版本需要 JDK 21。用 JDK 25 运行 Gradle 本身即可构建全部版本。

## 项目结构

```
Carpet-Hfut-Addition-master/
├── build.gradle            # 预处理器版本节点与链接配置
├── common.gradle           # 各版本子项目共用的构建逻辑
├── settings.gradle         # 读取 settings.json 注册版本子项目
├── settings.json           # 支持的版本列表
├── gradle.properties       # 模组基本信息
├── libs/                   # 本地依赖 jar（GCA，编译期经 flatDir 引用）
├── docs/                   # 规则 / 命令 / API 文档（中英）
├── .github/workflows/      # CI：dev 构建 / mixin 审计 / 多版本发布
├── versions/
│   ├── mainProject         # 主版本名（1.21.1）
│   ├── mapping-*.txt       # 版本间额外映射（严格 ExtraMapping 格式，按需手写）
│   ├── 1.21.1/             # 各版本子项目
│   │   └── gradle.properties
│   └── ...
└── src/main/
    ├── java/dev/whisperlyric/carpet_hfut_addition/
    │   ├── HFUTServerMod.java      # ModInitializer 入口
    │   ├── HFUTServer.java         # CarpetExtension（规则/日志/命令/翻译注册）
    │   ├── HFUTSettings.java       # Carpet 规则（@Rule 注解）
    │   ├── FakePlayerTickStageSettings.java  # 与 TIS 同名规则，拆分单独注册
    │   ├── GhostPearlFixSettings.java        # 与 IGNY 同名规则，拆分单独注册
    │   ├── api/                    # 供其他模组调用的公开 API
    │   ├── commands/               # /hfut、/pearltrace、/tradeseq 注册与 /player tickingStage 注入
    │   ├── helpers/                # 各规则的处理逻辑，及 GCA 菜单按钮兼容
    │   ├── logger/                 # 注解式 /log 日志框架
    │   │   ├── HFUTLoggers.java    #   声明 @Logger 字段即注册
    │   │   ├── annotation/Logger.java
    │   │   └── callback/LoggerCallback.java
    │   ├── utils/                  # 分类常量 / 翻译加载 / 文本 / 权限 / Mixin 插件
    │   └── mixins/                 # 按 mixins/rule/<规则名>/ 组织，参考 TIS/IGNY
    │       └── carpet/CarpetServerMixin.java  # 诊断 mixin（记录扩展注册）
    └── resources/
        ├── fabric.mod.json
        ├── carpet-hfut-addition.mixins.json       # 挂载 MixinConfigPlugin
        ├── carpet-hfut-addition.accesswidener     # AW（默认为空，按需添加）
        └── assets/carpet-hfut-addition/
            ├── icon.png
            └── lang/{en_us,zh_cn}.json            # 规则描述 / 分类 / 文案
```

## License

LGPL-3.0
