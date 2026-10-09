package com.nnpg.glazed.mixins;

import com.nnpg.glazed.modules.main.GlazedFreecam;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

// Ported from Glazed (1.21.4, Mojmap) to 1.21.1 (Yarn).
// GlazedFreelook parts removed because that module was not ported yet.
@Mixin(Camera.class)
public class CameraMixin {

    // Mojmap: detached
    @Shadow
    private boolean thirdPerson;

    // Mojmap: setup
    @Inject(method = "update", at = @At("HEAD"))
    private void glazed$stepFreecam(BlockView area, Entity focusedEntity, boolean tp, boolean inverseView, float tickDelta, CallbackInfo ci) {
        GlazedFreecam freecam = Modules.get().get(GlazedFreecam.class);
        if (freecam != null && freecam.isActive()) freecam.onGameRender();
    }

    @Inject(method = "update", at = @At("TAIL"))
    private void glazed$showBody(BlockView area, Entity focusedEntity, boolean tp, boolean inverseView, float tickDelta, CallbackInfo ci) {
        GlazedFreecam freecam = Modules.get().get(GlazedFreecam.class);
        if (freecam != null && freecam.isActive()) this.thirdPerson = true;
    }

    // Mojmap: setPosition(DDD)V
    @ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V"))
    private void glazed$setPos(Args args) {
        GlazedFreecam freecam = Modules.get().get(GlazedFreecam.class);
        if (freecam == null || !freecam.isActive()) return;

        args.set(0, freecam.getInterpolatedX(0.0f));
        args.set(1, freecam.getInterpolatedY(0.0f));
        args.set(2, freecam.getInterpolatedZ(0.0f));
    }

    @ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V"))
    private void glazed$setRotation(Args args) {
        GlazedFreecam freecam = Modules.get().get(GlazedFreecam.class);
        if (freecam == null || !freecam.isActive()) return;

        args.set(0, (float) freecam.getInterpolatedYaw(0.0f));
        args.set(1, (float) freecam.getInterpolatedPitch(0.0f));
    }

    // Mojmap: getMaxZoom -> Yarn: clipToSpace
    @ModifyVariable(method = "clipToSpace", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float glazed$noPullback(float distance) {
        GlazedFreecam freecam = Modules.get().get(GlazedFreecam.class);
        return freecam != null && freecam.isActive() ? 0.0f : distance;
    }
            }
