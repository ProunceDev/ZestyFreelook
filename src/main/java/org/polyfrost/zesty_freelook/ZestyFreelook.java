package org.polyfrost.zesty_freelook;

import org.polyfrost.zesty_freelook.command.ModCommand;
import org.polyfrost.zesty_freelook.config.ModConfig;
import cc.polyfrost.oneconfig.utils.Notifications;
import cc.polyfrost.oneconfig.utils.commands.CommandManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Main class for Zesty Freelook mod.
 */
@Mod(modid = ZestyFreelook.MODID, name = ZestyFreelook.NAME, version = ZestyFreelook.VERSION)
public class ZestyFreelook {

    public static final String MODID = "@ID@";
    public static final String NAME = "@NAME@";
    public static final String VERSION = "@VER@";

    @Mod.Instance(MODID)
    public static ZestyFreelook INSTANCE;

    public static ModConfig config;

    private final Minecraft client = Minecraft.getMinecraft();
    private final KeyBinding toggleKey = new KeyBinding("Toggle Freelook", 62, "Zesty Freelook");

    public boolean perspectiveEnabled = false;
    public boolean held = false;
    public float cameraPitch;
    public float cameraYaw;

    public ZestyFreelook() {
        INSTANCE = this;
    }

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        config = new ModConfig();
        CommandManager.INSTANCE.registerCommand(new ModCommand());
        ClientRegistry.registerKeyBinding(toggleKey);
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (client.thePlayer == null) return;

        if (!perspectiveEnabled && held) {
            resetFreelook();
        }

        if (perspectiveEnabled && client.gameSettings.thirdPersonView != 1) {
            perspectiveEnabled = false;
        }
    }

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event) {
        handleFreelookKeyInput();
    }

    @SubscribeEvent
    public void onMouse(InputEvent.MouseInputEvent event) {
        handleFreelookKeyInput();
    }

    @SubscribeEvent
    public void cameraSetup(EntityViewRenderEvent.CameraSetup event) {
        if (perspectiveEnabled) {
            event.pitch = cameraPitch;
            event.yaw = cameraYaw;
        }
    }

    /**
     * Handles key input for toggling freelook.
     */
    private void handleFreelookKeyInput() {
        if (client.thePlayer == null) return;

        boolean previousPerspectiveEnabled = perspectiveEnabled;

        if (config.holdToLook) {
            perspectiveEnabled = toggleKey.isKeyDown();

            if (perspectiveEnabled && !held) {
                enableFreelook();
            } else if (!perspectiveEnabled && held) {
                resetFreelook();
            }

        } else if (toggleKey.isPressed()) {
            perspectiveEnabled = !perspectiveEnabled;

            if (perspectiveEnabled) {
                enableFreelook();
            } else {
                resetFreelook();
            }
        }

        if (config.notifications && previousPerspectiveEnabled != perspectiveEnabled && config.enabled) {
            sendFreelookNotification();
        }
    }

    /**
     * Enables freelook mode by setting camera angles and switching to third-person view.
     */
    private void enableFreelook() {
        if (config.enabled) {
            held = true;
            cameraPitch = client.thePlayer.rotationPitch;
            cameraYaw = config.startFacingRear ? client.thePlayer.rotationYaw : client.thePlayer.rotationYaw + 180.0F;
            client.gameSettings.thirdPersonView = 1;
        }
    }

    /**
     * Resets freelook mode, switching back to first-person view.
     */
    private void resetFreelook() {
        held = false;
        client.gameSettings.thirdPersonView = 0;
    }

    /**
     * Sends notification when freelook is enabled or disabled.
     */
    private void sendFreelookNotification() {
        String message = perspectiveEnabled ? "Freelook Enabled!" : "Freelook Disabled!";
        Notifications.INSTANCE.send("ZestyFreelook", message, 1000);
    }
}
