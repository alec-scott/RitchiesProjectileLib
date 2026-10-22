package rbasamoyai.ritchiesprojectilelib.effects;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraft.client.Camera;

/**
 * Lets other mods adjust camera yaw, pitch and roll alongside RPL's screen shake.
 *
 * <p>PORT-NOTE(fabric): Fabric has no camera-angle event (NeoForge's {@code ViewportEvent.ComputeCameraAngles}), and two
 * mods each rebuilding the camera rotation would overwrite each other's roll. RPL's camera mixin runs every listener
 * on one shared {@link CameraModifier} and applies the result once.</p>
 */
public final class CameraAngleHooks {
	private static final List<Listener> LISTENERS = new CopyOnWriteArrayList<>();

	private CameraAngleHooks() {
	}

	public static void register(Listener listener) {
		LISTENERS.add(listener);
	}

	public static void run(Camera camera, float partialTicks, CameraModifier modifier) {
		for (Listener listener : LISTENERS)
			listener.modifyAngles(camera, partialTicks, modifier);
	}

	@FunctionalInterface
	public interface Listener {
		void modifyAngles(Camera camera, float partialTicks, CameraModifier modifier);
	}
}
