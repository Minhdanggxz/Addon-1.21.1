package com.nnpg.glazed.modules.main;

// Ported from Glazed (realnnpg/glazed, 1.21.4, Mojang mappings) to 1.21.1 (Yarn mappings).
// Keep the original license/credit if you redistribute.

import com.nnpg.glazed.GlazedAddon;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.meteor.MouseScrollEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.ChunkOcclusionEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;
import org.lwjgl.glfw.GLFW;

public class GlazedFreecam extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> speed = sgGeneral.add(new DoubleSetting.Builder()
        .name("speed")
        .description("Move freely.")
        .defaultValue(0.8)
    public float previousYaw;
    public float previousPitch;

    private Perspective currentPerspective;
    private double savedFovEffect;
    private boolean savedBobView;

    private boolean isMovingForward;
    private boolean isMovingBackward;
    private boolean isMovingRight;
    private boolean isMovingLeft;
    private boolean isMovingUp;
    private boolean isMovingDown;

    private boolean sneakOnEnable;

    private long lastFrameTime;

    public GlazedFreecam() {
        super(GlazedAddon.CATEGORY, "glazed-freecam", "Move freely.");
    }

    @Override
    public void onActivate() {
        if (mc.player == null || mc.world == null || mc.options == null) {
            toggle();
            return;
        }

        savedFovEffect = mc.options.getFovEffectScale().getValue();
        savedBobView = mc.options.getBobView().getValue();
        mc.options.getFovEffectScale().setValue(0.0);
        mc.options.getBobView().setValue(false);

        yaw = mc.player.getYaw();
        pitch = mc.player.getPitch();
        Vec3d right = Vec3d.fromPolar(0.0f, yaw + 90.0f);

        double moveX = 0, moveY = 0, moveZ = 0;
        double moveSpeed = speed.get() * 2 * (isBoundDown(mc.options.sprintKey) ? 2.0 : 1.0);

        if (isMovingForward) {
            moveX += forward.x * moveSpeed;
            moveZ += forward.z * moveSpeed;
        }
        if (isMovingBackward) {
            moveX -= forward.x * moveSpeed;
            moveZ -= forward.z * moveSpeed;
        }
        if (isMovingRight) {
            moveX += right.x * moveSpeed;
            moveZ += right.z * moveSpeed;
        }
        if (isMovingLeft) {
            moveX -= right.x * moveSpeed;
            moveZ -= right.z * moveSpeed;
        }
        if (isMovingUp) moveY += moveSpeed;
        if (isMovingDown) moveY -= moveSpeed;

        currentPosition.x += moveX * deltaTime * 5.0;
        currentPosition.y += moveY * deltaTime * 5.0;
        currentPosition.z += moveZ * deltaTime * 5.0;
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onMouseScroll(MouseScrollEvent event) {
        if (event.value == 0 || mc.currentScreen != null) return;

        adjustSpeed(event.value > 0 ? 1 : -1);
        event.cancel();
    }

    }

    private void pollMovementKeys() {
        boolean active = mc.currentScreen == null && !isKeyPressed(GLFW.GLFW_KEY_F3);

        isMovingForward = active && isBoundDown(mc.options.forwardKey);
        isMovingBackward = active && isBoundDown(mc.options.backKey);
        isMovingRight = active && isBoundDown(mc.options.rightKey);
        isMovingLeft = active && isBoundDown(mc.options.leftKey);
        isMovingUp = active && isBoundDown(mc.options.jumpKey);
        isMovingDown = active && isBoundDown(mc.options.sneakKey);
    }

    private boolean isBoundDown(KeyBinding bind) {
        if (bind == null || mc.getWindow() == null) return false;

        try {
            InputUtil.Key key = InputUtil.fromTranslationKey(bind.getBoundKeyTranslationKey());
            if (key == null || key.equals(InputUtil.UNKNOWN_KEY)) return false;

            long window = mc.getWindow().getHandle();
            if (key.getCategory() == InputUtil.Type.MOUSE) {
                return GLFW.glfwGetMouseButton(window, key.getCode()) == GLFW.GLFW_PRESS;
            }
            return GLFW.glfwGetKey(window, key.getCode()) == GLFW.GLFW_PRESS;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private boolean isKeyPressed(int key) {
        return mc.getWindow() != null && GLFW.glfwGetKey(mc.getWindow().getHandle(), key) == GLFW.GLFW_PRESS;
    }
}
