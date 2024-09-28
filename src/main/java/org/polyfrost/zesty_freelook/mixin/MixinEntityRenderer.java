package org.polyfrost.zesty_freelook.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.EntityRenderer;
import org.polyfrost.zesty_freelook.ZestyFreelook;
import org.polyfrost.zesty_freelook.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * Mixin to inject custom freelook camera handling into the EntityRenderer class.
 * It updates the camera's pitch and yaw when freelook mode is enabled.
 */
@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {

    @Shadow
    private Minecraft mc;

    /**
     * Handles camera movement in freelook mode with smooth camera updates.
     * Injected at the point where player angles are updated.
     */
    @Inject(
        method = "updateCameraAndRender",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/client/entity/EntityPlayerSP.setAngles(FF)V",
            ordinal = 0
        ),
        cancellable = true,
        locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    private void updateCameraSmooth(float partialTicks, long time, CallbackInfo info, boolean flag, float sens, float adjustedSens, float x, float y, int invert, float delta) {
        if (ZestyFreelook.INSTANCE.perspectiveEnabled) {
            updateFreelookCamera(x, y, invert);
        }
    }

    /**
     * Handles camera movement in freelook mode without smooth updates.
     * Injected at the point where player angles are updated.
     */
    @Inject(
        method = "updateCameraAndRender",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/client/entity/EntityPlayerSP.setAngles(FF)V",
            ordinal = 1
        ),
        cancellable = true,
        locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    private void updateCameraNormal(float partialTicks, long time, CallbackInfo info, boolean flag, float sens, float adjustedSens, float x, float y, int invert) {
        if (ZestyFreelook.INSTANCE.perspectiveEnabled) {
            updateFreelookCamera(x, y, invert);
        }
    }

    /**
     * Prevents the player from rotating when freelook mode is enabled.
     */
    @Redirect(
        method = "updateCameraAndRender",
        at = @At(value = "INVOKE", target = "net/minecraft/client/entity/EntityPlayerSP.setAngles(FF)V")
    )
    private void preventPlayerRotation(EntityPlayerSP player, float x, float y) {
        if (!ZestyFreelook.INSTANCE.perspectiveEnabled) {
            player.setAngles(x, y);
        }
    }

    /**
     * Modifies the camera's yaw when freelook mode is enabled.
     */
    @ModifyVariable(
        method = "orientCamera",
        at = @At(value = "STORE", ordinal = 1)
    )
    private float modifyCameraYaw(float originalYaw) {
        return ZestyFreelook.INSTANCE.perspectiveEnabled ? ZestyFreelook.INSTANCE.cameraYaw : originalYaw;
    }

    /**
     * Modifies the camera's pitch when freelook mode is enabled.
     */
    @ModifyVariable(
        method = "orientCamera",
        at = @At(value = "STORE", ordinal = 2)
    )
    private float modifyCameraPitch(float originalPitch) {
        return ZestyFreelook.INSTANCE.perspectiveEnabled ? ZestyFreelook.INSTANCE.cameraPitch : originalPitch;
    }

    /**
     * Updates the freelook camera's yaw and pitch based on mouse movement and config settings.
     */
    private void updateFreelookCamera(float x, float y, int invert) {
        ZestyFreelook.INSTANCE.cameraYaw += x / 8.0F;
        
        if (ModConfig.invertPitch) {
            ZestyFreelook.INSTANCE.cameraPitch += ((y * -1f) * invert) / 8.0F;
        } else {
            ZestyFreelook.INSTANCE.cameraPitch += (y * invert) / 8.0F;
        }

        ZestyFreelook.INSTANCE.cameraPitch = Math.max(-90.0f, Math.min(90.0f, ZestyFreelook.INSTANCE.cameraPitch));
    }
}
