package rbasamoyai.ritchiesprojectilelib.network.fabric;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import rbasamoyai.ritchiesprojectilelib.RitchiesProjectileLib;
import rbasamoyai.ritchiesprojectilelib.network.RPLNetwork;
import rbasamoyai.ritchiesprojectilelib.network.RootPacket;

/** One payload type carrying every RPL packet, prefixed with its RPL id (same scheme as the NeoForge build). */
public record FabricPacket(RootPacket pkt) implements CustomPacketPayload {
	public static final Type<FabricPacket> TYPE = new Type<>(RitchiesProjectileLib.resource("packet"));
	public static final StreamCodec<FriendlyByteBuf, FabricPacket> STREAM_CODEC = StreamCodec.of(
		(buf, packet) -> RPLNetwork.writeToBuf(packet.pkt(), buf),
		buf -> new FabricPacket(RPLNetwork.constructPacket(buf, buf.readVarInt()))
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
