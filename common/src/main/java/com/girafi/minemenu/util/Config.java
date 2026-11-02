package com.girafi.minemenu.util;

import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final General GENERAL = new General(BUILDER);
    public static final Visual VISUAL = new Visual(BUILDER);

    public static class General {
        public ModConfigSpec.BooleanValue toggle;
        public ModConfigSpec.BooleanValue shiftClickToEdit;
        public ModConfigSpec.BooleanValue rightClickToEdit;
        public ModConfigSpec.BooleanValue releaseToSelect;
        public ModConfigSpec.BooleanValue menuAnimation;
        public ModConfigSpec.ConfigValue<String> menuButtonIcon;
        public ModConfigSpec.EnumValue<MenuScope> menuScope;

        General(ModConfigSpec.Builder builder) {
            builder.translation("minemenu.configuration.general").push("general");
            toggle = builder
                    .comment("Press the menu key once to open the menu and again to close it, instead of holding it down")
                    .translation("minemenu.configuration.toggle")
                    .define("toggle", false);
            shiftClickToEdit = builder
                    .comment("Shift + left-click a button to edit")
                    .translation("minemenu.configuration.shiftClickToEdit")
                    .define("shiftClickToEdit", true);
            rightClickToEdit = builder
                    .comment("Right-click a button to edit")
                    .translation("minemenu.configuration.rightClickToEdit")
                    .define("rightClickToEdit", false);
            releaseToSelect = builder
                    .comment("Selects the button under the mouse when the menu key is released")
                    .translation("minemenu.configuration.releaseToSelect")
                    .define("releaseToSelect", false);
            menuAnimation = builder
                    .comment("Animate the menu when it opens")
                    .translation("minemenu.configuration.menuAnimation")
                    .define("menuAnimation", true);
            menuButtonIcon = builder
                    .comment("Item ID shown as the icon for empty buttons, e.g. minecraft:stone")
                    .translation("minemenu.configuration.menuButtonIcon")
                    .define("menuButtonIcon", "minecraft:stone", o -> o instanceof String id && Identifier.tryParse(id) != null);
            menuScope = builder
                    .comment("GLOBAL: one menu shared by all worlds and servers (old method)",
                            "PER_WORLD: singleplayer worlds and servers has its own menu. Starting as a copy of the global menu.json, if present.",
                            "Takes effect the next time you join a world or server.")
                    .translation("minemenu.configuration.menuScope")
                    .defineEnum("menuScope", MenuScope.PER_WORLD);
            builder.pop();
        }
    }

    public enum MenuScope {
        GLOBAL,
        PER_WORLD
    }

    public static class Visual {
        public ModConfigSpec.IntValue menuAlpha;
        public ModConfigSpec.IntValue menuRed;
        public ModConfigSpec.IntValue menuGreen;
        public ModConfigSpec.IntValue menuBlue;
        public ModConfigSpec.IntValue selectAlpha;
        public ModConfigSpec.IntValue selectRed;
        public ModConfigSpec.IntValue selectGreen;
        public ModConfigSpec.IntValue selectBlue;

        Visual(ModConfigSpec.Builder builder) {
            builder.translation("minemenu.configuration.visual").push("visual");
            builder.comment("Menu background color").translation("minemenu.configuration.menu").push("menu");
            menuAlpha = builder
                    .translation("minemenu.configuration.menuAlpha")
                    .defineInRange("alpha", 153, 0, 255);
            menuRed = builder
                    .translation("minemenu.configuration.menuRed")
                    .defineInRange("red", 0, 0, 255);
            menuGreen = builder
                    .translation("minemenu.configuration.menuGreen")
                    .defineInRange("green", 0, 0, 255);
            menuBlue = builder
                    .translation("minemenu.configuration.menuBlue")
                    .defineInRange("blue", 0, 0, 255);
            builder.pop();
            builder.comment("Selected button color").translation("minemenu.configuration.select").push("select");
            selectAlpha = builder
                    .translation("minemenu.configuration.selectAlpha")
                    .defineInRange("alpha", 153, 0, 255);
            selectRed = builder
                    .translation("minemenu.configuration.selectRed")
                    .defineInRange("red", 255, 0, 255);
            selectGreen = builder
                    .translation("minemenu.configuration.selectGreen")
                    .defineInRange("green", 0, 0, 255);
            selectBlue = builder
                    .translation("minemenu.configuration.selectBlue")
                    .defineInRange("blue", 0, 0, 255);
            builder.pop(2);
        }
    }

    public static final ModConfigSpec spec = BUILDER.build();
}