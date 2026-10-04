# libs/

compileOnly 用的本地二进制依赖，随仓库提交（社区惯例，参考 IGNY 的 malilib.jar 等）。

当前内容：[GugleCarpetAddition](https://modrinth.com/mod/gca)（GCA）各 MC 版本的子 jar，供假人菜单按钮兼容 mixin 编译使用（构建逻辑见 `common.gradle` 的 `gcaJarVariant`）。

无法改用 maven 坐标（`maven.modrinth:gca`）：Modrinth 上的 GCA 发布物是 wrapper jar——类文件全部内嵌在 `META-INF/jars/*.jar` 里，编译期不可见。

## 更新方法

从 GCA 的 wrapper 发布 jar（版本与 `gradle.properties` 的 `gca_compile_version` 一致）中提取 `META-INF/jars/` 下对应子 jar 放入本目录。本项目使用的替代关系（GCA 未单独构建的版本用相邻版本）与 `common.gradle` 保持一致：

| 本项目版本 | 使用的 GCA 子 jar |
|---|---|
| 1.21.3 | `mc1.21.2` |
| 1.21.8 | `mc1.21.6` |
| 26.3 | `mc26.3-snapshot-9` |
| 其余 | 与版本号相同的 `mc<版本>` |
