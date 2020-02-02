package net.earthcomputer.extradebug.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Inject(method = "func_6238_a", at = @At("HEAD"), cancellable = true)
    private void cancelFpsGraph(CallbackInfo ci) {
        ci.cancel();
    }

}
