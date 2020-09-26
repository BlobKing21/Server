package org.cloudburstmc.server.inventory;

import com.google.common.base.Preconditions;
import com.nukkitx.protocol.bedrock.data.inventory.ItemData;
import com.nukkitx.protocol.bedrock.packet.InventoryContentPacket;
import com.nukkitx.protocol.bedrock.packet.InventorySlotPacket;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.val;
import org.cloudburstmc.server.Server;
import org.cloudburstmc.server.blockentity.BlockEntity;
import org.cloudburstmc.server.entity.impl.BaseEntity;
import org.cloudburstmc.server.event.entity.EntityInventoryChangeEvent;
import org.cloudburstmc.server.event.inventory.InventoryOpenEvent;
import org.cloudburstmc.server.item.CloudItemStack;
import org.cloudburstmc.server.item.ItemStack;
import org.cloudburstmc.server.player.Player;

import javax.annotation.Nonnull;
import java.util.*;

import static org.cloudburstmc.server.block.BlockTypes.AIR;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class BaseInventory implements Inventory {

    protected final InventoryType type;

    protected int maxStackSize = MAX_STACK;

    protected int size;

    protected final String name;

    protected final String title;

    public final Int2ObjectMap<ItemStack> slots = new Int2ObjectOpenHashMap<>();

    protected final Set<Player> viewers = new HashSet<>();

    protected InventoryHolder holder;

    public BaseInventory(InventoryHolder holder, InventoryType type) {
        this(holder, type, new HashMap<>());
    }

    public BaseInventory(InventoryHolder holder, InventoryType type, Map<Integer, ItemStack> items) {
        this(holder, type, items, null);
    }

    public BaseInventory(InventoryHolder holder, InventoryType type, Map<Integer, ItemStack> items, Integer overrideSize) {
        this(holder, type, items, overrideSize, null);
    }

    public BaseInventory(InventoryHolder holder, InventoryType type, Map<Integer, ItemStack> items, Integer overrideSize, String overrideTitle) {
        this.holder = holder;

        this.type = type;

        if (overrideSize != null) {
            this.size = overrideSize;
        } else {
            this.size = this.type.getDefaultSize();
        }

        if (overrideTitle != null) {
            this.title = overrideTitle;
        } else {
            this.title = this.type.getDefaultTitle();
        }

        this.name = this.type.getDefaultTitle();

        if (!(this instanceof DoubleChestInventory)) {
            this.setContents(items);
        }
    }

    @Override
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public int getMaxStackSize() {
        return maxStackSize;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public ItemStack getItem(int index) {
        return this.slots.containsKey(index) ? this.slots.get(index) : ItemStack.get(AIR);
    }

    @Override
    public Map<Integer, ItemStack> getContents() {
        return new HashMap<>(this.slots);
    }

    @Override
    public void setContents(Map<Integer, ItemStack> items) {
        if (items.size() > this.size) {
            TreeMap<Integer, ItemStack> newItems = new TreeMap<>();
            for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
                newItems.put(entry.getKey(), entry.getValue());
            }
            items = newItems;
            newItems = new TreeMap<>();
            int i = 0;
            for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
                newItems.put(entry.getKey(), entry.getValue());
                i++;
                if (i >= this.size) {
                    break;
                }
            }
            items = newItems;
        }

        for (int i = 0; i < this.size; ++i) {
            if (!items.containsKey(i)) {
                if (this.slots.containsKey(i)) {
                    this.clear(i);
                }
            } else {
                if (!this.setItem(i, items.get(i))) {
                    this.clear(i);
                }
            }
        }
    }

    @Override
    public boolean setItem(int index, ItemStack item, boolean send) {
        if (index < 0 || index >= this.size) {
            return false;
        } else if (item.getType() == AIR || item.getCount() <= 0) {
            return this.clear(index, send);
        }

        InventoryHolder holder = this.getHolder();
        if (holder instanceof BaseEntity) {
            EntityInventoryChangeEvent ev = new EntityInventoryChangeEvent((BaseEntity) holder, this.getItem(index), item, index);
            Server.getInstance().getEventManager().fire(ev);
            if (ev.isCancelled()) {
                this.sendSlot(index, this.getViewers());
                return false;
            }

            item = ev.getNewItem();
        }

        if (holder instanceof BlockEntity) {
            ((BlockEntity) holder).setDirty();
        }

        ItemStack old = this.getItem(index);
        this.slots.put(index, item);
        this.onSlotChange(index, old, send);

        return true;
    }

    @Override
    public boolean contains(ItemStack item) {
        int count = Math.max(1, item.getCount());
        for (ItemStack i : this.getContents().values()) {
            if (item.equals(i)) {
                count -= i.getCount();
                if (count <= 0) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public Map<Integer, ItemStack> all(ItemStack item) {
        Map<Integer, ItemStack> slots = new HashMap<>();
        for (Map.Entry<Integer, ItemStack> entry : this.getContents().entrySet()) {
            if (item.equals(entry.getValue())) {
                slots.put(entry.getKey(), entry.getValue());
            }
        }

        return slots;
    }

    @Override
    public void remove(ItemStack item) {
        for (Map.Entry<Integer, ItemStack> entry : this.getContents().entrySet()) {
            if (item.equals(entry.getValue())) {
                this.clear(entry.getKey());
            }
        }
    }

    @Override
    public int first(ItemStack item, boolean exact) {
        int count = Math.max(1, item.getCount());
        for (Map.Entry<Integer, ItemStack> entry : this.getContents().entrySet()) {
            if (item.equals(entry.getValue()) && (entry.getValue().getCount() == count || (!exact && entry.getValue().getCount() > count))) {
                return entry.getKey();
            }
        }

        return -1;
    }

    @Override
    public int firstEmpty() {
        for (int i = 0; i < this.size; ++i) {
            if (this.getItem(i).isNull()) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public int firstNonEmpty() {
        for (int i = 0; i < this.size; ++i) {
            if (!this.getItem(i).isNull()) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public int firstFit(ItemStack item, boolean single) {
        int count = single ? 1 : item.getCount();
        val maxStackSize = item.getBehavior().getMaxStackSize(item);

        for (int i = 0; i < this.size; ++i) {
            ItemStack slot = this.getItem(i);
            if (slot.getCount() + count < maxStackSize && slot.equals(item)) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public void decrementCount(int slot) {
        ItemStack item = this.getItem(slot);

        if (item.getCount() > 0) {
            this.setItem(slot, item.decrementAmount());
        }
    }

    @Override
    public void incrementCount(int slot) {
        ItemStack item = this.getItem(slot);

        if (item.getType() != AIR) {
            this.setItem(slot, item.decrementAmount());
        }
    }

    @Override
    public boolean canAddItem(ItemStack item) {
        int count = item.getAmount();

        if (count <= 0) {
            return true;
        }

        for (int i = 0; i < this.getSize(); ++i) {
            ItemStack slot = this.getItem(i);
            if (item.equals(slot)) {
                int diff;
                if ((diff = slot.getBehavior().getMaxStackSize(slot) - slot.getCount()) > 0) {
                    count -= diff;
                }
            } else if (slot.getType() == AIR) {
                count += this.getMaxStackSize();
            }

            if (count <= 0) {
                return true;
            }
        }

        return false;
    }

    @SuppressWarnings("SuspiciousListRemoveInLoop")
    @Override
    public ItemStack[] addItem(ItemStack... slots) {
        Preconditions.checkNotNull(slots, "slots");

        if (slots.length == 0) {
            return slots;
        }

        List<ItemStack> itemSlots = new ArrayList<>(slots.length);
        for (ItemStack slot : slots) {
            if (!slot.isNull()) {
                itemSlots.add(slot);
            }
        }

        List<Integer> emptySlots = new ArrayList<>();

        for (int i = 0; i < this.getSize(); ++i) {
            ItemStack item = this.getItem(i);
            if (item.isNull()) {
                emptySlots.add(i);
            }

            int maxStack = item.getBehavior().getMaxStackSize(item);

            val copy = new ArrayList<>(itemSlots);
            for (int j = 0; j < copy.size(); j++) {
                ItemStack slot = copy.get(i);

                if (slot.equals(item) && item.getCount() < maxStack) {
                    int amount = Math.min(maxStack - item.getCount(), slot.getCount());
                    amount = Math.min(amount, this.getMaxStackSize());
                    if (amount > 0) {
                        slot = slot.decrementAmount(amount);
                        this.setItem(i, item.incrementAmount(amount));

                        if (slot.getCount() <= 0) {
                            itemSlots.remove(j);
                        } else {
                            itemSlots.set(j, slot);
                        }
                    }
                }
            }

            if (itemSlots.isEmpty()) {
                break;
            }
        }

        if (!itemSlots.isEmpty() && !emptySlots.isEmpty()) {
            for (int slotIndex : emptySlots) {
                if (!itemSlots.isEmpty()) {
                    ItemStack slot = itemSlots.get(0);
                    int amount = Math.min(slot.getBehavior().getMaxStackSize(slot), slot.getCount());
                    amount = Math.min(amount, this.getMaxStackSize());
                    slot = slot.decrementAmount(amount);
                    ItemStack item = slot.toBuilder().amount(amount).build();
                    this.setItem(slotIndex, item);
                    if (slot.getCount() <= 0) {
                        itemSlots.remove(slot);
                    }
                }
            }
        }

        return itemSlots.toArray(new ItemStack[0]);
    }

    protected int getEmptySlotsCount() {
        int count = 0;

        for (ItemStack item : this.slots.values()) {
            if (item == null || item.isNull()) {
                count++;
            }
        }

        return count;
    }

    private synchronized Int2ObjectMap<ItemStack> findMergable(@Nonnull ItemStack item) {
        Int2ObjectMap<ItemStack> mergable = new Int2ObjectOpenHashMap<>();

        for (val entry : this.slots.int2ObjectEntrySet()) {
            ItemStack content = entry.getValue();

            if (content != null && content.isMergeable(item)) {
                mergable.put(entry.getIntKey(), content);
            }
        }

        return mergable;
    }

    public boolean addItemToFirstEmptySlot(ItemStack item) {
        int slot = firstEmpty();
        if (slot < 0) {
            return false;
        }

        setItem(slot, item);
        return true;
    }

    @Override
    public ItemStack[] removeItem(ItemStack... slots) {
        List<ItemStack> itemSlots = new ArrayList<>();
        for (ItemStack slot : slots) {
            if (!slot.isNull()) {
                itemSlots.add(slot);
            }
        }

        for (int i = 0; i < this.size; ++i) {
            ItemStack item = this.getItem(i);
            if (item.isNull()) {
                continue;
            }

            for (ItemStack slot : new ArrayList<>(itemSlots)) {
                if (slot.equals(item)) {
                    int amount = Math.min(item.getCount(), slot.getCount());
                    slot = slot.decrementAmount(amount);
                    item = item.decrementAmount(amount);
                    this.setItem(i, item);
                    if (slot.getCount() <= 0) {
                        itemSlots.remove(slot);
                    }

                }
            }

            if (itemSlots.size() == 0) {
                break;
            }
        }

        return itemSlots.toArray(new ItemStack[0]);
    }

    @Override
    public boolean clear(int index, boolean send) {
        if (this.slots.containsKey(index)) {
            ItemStack item = ItemStack.get(AIR);
            ItemStack old = this.slots.get(index);
            InventoryHolder holder = this.getHolder();
            if (holder instanceof BaseEntity) {
                EntityInventoryChangeEvent ev = new EntityInventoryChangeEvent((BaseEntity) holder, old, item, index);
                Server.getInstance().getEventManager().fire(ev);
                if (ev.isCancelled()) {
                    this.sendSlot(index, this.getViewers());
                    return false;
                }
                item = ev.getNewItem();
            }
            if (holder instanceof BlockEntity) {
                ((BlockEntity) holder).setDirty();
            }

            if (!item.isNull()) {
                this.slots.put(index, item);
            } else {
                this.slots.remove(index);
            }

            this.onSlotChange(index, old, send);
        }

        return true;
    }

    @Override
    public void clearAll() {
        for (Integer index : this.getContents().keySet()) {
            this.clear(index);
        }
    }

    @Override
    public Set<Player> getViewers() {
        return viewers;
    }

    @Override
    public InventoryHolder getHolder() {
        return holder;
    }

    @Override
    public void setMaxStackSize(int maxStackSize) {
        this.maxStackSize = maxStackSize;
    }

    @Override
    public boolean open(Player who) {
        InventoryOpenEvent ev = new InventoryOpenEvent(this, who);
        who.getServer().getEventManager().fire(ev);
        if (ev.isCancelled()) {
            return false;
        }
        this.onOpen(who);

        return true;
    }

    @Override
    public void close(Player who) {
        this.onClose(who);
    }

    @Override
    public void onOpen(Player who) {
        this.viewers.add(who);
    }

    @Override
    public void onClose(Player who) {
        this.viewers.remove(who);
    }

    @Override
    public void onSlotChange(int index, ItemStack before, boolean send) {
        if (send) {
            this.sendSlot(index, this.getViewers());
        }
    }

    @Override
    public void sendContents(Player player) {
        this.sendContents(new Player[]{player});
    }

    @Override
    public void sendContents(Player... players) {
        InventoryContentPacket packet = new InventoryContentPacket();
        packet.setContents(new ItemData[this.getSize()]);
        for (int i = 0; i < this.getSize(); ++i) {
            packet.getContents()[i] = ((CloudItemStack) this.getItem(i)).getNetworkData();
        }

        for (Player player : players) {
            int id = player.getWindowId(this);
            if (id == -1 || !player.spawned) {
                this.close(player);
                continue;
            }
            packet.setContainerId(id);
            player.sendPacket(packet);
        }
    }

    @Override
    public boolean isFull() {
        if (this.slots.size() < this.getSize()) {
            return false;
        }

        for (ItemStack item : this.slots.values()) {
            if (item == null || item.isNull() || item.getCount() < item.getBehavior().getMaxStackSize(item) || item.getCount() < this.getMaxStackSize()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isEmpty() {
        if (this.getMaxStackSize() <= 0) {
            return false;
        }

        for (ItemStack item : this.slots.values()) {
            if (item != null && !item.isNull()) {
                return false;
            }
        }

        return true;
    }

    public int getFreeSpace(ItemStack item) {
        int maxStackSize = Math.min(item.getBehavior().getMaxStackSize(item), this.getMaxStackSize());
        int space = (this.getSize() - this.slots.size()) * maxStackSize;

        for (ItemStack slot : this.getContents().values()) {
            if (slot == null || slot.isNull()) {
                space += maxStackSize;
                continue;
            }

            if (slot.equals(item, true, true)) {
                space += maxStackSize - slot.getCount();
            }
        }

        return space;
    }

    @Override
    public void sendContents(Collection<Player> players) {
        this.sendContents(players.toArray(new Player[0]));
    }

    @Override
    public void sendSlot(int index, Player player) {
        this.sendSlot(index, new Player[]{player});
    }

    @Override
    public void sendSlot(int index, Player... players) {
        InventorySlotPacket packet = new InventorySlotPacket();
        packet.setSlot(index);
        packet.setItem(((CloudItemStack) this.getItem(index)).getNetworkData());

        for (Player player : players) {
            int id = player.getWindowId(this);
            if (id == -1) {
                this.close(player);
                continue;
            }
            packet.setContainerId(id);
            player.sendPacket(packet);
        }
    }

    @Override
    public void sendSlot(int index, Collection<Player> players) {
        this.sendSlot(index, players.toArray(new Player[0]));
    }

    @Override
    public InventoryType getType() {
        return type;
    }
}