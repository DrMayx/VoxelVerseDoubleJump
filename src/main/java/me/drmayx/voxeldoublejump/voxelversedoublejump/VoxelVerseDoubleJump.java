package me.drmayx.voxeldoublejump.voxelversedoublejump;

import me.drmayx.voxeldoublejump.commands.DoubleJumpCommand;
import me.drmayx.voxeldoublejump.listeners.DoubleJumpListener;
import me.drmayx.voxeldoublejump.utilities.Config;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class VoxelVerseDoubleJump extends JavaPlugin {

    private static String CONFIG_PATH = "plugins/VoxelVerseDoubleJump/config.yml";
    public boolean canDoubleJump = false;
    public YamlConfiguration config;

    @Override
    public void onEnable() {
        File configFile = new File(CONFIG_PATH);

        if(!configFile.exists())
        {
            config = Config.generateConfig(this);
        }
        else{
            config = YamlConfiguration.loadConfiguration(configFile);
        }
        getServer().getPluginManager().registerEvents(new DoubleJumpListener(this), this);
        getServer().getPluginCommand("doublejump").setExecutor(new DoubleJumpCommand(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
