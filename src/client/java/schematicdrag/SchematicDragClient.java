package schematicdrag;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback;
import net.minecraft.client.Minecraft;

public class SchematicDragClient implements ClientModInitializer {
	public static final String MOD_ID = "schematic-drag";

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(SchematicDragClient::onEndTick);
		ClientPreAttackCallback.EVENT.register((client, player, clickCount) -> DragSession.shouldCancelAttack(client));
	}

	private static void onEndTick(Minecraft client) {
		if (client.player == null || client.level == null) {
			DragSession.clear();
			return;
		}

		if (getScreen(client) != null || !Freecam.isActive(client)) {
			DragSession.clear();
			return;
		}

		DragSession.tick(client);
	}

	static Object getScreen(Minecraft client) {
		return client.gui.screen();
	}
}
