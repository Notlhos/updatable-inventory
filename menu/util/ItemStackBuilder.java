package menu.util;

import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.*;
import java.util.stream.Collectors;

public class ItemStackBuilder {

	private final ItemStack itemStack;

	//Super function, don't repeat without parents
	private Map<String, String> placeholders = new HashMap<>();

	private ItemStackBuilder() {
		itemStack = new ItemStack(Material.STONE);
		setName("");
		setLore(new ArrayList<>());
	}

	private ItemStackBuilder(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	public static ItemStackBuilder create() {
		return new ItemStackBuilder();
	}

	public static ItemStackBuilder create(ItemStack stack) {
		return new ItemStackBuilder(stack);
	}

	public static ItemStackBuilder create(Material material) {
		return new ItemStackBuilder(new ItemStack(material));
	}

	public ItemStackBuilder setItemMeta(ItemMeta meta) {
		this.itemStack.setItemMeta(meta);
		return this;
	}

	public ItemStackBuilder setMaterial(Material material) {
		itemStack.setType(material);
		return this;
	}

	public ItemStackBuilder changeAmount(int change) {
		itemStack.setAmount(itemStack.getAmount() + change);
		return this;
	}

	public ItemStackBuilder setAmount(int amount) {
		itemStack.setAmount(amount);
		return this;
	}

	public ItemStackBuilder setData(short data) {
		itemStack.setDurability(data);
		return this;
	}

	public ItemStackBuilder setData(MaterialData data) {
		itemStack.setData(data);
		return this;
	}

	public ItemStackBuilder setEnchantments(Map<Enchantment, Integer> enchantments) {
		for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
			itemStack.removeEnchantment(enchantment);
		}
		itemStack.addUnsafeEnchantments(enchantments);
		return this;
	}

	public ItemStackBuilder addEnchantment(Enchantment enchantment, int level) {
		itemStack.addUnsafeEnchantment(enchantment, level);
		return this;
	}

	public ItemStackBuilder setName(String name) {
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(name.equals("") ? " " : color(name));
		itemStack.setItemMeta(itemMeta);
		return this;
	}

	public ItemStackBuilder addBlankLore() {
		addLore(" ");
		return this;
	}

	public ItemStackBuilder addLore(String... lore) {
		ItemMeta itemMeta = itemStack.getItemMeta();
		List<String> original = itemMeta.getLore();
		if (original == null) {
			original = new ArrayList<>();
		}
		Collections.addAll(original, format(lore));
		itemMeta.setLore(original);
		itemStack.setItemMeta(itemMeta);
		return this;
	}

	public ItemStackBuilder addLore(List<String> lore) {
		ItemMeta itemMeta = itemStack.getItemMeta();
		List<String> original = itemMeta.getLore();
		if (original == null) {
			original = new ArrayList<>();
		}
		original.addAll(format(lore));
		itemMeta.setLore(original);
		itemStack.setItemMeta(itemMeta);
		return this;
	}

	public ItemStackBuilder setLore(String... lore) {
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setLore(format(Lists.newArrayList(lore)));
		itemStack.setItemMeta(itemMeta);
		return this;
	}

	public ItemStackBuilder setLore(List<String> lore) {
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setLore(format(lore));
		itemStack.setItemMeta(itemMeta);
		return this;
	}


	public ItemStackBuilder addPlaceholder(String name, String placeholder){
		placeholders.putIfAbsent("{" + name + "}", placeholder);
		return this;
	}

	public ItemStack build() {

		ItemStack item = new ItemStack(itemStack.getType());
		item.setDurability(itemStack.getDurability());
		item.setAmount(itemStack.getAmount());
		item.setItemMeta(itemStack.getItemMeta());

		if(placeholders.size() > 0){
			ItemMeta itemMeta = item.getItemMeta();
			List<String> original = itemMeta.getLore();

			if(itemMeta.hasDisplayName()){
				String fullName = itemMeta.getDisplayName();
				for(Map.Entry<String, String> placeholder : placeholders.entrySet()){
					if(fullName.contains(placeholder.getKey())){
						fullName = fullName.replace(placeholder.getKey(), placeholder.getValue());
					}
				}
				itemMeta.setDisplayName(fullName);
				item.setItemMeta(itemMeta);
			}

			if (original == null) {
				placeholders.clear();
				return item;
			}

			String fullLore = original.stream().collect(Collectors.joining("\\"));
			for(Map.Entry<String, String> placeholder : placeholders.entrySet()){
				if(fullLore.contains(placeholder.getKey())){
					fullLore = fullLore.replace(placeholder.getKey(), placeholder.getValue());
				}
			}
			itemMeta.setLore(Arrays.asList(fullLore.split("\\\\")));
			item.setItemMeta(itemMeta);
			placeholders.clear();
		}


		return item;
	}

	public static String[] format(String[] strings) {
		return format(Arrays.asList(strings)).toArray(new String[strings.length]);
	}

	public static List<String> format(List<String> strings) {
		return strings.stream().map(ItemStackBuilder::color).collect(Collectors.toList());
	}

	public static String color(String source) {
		return ChatColor.translateAlternateColorCodes('&', source);
	}


}