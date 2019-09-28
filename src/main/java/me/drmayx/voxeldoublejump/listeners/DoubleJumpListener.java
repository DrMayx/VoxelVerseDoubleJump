package me.drmayx.voxeldoublejump.listeners;

import me.drmayx.voxeldoublejump.utilities.PlayerData;
import me.drmayx.voxeldoublejump.utilities.Utils;
import me.drmayx.voxeldoublejump.voxelversedoublejump.VoxelVerseDoubleJump;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DoubleJumpListener implements Listener {

    private VoxelVerseDoubleJump plugin;
    private HashMap<UUID, PlayerData> jumpValues = new HashMap<>();
    private long jumpMultiplier;
    private long jumpY;

    public DoubleJumpListener(VoxelVerseDoubleJump plugin){

        this.plugin = plugin;
        this.jumpMultiplier = plugin.config.getLong("jump_multiplier");
        this.jumpY = plugin.config.getLong("jump_y");
    }

    @EventHandler
    public void onPlayerJoined(PlayerJoinEvent event){
        UUID playerID = event.getPlayer().getUniqueId();
        PlayerData playerData;
        if(jumpValues.containsKey(playerID)){
            playerData = jumpValues.get(playerID);
            playerData.jumped = false;
            playerData.startHeight = event.getPlayer().getLocation().getY();
            jumpValues.replace(playerID, playerData);
        }else{
            playerData = new PlayerData(false, event.getPlayer().getLocation().getY());
            jumpValues.put(playerID, playerData);
        }
    }

    @EventHandler
    public void onGameModeChanged(PlayerGameModeChangeEvent event){
        Player player = event.getPlayer();
        this.plugin.canDoubleJump = false;
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Double jump is off"));
    }


    @EventHandler
    public void onPlayerDoubleJumped(PlayerToggleFlightEvent event){
        Player player = event.getPlayer();
        if(this.jumpValues.get(player.getUniqueId()).jumped){
            event.setCancelled(true);
            return;
        }

        if(!player.getGameMode().equals(GameMode.CREATIVE) && !player.getGameMode().equals(GameMode.SPECTATOR) &&  !player.isSwimming() && !player.isFlying() && !playerHasElytraOn(player)){
            if(playerToggledDoubleJump()){
                event.setCancelled(true);
                jump(player);
                return;
            }
        }

        if(!Utils.playerCanFly(player)){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerTouchedTheGround(PlayerMoveEvent event){
        PlayerData playerData = this.jumpValues.get(event.getPlayer().getUniqueId());
        if(playerData != null && playerData.jumped &&
                (event.getPlayer().getLocation().getBlock().getType().equals(Material.LADDER) ||
            event.getPlayer().getLocation().getBlock().getType().equals(Material.VINE))){

            playerData.jumped = false;
            this.jumpValues.replace(event.getPlayer().getUniqueId(), playerData);
        }

        if(playerData != null && playerData.jumped && event.getPlayer().isOnGround()){
            Utils.dealFallDamage(event.getPlayer(), playerData);
            playerData.jumped = false;
            this.jumpValues.replace(event.getPlayer().getUniqueId(), playerData);
        }
    }

    @EventHandler
    public void onEntityThrown(ProjectileLaunchEvent event){
        if((event.getEntity().getShooter() instanceof Player) && (event.getEntityType().equals(EntityType.ENDER_PEARL))) {
            Player player = (Player) event.getEntity().getShooter();
            PlayerData playerData = this.jumpValues.get(player.getUniqueId());
            if (playerData != null) {
                playerData.jumped = false;
                this.jumpValues.replace(player.getUniqueId(), playerData);
            }
        }
    }

    @EventHandler
    public void onCommandIssued(PlayerCommandPreprocessEvent event){
        String command = event.getMessage().split(" ")[0];
        if(commandIsIgnoredByPlugin(command)){
            UUID playerID = event.getPlayer().getUniqueId();

            PlayerData playerData = this.jumpValues.get(playerID);
            playerData.jumped = false;
            this.jumpValues.replace(playerID, playerData);
        }
    }

    private boolean commandIsIgnoredByPlugin(String command) {
        List<String> ignoredCommands = plugin.config.getStringList("ignored_commands");
        for(int i = 0; i < ignoredCommands.size(); i++){
            if(command.toLowerCase().contains(ignoredCommands.get(i))){
                return true;
            }
        }
        return false;
    }

    private boolean playerHasElytraOn(Player player) {
        ItemStack chestplate = player.getInventory().getChestplate();
        return chestplate != null && chestplate.getType().equals(Material.ELYTRA);
    }

    private void jump(Player player) {
        PlayerData playerData = this.jumpValues.get(player.getUniqueId());
        if(!playerData.jumped){
            playerData.jumped = true;
            playerData.startHeight = player.getLocation().getBlockY() - 1;
            this.jumpValues.replace(player.getUniqueId(), playerData);
            Vector vector = player.getLocation().getDirection().multiply(this.jumpMultiplier).setY(this.jumpY);
            player.setVelocity(vector);
        }
    }

    private boolean playerToggledDoubleJump() {
        return plugin.canDoubleJump;
    }
}
