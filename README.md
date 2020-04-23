### Updatable inventory api - небольшая библиотека для создания различных меню с возможностью их обновления.

##### Библиотека не претендует на звание лучшей и без сомнения имеет множество плохих сторон, но и писалась она без учета глобального использования, скорее даже наоборот локального, поэтому может быть удалено в будущем, как грязь.

------------
**Что можно сделать с помощью этого апи:**

&bull; Статические инвентари для общего использования, так же можно добавить функцию обновления данных в инвентаре

&bull; Динамические инвентари для каждого игрока с возможностью обновлять данные

На примере показано как созадть статический инвентарь
```java
public class StaticExampleMenu extends MenuImpl {

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    @Override
    public void init() {
        setDisplayName(ChatColor.RED + "Статическое меню");
        setIndex(0, new CachedItem()
                .withCacheItem().setStackSupplier(() -> ItemStackBuilder.create(Material.PAPER)
                        .setName(ChatColor.GREEN + "Статический предмет без режима обновления")
                        .addBlankLore()
                        .build())
                .setClickConsumer(clickEvent -> clickEvent.getPlayer().sendMessage(ChatColor.YELLOW + "Ого, ты нажал на кнопку, чтоо??")));
        setIndex(1, new CachedItem()
                .setStackSupplier(() -> ItemStackBuilder.create(Material.PAPER)
                        .setName(getRandomColor() + "Дискотека")
                        .addBlankLore()
                        .build())
                .setClickConsumer(clickEvent -> clickEvent.getPlayer().kickPlayer(ChatColor.YELLOW + "Падло, хватит на меня давить!!!")));
        setIndex(2, new PerPlayerItem()
                .setItemStackFunction(player -> ItemStackBuilder.create(getRandomMaterial())
                        .setName(getRandomColor() + "Дискотека для " + player.getName())
                        .addBlankLore()
                        .build())
                .setClickConsumer(clickEvent -> clickEvent.getPlayer().damage(5)));
        setIndex(3, new ItemMenu(new ItemStack(Material.SKULL_ITEM)));
    }

    private ChatColor getRandomColor(){
        ChatColor[] colors = ChatColor.values();
        return colors[RANDOM.nextInt(colors.length)];
    }

    private Material getRandomMaterial(){
        Material[] materials = Material.values();
        return materials[RANDOM.nextInt(materials.length)];
    }
}
```
На примере показано как создать динамический инвентарь для отдельного игрока
```java
public class DinamicExampleMenu extends MenuImpl {

    private final Player owner;

    public DinamicExampleMenu(Player owner) {
        this.owner = owner;
        init();
        open(owner);
    }


    @Override
    public void init() {
        setDisplayName(ChatColor.RED + "Персональное меню");
        int index = 0;
        for(ItemStack item : owner.getInventory().getContents()){
            if(item != null && item.getType() != Material.AIR){
                setIndex(index++, new CachedItem()
                        .withCacheItem()
                        .setStackSupplier(() -> item)
                        .setClickConsumer(ClickEvent::backOrClose));
            }
        }
    }
}
```
Меню с возможностью перелистывания
```java
public class PaginatedExampleMenu extends PaginatedMenu {

    private final Player owner;

    public PaginatedExampleMenu(Player owner) {
        this.owner = owner;
        update();
        pages.get(0).open(owner);
    }


    @Override
    public void update() {
        MenuPageBuilder builder = new MenuPageBuilder();
        builder.withMaxRowSize(3); //Ограничение в 3 ряда для списка предметов
        builder.withCountInRow(3); //Ограничение в 3 предмета в одном ряду
        builder.withDisplayName("Первая страница");

        List<MenuItem> items = Arrays.asList(new CachedItem(), new CachedItem(), new CachedItem(), new CachedItem(), new CachedItem());

        builder.withAll(items);
        builder.push(); //Добавить страницу в класс родителя, то есть PaginatedMenu, а в данном случае его наследника

        //Так же можно конвертировать список предметов в список страниц

        List<AbstractItem> toConvert = Arrays.asList(new CachedItem(), new CachedItem(), new CachedItem(), new CachedItem(), new CachedItem());

        List<MenuPage> pages = convertToPages(toConvert);
        this.pages.addAll(pages); // Добавить страницы
    }
}
```
Для создания анимации достаточно
```java
    lib.scheduler.asyncRepeating(this, () -> {
            for (Player : getServer().getOnlinePlayers()) {
                InventoryHolder holder = player.getOpenInventory().getTopInventory().getHolder()
                if (holder instanceof AbstractMenu.MenuHolder) {
                    ((AbstractMenu.MenuHolder)holder).update()
                }
            }
    }, Duration.ofSeconds(1),  Duration.ofSeconds(1))
```
Так же можно добавить меню как кнопку в другое меню, если наследовать интерфейс MenuItem
```java
public static class ItemMenu extends MenuImpl implements MenuItem{
        private final ItemStack icon;

        public ItemMenu(ItemStack icon) {
            this.icon = icon;
        }

        @Override
        public ItemStack update(Player player) {
            return icon;
        }

        @Override
        public void onClick(InventoryClickEvent event) {
            open((Player) event.getWhoClicked());
        }
}
```
