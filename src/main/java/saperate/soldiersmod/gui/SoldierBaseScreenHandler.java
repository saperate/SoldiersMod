package saperate.soldiersmod.gui;

import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
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
   private final PlayerEntity player;
   private SoldierBase entity = null;
   private SoldierBaseScreen screen = null;

   public SoldierBaseScreenHandler(int syncId, PlayerInventory playerInventory) {
      this(syncId, playerInventory, new SimpleInventory(CONTAINER_SIZE));
   }

   public SoldierBaseScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
      super(SoldiersMod.SOLDIER_SCREEN_HANDLER_TYPE, syncId);
      checkSize(inventory, CONTAINER_SIZE);
      this.inventory = inventory;
      this.player = playerInventory.player;
      findEntity();

      inventory.onOpen(player);
      //TODO ADD FIELD FOR NAME AND ENTITY THINGY

      int i;
      int j;
      int v;
      int b;
      for(v = 0; v < 4; v++){
        var slot = this.addSlot(new Slot(inventory, v,15, v * 18 + 1));
        
      }

      for(b = 0; b < 2; b++){
         this.addSlot(new Slot(inventory,b + v, 108 + b * 18,19));
      }
      v+=b;
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
        this.inventory = new SimpleInventory(CONTAINER_SIZE);
        this.player = playerInventory.player;
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
         if (slot2.id >= 0 && slot2.id <= 6) {
            entity.equipItemStack();
         }
      }

      return itemStack;
   }

   public void onClosed(PlayerEntity player) {
      super.onClosed(player);
      this.inventory.onClose(player);

      entity.setGuiVisible(false);
   }

   public PlayerEntity getPlayerEntity(){
      return player;
   }


   public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
      super.onSlotClick(slotIndex,button,actionType,player);
      if(!player.getWorld().isClient){
         entity.equipItemStack();
      }
      
   }

   private void findEntity(){
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
            return;
         }
      }
   }
}