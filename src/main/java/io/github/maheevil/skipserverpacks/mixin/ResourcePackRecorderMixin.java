package io.github.maheevil.skipserverpacks.mixin;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundResourcePackPacket;
import net.minecraft.network.protocol.game.ServerboundResourcePackPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com/replaymod/recording/packet/ResourcePackRecorder")
public abstract class ResourcePackRecorderMixin {
    @Shadow public abstract ServerboundResourcePackPacket makeStatusPacket(String hash, ServerboundResourcePackPacket.Action action);

    @Inject(
            method = "handleResourcePack",
            at = @At(value = "HEAD"),
            cancellable = true,
            remap = false
    )
    public void onResourcePackSend(Connection netManager, ClientboundResourcePackPacket packet, CallbackInfoReturnable<ClientboundResourcePackPacket> ci) {
            String hash = packet.getHash();
            netManager.send(this.makeStatusPacket(hash, ServerboundResourcePackPacket.Action.ACCEPTED));
            netManager.send(this.makeStatusPacket(hash, ServerboundResourcePackPacket.Action.SUCCESSFULLY_LOADED));
            //ci.setReturnValue();
            ci.cancel();
    }
}
