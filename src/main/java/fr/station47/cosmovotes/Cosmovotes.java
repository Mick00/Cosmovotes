package fr.station47.cosmovotes;

import fr.station47.stationAPI.api.config.ConfigHelper;
import org.bukkit.plugin.java.JavaPlugin;

public class Cosmovotes extends JavaPlugin {
    public static ConfigHelper configs;
    public static Cosmovotes instance;
    public static VoteManager voteManager;

    public void onEnable(){
        configs = new ConfigHelper(this);
        instance = this;
        voteManager = new VoteManager();
    }

    public static Cosmovotes get(){
        return instance;
    }
}
