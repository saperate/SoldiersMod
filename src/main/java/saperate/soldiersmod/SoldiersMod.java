package saperate.soldiersmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import saperate.soldiersmod.entity.SoldierBase;
import saperate.soldiersmod.gui.SoldierBaseScreenHandler;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SoldiersMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("soldiersmod");

	public static final EntityType<SoldierBase> SOLDIERBASE = Registry.register(
			Registries.ENTITY_TYPE,
			new Identifier("soldiersmod", "soldier_base"),
			FabricEntityTypeBuilder.create(SpawnGroup.MISC, SoldierBase::new)
					.dimensions(EntityDimensions.fixed(0.6f, 2f)).build());

	public static final Identifier SOLDIER_SCREEN_HANDLER_ID = new Identifier("soldiersmod", "soldier_base");

	public static final ExtendedScreenHandlerType<SoldierBaseScreenHandler> SOLDIER_SCREEN_HANDLER_TYPE = new ExtendedScreenHandlerType<>(
		(syncId, inventory, buf) -> {
			return new SoldierBaseScreenHandler(syncId, inventory, buf);
		});

	@Override
	public void onInitialize() {
		ServerPlayNetworking.registerGlobalReceiver(PacketIdentifiers.OPEN_SOLDIER_SCREEN, SoldiersMod::handleOpenSoldierScreen);
		FabricDefaultAttributeRegistry.register(SOLDIERBASE, SoldierBase.createMobAttributes());
		Registry.register(Registries.SCREEN_HANDLER, new Identifier("soldiersmod", "soldier_screen_type_base"),
				SOLDIER_SCREEN_HANDLER_TYPE);
				
	}
	public class PacketIdentifiers {
		public static final Identifier OPEN_SOLDIER_SCREEN = new Identifier("soldiersmod", "open_soldier_screen");
	}

	private static void handleOpenSoldierScreen(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
    // Read the data from the packet
    UUID entityUUID = buf.readUuid();
    int syncId = buf.readInt(); // Read syncId from the buffer
    
    // Get the ExtendedScreenHandlerType for your soldier screen
    
    // Open the screen on the client
    player.openHandledScreen(new ExtendedScreenHandlerFactory() {
        @Override
        public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
            buf.writeUuid(entityUUID);
        }

        @Override
        public Text getDisplayName() {
            return Text.of("Your Screen Title");
        }

        @Override
        public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
            // Create and return your screen handler here using the provided entityUUID and syncId
            // You can use this information to retrieve the correct soldier entity and its data
            return new SoldierBaseScreenHandler(syncId, inv, entityUUID);
        }
    });
}
}