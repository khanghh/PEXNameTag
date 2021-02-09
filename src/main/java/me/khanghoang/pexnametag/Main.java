package me.khanghoang.pexnametag;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.bukkit.PermissionsEx;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import java.io.File;
import me.khanghoang.pexnametag.PEXNameTagCommand;
import us.myles.ViaVersion.api.Via;

public class Main extends JavaPlugin implements Listener {
    public Scoreboard sb;
    public Scoreboard sbOldVer;
    // private FileConfiguration configFile;
    private ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
    @Override
    public void onDisable() {
        // Don't log disabling, Spigot does that for you automatically!
    }

    public void loadConfig() {
      this.getConfig().options().header("#PEXNameTag group prefix config for 1.8.x-1.12.x maximum 16 characters long");
      this.getConfig().addDefault("GroupPrefix.Member", "");
      this.getConfig().addDefault("GroupPrefix.Builder", "[Builder]");
      this.getConfig().addDefault("GroupPrefix.Tester", "[Tester]");
      this.getConfig().addDefault("GroupPrefix.Helper", "[Helper]");
      this.getConfig().addDefault("GroupPrefix.Admin", "[Admin]");
      this.getConfig().addDefault("GroupPrefix.Owner", "[Owner]");
      this.getConfig().options().copyDefaults(true);
      this.saveConfig();
      sb = Bukkit.getScoreboardManager().getNewScoreboard();
      sbOldVer = Bukkit.getScoreboardManager().getNewScoreboard();
      for (PermissionGroup g : PermissionsEx.getPermissionManager().getGroups()) {
        String oldPrefix = (getConfig().getString("GroupPrefix." + g.getName()));
        if (oldPrefix == null) {
          oldPrefix = "";
        }
        sb.registerNewTeam(g.getName());
        sb.getTeam(g.getName()).setPrefix(g.getPrefix().replace("&", "§"));
        sbOldVer.registerNewTeam(g.getName());
        sbOldVer.getTeam(g.getName()).setPrefix(oldPrefix.replace("&", "§"));
      }
    }

    public void applyNameTag() {
      (new BukkitRunnable() {
        public void run() {
          for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            PermissionUser u = PermissionsEx.getUser(p);
            for (PermissionGroup g : u.getGroups()) {
              sb.getTeam(g.getName()).addEntry(p.getName());
              sbOldVer.getTeam(g.getName()).addEntry(p.getName());
            }
            int version = Via.getAPI().getPlayerVersion(p);
            if (version > 340) {
              p.setScoreboard(sb);
            } else {
              p.setScoreboard(sbOldVer);
            }
          } 
        }
      }).runTaskLater((Plugin)this, 20L);
    }

    @Override
    public void onEnable() {
      this.loadConfig();
      Bukkit.getServer().getPluginManager().registerEvents(this, (Plugin)this);
      this.getCommand("pexnametag").setExecutor(new PEXNameTagCommand(this));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
      this.applyNameTag();
    }
}
