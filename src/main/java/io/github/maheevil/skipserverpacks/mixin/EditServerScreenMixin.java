package io.github.maheevil.skipserverpacks.mixin;

import io.github.maheevil.skipserverpacks.ducks.SkippedRequiredPackGetter;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.EditServerScreen;
import net.minecraft.client.multiplayer.ServerData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EditServerScreen.class)
public class EditServerScreenMixin {
    @Shadow @Final private ServerData serverData;

    @Inject(
            method = "method_36219",
            at = @At("TAIL")
    )
    private void tailInject$onServerEditPackStatusButton$skipserverpacks(CycleButton<?> cycleButton, ServerData.ServerPackStatus serverPackStatus, CallbackInfo ci){
        ((SkippedRequiredPackGetter) this.serverData).setRequiredPackSkipped$skipserverpacks(false);
    }
}
