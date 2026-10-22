package rbasamoyai.ritchiesprojectilelib.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rbasamoyai.ritchiesprojectilelib.RPLTags;
import rbasamoyai.ritchiesprojectilelib.network.ClientboundPreciseMotionSyncPacket;
import rbasamoyai.ritchiesprojectilelib.network.RPLNetwork;

@Mixin(ServerEntity.class)
public class ServerEntityMixin {
	@Shadow @Final private Entity entity;

	@Inject(method = "sendChanges", at = @At("HEAD"))
	private void ritchiesprojectilelib$sendChanges$0(CallbackInfo ci) {
		if (this.ritchiesprojectilelib$hasPreciseMotion()) {
			Vec3 pos = this.entity.position();
			Vec3 vel = this.entity.getDeltaMovement();
			RPLNetwork.sendToClientTracking(new ClientboundPreciseMotionSyncPacket(this.entity.getId(), pos.x, pos.y, pos.z, vel.x, vel.y, vel.z, this.entity.getYRot(), this.entity.getXRot(), this.entity.onGround(), 3), this.entity);
			// PORT-NOTE(mc26.1): Entity.hasImpulse was renamed needsSync.
			this.entity.needsSync = false;
		}
	}

	/**
	 * Precise-motion entities are synced by RPL's own packet, so vanilla's movement and velocity packets are dropped.
	 *
	 * <p>PORT-NOTE(mc26.1): sendChanges sends through ServerEntity.Synchronizer instead of a Consumer, so this filters
	 * by packet type rather than picking calls by ordinal.</p>
	 */
	@WrapOperation(method = "sendChanges", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerEntity$Synchronizer;sendToTrackingPlayers(Lnet/minecraft/network/protocol/Packet;)V"))
	private void ritchiesprojectilelib$dropVanillaMotion(ServerEntity.Synchronizer instance, Packet<? super ClientGamePacketListener> packet, Operation<Void> original) {
		if (!this.ritchiesprojectilelib$hasPreciseMotion() || !ritchiesprojectilelib$isMotionPacket(packet))
			original.call(instance, packet);
	}

	@Unique
	private boolean ritchiesprojectilelib$hasPreciseMotion() {
		// PORT-NOTE(mc26.1): EntityType.is(TagKey) is gone; check tags through the type holder.
		return this.entity.typeHolder().is(RPLTags.PRECISE_MOTION);
	}

	@Unique
	private static boolean ritchiesprojectilelib$isMotionPacket(Packet<?> packet) {
		if (packet instanceof ClientboundBundlePacket bundle) {
			for (Packet<?> sub : bundle.subPackets()) {
				if (ritchiesprojectilelib$isMotionPacket(sub))
					return true;
			}
			return false;
		}
		return packet instanceof ClientboundMoveEntityPacket
			|| packet instanceof ClientboundEntityPositionSyncPacket
			|| packet instanceof ClientboundSetEntityMotionPacket;
	}
}
