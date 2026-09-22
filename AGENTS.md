# AGENTS.md

Guidelines and architecture guide for AI coding agents working on **EML** (_Enchantments Max Level and Descriptions_).

---

## 1. Project Overview

**EML** (_Enchantments Max Level and Descriptions_, mod ID `eml`) is a lightweight, client-side **Fabric** mod for **Minecraft 26.2 and 26.3** (built on Java 25+) that enhances enchantment tooltips:

- Shows levels as `current/max` (e.g. `Sharpness V/V ★`, `Looting I/III`).
- Omits level numbers for single-level enchantments (`Mending ★`, `Silk Touch ★`).
- Displays concise enchantment descriptions and compact vanilla 16x16 item applicability icons (e.g., swords, axes, bows, shields) in tooltips via the native inventory item renderer.
- Keeps multi-enchantment item tooltips compact by hiding descriptions until **Shift** is held.
- Unfolds full textual applicability and extra details (e.g., undead mob lists for Smite) when **Shift** is pressed.

---

## 2. Technology Stack & Environment

- **Language:** Java 25 (`options.release = 25`, `JavaVersion.VERSION_25`).
- **Build System:** Gradle 9.x with Gradle Wrapper (`./gradlew`).
- **Mod Loader:** Fabric Loader (`0.19.5+`).
- **Loom:** Fabric Loom (`net.fabricmc.fabric-loom` version `1.17.21`).
- **Bytecode Manipulation:** SpongePowered Mixin + LlamaLad7 MixinExtras (`@Local`, `@ModifyReturnValue`, etc.).
- **Environment:** Strictly client-side (`"environment": "client"` in `fabric.mod.json`).

---

## 3. Project Structure & Responsibilities

```
src/main/
├── java/io/github/noiryoo/eml/
│   ├── mixin/
│   │   ├── EnchantmentMixin.java (level and maximum marker)
│   │   ├── ItemEnchantmentsMixin.java (details and Shift behavior)
│   │   └── ClientTooltipComponentMixin.java (native icon row conversion)
│   └── tooltip/
│       ├── EnchantmentApplicability.java (supported item grouping and text)
│       ├── EnchantmentTooltip.java (description, icon row and detailed view)
│       ├── ItemIconsLine.java (item payload with a textual fallback)
│       └── ItemIconsTooltip.java (native item rendering and row layout)
└── resources/
    ├── fabric.mod.json
    ├── eml.mixins.json
    └── assets/eml/
        ├── icon.png
        └── lang/
            ├── en_us.json
            └── ru_ru.json
```

---

## 4. Coding & Architecture Guidelines

1. **Language & Documentation:**
   - All code, comments, JSDoc/Javadoc, variable names, and git commit descriptions must be in **English**.
   - Communication with the user in chat should follow user preferences (Russian).
2. **Git Discipline:**
   - **Never run `git commit` or `git push` automatically.** All changes are staged/prepared locally for the user to review and commit.
3. **Simplicity & YAGNI:**
   - Prefer simple, declarative implementations. Do not introduce speculative abstractions, factories, or wrapper layers without immediate necessity.
   - Keep handwritten source files concise (ideally under 200 lines, strictly under 300 lines).
4. **Naming Conventions:**
   - Never use `Custom` as a prefix or suffix in classes, methods, or variables (e.g. use `EnchantmentTooltip` or `ApplicabilityMapper`, never `CustomTooltip`).
5. **Native Item Icons:**
   - Use `GuiGraphicsExtractor.item` to render real item stacks, including shields and modded item models.
   - `ItemIconsLine` preserves its payload through `Component.getVisualOrderText`; `ClientTooltipComponentMixin` converts it to `ItemIconsTooltip`.
   - Keep a textual fallback for search and narration. Do not introduce font overrides, private-use glyphs, or standalone item icon PNGs.
   - Expand broad durability and armor categories into concrete subgroups for icons. Preserve exact items when a whole group does not apply.
   - Preserve Shift behavior and keep icon extraction on the render thread.

---

## 5. Build & Verification Commands

- **Compile & Build Mod:**

  ```bash
  # Build for Minecraft 26.2 (default)
  ./gradlew build

  # Build for Minecraft 26.3
  ./gradlew build -Pminecraft_version=26.3
  ```

  Produces `build/libs/eml-1.0.0+<minecraft_version>.jar`.

- **Run Test Client:**

  ```bash
  ./gradlew runClient
  ```

  Launches the Minecraft client in dev environment using the `run/` directory.

- **In-Game Verification (Creative Mode):**

  ```mcfunction
  /give @s minecraft:enchanted_book[minecraft:stored_enchantments={"minecraft:looting":1}]
  /give @s minecraft:enchanted_book[minecraft:stored_enchantments={"minecraft:sharpness":5}]
  /give @s minecraft:enchanted_book[minecraft:stored_enchantments={"minecraft:mending":1}]
  /give @s minecraft:enchanted_book[minecraft:stored_enchantments={"minecraft:smite":5}]
  /give @s minecraft:diamond_sword[minecraft:enchantments={"minecraft:sharpness":5,"minecraft:fire_aspect":2,"minecraft:unbreaking":3}]
  ```

  - Verify `Mending ★` has no `I/I`.
  - Verify `Sharpness V/V ★` has native item icons for its supported categories.
  - Verify multi-enchanted sword hides descriptions until **Shift** is held.
  - Verify holding Shift reveals extra details (e.g. undead mob list for Smite).
