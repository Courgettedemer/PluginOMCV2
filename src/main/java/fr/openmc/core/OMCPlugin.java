package fr.openmc.core;

import dev.xernas.menulib.MenuLib;
import fr.openmc.core.commands.CommandsManager;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.commands.utils.SpawnManager;
import fr.openmc.core.listeners.ListenersManager;
import fr.openmc.core.utils.LuckPermsAPI;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import fr.openmc.core.utils.database.DatabaseManager;
import fr.openmc.core.utils.MotdUtils;
import fr.openmc.core.utils.text.TextManager;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import net.raidstone.wgevents.WorldGuardEvents;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;

public final class OMCPlugin extends JavaPlugin {
    @Getter static OMCPlugin instance;
    @Getter static FileConfiguration configs;
    @Getter static TextManager textManager;
    private DatabaseManager dbManager;


    public LuckPerms lpApi;

    @Override
    public void onEnable() {
        instance = this;

        /* CONFIG */
        saveDefaultConfig();
        configs = this.getConfig();
        
        /* EXTERNALS */
        MenuLib.init(this);
        new LuckPermsAPI(this);

        /* MANAGERS */
        dbManager = new DatabaseManager();
        new CommandsManager();
        CustomItemRegistry.init();
        new SpawnManager(this);
        new CityManager();
        new ListenersManager();
        new EconomyManager();
        new MotdUtils(this);
        textManager = new TextManager(this, new File(this.getDataFolder(), "translations"), "fr");
        textManager.loadAllLanguages();


        getLogger().info("Plugin activé");
    }

    @Override
    public void onDisable() {
        if (dbManager != null) {
            try {
                dbManager.close();
            } catch (SQLException e) {
                getLogger().severe("Impossible de fermer la connexion à la base de données");
            }
        }

        getLogger().info("Plugin désactivé");
    }

    public static void registerEvents(Listener... listeners) {
        for (Listener listener : listeners) {
            instance.getServer().getPluginManager().registerEvents(listener, instance);
        }
    }
}
