package saperate.soldiersmod.entity;

import java.util.UUID;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import saperate.soldiersmod.SoldiersMod;
import saperate.soldiersmod.SoldiersMod.PacketIdentifiers;
import saperate.soldiersmod.gui.SoldierBaseScreenHandler;
import saperate.soldiersmod.gui.SoldierInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerPlayerEntity;

public class SoldierBase extends PathAwareEntity implements SoldierInventory {
    private final UUID entityUUID;
    public UUID Owner_UUID;
    public UUID[] Auth_UUID;
    private static int nextSyncId = 0;

    public DefaultedList<ItemStack> inventory = DefaultedList.ofSize(14, ItemStack.EMPTY);

    public SoldierBase(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.setCustomNameVisible(true);
        this.entityUUID = UUID.randomUUID();
    }

    protected void initGoals() {
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
    }

    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        Owner_UUID = player.getUuid();
        ServerPlayerEntity serverPlayer;
        if (player instanceof ServerPlayerEntity) {

            // use packets to open gui

            serverPlayer = (ServerPlayerEntity) player;
            int syncId = nextSyncId++;
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeUuid(entityUUID);
            buf.writeInt(syncId);

            PacketByteBuf buf2 = PacketByteBufs.create();
            buf2.writeUuid(entityUUID);
            buf2.writeInt(syncId);

            // Create the packet handler using the player's inventory and syncId
            SoldiersMod.SOLDIER_SCREEN_HANDLER_TYPE.create(syncId, player.getInventory(),buf2);
            // Send the packet to the serverPlayer
            ServerPlayNetworking.send(serverPlayer, PacketIdentifiers.OPEN_SOLDIER_SCREEN, buf);

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

    // TODO Add health bar to ui

}
