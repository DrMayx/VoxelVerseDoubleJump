package me.drmayx.voxeldoublejump.utilities;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Config {

    public static YamlConfiguration generateConfig(JavaPlugin plugin){
        YamlConfiguration config = (YamlConfiguration) plugin.getConfig();
        config.options().header("ignored_commands - commands in this list will cause the plugin to NOT cause damage.\n" +
                "It's important to put commands such as /spawn or /tpa as the plugin calculates fall damage from double jump based on y position.\n" +
                "jump_multiplier and jump_y - no idea. I have no clue how they impact the jump height or distance. Tweak them and explore.");

        List<String> ignored = new ArrayList<>();
        ignored.add("spawn");
        ignored.add("tpa");
        ignored.add("tp");
        config.set("ignored_commands", ignored);

        config.set("jump_multiplier", 1);
        config.set("jump_y", 1);


        plugin.saveConfig();
        return (YamlConfiguration) plugin.getConfig();
    }

}