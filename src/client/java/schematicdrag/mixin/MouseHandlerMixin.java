package schematicdrag.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import schematicdrag.DragSession;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
	private static final int LEFT_MOUSE_BUTTON = 1;
	private static final int BUTTON_PRESSED = 1;
	private static final int BUTTON_RELEASED = 0;

	@Inject(method = "onButton", at = @At("HEAD"), cancellable = true)
	private void schematicDrag$onButton(long window, MouseButtonInfo buttonInfo, int action, CallbackInfo ci) {
		Minecraft client = Minecraft.getInstance();

		if (client.player == null || client.level == null) {
			return;
		}

		if (buttonInfo.button() != LEFT_MOUSE_BUTTON) {
			return;
		}

		if (action == BUTTON_RELEASED) {
			if (DragSession.isDragging()) {
				DragSession.clear();
				ci.cancel();
			}

			return;
		}

		if (action == BUTTON_PRESSED && DragSession.tryBegin(client)) {
			ci.cancel();
		}
	}

	@Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
	private void schematicDrag$onScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
		if (DragSession.onScroll(vertical)) {
			ci.cancel();
		}
	}
}
