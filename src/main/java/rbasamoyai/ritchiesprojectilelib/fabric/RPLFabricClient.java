package rbasamoyai.ritchiesprojectilelib.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import rbasamoyai.ritchiesprojectilelib.RPLClient;
import rbasamoyai.ritchiesprojectilelib.network.fabric.RPLNetworkClientImpl;

public class RPLFabricClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		RPLClient.init();
		RPLNetworkClientImpl.clientInit();
		ClientTickEvents.END_CLIENT_TICK.register(this::onEndClientTick);
		// PORT-NOTE(mc26.1): Porting Lib's CameraSetupCallback is replaced by mixin.fabric.CameraMixin.
		ClientPlayConnectionEvents.DISCONNECT.register((handler, minecraft) -> RPLClient.onPlayerLogout(minecraft.player));
	}

	public void onEndClientTick(Minecraft minecraft) {
		RPLClient.onEndClientGameTick(minecraft);
	}
}
