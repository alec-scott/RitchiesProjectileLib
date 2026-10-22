package rbasamoyai.ritchiesprojectilelib.network;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import rbasamoyai.ritchiesprojectilelib.EnvExecute;
import rbasamoyai.ritchiesprojectilelib.effects.screen_shake.ScreenShakeEffect;

public record ClientboundShakeScreenPacket(@Nullable Identifier modHandlerId, ScreenShakeEffect effect) implements RootPacket {

    public ClientboundShakeScreenPacket(FriendlyByteBuf buf) {
        this(buf.readBoolean() ? buf.readIdentifier() : null, new ScreenShakeEffect(buf.readVarInt(), buf.readFloat(),
            buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readDouble(), buf.readDouble(), buf.readDouble()));
    }

    public ClientboundShakeScreenPacket(ScreenShakeEffect effect) {
        this(null, effect);
    }

    @Override
    public void rootEncode(FriendlyByteBuf buf) {
        buf.writeBoolean(this.modHandlerId != null);
        if (this.modHandlerId != null)
            buf.writeIdentifier(this.modHandlerId);
        buf.writeVarInt(this.effect.duration)
            .writeFloat(this.effect.yawMagnitude)
            .writeFloat(this.effect.pitchMagnitude)
            .writeFloat(this.effect.rollMagnitude)
            .writeFloat(this.effect.yawJitter)
            .writeFloat(this.effect.pitchJitter)
            .writeFloat(this.effect.rollJitter)
            .writeDouble(this.effect.posX)
            .writeDouble(this.effect.posY)
            .writeDouble(this.effect.posZ);
    }

    @Override
    public void handle() {
        EnvExecute.executeOnClient(() -> () -> RPLClientHandlers.shakeScreen(this));
    }

}
