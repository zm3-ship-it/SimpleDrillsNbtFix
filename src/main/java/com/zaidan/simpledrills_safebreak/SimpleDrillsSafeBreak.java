package com.zaidan.simpledrills_safebreak;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(SimpleDrillsSafeBreak.MODID)
public class SimpleDrillsSafeBreak {
    public static final String MODID = "simpledrills_safebreak";

    public SimpleDrillsSafeBreak() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        Player player = event.getPlayer();
        if (player == null) return;

        ItemStack held = player.getMainHandItem();
        if (held.isEmpty()) return;

        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(held.getItem());
        if (itemId == null) return;

        // Broad match for Simple Drills drills
        boolean isSimpleDrills = "simpledrills".equals(itemId.getNamespace());
        boolean isDrill = itemId.getPath().contains("drill");
        if (!isSimpleDrills || !isDrill) return;

        // Treat BlockEntities like bedrock for drill AOE:
        // If ANY BlockEntity exists in the drill's 3x3 area, cancel the whole break.
        BlockPos center = event.getPos();
        int r = 1; // radius 1 => 3x3x3 safety cube

        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                for (int dz = -r; dz <= r; dz++) {
                    BlockPos p = center.offset(dx, dy, dz);
                    if (level.getBlockEntity(p) != null) {
                        event.setCanceled(true);
                        player.displayClientMessage(
                            Component.literal("§cDrill blocked near containers/vehicles (protected like bedrock). Use a pickaxe."),
                            true
                        );
                        return;
                    }
                }
            }
        }
    }
}
