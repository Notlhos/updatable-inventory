package menu.item;

import menu.AbstractMenu;
import menu.event.ClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.function.Consumer;

public abstract class AbstractItem<V extends AbstractItem> implements MenuItem {

	private Consumer<ClickEvent> clickConsumer = (clickable) -> {};

	public V setClickConsumer(Consumer<ClickEvent> clickConsumer) {
		this.clickConsumer = clickConsumer;
		return (V) this;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		InventoryHolder holder = player.getOpenInventory().getTopInventory().getHolder();
		if(holder instanceof AbstractMenu.MenuHolder){
			clickConsumer.accept(new ClickEvent(player, this, (AbstractMenu.MenuHolder) holder, event));
		} else throw new IndexOutOfBoundsException();
	}
}