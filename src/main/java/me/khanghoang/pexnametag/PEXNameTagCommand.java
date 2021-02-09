package me.khanghoang.pexnametag;

import org.bukkit.entity.Player;
import me.khanghoang.pexnametag.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PEXNameTagCommand implements CommandExecutor {
    Main plugin;

    public PEXNameTagCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = (Player)sender;
        String cmdName = cmd.getName().toLowerCase();
        if (args.length > 0) {
          if (args[0].equals("reload") && (p.hasPermission("pexnametag.reload"))) {
            this.plugin.loadConfig();
            this.plugin.applyNameTag();
            sender.sendMessage("§7§aName tag reloaded.");
          } else if (args.length == 1 && args[0].equals("set") && (p.hasPermission("pexnametag.set"))) {
            String group = args[1];
            String prefix = args[2];
            if (group.matches("[a-zA-Z]+")) {
              if (prefix.length() < 16) {
                prefix += " ";
                this.plugin.getConfig().set("GroupPrefix." + group, prefix);
                this.plugin.loadConfig();
                this.plugin.applyNameTag();
                sender.sendMessage("§7§aPrefix for " + group + " has been set as " + prefix.replace("&", "§"));
              } else {
                sender.sendMessage("§7§oPrefix must be less than 16 characters long");
              }
            } else {
              sender.sendMessage("§7§oInvalid group name");
            }
          } else {
            sender.sendMessage("§7§oYou do not have permission to perform this action.");
          }
          return true;
        } 
        return false;
    }
}