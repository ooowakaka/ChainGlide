# ChainGlide

[![License: GPL-3.0](https://img.shields.io/badge/License-GPL--3.0-blue.svg)](LICENSE)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-green.svg)](https://www.minecraft.net/)
[![NeoForge](https://img.shields.io/badge/NeoForge-21.1+-orange.svg)](https://neoforged.net/)
[![Create](https://img.shields.io/badge/Create-6.0+-red.svg)](https://modrinth.com/mod/create)

**ChainGlide** 是一个 Minecraft 1.21.1 NeoForge 客户端辅助 mod，专注于增强 [Create](https://modrinth.com/mod/create) 模组的锁链传动（Chain Conveyor）体验，并提供通用的功能扩展框架。

> ⚠️ **免责声明**：本 mod 为客户端辅助工具，部分功能可能违反服务器规则。请在单人或允许使用的服务器上使用，开发者不对任何封号负责。

---

## ✨ 功能

| 功能 | 命令Key | 说明 |
|------|---------|------|
| 🚀 锁链速度 | `chainspeed` | 修改锁链移动速度倍率（0.1x ~ 50x），可实时调整 |
| 🔒 锁链锁定 | `chainlock` | 锁定在锁链上不脱钩，按**右Shift**紧急脱离 |
| 🖐 空手上链 | `chainemptyhand` | 无需手持扳手，右键即可挂上锁链 |
| 🎯 虚空锁链 | `straightdash` | 悬挂在锁链上并向视角方向全速冲刺 |
| 🪶 无摔落伤害 | `nofall` | 免疫摔落伤害，从高处掉下也不掉血 |

### 附加

| 功能 | 说明 |
|------|------|
| 🖥 HUD 显示 | 屏幕右上角显示当前启用的功能列表，颜色可自定义 |
| ⌨ 按键绑定 | 每个功能自动生成可配置的快捷键（设置 → 控制 → ChainGlide） |
| 💾 配置保存 | 功能状态和设置自动保存到 `.minecraft/config/chainglide.json` |
| 🔌 扩展框架 | 其他 mod 可依赖 ChainGlide，零样板代码添加新功能（[扩展指南](#-扩展开发)） |

---

## 📦 安装

1. 安装 [Minecraft 1.21.1](https://www.minecraft.net/) + [NeoForge](https://neoforged.net/)
2. 安装 [Create 6.0+](https://modrinth.com/mod/create)
3. 下载 `chainglide-1.21.1-x.x.x.jar`
4. 放入 `.minecraft/mods/` 文件夹
5. 启动游戏

> 💡 本 mod 为**纯客户端** mod，只需装在客户端，服务器不需要安装。

---

## 🎮 使用方法

### 命令（自动 Tab 补全）

```
/ooowakaka chainspeed on|off         开关锁链速度
/ooowakaka chainspeed speed 5.0      设置速度倍率
/ooowakaka chainspeed info           查看详情

/ooowakaka chainlock on|off          开关锁链锁定
/ooowakaka chainemptyhand on|off     开关空手上链
/ooowakaka straightdash on|off       开关虚空锁链
/ooowakaka straightdash speed 3.0    设置冲刺速度
/ooowakaka nofall on|off             开关无摔落伤害

/ooowakaka list                      列出所有功能
/ooowakaka hud on|off                开关HUD显示
/ooowakaka hud color pink            切换HUD颜色
/ooowakaka hud speed 2.0             设置颜色动画速度
```

### 可用颜色

`pink` `blue` `green` `cyan` `red` `purple` `rainbow`

---

## 🔧 构建

```bash
git clone https://github.com/ooowakaka/ChainGlide.git
cd ChainGlide
./gradlew build
# 输出在 build/libs/chainglide-1.21.1-x.x.x.jar
```

要求：JDK 21+

---

## 🔌 扩展开发

ChainGlide 提供了一套功能框架，其他 mod 只需依赖它即可快速开发带 HUD/命令/按键/配置的新功能。

### 依赖配置

**build.gradle:**

```gradle
repositories {
    flatDir { dirs 'libs' }
}
dependencies {
    implementation fg.deobf('com.ooowakaka.ooowakaka:chainglide:1.21.1-1.0.1')
}
```

**neoforge.mods.toml:**

```toml
[[dependencies.你的modId]]
modId="chainglide"
type="required"
versionRange="[1.0,)"
ordering="AFTER"
side="CLIENT"
```

### 示例：3 步添加新功能

```java
// ① 写 Feature 类
public class MyFeature extends Feature {
    public final FloatSetting power;

    public MyFeature() {
        super("我的功能", "myfeature", "功能描述", FeatureCategory.MOVEMENT);
        power = new FloatSetting("强度", "强度倍率", 1.0f, 0.1f, 100f, 0.1f);
        addSetting(power);
    }

    @Override public void onTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            // 每帧逻辑...
        }
    }
}
```

```java
// ② 在 @Mod 构造函数里注册
@Mod(value = "yourmod", dist = Dist.CLIENT)
public class YourMod {
    public YourMod() {
        FeatureManager.INSTANCE.register(new MyFeature());
    }
}
```

```toml
# ③ neoforge.mods.toml 依赖 chainlide（见上方）
```

新功能自动获得：`/ooowakaka myfeature on|off` 命令 + Tab补全 + HUD显示 + 按键绑定 + 配置持久化。

> 📖 完整扩展文档：见源码 `Feature.java`、`FeatureManager.java`、`Setting.java`

---

## 📂 项目结构

```
src/main/java/com/ooowakaka/ooowakaka/
├── OoowakakaMod.java         主 Mod 入口
├── OoowakakaModClient.java   客户端初始化 & 功能注册
├── command/ModCommand.java   命令注册 (/ooowakaka)
├── config/ConfigStore.java   配置持久化 (JSON)
├── feature/
│   ├── Feature.java          功能抽象基类
│   ├── FeatureCategory.java  功能分类枚举
│   ├── FeatureManager.java   功能管理器 (单例)
│   ├── chain/                ← 内置功能实现
│   │   ├── ChainSpeedFeature.java
│   │   ├── ChainLockFeature.java
│   │   ├── ChainEmptyHandFeature.java
│   │   ├── StraightDashFeature.java
│   │   └── NoFallFeature.java
│   └── setting/              ← Setting 体系
│       ├── Setting.java
│       ├── FloatSetting.java
│       ├── IntSetting.java
│       ├── BooleanSetting.java
│       └── EnumSetting.java
├── hud/HudRenderer.java      功能列表 HUD 渲染
├── keybind/KeyBindManager.java 按键绑定管理
├── mixin/                    ← Create 模组 Mixin
│   ├── ChainConveyorRidingHandlerMixin.java
│   ├── ChainConveyorInteractionHandlerMixin.java
│   └── PlayerSkyhookRendererMixin.java
└── util/CreateReflect.java  Create 反射工具
```

---

## ⚖️ 许可证

GPL-3.0 © [ooowakaka](https://github.com/ooowakaka)

本 mod 使用 [Create](https://github.com/Creators-of-Create/Create) 的部分内部 API（通过 Mixin 和反射）。Create 使用 MIT 许可证。
