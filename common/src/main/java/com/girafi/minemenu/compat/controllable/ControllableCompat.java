package com.girafi.minemenu.compat.controllable;

import com.girafi.minemenu.Constants;
import com.girafi.minemenu.data.menu.MenuItem;
import com.girafi.minemenu.data.menu.RadialMenu;
import com.girafi.minemenu.gui.RadialMenuScreen;
import com.girafi.minemenu.helper.RadialMenuInput;
import com.girafi.minemenu.helper.AngleHelper;
import com.mrcrayfish.controllable.Config;
import com.mrcrayfish.controllable.Controllable;
import com.mrcrayfish.controllable.client.Action;
import com.mrcrayfish.controllable.client.binding.BindingRegistry;
import com.mrcrayfish.controllable.client.binding.ButtonBinding;
import com.mrcrayfish.controllable.client.binding.context.InScreenContext;
import com.mrcrayfish.controllable.client.binding.context.rule.ContextRule;
import com.mrcrayfish.controllable.client.binding.context.rule.HasPlayerRule;
import com.mrcrayfish.controllable.client.binding.context.rule.HasScreenRule;
import com.mrcrayfish.controllable.client.binding.handlers.OnPressHandler;
import com.mrcrayfish.controllable.client.input.Buttons;
import com.mrcrayfish.controllable.client.input.Controller;
import com.mrcrayfish.controllable.client.overlay.ActionHintOverlay;
import com.mrcrayfish.controllable.client.overlay.OverlayRenderer;
import com.mrcrayfish.controllable.client.settings.ActionVisibility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ControllableCompat implements RadialMenuInput.Source {
    private static final String CATEGORY = "key.categories.minemenu";
    private static final float STICK_THRESHOLD = 0.5F;
    private static final ControllableCompat INSTANCE = new ControllableCompat();

    private ButtonBinding openBinding;
    private ButtonBinding selectBinding;
    private ButtonBinding editBinding;
    private ButtonBinding editCategoryBinding;
    /**
     * The button hints shown by Controllable in the bottom corner.
     */
    @Nullable
    private Map<Integer, Action> hintActions;
    private boolean hintLookupDone;
    private boolean engaged;
    private double angle = Double.NaN;

    public static void init() {
        // Tried before Controllable's own bindings, falls through to them when the radial menu can not be opened
        RadialMenuContext anywhere = new RadialMenuContext(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "in_game_any_screen"), Set.of(HasPlayerRule.get()));
        RadialMenuContext inRadialMenu = new RadialMenuContext(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "in_radial_menu"), Set.of(HasPlayerRule.get(), HasScreenRule.of(Set.<Class<? extends Screen>>of(RadialMenuScreen.class))));

        INSTANCE.openBinding = new ButtonBinding(Buttons.DPAD_LEFT, "minemenu.controllable.open_menu", CATEGORY, anywhere, OnPressHandler.create(context -> {
            Screen screen = context.minecraft().gui.screen();
            if (screen == null || screen instanceof RadialMenuScreen) {
                // Opening and closing is handled by KeyboardHandlerHelper, through isOpenHeld()
                return Optional.of(() -> INSTANCE.engaged = true);
            }
            return Optional.empty();
        }));
        ButtonBinding select = new ButtonBinding(Buttons.A, "minemenu.controllable.select", CATEGORY, inRadialMenu, OnPressHandler.create(context -> Optional.of(() -> {
            INSTANCE.engaged = true;
            RadialMenuScreen.INSTANCE.selectHovered();
        })));
        ButtonBinding edit = new ButtonBinding(Buttons.X, "minemenu.controllable.edit", CATEGORY, inRadialMenu, OnPressHandler.create(context -> Optional.of(() -> {
            INSTANCE.engaged = true;
            // Nothing is selected until the stick has been moved, which would otherwise count as the center
            if (!Double.isNaN(INSTANCE.getAngle())) {
                RadialMenuScreen.INSTANCE.editHovered(true);
            }
        })));
        ButtonBinding editCategory = new ButtonBinding(Buttons.Y, "minemenu.controllable.edit_category", CATEGORY, inRadialMenu, OnPressHandler.create(context -> Optional.of(() -> {
            INSTANCE.engaged = true;
            RadialMenuScreen.INSTANCE.editCategory();
        })));

        INSTANCE.selectBinding = select;
        INSTANCE.editBinding = edit;
        INSTANCE.editCategoryBinding = editCategory;

        BindingRegistry registry = Controllable.getBindingRegistry();
        registry.register(INSTANCE.openBinding);
        registry.register(select);
        registry.register(edit);
        registry.register(editCategory);
        RadialMenuInput.register(INSTANCE);
    }

    @Override
    public boolean isActive() {
        this.updateAngle();
        return this.engaged && Controllable.getController() != null;
    }

    @Override
    public double getAngle() {
        this.updateAngle();
        return this.angle;
    }

    @Override
    public boolean isOpenHeld() {
        Controller controller = Controllable.getController();
        return controller != null && !this.openBinding.isUnbound() && controller.isButtonPressed(this.openBinding.getButton());
    }

    @Override
    public void onMenuClosed() {
        this.engaged = false;
        this.angle = Double.NaN;
    }

    /**
     * Adds the radial menu's buttons to Controllable's button hints. Controllable rebuilds the hints at the start of each client tick, so this has to run at the end of the client tick.
     */
    @Override
    public void tick() {
        Minecraft mc = Minecraft.getInstance();
        if (!RadialMenuScreen.active || mc.gui.hud.isHidden() || Config.CLIENT.options.showButtonHints.get() == ActionVisibility.NONE) {
            return;
        }
        Map<Integer, Action> actions = this.getHintActions();
        if (actions == null) {
            return;
        }

        if (!Double.isNaN(this.getAngle())) {
            int index = RadialMenuInput.getHoveredIndex();
            MenuItem item = index >= 0 ? RadialMenu.getActiveArray()[index] : null;
            if (index >= 0) {
                addHint(actions, this.selectBinding, item == null ? "minemenu.controllable.hint.add" : "minemenu.controllable.hint.select", Action.Side.LEFT);
            }
            if (item != null) {
                addHint(actions, this.editBinding, "minemenu.controllable.hint.edit", Action.Side.LEFT);
            }
        }
        addHint(actions, this.editCategoryBinding, "minemenu.controllable.hint.edit_category", Action.Side.RIGHT);
    }

    private static void addHint(Map<Integer, Action> actions, ButtonBinding binding, String translationKey, Action.Side side) {
        if (!binding.isUnbound()) {
            actions.put(binding.getButton(), new Action(Component.translatable(translationKey), side));
        }
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private Map<Integer, Action> getHintActions() {
        if (!this.hintLookupDone) {
            this.hintLookupDone = true;
            try {
                Field overlaysField = OverlayRenderer.class.getDeclaredField("OVERLAYS");
                overlaysField.setAccessible(true);
                for (Object overlay : (List<?>) overlaysField.get(null)) {
                    if (overlay instanceof ActionHintOverlay) {
                        Field actionsField = ActionHintOverlay.class.getDeclaredField("actions");
                        actionsField.setAccessible(true);
                        this.hintActions = (Map<Integer, Action>) actionsField.get(overlay);
                    }
                }
                if (this.hintActions == null) {
                    Constants.LOG.warn("Could not find Controllable's button hint overlay, Mine Menu button hints are disabled");
                }
            } catch (ReflectiveOperationException | RuntimeException e) {
                Constants.LOG.warn("Could not access Controllable's button hints, Mine Menu button hints are disabled", e);
            }
        }
        return this.hintActions;
    }

    private void updateAngle() {
        Controller controller = Controllable.getController();
        if (controller == null || !RadialMenuScreen.active) {
            return;
        }
        float x = controller.getRThumbStickXValue();
        float y = controller.getRThumbStickYValue();
        if (x * x + y * y >= STICK_THRESHOLD * STICK_THRESHOLD) {
            this.angle = AngleHelper.correctAngle(-Math.toDegrees(Math.atan2(x, y)));
            this.engaged = true;
        }
    }

    private static class RadialMenuContext extends InScreenContext {
        private final Set<ContextRule> rules;

        RadialMenuContext(Identifier id, Set<ContextRule> rules) {
            super(id);
            this.rules = rules;
        }

        @Override
        public Set<ContextRule> createRules() {
            return this.rules;
        }

        @Override
        public int priority() {
            return 10;
        }
    }
}
