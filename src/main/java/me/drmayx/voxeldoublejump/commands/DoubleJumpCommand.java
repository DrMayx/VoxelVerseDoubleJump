package me.drmayx.voxeldoublejump.commands;

import me.drmayx.voxeldoublejump.utilities.Utils;
import me.drmayx.voxeldoublejump.voxelversedoublejump.VoxelVerseDoubleJump;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class DoubleJumpCommand implements CommandExecutor {

    private VoxelVerseDoubleJump plugin;

    public DoubleJumpCommand(VoxelVerseDoubleJump plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length > 0 && args[0].equalsIgnoreCase("reload")){
            if((sender instanceof ConsoleCommandSender) || sender.hasPermission("doublejump.admin"))
            plugin.getServer().getPluginManager().disablePlugin(this.plugin);
            plugin.getServer().getPluginManager().enablePlugin(this.plugin);
            return true;
        }

        if(sender instanceof Player){
            Player player = (Player) sender;
            if(Utils.playerCanDoubleJump(player)) {
                this.plugin.canDoubleJump = !this.plugin.canDoubleJump;
                player.setAllowFlight(this.plugin.canDoubleJump);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(String.format("Double jump is %s", this.plugin.canDoubleJump ? "on" : "off")));
            }
        }

        return true;
    }
}
