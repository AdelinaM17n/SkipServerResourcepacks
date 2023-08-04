package io.github.maheevil.skipserverpacks.mixin;

import io.github.maheevil.skipserverpacks.ducks.SkippedRequiredPackGetter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ClientboundResourcePackPacket;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.net.URL;

@Mixin(ClientCommonPacketListenerImpl.class)
public abstract class ClientCommonPacketListenerImplMixin {
    @Shadow
    @Final
    @Nullable
    protected ServerData serverData;

    @Shadow
    protected abstract void send(ServerboundResourcePackPacket.Action action);

    @Shadow
    @Final
    protected Minecraft minecraft;

    @ModifyArgs(
            method = "showServerPackPrompt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/ConfirmScreen;<init>(Lit/unimi/dsi/fastutil/booleans/BooleanConsumer;Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/Component;)V"
            )
    )
    private void editArgs$ConfirmScreen$skipserverpacks(Args args, URL uRL, String string, boolean bl, @Nullable Component component) {
        if (bl) {
            // convert this to a translatable and add some localisations
            args.set(2, Component.literal(
                    """
                            SkipServerResourcePacks mod allows you to decline the resource-pack without getting disconnected using an advanced technique called "lying" to server

                            Click "Accept" to accept the resource-pack and join.
                            Click "Decline" to decline the resource-pack and join.
                            """
            ).withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD));

            args.set(3, Component.literal("Accept"));
            args.set(4, Component.literal("Decline"));
        }
    }

    @Inject(
            method = "method_52772",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/client/multiplayer/ClientCommonPacketListenerImpl.send (Lnet/minecraft/network/protocol/common/ServerboundResourcePackPacket$Action;)V",
                    ordinal = 1,
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void injectAtServerDataSetPackDecline$skipserverpacks(Screen screen, URL uRL, String string, boolean bl, boolean bl2, CallbackInfo ci) {
        if (bl) {
            if (serverData != null) {
                handleDeclineActions$skipserverpacks(true);
                ServerList.saveSingleServer(this.serverData);
            }
            ci.cancel();
        } else if (this.serverData != null) {
            this.serverData.setResourcePackStatus(ServerData.ServerPackStatus.DISABLED);
            this.minecraft.setScreen(new ConfirmScreen(
                    xBool -> {
                        this.minecraft.setScreen(screen);
                        handleDeclineActions$skipserverpacks(!xBool);
                    },
                    Component.literal("To lie or to not lie?"),
                    Component.literal(
                            """
                                    You can decline the resource pack without letting the server know, either option won't disconnect you.
                                                                
                                    This is useful if the server won't let you decline the resource-pack even if is not marked as required
                                    """
                    ),
                    Component.literal("Decline and let the server know"),
                    Component.literal("Decline without the Server Knowing")
            ));
            ServerList.saveSingleServer(this.serverData);
        }
        ci.cancel();
    }

    @Unique
    private void handleDeclineActions$skipserverpacks(boolean liedToServer) {
        if (liedToServer) {
            this.send(ServerboundResourcePackPacket.Action.ACCEPTED);
            this.send(ServerboundResourcePackPacket.Action.SUCCESSFULLY_LOADED);
            assert this.serverData != null;
            ((SkippedRequiredPackGetter) this.serverData).setRequiredPackSkipped$skipserverpacks(true);
        } else {
            this.send(ServerboundResourcePackPacket.Action.DECLINED);
        }
    }

    @Redirect(
            method = "handleResourcePack",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/client/multiplayer/ServerData.getResourcePackStatus ()Lnet/minecraft/client/multiplayer/ServerData$ServerPackStatus;",
                    ordinal = 2
            )
    )
    private ServerData.ServerPackStatus redirectPackStatus$ordinal2$skipserverpacks(ServerData instance) {
        if (((SkippedRequiredPackGetter) instance).getRequiredPackSkipped$skipserverpacks()) {
            return ServerData.ServerPackStatus.ENABLED;
        } else {
            return instance.getResourcePackStatus();
        }
    }

    @Inject(
            method = "handleResourcePack",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/client/multiplayer/ClientCommonPacketListenerImpl.send (Lnet/minecraft/network/protocol/common/ServerboundResourcePackPacket$Action;)V",
                    ordinal = 2,
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    public void injectBeforeDeclinePack$handleResourcePack$skipserverpacks(ClientboundResourcePackPacket par1, CallbackInfo ci) {
        assert this.serverData != null;
        if(((SkippedRequiredPackGetter) this.serverData).getRequiredPackSkipped$skipserverpacks()) {
            this.send(ServerboundResourcePackPacket.Action.ACCEPTED);
            this.send(ServerboundResourcePackPacket.Action.SUCCESSFULLY_LOADED);
            ci.cancel();
        }
    }

}
