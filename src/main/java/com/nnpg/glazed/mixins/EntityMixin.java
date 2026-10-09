package com.nnpg.glazed.mixins;

import com.nnpg.glazed.modules.main.GlazedFreecam;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static meteordevelopment.meteorclient.MeteorClient.mc;

// Ported from Glazed (1.21.4, Mojmap) to 1.21.1 (Yarn).
// GlazedFreelook parts removed because that module was not ported.
@Mixin(Entity.class)
public class EntityMixin {

    // Mojmap: turn -> Yarn: changeLookDirection
    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
    private void glazed$freecamLook(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci) {
        if ((Object) this != mc.player) return;

        GlazedFreecam freecam = Modules.get().get(GlazedFreecam.class);

        if (freecam != null && freecam.isActive()) {
            freecam.updateRotation(cursorDeltaX * 0.15, cursorDeltaY * 0.15);
            ci.cancel();
        }
    }
}
