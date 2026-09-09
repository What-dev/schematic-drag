package schematicdrag;

import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import fi.dy.masa.litematica.selection.Box;
import fi.dy.masa.malilib.util.position.PositionUtils.CoordinateType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public final class DragSession {
	private static final double MIN_DISTANCE = 1.0;
	private static final int MAX_SCROLL_STEP = 16;

	private static SchematicPlacement placement;
	private static Vec3 grabOffset = Vec3.ZERO;
	private static Vec3 grabCameraPos = Vec3.ZERO;
	private static Vec3 grabLook = Vec3.ZERO;
	private static double distance;
	private static double grabDistance;
	private static boolean awaitingMovement;

	private DragSession() {
	}

	public static boolean isDragging() {
		return placement != null;
	}

	public static boolean shouldCancelAttack(Minecraft client) {
		return isDragging();
	}

	public static boolean tryBegin(Minecraft client) {
		if (SchematicDragClient.getScreen(client) != null || !Freecam.isActive(client) || isDragging()) {
			return false;
		}

		PlacementPicker.Hit hit = PlacementPicker.pick(client);

		if (hit == null || hit.placement().isLocked()) {
			return false;
		}

		Entity camera = client.getCameraEntity();

		if (camera == null) {
			return false;
		}

		Vec3 cameraPos = camera.getEyePosition(1.0F);
		SchematicPlacement grabbedPlacement = hit.placement();
		Vec3 origin = Vec3.atLowerCornerOf(grabbedPlacement.getOrigin());
		Vec3 anchor = anchorPoint(grabbedPlacement, hit.point());
		placement = grabbedPlacement;
		grabOffset = anchor.subtract(origin);
		distance = Math.max(MIN_DISTANCE, cameraPos.distanceTo(anchor));
		grabCameraPos = cameraPos;
		grabLook = camera.getViewVector(1.0F);
		grabDistance = distance;
		awaitingMovement = true;
		return true;
	}

	private static Vec3 anchorPoint(SchematicPlacement placement, Vec3 hitPoint) {
		Box box = placement.getEclosingBox();

		if (box == null || box.getPos1() == null || box.getPos2() == null) {
			return hitPoint;
		}

		int minX = Math.min(box.getPos1().getX(), box.getPos2().getX());
		int minZ = Math.min(box.getPos1().getZ(), box.getPos2().getZ());
		int maxX = Math.max(box.getPos1().getX(), box.getPos2().getX());
		int maxZ = Math.max(box.getPos1().getZ(), box.getPos2().getZ());
		return new Vec3((minX + maxX + 1) / 2.0, hitPoint.y, (minZ + maxZ + 1) / 2.0);
	}

	public static void tick(Minecraft client) {
		if (!isDragging()) {
			return;
		}

		if (placement.isLocked() || !placement.isEnabled()) {
			clear();
			return;
		}

		if (awaitingMovement && !hasMoved(client)) {
			return;
		}

		awaitingMovement = false;
		apply(client);
	}

	public static boolean onScroll(double vertical) {
		if (!isDragging() || vertical == 0.0) {
			return false;
		}

		double magnitude = Math.abs(vertical);
		int step = (int) Math.min(MAX_SCROLL_STEP,
				Math.max(1, Math.ceil(Math.pow(2.0, Math.max(0.0, magnitude - 1.0)))));
		distance = Math.max(MIN_DISTANCE, distance + Math.signum(vertical) * step);
		awaitingMovement = false;
		return true;
	}

	public static void clear() {
		placement = null;
		grabOffset = Vec3.ZERO;
		grabCameraPos = Vec3.ZERO;
		grabLook = Vec3.ZERO;
		distance = 0.0;
		grabDistance = 0.0;
		awaitingMovement = false;
	}

	private static boolean hasMoved(Minecraft client) {
		Entity camera = client.getCameraEntity();

		if (camera == null) {
			return false;
		}

		Vec3 cameraPos = camera.getEyePosition(1.0F);
		Vec3 look = camera.getViewVector(1.0F);
		return cameraPos.distanceToSqr(grabCameraPos) > 1.0E-8
				|| look.distanceToSqr(grabLook) > 1.0E-8
				|| Math.abs(distance - grabDistance) > 1.0E-8;
	}

	private static void apply(Minecraft client) {
		Entity camera = client.getCameraEntity();

		if (camera == null || placement == null) {
			return;
		}

		Vec3 cameraPos = camera.getEyePosition(1.0F);
		Vec3 look = camera.getViewVector(1.0F);
		Vec3 target = cameraPos.add(look.scale(distance)).subtract(grabOffset);
		BlockPos origin = BlockPos.containing(target);
		BlockPos current = placement.getOrigin();

		if (placement.isCoordinateLocked(CoordinateType.X)) {
			origin = new BlockPos(current.getX(), origin.getY(), origin.getZ());
		}
		if (placement.isCoordinateLocked(CoordinateType.Y)) {
			origin = new BlockPos(origin.getX(), current.getY(), origin.getZ());
		}
		if (placement.isCoordinateLocked(CoordinateType.Z)) {
			origin = new BlockPos(origin.getX(), origin.getY(), current.getZ());
		}

		if (origin.equals(current)) {
			return;
		}

		placement.setOrigin(origin, ignored -> {
		});
	}
}
