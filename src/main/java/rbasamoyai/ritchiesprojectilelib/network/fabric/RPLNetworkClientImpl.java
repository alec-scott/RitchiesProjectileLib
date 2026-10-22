package rbasamoyai.ritchiesprojectilelib.network.fabric;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import rbasamoyai.ritchiesprojectilelib.network.RootPacket;

/** Client-only half of {@link RPLNetworkImpl}, kept separate so the server never loads client networking classes. */
public class RPLNetworkClientImpl {
	public static void clientInit() {
		ClientPlayNetworking.registerGlobalReceiver(FabricPacket.TYPE,
			(payload, context) -> context.client().execute(() -> payload.pkt().handle(context.client(), context.client().getConnection(), null)));
	}

	public static void sendToServer(RootPacket pkt) {
		ClientPlayNetworking.send(new FabricPacket(pkt));
	}
}
