package saperate.soldiersmod.entity;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer;
import net.minecraft.client.render.entity.model.ArmorEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.Identifier;
import saperate.soldiersmod.SoldiersModClient;

public class SoldierBaseRenderer extends MobEntityRenderer<SoldierBase, SoldierBaseModel<SoldierBase>> {
    public SoldierBaseRenderer(EntityRendererFactory.Context context) {
        super(context,
                new SoldierBaseModel<SoldierBase>(context.getPart(SoldiersModClient.MODEL_SOLDIER_BASE_LAYER), false),
                0.5f);
        this.addFeature(new ArmorFeatureRenderer<>(this,
                new ArmorEntityModel<>(context.getPart(EntityModelLayers.PLAYER_INNER_ARMOR)),
                new ArmorEntityModel<>(context.getPart(EntityModelLayers.PLAYER_OUTER_ARMOR)),
                context.getModelManager()));
        this.addFeature(new SoldierBaseHeldItemFeatureRenderer<>(this, context.getHeldItemRenderer()));
        this.addFeature(new StuckArrowsFeatureRenderer<SoldierBase, SoldierBaseModel<SoldierBase>>(context, this));
    }

    @Override
    public Identifier getTexture(SoldierBase entity) {
        return new Identifier("soldiersmod", "textures/entity/soldierbase/soldierbase.png");
    }
}
