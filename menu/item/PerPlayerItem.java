package menu.item;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public class PerPlayerItem extends AbstractItem<PerPlayerItem>{

	private Function<Player, ItemStack> stackPlayerFunction = (player) -> new ItemStack(Material.WATER);

	public PerPlayerItem setItemStackFunction(Function<Player, ItemStack> stackPlayerFunction) {
		this.stackPlayerFunction = stackPlayerFunction;
		return this;
	}

	public Function<Player, ItemStack> getStackPlayerFunction() {
		return stackPlayerFunction;
	}

	@Override
	public ItemStack update(Player player) {
		return stackPlayerFunction.apply(player);
	}
}