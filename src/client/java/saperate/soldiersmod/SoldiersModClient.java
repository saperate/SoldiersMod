package saperate.soldiersmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import saperate.soldiersmod.entity.SoldierBaseModel;
import saperate.soldiersmod.entity.SoldierBaseRenderer;
import saperate.soldiersmod.gui.SoldierBaseScreen;

@Environment(EnvType.CLIENT)
public class SoldiersModClient implements ClientModInitializer {

    public static final EntityModelLayer MODEL_SOLDIER_BASE_LAYER = new EntityModelLayer(new Identifier("soldiersmod", "soldier_base"), "main");


    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(SoldiersMod.SOLDIERBASE, (context) -> {
            return new SoldierBaseRenderer(context);
        });

        EntityModelLayerRegistry.registerModelLayer(MODEL_SOLDIER_BASE_LAYER, SoldierBaseModel::getTexturedModelData);
        HandledScreens.register(SoldiersMod.SOLDIER_SCREEN_HANDLER_TYPE, SoldierBaseScreen::new);
    }

}