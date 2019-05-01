package dmillerw.menu.handler;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigHandler {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final General GENERAL = new General(BUILDER);
    public static final Visual VISUAL = new Visual(BUILDER);

    public static class General {
        public ForgeConfigSpec.BooleanValue toggle;
        public ForgeConfigSpec.BooleanValue rightClickToEdit;
        public ForgeConfigSpec.BooleanValue releaseToSelect;
        public ForgeConfigSpec.BooleanValue menuAnimation;
        public ForgeConfigSpec.ConfigValue menuButtonIcon;

        General(ForgeConfigSpec.Builder builder) {
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
        public ForgeConfigSpec.IntValue menuAlpha;
        public ForgeConfigSpec.IntValue menuRed;
        public ForgeConfigSpec.IntValue menuGreen;
        public ForgeConfigSpec.IntValue menuBlue;
        public ForgeConfigSpec.IntValue selectAlpha;
        public ForgeConfigSpec.IntValue selectRed;
        public ForgeConfigSpec.IntValue selectGreen;
        public ForgeConfigSpec.IntValue selectBlue;

        Visual(ForgeConfigSpec.Builder builder) {
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

    public static final ForgeConfigSpec spec = BUILDER.build();
}