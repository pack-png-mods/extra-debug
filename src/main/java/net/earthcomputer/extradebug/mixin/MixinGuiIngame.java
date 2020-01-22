package net.earthcomputer.extradebug.mixin;

import net.earthcomputer.extradebug.ExtraDebug;
import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiIngame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public class MixinGuiIngame {

	@Shadow private Minecraft mc;

	@Inject(method = "func_4066_a", at = @At(value = "INVOKE", target = "Ljava/lang/Runtime;maxMemory()J", remap = false))
	private void onRenderDebug(CallbackInfo ci) {
		ExtraDebug.extraDebug(mc);
	}

}
