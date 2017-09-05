package wands.catscraft;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import wands.catscraft.wands.TerracottaTurner;

public class Wands extends JavaPlugin {
    private TerracottaTurner terracottaTurner = null;


    @Override
    public void onEnable() {
        terracottaTurner = new TerracottaTurner();
        Bukkit.getPluginManager().registerEvents(terracottaTurner, this);
    }

    @Override
    public void onDisable() {
        terracottaTurner = null;
    }
}
