package saperate.soldiersmod.gui;

import java.util.UUID;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SoldierBaseScreen extends HandledScreen<SoldierBaseScreenHandler> {
   private static final Identifier TEXTURE = new Identifier("soldiersmod","textures/gui/soldierscreen.png");

   private static final int ARMOR_SLOT_X = 8;
    private static final int ARMOR_SLOT_Y = 18;
    private static final int WEAPON_SLOT_X = 8;
    private static final int WEAPON_SLOT_Y = 54;
    private static final int OFFHAND_SLOT_X = 8;
    private static final int OFFHAND_SLOT_Y = 72;
    private final SoldierBaseScreenHandler handler;

    public SoldierBaseScreen(SoldierBaseScreenHandler handler,PlayerInventory plrInv,PacketByteBuf buf){
      
      super(handler,plrInv, Text.of("Hi"));

      this.handler = handler;
      System.out.println("SyncId Screen:" + this.handler.syncId);
    }

    public SoldierBaseScreen(SoldierBaseScreenHandler handler, PlayerInventory inventory, Text title) {
      super(handler, inventory, title);
      this.handler = handler;

      System.out.println(handler);
      System.out.println("-=-=-=-");
      System.out.println(handler.getEntity());
      System.out.println("-=-=-=-");
      System.out.println(handler.getInventory());
      System.out.println("-=-=-=-");
      System.out.println(this.client.player);
   }

   protected void init() {
      super.init();
   }
   

   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      this.renderBackground(context);
      super.render(context, mouseX, mouseY, delta);
      this.drawMouseoverTooltip(context, mouseX, mouseY);
   }

   protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
      int i = (this.width - this.backgroundWidth) / 2;
      int j = (this.height - 32 - this.backgroundHeight) / 2;
      context.drawTexture(TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight + 16);
   }

   
   @Override
   protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
      //Left empty cause i dont want a title in my GUI
   }

}
