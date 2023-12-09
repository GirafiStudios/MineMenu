package com.girafi.minemenu.util;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final General GENERAL = new General(BUILDER);
    public static final Visual VISUAL = new Visual(BUILDER);

    public static class General {
        public ModConfigSpec.BooleanValue toggle;
        public ModConfigSpec.BooleanValue rightClickToEdit;
        public ModConfigSpec.BooleanValue releaseToSelect;
        public ModConfigSpec.BooleanValue menuAnimation;
        public ModConfigSpec.ConfigValue<String> menuButtonIcon;

        General(ModConfigSpec.Builder builder) {
            builder.push("general");
            toggle = builder
                    .define("toggle", false);
            rightClickToEdit = builder
                    .define("rightClickToEdit", false);
            releaseToSelect = builder
                    .define("releaseToSelect", false);
            menuAnimation = builder
                    .define("menuAnimation", true);
            menuButtonIcon = builder
                    .define("menuButtonIcon", "minecraft:stone");
            builder.pop();
        }
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
            builder.push("visual");
            builder.push("menu");
            menuAlpha = builder
                    .defineInRange("alpha", 153, 0, 255);
            menuRed = builder
                    .defineInRange("red", 0, 0, 255);
            menuGreen = builder
                    .defineInRange("green", 0, 0, 255);
            menuBlue = builder
                    .defineInRange("blue", 0, 0, 255);
            builder.pop();
            builder.push("select");
            selectAlpha = builder
                    .defineInRange("alpha", 153, 0, 255);
            selectRed = builder
                    .defineInRange("red", 255, 0, 255);
            selectGreen = builder
                    .defineInRange("green", 0, 0, 255);
            selectBlue = builder
                    .defineInRange("blue", 0, 0, 255);
            builder.pop();
        }
    }

    public static final ModConfigSpec spec = BUILDER.build();
}