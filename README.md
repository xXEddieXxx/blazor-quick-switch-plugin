# Blazor Quick Switch

A JetBrains Rider plugin that lets you quickly switch between related Blazor component files using a single keyboard shortcut.

Inspired by the [Angular CLI QuickSwitch](https://plugins.jetbrains.com/plugin/10996-angular-cli-quickswitch) plugin.

## Supported file types

| Extension      | Description              |
|----------------|--------------------------|
| `.razor`       | Razor component markup   |
| `.razor.cs`    | Code-behind              |
| `.razor.css`   | Scoped CSS               |
| `.razor.js`    | JavaScript interop       |

## Usage

1. Open any Blazor component file in Rider
2. Press **Alt+S** to cycle to the next related file
3. Press again to continue cycling

The plugin skips file types that don't exist for the current component. For example, if you only have `Counter.razor` and `Counter.razor.cs`, it will toggle between those two.

The shortcut can be customized via **Settings > Keymap** and searching for "Blazor Quick Switch".

## Installation

- **From JetBrains Marketplace:** Search for "Blazor Quick Switch" in **Settings > Plugins > Marketplace**
- **From disk:** Download the `.zip` from [Releases](https://github.com/xXEddieXxx/blazor-quick-switch-plugin/releases) and install via **Settings > Plugins > Install Plugin from Disk...**

## Building from source

```bash
./gradlew buildPlugin
```

The plugin zip will be in `build/distributions/`.
