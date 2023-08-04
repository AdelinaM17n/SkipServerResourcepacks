package io.github.maheevil.skipserverpacks.mixin;

import io.github.maheevil.skipserverpacks.ducks.SkippedRequiredPackGetter;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ServerData.class)
public class ServerDataMixin implements SkippedRequiredPackGetter {
    @Unique
    private boolean requiredPackSkipped = false;

    @Inject(
            method = "read",
            at = @At(
                    "TAIL"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void injectTail$read$skipserverpacks(CompoundTag nbtCompound, CallbackInfoReturnable<ServerData> cir, ServerData serverData) {
        if (nbtCompound.contains("requiredPackSkipped")) {
            ((SkippedRequiredPackGetter) serverData).setRequiredPackSkipped$skipserverpacks(
                    nbtCompound.getBoolean("requiredPackSkipped")
            );
        }
    }

    @Inject(
            method = "write",
            at = @At(
                    "TAIL"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void injectTail$write$skipserverpacks(CallbackInfoReturnable<CompoundTag> cir, CompoundTag compoundTag) {
        compoundTag.putBoolean("requiredPackSkipped", requiredPackSkipped);
    }

    @Override
    public boolean getRequiredPackSkipped$skipserverpacks() {
        return requiredPackSkipped;
    }

    @Override
    public void setRequiredPackSkipped$skipserverpacks(boolean value) {
        this.requiredPackSkipped = value;
    }
}
