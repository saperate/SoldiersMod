package saperate.soldiersmod.entity;

import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import saperate.soldiersmod.gui.SoldierBaseScreenHandler;
import saperate.soldiersmod.gui.SoldierInventory;
import net.minecraft.nbt.NbtCompound;

public class SoldierBase extends PathAwareEntity implements SoldierInventory {
    public UUID Owner_UUID;
    public UUID[] Auth_UUID;
    private Boolean IsGUIOpened;

    private DefaultedList<ItemStack> items = DefaultedList.ofSize(27, ItemStack.EMPTY);

    public SoldierBase(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        setGuiVisible(false);
    }

    protected void initGoals() {
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
    }

    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        setGuiVisible(true);
        Inventory inventory = (Inventory) this;

        Owner_UUID = player.getUuid();
        if (!player.getWorld().isClient) {
            /*
             * ItemStack newItsem = new ItemStack(Items.DIAMOND, 1); // Replace with the
             * desired item
             * inventory.set(0, newItem); // Set the first slot of the soldier's inventory
             * to the new item
             */
            player.openHandledScreen(createScreenHandlerFactory(inventory));
            var y = this.getEquippedStack(EquipmentSlot.CHEST);
            System.out.println(y);

        }
        return ActionResult.SUCCESS;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound tag) {
        super.writeCustomDataToNbt(tag);
        Inventories.writeNbt(tag, items);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound tag) {
        super.readCustomDataFromNbt(tag);
        items = DefaultedList.ofSize(getItems().size(), ItemStack.EMPTY);
        Inventories.readNbt(tag, items);
    }

    public NamedScreenHandlerFactory createScreenHandlerFactory(Inventory inventory) {
        return new SimpleNamedScreenHandlerFactory((syncId, playerInventory, playerEntity) -> {
                return new SoldierBaseScreenHandler(syncId, playerInventory, inventory);
            }, Text.of("Soldier Inventory"));
    }

    public void setGuiVisible(boolean val){
        IsGUIOpened = val;
        return;
    }

    public Boolean getGuiVisible(){
        return IsGUIOpened;
    }

}