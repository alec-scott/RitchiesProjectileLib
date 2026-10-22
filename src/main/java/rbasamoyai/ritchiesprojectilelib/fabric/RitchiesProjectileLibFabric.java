package rbasamoyai.ritchiesprojectilelib.fabric;

import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.v5.ModConfigEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import rbasamoyai.ritchiesprojectilelib.RitchiesProjectileLib;
import rbasamoyai.ritchiesprojectilelib.config.RPLConfigs;
import rbasamoyai.ritchiesprojectilelib.network.fabric.RPLNetworkImpl;

public class RitchiesProjectileLibFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		RitchiesProjectileLib.init();
		RPLNetworkImpl.serverInit();
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> RitchiesProjectileLib.onPlayerJoin(handler.getPlayer()));
		ServerTickEvents.END_LEVEL_TICK.register(RitchiesProjectileLib::onServerLevelTickEnd);
		RPLConfigs.registerConfigs((type, spec) -> ConfigRegistry.INSTANCE.register(RitchiesProjectileLib.MOD_ID, type, spec));
		ModConfigEvents.loading(RitchiesProjectileLib.MOD_ID).register(RPLConfigs::onModConfigLoad);
		ModConfigEvents.reloading(RitchiesProjectileLib.MOD_ID).register(RPLConfigs::onModConfigReload);
	}
}
