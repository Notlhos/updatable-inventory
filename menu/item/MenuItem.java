package menu.item;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public interface MenuItem {

	ItemStack update(Player player);
	void onClick(InventoryClickEvent event);

}