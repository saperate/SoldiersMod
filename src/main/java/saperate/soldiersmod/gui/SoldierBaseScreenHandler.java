package saperate.soldiersmod.gui;

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
import saperate.soldiersmod.SoldiersMod;
import saperate.soldiersmod.SoldiersModClient;
import saperate.soldiersmod.entity.SoldierBase;

public class SoldierBaseScreenHandler  extends ScreenHandler {
   private static final int CONTAINER_SIZE = 14;
   private static final int INVENTORY_START = 9;
   private static final int INVENTORY_END = 36;
   private static final int HOTBAR_START = 36;
   private static final int HOTBAR_END = 45;
   private final Inventory inventory;

   public SoldierBaseScreenHandler(int syncId, PlayerInventory playerInventory) {
      this(syncId, playerInventory, new SimpleInventory(CONTAINER_SIZE));
   }

   public SoldierBaseScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
      super(SoldiersMod.SOLDIER_SCREEN_HANDLER_TYPE, syncId);
      checkSize(inventory, CONTAINER_SIZE);
      this.inventory = inventory;
      inventory.onOpen(playerInventory.player);

      

      int i;
      int j;
      int v;
      for(v = 0; v < 4; v++){
        this.addSlot(new Slot(inventory, v,15, v * 18 + 1));
      }

      this.addSlot(new Slot(inventory,0 + 1, 70 + v * 18,100 * 18));
      this.addSlot(new Slot(inventory,1 + 2, 70 + v * 18,100 * 18));
      v += 2;

      for(i = 0; i < 2; ++i) {
         for(j = 0; j < 4; ++j) {
            this.addSlot(new Slot(inventory, j + i * 4 + v, 90 + j * 18, 37 + i * 18));
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
        int inventorySize = buf.readInt();
        NbtCompound tag = buf.readNbt();
        this.inventory = new SimpleInventory(1);
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
}
