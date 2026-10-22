package rbasamoyai.ritchiesprojectilelib.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.util.function.Supplier;

public class EnvExecuteImpl {
	public static void executeOnClient(Supplier<Runnable> sup) {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
			sup.get().run();
	}
}
