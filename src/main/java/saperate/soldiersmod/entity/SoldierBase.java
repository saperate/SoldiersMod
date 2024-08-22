package saperate.soldiersmod.entity;

import java.util.UUID;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.AvoidSunlightGoal;
import net.minecraft.entity.ai.goal.EscapeSunlightGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;
import saperate.soldiersmod.SoldierBaseScreenHandler;

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
      this.goalSelector.add(2, new AvoidSunlightGoal(this));
      this.goalSelector.add(3, new EscapeSunlightGoal(this, 1.0));
      this.goalSelector.add(3, new FleeEntityGoal(this, WolfEntity.class, 6.0F, .5f, .5f));
      this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0));
      this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.add(6, new LookAroundGoal(this));
      this.targetSelector.add(1, new RevengeGoal(this));
      this.targetSelector.add(2, new ActiveTargetGoal(this, PlayerEntity.class, true));
      this.targetSelector.add(3, new ActiveTargetGoal(this, IronGolemEntity.class, true));
      this.targetSelector.add(3, new ActiveTargetGoal(this, TurtleEntity.class, 10, true, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
      this.goalSelector.add(4, new MeleeAttackGoal(this,1.0,true));
    }

    public static DefaultAttributeContainer.Builder createHostileAttributes() {
        return MobEntity.createMobAttributes()
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE)
            .add(EntityAttributes.GENERIC_MAX_HEALTH)
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        // Add more attributes as needed
    }
    
    

    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        setGuiVisible(true);
        equippedToInv(this);
        Owner_UUID = player.getUuid();
        if (!player.getWorld().isClient) {
            NamedScreenHandlerFactory screenHandlerFactory = createScreenHandlerFactory(this);
            player.openHandledScreen(screenHandlerFactory);
        }
        System.out.println(speed);
        speed = 1.0f;
        return ActionResult.SUCCESS;
    }

    public static NamedScreenHandlerFactory createScreenHandlerFactory(Inventory inventory) {
        return new SimpleNamedScreenHandlerFactory((syncId, playerInventory, playerEntity) -> {
            return new SoldierBaseScreenHandler(syncId, playerInventory, inventory);
        }, Text.of("Soldier Inventory"));
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


    public void setGuiVisible(boolean val) {
        IsGUIOpened = val;
    }

    public Boolean getGuiVisible() {
        return IsGUIOpened;
    }

    public void equipItemStack() {
        System.out.println("Equipping armor: " + items.get(1));
        this.equipStack(EquipmentSlot.HEAD, items.get(0));
        this.equipStack(EquipmentSlot.CHEST, items.get(1));
        this.equipStack(EquipmentSlot.LEGS, items.get(2));
        this.equipStack(EquipmentSlot.FEET, items.get(3));

        this.equipStack(EquipmentSlot.MAINHAND, items.get(4));
        this.equipStack(EquipmentSlot.OFFHAND, items.get(5));
    }

    public Inventory equippedToInv(Inventory inventory) {
        // also very ugly but as before, idk what else to do
        inventory.setStack(0, this.getEquippedStack(EquipmentSlot.HEAD).copy());
        inventory.setStack(1, this.getEquippedStack(EquipmentSlot.CHEST).copy());
        inventory.setStack(2, this.getEquippedStack(EquipmentSlot.LEGS).copy());
        inventory.setStack(3, this.getEquippedStack(EquipmentSlot.FEET).copy());

        inventory.setStack(4, this.getEquippedStack(EquipmentSlot.MAINHAND).copy());
        inventory.setStack(5, this.getEquippedStack(EquipmentSlot.OFFHAND).copy());
        return inventory;
    }


}