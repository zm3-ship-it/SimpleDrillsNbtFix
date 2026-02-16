package com.zaidan.simpledrills_safebreak;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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

        boolean isSimpleDrills = "simpledrills".equals(itemId.getNamespace());
        boolean isDrill = itemId.getPath().contains("drill");

        if (!isSimpleDrills || !isDrill) return;

        if (level.getBlockEntity(event.getPos()) != null) {
            event.setCanceled(true);
            player.displayClientMessage(
                net.minecraft.network.chat.Component.literal("§cDrills cannot break containers or vehicles (data protection enabled)."),
                true
            );
        }
    }
}