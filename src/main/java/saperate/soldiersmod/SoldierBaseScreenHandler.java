package saperate.soldiersmod;

import java.util.List;

import com.mojang.datafixers.util.Pair;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import saperate.soldiersmod.SoldiersMod;
import saperate.soldiersmod.entity.SoldierBase;

public class SoldierBaseScreenHandler extends ScreenHandler {
    public static final Identifier EMPTY_HELMET_SLOT_TEXTURE = new Identifier("item/empty_armor_slot_helmet");
    public static final Identifier EMPTY_CHESTPLATE_SLOT_TEXTURE = new Identifier("item/empty_armor_slot_chestplate");
    public static final Identifier EMPTY_LEGGINGS_SLOT_TEXTURE = new Identifier("item/empty_armor_slot_leggings");
    public static final Identifier EMPTY_BOOTS_SLOT_TEXTURE = new Identifier("item/empty_armor_slot_boots");
    public static final Identifier EMPTY_OFFHAND_ARMOR_SLOT = new Identifier("item/empty_armor_slot_shield");
    private final Identifier[] EMPTY_ARMOR_SLOT_TEXTURES = new Identifier[]{EMPTY_BOOTS_SLOT_TEXTURE, EMPTY_LEGGINGS_SLOT_TEXTURE, EMPTY_CHESTPLATE_SLOT_TEXTURE, EMPTY_HELMET_SLOT_TEXTURE};
    private static final EquipmentSlot[] EQUIPMENT_SLOT_ORDER = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    private static final int CONTAINER_SIZE = 14;
    private static final int INVENTORY_START = 9;
    private static final int INVENTORY_END = 36;
    private static final int HOTBAR_START = 36;
    private static final int HOTBAR_END = 45;
    private final Inventory inventory;
    private final PlayerEntity player;
    private SoldierBase entity = null;

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


        for(int i = 0; i < 4; ++i) {
            EquipmentSlot equipmentSlot = EQUIPMENT_SLOT_ORDER[i];
            this.addSlot(new Slot(inventory, i, 15, 1 + i * 18) {
                public void setStack(ItemStack stack) {
                    super.setStack(stack);
                }

                public int getMaxItemCount() {
                    return 1;
                }

                public boolean canInsert(ItemStack stack) {
                    return equipmentSlot == MobEntity.getPreferredEquipmentSlot(stack);
                }

                public boolean canTakeItems(PlayerEntity playerEntity) {
                    ItemStack itemStack = this.getStack();
                    return (itemStack.isEmpty() || playerEntity.isCreative() || !EnchantmentHelper.hasBindingCurse(itemStack)) && super.canTakeItems(playerEntity);
                }

                public Pair<Identifier, Identifier> getBackgroundSprite() {
                    return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, EMPTY_ARMOR_SLOT_TEXTURES[equipmentSlot.getEntitySlotId()]);
                }
            });
        }


        for (int b = 0; b < 2; b++) {
            this.addSlot(new Slot(inventory, b + 4, 108 + b * 18, 19));
        }

        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 4; ++j) {
                this.addSlot(new Slot(inventory, j + i * 4 + 6, 90 + j * 18, 37 + i * 18));
            }
        }

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; ++i) {
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
        return ItemStack.EMPTY;
    }



    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.inventory.onClose(player);

        entity.setGuiVisible(false);
    }

    public PlayerEntity getPlayerEntity() {
        return player;
    }


    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        super.onSlotClick(slotIndex, button, actionType, player);
        if (!player.getWorld().isClient) {
            entity.equipItemStack();
        }

    }

    private void findEntity() {
        //get every SoldierBase in a 10 block radius
        List<SoldierBase> potentialEntities = player.getWorld().getEntitiesByClass(
                SoldierBase.class,
                new Box(player.getX() - 10, player.getY() - 10, player.getZ() - 10,
                        player.getX() + 10, player.getY() + 10, player.getZ() + 10),
                entity -> true);

        //iterate through the list to find the one that is opened
        for (var i = 0; i < potentialEntities.size(); i++) {
            var curr = potentialEntities.get(i);
            if (curr.Owner_UUID == player.getUuid() && curr.getGuiVisible()) {
                this.entity = curr;
                return;
            }
        }
    }
}