package rbasamoyai.ritchiesprojectilelib.network.fabric;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import rbasamoyai.ritchiesprojectilelib.network.RootPacket;

// PORT-NOTE(mc26.1): Porting Lib's SimpleChannel has no 26.1 release; uses Fabric's payload networking instead.
public class RPLNetworkImpl {
	public static void sidedInit() {
		PayloadTypeRegistry.clientboundPlay().register(FabricPacket.TYPE, FabricPacket.STREAM_CODEC);
		PayloadTypeRegistry.serverboundPlay().register(FabricPacket.TYPE, FabricPacket.STREAM_CODEC);
	}

	public static void serverInit() {
		ServerPlayNetworking.registerGlobalReceiver(FabricPacket.TYPE,
			(payload, context) -> context.server().execute(() -> payload.pkt().handle(context.server(), context.player().connection, context.player())));
	}

	public static void sendToServer(RootPacket pkt) {
		RPLNetworkClientImpl.sendToServer(pkt);
	}

	public static void sendToClientPlayer(RootPacket pkt, ServerPlayer player) {
		ServerPlayNetworking.send(player, new FabricPacket(pkt));
	}

	public static void sendToClientTracking(RootPacket pkt, Entity tracked) {
		FabricPacket payload = new FabricPacket(pkt);
		for (ServerPlayer player : PlayerLookup.tracking(tracked))
			ServerPlayNetworking.send(player, payload);
	}

	public static void sendToClientAll(RootPacket pkt, MinecraftServer server) {
		FabricPacket payload = new FabricPacket(pkt);
		for (ServerPlayer player : PlayerLookup.all(server))
			ServerPlayNetworking.send(player, payload);
	}
}
