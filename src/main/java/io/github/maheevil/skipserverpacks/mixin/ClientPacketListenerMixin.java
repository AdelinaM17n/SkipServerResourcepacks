package io.github.maheevil.skipserverpacks.mixin;

import net.minecraft.client.multiplayer.ClientPacketListener;
//import net.minecraft.network.protocol.game.ClientboundResourcePackPacket;
//import net.minecraft.network.protocol.game.ServerboundResourcePackPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
	//@Shadow protected abstract void send(ServerboundResourcePackPacket.Action action);

	/*@Inject(
			method = "handleResourcePack",
			at = @At(value = "HEAD"),
			cancellable=true
	)
	public void onResourcePackSend(ClientboundResourcePackPacket par1, CallbackInfo ci) {
			this.send(ServerboundResourcePackPacket.Action.ACCEPTED);
			this.send(ServerboundResourcePackPacket.Action.SUCCESSFULLY_LOADED);
			ci.cancel();
	}*/
}
