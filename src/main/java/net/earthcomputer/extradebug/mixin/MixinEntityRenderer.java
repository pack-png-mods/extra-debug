package net.earthcomputer.extradebug.mixin;

import net.earthcomputer.extradebug.ExtraDebug;
import net.minecraft.client.Minecraft;
import net.minecraft.src.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {

    @Shadow private Minecraft mc;

    @Inject(method = "func_4134_c", at = @At(value = "FIELD", target = "Lnet/minecraft/src/EntityRenderer;field_1385_k:Lnet/minecraft/src/Entity;"))
    private void injectChunkBorders(float partialTicks, CallbackInfo ci) {
        ExtraDebug.renderChunkBorders(mc, partialTicks);
    }

}
