package saperate.soldiersmod.entity;

import java.util.UUID;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
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

    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);

    public SoldierBase(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    protected void initGoals() {
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
    }

    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        Owner_UUID = player.getUuid();
        if (!player.getWorld().isClient) {
            /*
             * ItemStack newItem = new ItemStack(Items.DIAMOND, 1); // Replace with the
             * desired item
             * inventory.set(0, newItem); // Set the first slot of the soldier's inventory
             * to the new item
             */
            for (var i = 0; i < inventory.size(); i++) {
                System.out.print(inventory);
            }

            player.openHandledScreen(createScreenHandlerFactory());
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound tag) {
        super.writeCustomDataToNbt(tag);
        Inventories.writeNbt(tag, inventory);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound tag) {
        super.readCustomDataFromNbt(tag);
        inventory = DefaultedList.ofSize(getItems().size(), ItemStack.EMPTY);
        Inventories.readNbt(tag, inventory);
    }

    public NamedScreenHandlerFactory createScreenHandlerFactory() {
        return new SimpleNamedScreenHandlerFactory((syncId, playerInventory, playerEntity) -> {
                return new SoldierBaseScreenHandler(syncId, playerInventory, this);
            }, Text.of("Soldier Inventory"));
    }

}
