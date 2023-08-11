package saperate.soldiersmod.gui;

import org.joml.Quaternionf;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import saperate.soldiersmod.entity.SoldierBase;

public class SoldierBaseScreen extends HandledScreen<SoldierBaseScreenHandler> {
   private static final Identifier TEXTURE = new Identifier("soldiersmod","textures/gui/soldierscreen.png");

   private static final int ARMOR_SLOT_X = 8;
    private static final int ARMOR_SLOT_Y = 18;
    private static final int WEAPON_SLOT_X = 8;
    private static final int WEAPON_SLOT_Y = 54;
    private static final int OFFHAND_SLOT_X = 8;
    private static final int OFFHAND_SLOT_Y = 72;

    private TextFieldWidget nameField;

   public SoldierBaseScreen(SoldierBaseScreenHandler handler, PlayerInventory inventory, Text title) {
      super(handler, inventory, title);
   }

   protected void init() {
      super.init();
      nameField = this.addDrawableChild(new TextFieldWidget(this.textRenderer,1,1,100,10,this.title));
      nameField.setMaxLength(50);
      nameField.setFocusUnlocked(true);
      setInitialFocus(nameField);
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
   }

   
   @Override
   protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
      //Left empty cause i dont want a title in my GUI
   }

   @Override
   public boolean keyPressed(int keyCode, int scanCode, int modifiers) { 
      // Override to prvent from closing while typing
      if(nameField.isActive() && keyCode != 256){
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
}
