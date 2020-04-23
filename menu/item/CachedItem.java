package menu.item;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Supplier;

public class CachedItem extends AbstractItem<CachedItem>{

    private Supplier<ItemStack> stackSupplier = () -> new ItemStack(Material.WATER);

    private boolean cacheItem = false;
    private ItemStack cachedItem = null;

    public CachedItem setStackSupplier(Supplier<ItemStack> stackSupplier) {
        this.stackSupplier = stackSupplier;
        return this;
    }

    public CachedItem withCacheItem() {
        this.cacheItem = true;
        return this;
    }

    public Supplier<ItemStack> getStackSupplier() {
        return stackSupplier;
    }

    @Override
    public ItemStack update(Player player) {

        if(cacheItem){
            if(cachedItem != null){
                return cachedItem;
            }else {
                return cachedItem = stackSupplier.get();
            }
        }

        return stackSupplier.get();
    }
}