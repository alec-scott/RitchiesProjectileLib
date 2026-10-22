package rbasamoyai.ritchiesprojectilelib.projectile_burst;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;

/**
 * Renders each sub-projectile of a {@link ProjectileBurst}.
 *
 * <p>PORT-NOTE(mc26.1): entity renderers extract a render state, then submit geometry. Sub-projectile positions are
 * interpolated into {@link State#subProjectiles} at extraction, and subclasses draw one sub-projectile at a time in
 * {@link #submitSubProjectile} (previously {@code renderSubProjectile}, which drew straight into a buffer source).</p>
 */
public abstract class ProjectileBurstRenderer<T extends ProjectileBurst, S extends ProjectileBurstRenderer.State> extends EntityRenderer<T, S> {

	protected ProjectileBurstRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void extractRenderState(T entity, S state, float partialTick) {
		super.extractRenderState(entity, state, partialTick);
		state.subProjectiles.clear();
		for (ProjectileBurst.SubProjectile subProjectile : entity.getSubProjectiles()) {
			double[] disp = subProjectile.displacement();
			double[] vel = subProjectile.velocity();
			state.subProjectiles.add(new double[]{ disp[0] + vel[0] * partialTick, disp[1] + vel[1] * partialTick, disp[2] + vel[2] * partialTick });
		}
	}

	@Override
	public void submit(S state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
		for (double[] offset : state.subProjectiles) {
			poseStack.pushPose();
			poseStack.translate(offset[0], offset[1], offset[2]);
			this.submitSubProjectile(state, poseStack, collector, camera);
			poseStack.popPose();
		}
		super.submit(state, poseStack, collector, camera);
	}

	/** Draws one sub-projectile; {@code poseStack} is already translated to it. */
	protected void submitSubProjectile(S state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
	}

	public static class State extends EntityRenderState {
		/** Interpolated offset of each sub-projectile from the burst entity. */
		public final List<double[]> subProjectiles = new ArrayList<>();
	}

}
