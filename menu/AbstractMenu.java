package menu;

import menu.item.MenuItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public abstract class AbstractMenu {

	private static Map<Player, MenuHolder> playerMenuHolderMap = new WeakHashMap<>();

	private String displayName = "CRUTCH";
	private Map<Integer, MenuItem> itemMap = new HashMap<>();

	public void setIndex(int index, MenuItem item){
		itemMap.put(index, item);
	}

	public void addAll(Map<Integer, MenuItem> itemMap){
		this.itemMap.putAll(itemMap);
	}

	public void removeIndex(int index){
		itemMap.remove(index);
	}

	public MenuItem getIndex(int index){
		return itemMap.get(index);
	}

	public int getSize(){
		return itemMap.size();
	}

	public void update() {}

	public void open(Player player){
		Inventory inventory = player.getOpenInventory().getTopInventory();

		MenuHolder menuHolder = new MenuHolder(player, this);
		if(inventory.getHolder() instanceof MenuHolder){
			menuHolder.setPrevious((MenuHolder) inventory.getHolder());
		}

		menuHolder.open();
	}

	public void close(Player player){
		playerMenuHolderMap.remove(player);
	}

	public String getDisplayName(){
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = ChatColor.translateAlternateColorCodes('&', displayName);
	}

	public static class MenuHolder implements InventoryHolder {

		private Player owner;
		private AbstractMenu menu;
		private Inventory inventory;

		private MenuHolder previous;


		public MenuHolder(Player owner, AbstractMenu menu) {
			this.owner = owner;
			this.menu = menu;
			this.inventory = prepareForPlayer();

		}

		private Inventory prepareForPlayer(){

			if(menu.itemMap.isEmpty()){
				owner.sendMessage("Empty inventory");
				throw new NullPointerException("Empty inventory " + menu.getClass().getSimpleName());
			}

			int maxIndex = Math.max(menu.itemMap.keySet().stream().max(Comparator.comparingInt(Integer::intValue)).orElse(0), 1);

			Inventory inventory = Bukkit.createInventory(this, (int) (Math.ceil(((maxIndex + 1) / 9.0)) * 9), menu.getDisplayName());
			for(Map.Entry<Integer, MenuItem> item : menu.itemMap.entrySet()){
				inventory.setItem(item.getKey(), item.getValue().update(owner));
			}

			return inventory;
		}

		public void open(){
			playerMenuHolderMap.put(owner, this);
			owner.openInventory(inventory);
		}

		public void close(){
			menu.close(owner);
		}

		public void update(){

			menu.update();

			for(int index = 0; index < inventory.getSize(); index++){
				MenuItem item = menu.itemMap.get(index);
				if(item == null && inventory.getItem(index) != null){
					inventory.setItem(index, null);
				}else if(item != null){
					inventory.setItem(index, item.update(owner));
				}
			}

		}

		public void onClick(int index, InventoryClickEvent event){
			MenuItem item = menu.itemMap.get(index);
			if(item != null){
				item.onClick(event);
			}
		}

		public Player getOwner() {
			return owner;
		}

		public AbstractMenu getMenu() {
			return menu;
		}

		public MenuHolder getPrevious() {
			return previous;
		}

		public void setPrevious(MenuHolder previous) {
			this.previous = previous;
		}

		@Override
		public Inventory getInventory() {
			return inventory;
		}
	}
}