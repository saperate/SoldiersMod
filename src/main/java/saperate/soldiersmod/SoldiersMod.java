package saperate.soldiersmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import saperate.soldiersmod.entity.SoldierBase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SoldiersMod implements ModInitializer {
    public static final String MODID = "soldiersmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static final Identifier SOLDIER_SCREEN_HANDLER_ID = new Identifier("soldiersmod", "soldier_base");
    public static ScreenHandlerType<? extends SoldierBaseScreenHandler> SOLDIER_SCREEN_HANDLER_TYPE = ScreenHandlerRegistry.registerSimple(
            new Identifier("soldiersmod", "soldierbasescreenhandler"),
            SoldierBaseScreenHandler::new
    );
    public static final EntityType<SoldierBase> SOLDIERBASE = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MODID, "soldier_base"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, SoldierBase::new).dimensions(EntityDimensions.fixed(0.6f, 2f)).build()
    );

    @Override
    public void onInitialize() {

        LOGGER.info("Hello Fabric world!");
        FabricDefaultAttributeRegistry.register(SOLDIERBASE, SoldierBase.createHostileAttributes());
    }

}