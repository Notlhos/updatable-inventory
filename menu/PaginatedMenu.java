package menu;

import menu.item.AbstractItem;
import menu.item.CachedItem;
import menu.item.MenuItem;
import menu.util.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Supplier;

public abstract class PaginatedMenu {

    protected List<MenuPage> pages = new ArrayList<>();

    private Map<Integer, AbstractItem> navigateItems = new HashMap<>();

    private CachedItem nextPageItem = new CachedItem().withCacheItem()
		.setStackSupplier(() -> ItemStackBuilder
			.create()
			.setMaterial(Material.PAPER)
			.setName(ChatColor.YELLOW + "Перейти на следующую страницу")
			.build());
    private CachedItem previousPageItem = new CachedItem().withCacheItem()
		.setStackSupplier(() -> ItemStackBuilder
			.create()
			.setMaterial(Material.PAPER)
			.setName(ChatColor.YELLOW + "Вернуться назад")
			.build());

    public void setNextPageItem(CachedItem nextPageItem) {
        this.nextPageItem = nextPageItem;
    }

    public void setPreviousPageItem(CachedItem previousPageItem) {
        this.previousPageItem = previousPageItem;
    }

    public void addNavigationItem(int index, AbstractItem item) {
        if(index >= 45 && index <= 53) {
            navigateItems.put(index, item);
        }
    }

    public void addPage(MenuPage page){
        pages.add(page);
    }

    public MenuPage removeLastPage(){
        return pages.isEmpty() ? null : pages.remove(pages.size() - 1);
    }

    public void update() {}

    public class MenuPageBuilder{

    	private String pageName = "NONE";
    	private Map<Integer, MenuItem> itemMap = new HashMap<>();
		private Map<Integer, MenuItem> navigateItems = new HashMap<>();
    	private int maxIndex = 8;
		private int minIndex = 0;
    	private int maxRowSize = 4;

    	public void withIndex(int index, MenuItem item){
    		int row = index / 9;
    		if(row > maxRowSize){
				throw new IndexOutOfBoundsException();
			}

    		int formatedIndex = index % 9;
			if(formatedIndex > maxIndex || formatedIndex < minIndex){
				throw new IndexOutOfBoundsException();
			}

    		itemMap.put(index, item);
		}

		public void withAll(Collection<MenuItem> items){
			Iterator<MenuItem> itemIterator = items.iterator();
			for(int row = 0; row < maxRowSize; row++){
				for(int index = minIndex; index < maxIndex; index++){
					if(!itemIterator.hasNext()){
						return;
					}
					itemMap.put(index + 9 * row, itemIterator.next());
				}
			}
		}

		public void withNavigation(int index, MenuItem item) {
    		if(index >= 45 && index <= 53) {
				navigateItems.put(index, item);
			}
		}

		public MenuItem getIndex(int index){
    		return itemMap.get(index);
		}

		public int getSize(){
    		return itemMap.size();
		}

		public void withDisplayName(String name){
    		pageName = ChatColor.translateAlternateColorCodes('&', name);
		}

		public void withCountInRow(int countInRow) {
			int size = countInRow / 2;
			this.maxIndex = size + 4;
			this.minIndex = size - 4;
		}

		public void withMaxRowSize(int maxRowSize) {
    		if(maxRowSize > 4){
    			throw new IndexOutOfBoundsException();
			}
			this.maxRowSize = maxRowSize;
		}

		public boolean push(){
            MenuPage guildMenuPage = new MenuPage(PaginatedMenu.this, pages.size());
			guildMenuPage.setDisplayName(pageName);
			guildMenuPage.addAll(itemMap);
			guildMenuPage.addAll(navigateItems);
    		return pages.add(guildMenuPage);
		}
	}

    protected List<MenuPage> convertToPages(List<AbstractItem> items) {
        int pageSize = Math.min(35, items.size());
        int numPages = (int) Math.ceil((double) items.size() / (double) pageSize);
        List<MenuPage> pages = new ArrayList<>(numPages);
        for (int pageIndex = 0; pageIndex < numPages; ) {
            MenuPage page = new MenuPage(this, pageIndex);
            List<AbstractItem> subList = items.subList(pageIndex * pageSize, Math.min(++pageIndex * pageSize, items.size()));
            int finalPageIndex = pageIndex;
            page.setDisplayName(ChatColor.RED + "Страница - " + finalPageIndex);
            for (int index = 0; index < subList.size(); index++) {
                AbstractItem item = subList.get(index);
                page.setIndex(index, item);
            }

            for(Map.Entry<Integer, AbstractItem> entry : navigateItems.entrySet()){
                page.setIndex(entry.getKey(), entry.getValue());
            }

            if (finalPageIndex != numPages) {
                ItemStack stack = nextPageItem.update(null);
                nextPageItem.setStackSupplier(() -> ItemStackBuilder.create(stack)
                        .addPlaceholder("page", finalPageIndex + "")
                        .addPlaceholder("nextPage", finalPageIndex != pageSize ? finalPageIndex + 1 + "" : finalPageIndex + "")
                        .addPlaceholder("maxPages", numPages + "")
                        .build());
                page.setNextPageItem(nextPageItem);
            }
            if (finalPageIndex != 1) {
                ItemStack stack = previousPageItem.update(null);
                previousPageItem.setStackSupplier(() -> ItemStackBuilder.create(stack)
                        .addPlaceholder("page", finalPageIndex + "")
                        .addPlaceholder("nextPage", finalPageIndex != pageSize ? finalPageIndex + 1 + "" : finalPageIndex + "")
                        .addPlaceholder("maxPages", numPages + "")
                        .build());
                page.setPreviousPageItem(previousPageItem);
            }
            pages.add(page);
        }
        return pages;
    }

    public static class MenuPage extends MenuImpl {

        private final PaginatedMenu parent;
        private final int pageIndex;

        private Map<Player, MenuHolder> cachedHolders = new WeakHashMap<>();

        public MenuPage(PaginatedMenu parent, int pageIndex) {
            this.parent = parent;
            this.pageIndex = pageIndex;
        }

        @Override
        public void open(Player player) {
            MenuHolder cachedHolder = cachedHolders.get(player);
            if (cachedHolder == null) {
                InventoryHolder holder = player.getOpenInventory().getTopInventory().getHolder();
                cachedHolder = new MenuHolder(player, this);
                if(holder instanceof MenuHolder){
					MenuHolder previousHolder = (MenuHolder) holder;
                    if(previousHolder.getMenu() instanceof MenuPage){
                        cachedHolder.setPrevious(previousHolder.getPrevious());
                    }else {
                        cachedHolder.setPrevious(previousHolder);
                    }
                }
            }
            cachedHolder.open();
        }

        @Override
        public void close(Player player){
            cachedHolders.remove(player);
        }

        void setNextPageItem(CachedItem item) {
            setIndex(50, new CachedItem().withCacheItem().setStackSupplier(item.getStackSupplier()).setClickConsumer(clickEvent -> {
                MenuPage page = parent.pages.get(pageIndex + 1);
                page.open(clickEvent.getPlayer());
            }));
        }

        void setPreviousPageItem(CachedItem item) {
            setIndex(48, new CachedItem().withCacheItem().setStackSupplier(item.getStackSupplier()).setClickConsumer(clickEvent -> {
                MenuPage page = parent.pages.get(pageIndex - 1);
                page.open(clickEvent.getPlayer());
            }));
        }
    }
}