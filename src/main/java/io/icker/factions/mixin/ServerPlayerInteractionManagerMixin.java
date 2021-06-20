package io.icker.factions.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.icker.factions.event.PlayerInteractEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {
	@Shadow
	public ServerWorld world;
	@Shadow
	public ServerPlayerEntity player;

    // TODO: Prevent inventory and door desync
    @Inject(at = @At("HEAD"), method = "tryBreakBlock", cancellable = true)
    private void tryBreakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> info) {
        if (PlayerInteractEvents.preventBlockChange(player, world, pos)) {
            PlayerInteractEvents.warnPlayer(player, "break blocks");
            info.setReturnValue(false);
        }
    }

	@Inject(at = @At("HEAD"), method = "interactBlock", cancellable = true)
	public void interactBlock(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult blockHitResult, CallbackInfoReturnable<ActionResult> info) {
        if (PlayerInteractEvents.preventBlockChange(player, world, blockHitResult.getBlockPos())) {
            PlayerInteractEvents.warnPlayer(player, "use blocks");
            info.setReturnValue(ActionResult.FAIL);
        }
    }

	@Inject(at = @At("HEAD"), method = "interactItem", cancellable = true)
	public void interactItem(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, CallbackInfoReturnable<ActionResult> info) {
        if (PlayerInteractEvents.preventUseItem(player, world)) {
            PlayerInteractEvents.warnPlayer(player, "use items");
            info.setReturnValue(ActionResult.FAIL);
        }
	}
}