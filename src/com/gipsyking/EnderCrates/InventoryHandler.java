package com.gipsyking.EnderCrates;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class InventoryHandler {

	private ArrayList<CrateInventory> inventories = new ArrayList<CrateInventory>();
	private EnderCrates plugin;
	public Logger logger;
	
	public InventoryHandler(EnderCrates enderplugin) {
		this.plugin = enderplugin;
		this.logger = Logger.getLogger("EnderCrates");
	}
	
	public void openInventory(Player player, Block block) {
		for (CrateInventory crate: inventories) {
			if (crate.block.equals(block)) {
				player.sendMessage("Crate is in use");
				return;
			}
		}
		Inventory inventory = plugin.getServer().createInventory(player, InventoryType.ENDER_CHEST);
		InventoryView view = player.openInventory(inventory);
		inventories.add(new CrateInventory(inventory, view, block, player));
	}

	public void saveInventory(InventoryView inventoryView) {
		CrateInventory crate = getCrate(inventoryView);
		if (crate != null) {
			for (ItemStack stack: crate.inventory.getContents()) {
				if (stack != null) {
					drop(crate);
					return;
				}
			}
			dropNew(crate);
		}
		
	}

	private CrateInventory getCrate(InventoryView view) {
		if (view.getType() != InventoryType.ENDER_CHEST) {
			return null;
		}
		logger.info("searching " + inventories.size() + " currently open inventories for crate");
		for (CrateInventory crate: inventories) {
			if (crate.view.equals(view)) {
				inventories.remove(crate);
				return crate;
			}
		}
		return null;
	}
	
	public void restore(Block block, ItemStack itemStack) {
		NMSWrapper.restore(block, itemStack);
	}
	
	private void dropCrate(CrateInventory crate, ItemStack stack) {
		crate.block.getWorld().dropItemNaturally(crate.block.getLocation(), stack);
		crate.block.setType(Material.AIR);
	}
	
	private void drop(CrateInventory crate) {
		dropCrate(crate, NMSWrapper.crateItem(crate.inventory));
	}

	private void dropNew(CrateInventory crate) {
		dropCrate(crate, NMSWrapper.emptyCrateItem());
	}
}
