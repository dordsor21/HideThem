package me.dordsor21.HideThem;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

	public static Main instance = null;

	public static final String hider = "hider";
	public static final String fhider = "fhider";
	
	public void onEnable()
	{
		instance = this;
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		this.saveDefaultConfig();
	}
	public boolean onCommand(CommandSender sender, Command command, String cmd, String[] args){
		if(!(sender instanceof Player)){
			return false;
		}else{
			Player player = (Player) sender;
			if(cmd.equalsIgnoreCase("hthide") || cmd.equalsIgnoreCase("hth")){
				this.reloadConfig();
				if(args.length == 0){
					if(!player.hasMetadata(hider)){
						hideAllPlayers(player);
						if(player.hasMetadata(fhider)){
							player.removeMetadata(fhider, this);;
						}
						player.setMetadata(hider, new FixedMetadataValue(this, true));
					}else{
						player.sendMessage(ChatColor.RED + "You are already hiding everyone.");
					}
				}else if(args.length == 1){
					if(Bukkit.getPlayer(args[0]).isOnline()){
						Player p = Bukkit.getPlayer(args[0]);
						if(!p.hasPermission("hidethem.exempt")){
							player.hidePlayer(p);
							player.sendMessage(ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "~" + ChatColor.DARK_GREEN + "] " + ChatColor.DARK_GRAY + args[0] + " has been hidden!");
						}else{
							player.sendMessage(ChatColor.RED + "You may not hide " + ChatColor.AQUA + args[0]);
						}
					}
				}else{
					player.sendMessage(ChatColor.RED + "/hth *[Player]");
				}
			}else if(cmd.equalsIgnoreCase("htshow") || cmd.equalsIgnoreCase("hts")){
				if(player.hasMetadata(hider)){
					if(args.length == 0){
						player.sendMessage(ChatColor.RED + "/hts [all|friends]");
					}else if(args[0].equalsIgnoreCase("all")){
						showAllPlayers(player);
						player.removeMetadata(hider, this);
					}else if(args[0].equalsIgnoreCase("friends")){
						showFriendPlayers(player);
						player.removeMetadata(hider, this);
						player.setMetadata(fhider, new FixedMetadataValue(this, true));
						player.sendMessage(ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "~" + ChatColor.DARK_GREEN + "]" + ChatColor.DARK_GRAY + " All friends shown!");
					}else if(Bukkit.getPlayer(args[0]).isOnline()){
						Player p = Bukkit.getPlayer(args[0]);
						player.showPlayer(p);
						player.sendMessage(ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "~" + ChatColor.DARK_GREEN + "] " + ChatColor.DARK_GRAY + args[0] + " has been shown!");
					}else{
						player.sendMessage(ChatColor.RED + "/hts [all|friends|player]");
					}
				}else if(player.hasMetadata(fhider)){
					if(args.length == 0 || args.length > 1){
						player.sendMessage(ChatColor.RED + "/hts [all|friends]");
					}else if(args[0].equalsIgnoreCase("all")){
						showAllPlayers(player);
						player.removeMetadata(fhider, this);
					}else if(args[0].equalsIgnoreCase("friends")){
						player.sendMessage(ChatColor.RED + "You are already showing friends.");
					}else if(Bukkit.getPlayer(args[0]).isOnline()){
						Player p = Bukkit.getPlayer(args[0]);
						player.showPlayer(p);
						player.sendMessage(ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "~" + ChatColor.DARK_GREEN + "] " + ChatColor.DARK_GRAY + args[0] + " has been shown!");
					}else{
						player.sendMessage(ChatColor.RED + "/hts [all|friends]");
					}
				}else if(args[0].equalsIgnoreCase("friends")){
					player.setMetadata(fhider, new FixedMetadataValue(this, true));
					for (Player p : Bukkit.getOnlinePlayers())
						if(!p.hasPermission("hidethem.exempt")){
							player.hidePlayer(p);
						}else{
							player.showPlayer(p);
						}
					showFriendPlayers(player);
					player.sendMessage(ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "~" + ChatColor.DARK_GREEN + "]" + ChatColor.DARK_GRAY + " All friends shown!");
				}else{
					if(Bukkit.getPlayer(args[0]).isOnline()){
						Player p = Bukkit.getPlayer(args[0]);
						player.showPlayer(p);
						player.sendMessage(ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "~" + ChatColor.DARK_GREEN + "] " + ChatColor.DARK_GRAY + args[0] + " has been shown!");
					}else{
						player.sendMessage(ChatColor.RED + "You are not hiding anyone.");
					}
				}
			}else if(cmd.equalsIgnoreCase("htfriend") || cmd.equalsIgnoreCase("htf")){
				if(!(args.length == 2)){
					player.sendMessage(ChatColor.RED + "/htf add|remove <player>");
				}else if(args[0].equalsIgnoreCase("add")){
					String Friend = args[1].toLowerCase();
					this.reloadConfig();
					List<String> list = this.getConfig().getStringList(player.getUniqueId() + ".friends");
					if(list.contains(Friend)){
						player.sendMessage(ChatColor.AQUA + args[1] + ChatColor.RED + " is already part of your friends list.");
					}else{
						list.add(Friend);
						this.getConfig().set(player.getUniqueId() + ".friends", list);
						player.sendMessage(ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "~" + ChatColor.DARK_GREEN + "]" + ChatColor.DARK_GRAY + " You added " + ChatColor.AQUA + args[1] + ChatColor.DARK_GRAY + " to your friend list!");
						this.saveConfig();
						if(Bukkit.getPlayer(Friend).isOnline()&&player.hasMetadata(fhider)){
							player.showPlayer(Bukkit.getPlayer(Friend));
						}
					}
					if(list.contains("")){
						list.remove("");
					}
					this.saveConfig();
				}else if(args[0].equalsIgnoreCase("remove")){
					String Friend = args[1].toLowerCase();
					if(this.getConfig().getList(player.getUniqueId() + ".friends").contains(Friend)){
						if(player.hasMetadata(fhider)){
							if(Bukkit.getPlayer(args[1]).isOnline()){
								player.hidePlayer(Bukkit.getPlayer(args[1]));
							}
						}
						this.getConfig().getList(player.getUniqueId() + ".friends").remove(Friend);
						this.saveConfig();
						player.sendMessage(ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "~" + ChatColor.DARK_GREEN + "]" + ChatColor.DARK_GRAY + " You removed " + ChatColor.AQUA + args[1] + ChatColor.DARK_GRAY + " from your friend list!");
					}else{
						player.sendMessage(ChatColor.AQUA + args[1] + ChatColor.RED + " is not part of your friends list.");
					}
				}else{
					player.sendMessage(ChatColor.RED + "/htf add|remove <player>");
				}
			}else if(cmd.equalsIgnoreCase("hthelp")){
				player.sendMessage(ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "~" + ChatColor.DARK_GREEN + "]" + ChatColor.GRAY + " Help for HideThem plugin");
				player.sendMessage(ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "~" + ChatColor.DARK_GREEN + "]" + ChatColor.GRAY + " /hthide : " + ChatColor.DARK_GRAY + "Hides all players on the server.");
				player.sendMessage(ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "~" + ChatColor.DARK_GREEN + "]" + ChatColor.GRAY + " /htshow all|friends : " + ChatColor.DARK_GRAY + "Shows all players, or your friends");
				player.sendMessage(ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "~" + ChatColor.DARK_GREEN + "]" + ChatColor.GRAY + " /htfriend add|remove : " + ChatColor.DARK_GRAY + "Add or remove friends");
			}
		}
		return false;
	}
	public void hideAllPlayers(Player player){
		for (Player p : Bukkit.getOnlinePlayers())
			if(!p.hasPermission("hidethem.exempt")){
				player.hidePlayer(p);
			}else{
				player.showPlayer(p);
			}
		player.sendMessage(ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "~" + ChatColor.DARK_GREEN + "]" + ChatColor.DARK_GRAY + " All players hidden! To show all players use /htshow all");
		player.sendMessage(ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "~" + ChatColor.DARK_GREEN + "]" + ChatColor.DARK_GRAY + " To show your friends, use /htshow friends");
		this.saveConfig();
	}
	public void showAllPlayers(Player player){
		for (Player p : Bukkit.getOnlinePlayers())
			player.showPlayer(p);
		player.sendMessage(ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "~" + ChatColor.DARK_GREEN + "]" + ChatColor.DARK_GRAY + " All players shown!");
	}
	public void showFriendPlayers(Player player){
		for (Player p : Bukkit.getOnlinePlayers()){
			this.reloadConfig();
			if(this.getConfig().getStringList(player.getUniqueId().toString() + ".friends").contains(p.getName().toLowerCase())){
				player.showPlayer(p);
			}
		}
	}
	@EventHandler
	public void onplayerJoin(PlayerJoinEvent e){
		Player player = e.getPlayer();
		player.setMetadata(hider, new FixedMetadataValue(this, true));
		for(Player p : Bukkit.getOnlinePlayers()){
			if(!p.hasPermission("hidethem.exempt")){
				player.hidePlayer(p);
			}
			if(p.hasMetadata(hider) && (!player.hasPermission("hidethem.exempt"))){
				p.hidePlayer(player);
			}else if(p.hasMetadata(fhider) && (!player.hasPermission("hidethem.exempt"))){
				this.reloadConfig();
				if(this.getConfig().getStringList(p.getUniqueId().toString() + ".friends").contains(player.getName())){
					p.showPlayer(player);
				}
			}else{
				p.showPlayer(player);
			}
			if(e.getPlayer().hasPlayedBefore() == false){
				List<String> list = Arrays.asList("");
				this.getConfig().set(e.getPlayer().getUniqueId() + ".friends", list);
				player.sendMessage(ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "~" + ChatColor.DARK_GREEN + "]" + ChatColor.DARK_GRAY + " Use /htf add <player> to add your friends, then /hts friends to show them");
				player.sendMessage(ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "~" + ChatColor.DARK_GREEN + "]" + ChatColor.DARK_GRAY + " Use /hts to show all players, and /hth to hide all players");
				this.saveConfig();
			}
		}
	}
}