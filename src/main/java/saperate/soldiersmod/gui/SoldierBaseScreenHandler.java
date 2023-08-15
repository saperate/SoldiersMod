package saperate.soldiersmod.gui;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import saperate.soldiersmod.SoldiersMod;
import saperate.soldiersmod.SoldiersModClient;
import saperate.soldiersmod.entity.SoldierBase;

public class SoldierBaseScreenHandler  extends ScreenHandler {
   private static final int CONTAINER_SIZE = 14;
   private static final int INVENTORY_START = 9;
   private static final int INVENTORY_END = 36;
   private static final int HOTBAR_START = 36;
   private static final int HOTBAR_END = 45;
   private Inventory inventory;
   private UUID entityUuid;

   public SoldierBaseScreenHandler(int syncId, PlayerInventory playerInventory) {
      this(syncId, playerInventory, new SimpleInventory(CONTAINER_SIZE),null);
  }

  public SoldierBaseScreenHandler(int syncId, PlayerInventory playerInventory, UUID entityUuid) {
   this(syncId, playerInventory,  new SimpleInventory(CONTAINER_SIZE), entityUuid);
}


   public SoldierBaseScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, UUID entityUuid) {
      super(SoldiersMod.SOLDIER_SCREEN_HANDLER_TYPE, syncId);
      System.out.println("SyncId handler: " + syncId);
      checkSize(inventory, CONTAINER_SIZE);
      this.inventory = inventory;
      inventory.onOpen(playerInventory.player);
      this.entityUuid = entityUuid;
      //TODO ADD FIELD FOR NAME AND ENTITY THINGY
      
      

      int i;
      int j;
      int v;
      int b;
      for(v = 0; v < 4; v++){
        this.addSlot(new Slot(inventory, v,15, v * 18 + 1));
      }

      for(b = 0; b < 2; b++){
         this.addSlot(new Slot(inventory,b + v, 108 + b * 18,10));
      }
      v+=b;
      for(i = 0; i < 2; ++i) {
         for(j = 0; j < 4; ++j) {
            this.addSlot(new Slot(inventory, j + i * 4 + v, 90 + j * 18, 28 + i * 18));
         }
      }

      for(i = 0; i < 3; ++i) {
         for(j = 0; j < 9; ++j) {
            this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for(i = 0; i < 9; ++i) {
         this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
      }

   }

   public SoldierBaseScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
      super(SoldiersMod.SOLDIER_SCREEN_HANDLER_TYPE, syncId);
      inventory = new SimpleInventory(CONTAINER_SIZE);
      checkSize(inventory, CONTAINER_SIZE);
      inventory.onOpen(playerInventory.player);
      this.entityUuid = buf.readUuid();
      System.out.println("SoldierEntity received in screen handler: " + entityUuid);
      //TODO ADD FIELD FOR NAME AND ENTITY THINGY
      
      

      int i;
      int j;
      int v;
      int b;
      for(v = 0; v < 4; v++){
        this.addSlot(new Slot(inventory, v,15, v * 18 + 1));
      }

      for(b = 0; b < 2; b++){
         this.addSlot(new Slot(inventory,b + v, 108 + b * 18,10));
      }
      v+=b;
      for(i = 0; i < 2; ++i) {
         for(j = 0; j < 4; ++j) {
            this.addSlot(new Slot(inventory, j + i * 4 + v, 90 + j * 18, 28 + i * 18));
         }
      }

      for(i = 0; i < 3; ++i) {
         for(j = 0; j < 9; ++j) {
            this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for(i = 0; i < 9; ++i) {
         this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
      }
      
  }

   public boolean canUse(PlayerEntity player) {
      return this.inventory.canPlayerUse(player);
   }

   public ItemStack quickMove(PlayerEntity player, int slot) {
      ItemStack itemStack = ItemStack.EMPTY;
      Slot slot2 = (Slot)this.slots.get(slot);
      if (slot2 != null && slot2.hasStack()) {
         ItemStack itemStack2 = slot2.getStack();
         itemStack = itemStack2.copy();
         if (slot < 9) {
            if (!this.insertItem(itemStack2, 9, 45, true)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.insertItem(itemStack2, 0, 9, false)) {
            return ItemStack.EMPTY;
         }

         if (itemStack2.isEmpty()) {
            slot2.setStack(ItemStack.EMPTY);
         } else {
            slot2.markDirty();
         }

         if (itemStack2.getCount() == itemStack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot2.onTakeItem(player, itemStack2);
      }

      return itemStack;
   }

   public void onClosed(PlayerEntity player) {
      super.onClosed(player);
      this.inventory.onClose(player);
   }

   public UUID getEntity(){
      return entityUuid;
   }

   public Inventory getInventory(){
      return inventory;
   }

   public ScreenHandler getScreenHandlerInstance() {
      return this; // Return the current instance of the screen handler
  }
}
