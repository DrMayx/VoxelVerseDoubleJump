package me.drmayx.voxeldoublejump.utilities;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Utils {

    public static boolean playerCanFly(Player player) {
        return player.hasPermission("essentials.fly") && player.hasPermission("essentials.fly.safelogin");
    }

    public static boolean playerCanDoubleJump(Player player) {
        return player.hasPermission("doublejump.jump");
    }

    /*
    Fall damage is 1 half heart for each block of fall distance after the third.
    Thus, falling 4 blocks causes 1 half heart damage, 2 half heart damage for 5 blocks, and so forth.
    Assuming full health (but no Feather Falling or relevant status effects), a 23 block fall should be fatal (23 - 3 = 20 half heart × 10 of damage),
    but due to the way fall distance is calculated, a 23.5 block fall is required instead.

    /\ https://minecraft.gamepedia.com/Damage#Fall_damage
     */
    public static void dealFallDamage(Player player, PlayerData playerData){

        // take no damage on landing at all
        int x = player.getLocation().getBlockX();
        int y = player.getLocation().getBlockY();
        int z = player.getLocation().getBlockZ();
        if((player.getInventory().getChestplate() != null && player.getInventory().getChestplate().getType().equals(Material.ELYTRA)) ||
            player.getLocation().getBlock().getType().equals(Material.WATER) ||
            player.getLocation().getBlock().getType().equals(Material.COBWEB) ||
            player.getLocation().getBlock().getType().equals(Material.LAVA) ||
            player.getLocation().getBlock().getType().equals(Material.LADDER) ||
            player.getLocation().getBlock().getType().equals(Material.VINE) ||
            player.getWorld().getBlockAt(x,y,z).getType().equals(Material.SLIME_BLOCK) ||
            player.getWorld().getBlockAt(x,y-1,z).getType().equals(Material.SLIME_BLOCK) ||
            player.getWorld().getBlockAt(x,y-2,z).getType().equals(Material.SLIME_BLOCK) ||
            player.getWorld().getBlockAt(x,y-3,z).getType().equals(Material.SLIME_BLOCK)){
            return;
        }

        // check for slow falling damage
        PotionEffect slowFallingEffect = player.getPotionEffect(PotionEffectType.SLOW_FALLING);
        if(slowFallingEffect != null)
        {
            return;
        }

        double landingY = player.getLocation().getBlockY();
        double difference = Math.abs(playerData.startHeight - landingY);

        // if player lands on the same level on higher
        // landing >~ startHeight
        if (playerData.startHeight < landingY){

            return;
        }

        // Base damage reduction
        if(playerData.startHeight > landingY && difference > 1.5){
            difference -= 3;
        }

        // check for feather falling
        int featherFallingDamage = 0;
        ItemStack boots = player.getInventory().getBoots();

        if(boots != null && boots.containsEnchantment(Enchantment.PROTECTION_FALL)){

            featherFallingDamage = boots.getEnchantmentLevel(Enchantment.PROTECTION_FALL) * 2;
        }

        difference -= featherFallingDamage;

        // check jump boost status effect
        PotionEffect jumpBoostEffect = player.getPotionEffect(PotionEffectType.JUMP);
        if(jumpBoostEffect != null)
        {
            difference -= jumpBoostEffect.getAmplifier();
        }

        //check for resistance status effect
        PotionEffect resistanceEffect = player.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        if(resistanceEffect != null)
        {
            difference -= resistanceEffect.getAmplifier() * 0.2;
        }

        if(difference > 0.5){
            player.damage(difference);
        }

    }
}
