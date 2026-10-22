package rbasamoyai.ritchiesprojectilelib;

import rbasamoyai.ritchiesprojectilelib.fabric.EnvExecuteImpl;

import java.util.function.Supplier;

public class EnvExecute {

	public static void executeOnClient(Supplier<Runnable> sup) { EnvExecuteImpl.executeOnClient(sup); }

}
