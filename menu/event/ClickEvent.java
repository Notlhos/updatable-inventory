package menu.event;

import menu.AbstractMenu;
import menu.item.AbstractItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ClickEvent {

    private final Player player;
    private final AbstractItem clickedItem;
    private final AbstractMenu.MenuHolder clickedMenuHolder;
    private final InventoryClickEvent inventoryClickEvent;

    public ClickEvent(Player player, AbstractItem clickedItem, AbstractMenu.MenuHolder clickedMenuHolder, InventoryClickEvent inventoryClickEvent) {
        this.player = player;
        this.clickedItem = clickedItem;
        this.clickedMenuHolder = clickedMenuHolder;
        this.inventoryClickEvent = inventoryClickEvent;
    }

    public Player getPlayer() {
        return player;
    }

    public AbstractItem getClickedItem() {
        return clickedItem;
    }

    public AbstractMenu.MenuHolder getClickedMenuHolder() {
        return clickedMenuHolder;
    }

    public InventoryClickEvent getInventoryClickEvent() {
        return inventoryClickEvent;
    }

    public void backOrClose(){
		AbstractMenu.MenuHolder holder = clickedMenuHolder.getPrevious();
        if(holder != null){
			clickedMenuHolder.close();
            holder.open();
        } else {
			clickedMenuHolder.close();
        	player.closeInventory();
		}
    }
}