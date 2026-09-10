package schematicdrag;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public final class Freecam {
	private Freecam() {
	}

	public static boolean isInactive(Minecraft client) {
		Entity camera = client.getCameraEntity();
		Player player = client.player;

		if (camera == null || player == null) {
			return true;
		}

		if (camera != player) {
			return false;
		}

		return !FabricLoader.getInstance().isModLoaded("tweakeroo") || !tweakerooFreeCameraEnabled();
	}

	private static boolean tweakerooFreeCameraEnabled() {
		try {
			Class<?> featureToggle = Class.forName("tweakeroo.config.FeatureToggle");
			Object toggle = featureToggle.getField("TWEAK_FREE_CAMERA").get(null);
			Object value = toggle.getClass().getMethod("getBooleanValue").invoke(toggle);
			return Boolean.TRUE.equals(value);
		} catch (ReflectiveOperationException ignored) {
			return false;
		}
	}
}
