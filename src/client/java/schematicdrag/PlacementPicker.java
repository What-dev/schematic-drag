package schematicdrag;

import java.util.Map;

import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacementManager;
import fi.dy.masa.litematica.schematic.placement.SubRegionPlacement.RequiredEnabled;
import fi.dy.masa.litematica.selection.Box;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class PlacementPicker {
	private static final double MAX_REACH = 512.0;

	private PlacementPicker() {
	}

	public static Hit pick(Minecraft client) {
		Entity camera = client.getCameraEntity();

		if (camera == null) {
			return null;
		}

		Vec3 start = camera.getEyePosition(1.0F);
		Vec3 look = camera.getViewVector(1.0F);
		Vec3 end = start.add(look.scale(MAX_REACH));

		SchematicPlacementManager manager = DataManager.getSchematicPlacementManager();
		Hit best = null;
		double bestDistance = Double.MAX_VALUE;

		for (SchematicPlacement placement : manager.getAllSchematicsPlacements()) {
			if (!placement.isEnabled() || placement.isLocked() || !placement.isRenderingEnabled()) {
				continue;
			}

			for (Box box : placementBoxes(placement)) {
				AABB aabb = toAabb(box);

				if (aabb == null) {
					continue;
				}

				var clip = aabb.clip(start, end);

				if (clip.isEmpty()) {
					continue;
				}

				double distance = start.distanceToSqr(clip.get());

				if (distance < bestDistance) {
					bestDistance = distance;
					best = new Hit(placement, clip.get());
				}
			}
		}

		return best;
	}

	private static Iterable<Box> placementBoxes(SchematicPlacement placement) {
		Map<String, Box> boxes = placement.getSubRegionBoxes(RequiredEnabled.RENDERING_ENABLED);

		if (!boxes.isEmpty()) {
			return boxes.values();
		}

		Box enclosing = placement.getEclosingBox();
		return enclosing == null ? java.util.List.of() : java.util.List.of(enclosing);
	}

	private static AABB toAabb(Box box) {
		if (box.getPos1() == null || box.getPos2() == null) {
			return null;
		}

		int minX = Math.min(box.getPos1().getX(), box.getPos2().getX());
		int minY = Math.min(box.getPos1().getY(), box.getPos2().getY());
		int minZ = Math.min(box.getPos1().getZ(), box.getPos2().getZ());
		int maxX = Math.max(box.getPos1().getX(), box.getPos2().getX());
		int maxY = Math.max(box.getPos1().getY(), box.getPos2().getY());
		int maxZ = Math.max(box.getPos1().getZ(), box.getPos2().getZ());
		return new AABB(minX, minY, minZ, maxX + 1, maxY + 1, maxZ + 1);
	}

	public record Hit(SchematicPlacement placement, Vec3 point) {
	}
}
