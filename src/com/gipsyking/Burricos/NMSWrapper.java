package com.gipsyking.Burricos;

// all the cancer, at least it's in one place
import net.minecraft.server.v1_7_R1.EntityHorse;
import net.minecraft.server.v1_7_R1.IInventoryListener;
import net.minecraft.server.v1_7_R1.InventoryHorseChest;
import net.minecraft.server.v1_7_R1.InventorySubcontainer;
import net.minecraft.server.v1_7_R1.NBTTagCompound;
import net.minecraft.server.v1_7_R1.NBTTagList;
import net.minecraft.server.v1_7_R1.NBTTagString;
import net.minecraft.server.v1_7_R1.ItemStack;

import org.bukkit.craftbukkit.v1_7_R1.entity.CraftHorse;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_7_R1.util.CraftMagicNumbers;
import org.bukkit.Material;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;


public class NMSWrapper {

	public static org.bukkit.inventory.ItemStack zip(org.bukkit.inventory.ItemStack[] stacks) {
		ItemStack nmsStack = CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.CHEST));
		NBTTagCompound tag = new NBTTagCompound();
		nmsStack.setTag(tag);
		
		NBTTagCompound display = new NBTTagCompound();
		display.setString("Name", "Donkey.zip");
		NBTTagList lore = new NBTTagList();
		lore.add(new NBTTagString(Burricos.ZIP_LORE));
		display.set("Lore", lore);
		
		NBTTagList contents = new NBTTagList();
		
		for (org.bukkit.inventory.ItemStack invStack: stacks) {
			NBTTagCompound itemTag = new NBTTagCompound();
			if (invStack != null) {
				CraftItemStack.asNMSCopy(invStack).save(itemTag);
			}
						
			contents.add(itemTag);
		}
		
		tag.set("display", display);
		tag.set("contents", contents);
		
		return CraftItemStack.asCraftMirror(nmsStack);
	}

	public static void unzip(org.bukkit.inventory.ItemStack itemStack, Inventory inventory) {
		ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
		if (nmsItemStack == null) {
			return;
		}
		NBTTagCompound tag = nmsItemStack.getTag();
		if (tag == null || !tag.hasKey("contents")) {
			return;
		}
		
		nmsItemStack.setTag(null);
		
		NBTTagList contents = tag.getList("contents", 10);
		
		for (int i = 0; i < contents.size(); i++) {
			NBTTagCompound itemTag = contents.get(i);
			
			ItemStack nmsStack = ItemStack.createStack(itemTag);
			
			if (!itemTag.isEmpty()) {
				inventory.setItem(i, CraftItemStack.asCraftMirror(nmsStack));
			}
		}
		
	}

	public static void openDonkeyContainer(Player player, Horse horse) {
		((CraftPlayer)player).getHandle().openContainer(((CraftHorse)horse).getHandle().inventoryChest);
	}

	public static void setLargeDonkeyChest(Horse horse) {
		final EntityHorse handle = ((CraftHorse)horse).getHandle();
		handle.inventoryChest = new InventoryHorseChest("Burrico", 54, handle);
		
		// normal horse invs trust their custom view to only allow saddles for 1st slot, we don't:
		handle.inventoryChest.a(new IInventoryListener() {
			
			@Override
			public void a(InventorySubcontainer arg0) {
				NMSWrapper.setSaddled(handle);
			}
		});
	}

	private static void setSaddled(EntityHorse handle) {
		ItemStack item = handle.inventoryChest.getItem(0);
		boolean isSaddled = item != null && CraftMagicNumbers.getId(item.getItem()) == 329;
		
		// handle.cs(): is saddled?
		if (isSaddled && !handle.cs() && handle.ticksLived > 20) {
			handle.makeSound("mob.horse.leather", 0.5F, 1.0F);
		}
		// handle.n(boolean): set saddled
		handle.n(isSaddled);
	}

	public static void unsetLargeDonkeyChest(Horse horse) {
		horse.getInventory().clear(); // just in case
		
		EntityHorse handle = ((CraftHorse)horse).getHandle();
		handle.inventoryChest = null; // loadChest needs this
		handle.loadChest();
	}
}
