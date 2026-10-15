package cn.enaium.fabricpvz.examplemod.mixin;

import cn.enaium.fabricpvz.examplemod.ExampleMod;
import cn.enaium.fabricpvz.portable.lawn.Board;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Demonstrates {@code @Inject} into instance methods together with {@code @Shadow} access to a game
 * field.
 *
 * <p>On every board update the injected code records the player's sun counter into
 * {@link ExampleMod#lastObservedSun}. The shadowed field proves that member references inside the
 * mixin are rewritten to the target class: the field does not exist on this mixin class itself, only
 * on {@link Board}.
 */
@Mixin(Board.class)
public final class BoardMixin {

    @Shadow
    private int mSunMoney;

    /** Injected at the head of the board's tick (upstream {@code Board::Update}). */
    @Inject(method = "update()V", at = @At("HEAD"))
    private void examplemod$onBoardUpdate(CallbackInfo ci) {
        ExampleMod.boardUpdates++;
        ExampleMod.lastObservedSun = this.mSunMoney;
    }

    /**
     * Injected at the head of the sun-spending check: shows argument passing without cancelling. The
     * target returns a value, so the handler takes a {@link CallbackInfoReturnable} (Mixin requires
     * the callback type to match the target's return type).
     */
    @Inject(method = "canTakeSunMoney(I)Z", at = @At("HEAD"))
    private void examplemod$observeSunSpending(int theAmount, CallbackInfoReturnable<Boolean> cir) {
        ExampleMod.lastObservedSun = this.mSunMoney;
    }
}
