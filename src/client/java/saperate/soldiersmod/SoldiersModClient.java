package saperate.soldiersmod;

import java.util.UUID;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.C2SPlayChannelEvents.Register;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import saperate.soldiersmod.SoldiersMod.PacketIdentifiers;
import saperate.soldiersmod.entity.SoldierBase;
import saperate.soldiersmod.entity.SoldierBaseModel;
import saperate.soldiersmod.entity.SoldierBaseRenderer;
import saperate.soldiersmod.gui.SoldierBaseScreen;
import saperate.soldiersmod.gui.SoldierBaseScreenHandler;
import saperate.soldiersmod.gui.SoldierInventory;

@Environment(EnvType.CLIENT)
public class SoldiersModClient implements ClientModInitializer {

	public static final EntityModelLayer MODEL_SOLDIER_BASE_LAYER = new EntityModelLayer(new Identifier("soldiersmod", "soldier_base"),"main");


	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(SoldiersMod.SOLDIERBASE, (context) -> {
            return new SoldierBaseRenderer(context);
        });
		

		EntityModelLayerRegistry.registerModelLayer(MODEL_SOLDIER_BASE_LAYER, SoldierBaseModel::getTexturedModelData);
		HandledScreens.register(SoldiersMod.SOLDIER_SCREEN_HANDLER_TYPE, (gui, inventory, title) -> new SoldierBaseScreen(gui, inventory.player.getInventory(), title));
	ClientPlayNetworking.registerGlobalReceiver(PacketIdentifiers.OPEN_SOLDIER_SCREEN, (client, handler, buf, responseSender) -> {
            // Read the data from the packet
            UUID entityUUID = buf.readUuid();
            int syncId = buf.readInt();

            // Open the soldier screen on the client
            MinecraftClient.getInstance().execute(() -> {
                //Entity entity = client.world.getEntityByUuid(entityUUID);
                if (true){//entity instanceof SoldierBase) {
                    // Open the screen for the soldier entity
					
                    client.setScreen(new SoldierBaseScreen(null,client.player.getInventory(),buf));
                }
            });
        });
	}
}