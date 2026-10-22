package rbasamoyai.ritchiesprojectilelib.mixin.fabric;

import net.minecraft.client.Camera;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rbasamoyai.ritchiesprojectilelib.RPLClient;
import rbasamoyai.ritchiesprojectilelib.effects.CameraAngleHooks;
import rbasamoyai.ritchiesprojectilelib.effects.CameraModifier;

/**
 * Applies screen shake to the camera, like NeoForge's {@code ViewportEvent.ComputeCameraAngles}. Vanilla's camera has
 * no roll, so the rotation is rebuilt with one (as NeoForge's camera patch does).
 */
@Mixin(Camera.class)
public abstract class CameraMixin {
	@Shadow @Final private static Vector3f FORWARDS;
	@Shadow @Final private static Vector3f UP;
	@Shadow @Final private static Vector3f LEFT;
	@Shadow @Final private Vector3f forwards;
	@Shadow @Final private Vector3f up;
	@Shadow @Final private Vector3f left;
	@Shadow @Final private Quaternionf rotation;
	@Shadow private float xRot;
	@Shadow private float yRot;
	@Shadow private int matrixPropertiesDirty;

	@Shadow protected abstract void setRotation(float yRot, float xRot);

	@Inject(method = "alignWithEntity", at = @At("TAIL"))
	private void ritchiesprojectilelib$applyScreenShake(final float partialTicks, final CallbackInfo ci) {
		final float[] angles = {this.yRot, this.xRot, 0.0F};
		CameraModifier modifier = new CameraModifier() {
			@Override public void setYaw(float yaw) { angles[0] = yaw; }
			@Override public float getYaw() { return angles[0]; }
			@Override public void setPitch(float pitch) { angles[1] = pitch; }
			@Override public float getPitch() { return angles[1]; }
			@Override public void setRoll(float roll) { angles[2] = roll; }
			@Override public float getRoll() { return angles[2]; }
		};
		RPLClient.onCameraSetup((Camera) (Object) this, partialTicks, modifier);
		CameraAngleHooks.run((Camera) (Object) this, partialTicks, modifier);
		if (angles[0] == this.yRot && angles[1] == this.xRot && angles[2] == 0.0F)
			return;
		this.setRotation(angles[0], angles[1]);
		if (angles[2] != 0.0F)
			this.ritchiesprojectilelib$applyRoll(angles[2]);
	}

	@Unique
	private void ritchiesprojectilelib$applyRoll(final float roll) {
		final float degToRad = (float) (Math.PI / 180.0);
		this.rotation.rotationYXZ((float) Math.PI - this.yRot * degToRad, -this.xRot * degToRad, -roll * degToRad);
		FORWARDS.rotate(this.rotation, this.forwards);
		UP.rotate(this.rotation, this.up);
		LEFT.rotate(this.rotation, this.left);
		this.matrixPropertiesDirty |= 3;
	}
}
