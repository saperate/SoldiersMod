package saperate.soldiersmod.gui;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import com.mojang.blaze3d.systems.RenderSystem;

import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import saperate.soldiersmod.entity.SoldierBase;
import net.minecraft.network.PacketByteBuf;

public class SoldierBaseScreen extends HandledScreen<SoldierBaseScreenHandler> {
   private static final Identifier TEXTURE = new Identifier("soldiersmod", "textures/gui/soldierscreen.png");

   private static final int ARMOR_SLOT_X = 8;
   private static final int ARMOR_SLOT_Y = 18;
   private static final int WEAPON_SLOT_X = 8;
   private static final int WEAPON_SLOT_Y = 54;
   private static final int OFFHAND_SLOT_X = 8;
   private static final int OFFHAND_SLOT_Y = 72;

   //private TextFieldWidget nameField;
   public PlayerEntity player;
   public SoldierBase entity;

   public SoldierBaseScreen(SoldierBaseScreenHandler handler, PlayerInventory inventory, Text title) {
      super(handler, inventory, title);
      player = handler.getPlayerEntity();
      findEntity();
   }

   public SoldierBase findEntity(){
      //get every SoldierBase in a 10 block radius
      List<SoldierBase> potentialEntities = player.getWorld().getEntitiesByClass(
            SoldierBase.class,
            new Box(player.getX() - 10, player.getY() - 10, player.getZ() - 10,
                  player.getX() + 10, player.getY() + 10, player.getZ() + 10),
            entity -> true);

      //iterate through the list to find the one that is opened
      for (var i = 0; i < potentialEntities.size(); i++) {
         var curr = potentialEntities.get(i);
         var IsGUIOpened = curr.getGuiVisible();
         if (curr.Owner_UUID == player.getUuid() && IsGUIOpened == true) {
            this.entity = curr;
         }
      }

      return entity;
   }

   protected void init() {
      super.init();
      //nameField = this.addDrawableChild(new TextFieldWidget(this.textRenderer, 1, 1, 100, 10, this.title));
      //nameField.setMaxLength(50);
      //nameField.setFocusUnlocked(true);
      //setInitialFocus(nameField);
   }

   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      this.renderBackground(context);
      super.render(context, mouseX, mouseY, delta);
      this.drawMouseoverTooltip(context, mouseX, mouseY);
   }

   protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
      int i = (this.width - this.backgroundWidth) / 2;
      int j = (this.height - 32 - this.backgroundHeight) / 2;
      context.drawTexture(TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight + 32);
      drawEntity(context, i + 61, j + 83, 30, (float)(i + 61) - mouseX, (float)(j + 83 - 50) - mouseY, this.entity);
   }

   @Override
   protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
      // Left empty cause i dont want a title in my GUI
   }

/* 
   @Override
   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      // Override to prvent from closing while typing
      if (nameField.isActive() && keyCode != 256) {
         return true;
      }
      if (super.keyPressed(keyCode, scanCode, modifiers)) {
         return true;
      } else if (this.client.options.inventoryKey.matchesKey(keyCode, scanCode)) {
         this.close();
         return true;
      } else {
         this.handleHotbarKeyPressed(keyCode, scanCode);
         if (this.focusedSlot != null && this.focusedSlot.hasStack()) {
            if (this.client.options.pickItemKey.matchesKey(keyCode, scanCode)) {
               this.onMouseClick(this.focusedSlot, this.focusedSlot.id, 0, SlotActionType.CLONE);
            } else if (this.client.options.dropKey.matchesKey(keyCode, scanCode)) {
               this.onMouseClick(this.focusedSlot, this.focusedSlot.id, hasControlDown() ? 1 : 0, SlotActionType.THROW);
            }
         }

         return true;
      }
   }
*/
   public void removed() {
      if (this.client.player != null) {
         this.handler.onClosed(this.client.player);
      }
   }

   


   
   public static void drawEntity(DrawContext context, int x, int y, int size, float mouseX, float mouseY, LivingEntity entity) {
      float f = (float)Math.atan((double)(mouseX / 40.0F));
      float g = (float)Math.atan((double)(mouseY / 40.0F));
      Quaternionf quaternionf = (new Quaternionf()).rotateZ(3.1415927F);
      Quaternionf quaternionf2 = (new Quaternionf()).rotateX(g * 20.0F * 0.017453292F);
      quaternionf.mul(quaternionf2);
      float h = entity.bodyYaw;
      float i = entity.getYaw();
      float j = entity.getPitch();
      float k = entity.prevHeadYaw;
      float l = entity.headYaw;
      entity.bodyYaw = 180.0F + f * 20.0F;
      entity.setYaw(180.0F + f * 40.0F);
      entity.setPitch(-g * 20.0F);
      entity.headYaw = entity.getYaw();
      entity.prevHeadYaw = entity.getYaw();
      drawEntity(context, x, y, size, quaternionf, quaternionf2, entity);
      entity.bodyYaw = h;
      entity.setYaw(i);
      entity.setPitch(j);
      entity.prevHeadYaw = k;
      entity.headYaw = l;
   }

   public static void drawEntity(DrawContext context, int x, int y, int size, Quaternionf quaternionf, @Nullable Quaternionf quaternionf2, LivingEntity entity) {
      context.getMatrices().push();
      context.getMatrices().translate((double)x, (double)y, 50.0);
      context.getMatrices().multiplyPositionMatrix((new Matrix4f()).scaling((float)size, (float)size, (float)(-size)));
      context.getMatrices().multiply(quaternionf);
      DiffuseLighting.method_34742();
      EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
      if (quaternionf2 != null) {
         quaternionf2.conjugate();
         entityRenderDispatcher.setRotation(quaternionf2);
      }

      entityRenderDispatcher.setRenderShadows(false);
      RenderSystem.runAsFancy(() -> {
         entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, context.getMatrices(), context.getVertexConsumers(), 15728880);
      });
      context.draw();
      entityRenderDispatcher.setRenderShadows(true);
      context.getMatrices().pop();
      DiffuseLighting.enableGuiDepthLighting();
   }


}