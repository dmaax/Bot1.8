package dev.dudumaax.bot;

import javax.security.auth.login.LoginException;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import dev.dudumaax.bot.sql.MySQL;
import dev.dudumaax.bot.sql.SQLGetter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin implements Listener {
	
	public static Main plugin;
	UtilsMySQL utils = new UtilsMySQL();
	public MySQL SQL;
	public SQLGetter data;
	
	public void onEnable() {
		plugin = this;
		Files.base(this);
		this.SQL = new MySQL();
		this.data = new SQLGetter(this);
		SQL.checkConfigMySQL();
		
		if(SQL.isConnected()) {
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Conectado ao MySQL");
			data.createDefaults();
		}
		
		System.out.println("Bot Iniciado");
		JDABuilder builder = JDABuilder.createDefault(getDiscordToken());
		builder.setActivity(Activity.watching("Dudumaax coding."));
		builder.setStatus(OnlineStatus.DO_NOT_DISTURB);
		JDA bot = null;
		try {
			bot = builder.build();
			bot.awaitReady();
			bot.addEventListener(new AllListeners(bot));
		} catch (LoginException | InterruptedException e) {
			e.printStackTrace();
		}
		
		AllListeners allListeners = new AllListeners(bot);
		this.getServer().getPluginManager().registerEvents(allListeners, this);
	}
	
	private String getDiscordToken() {
		return System.getenv("DISCORD_TOKEN");
	}
	
	public void onDisable() {
		HandlerList.unregisterAll();
		SQL.disconnect();
	}
	
	public static String getServerName() {
		if(Main.getPlugin(Main.class).getDataFolder().getAbsolutePath().toLowerCase().contains("survival")) {
		return "Survival";
		}
		else if(Main.getPlugin(Main.class).getDataFolder().getAbsolutePath().toLowerCase().contains("rankup")) {
			return "RankUP";
		}
		else if(Main.getPlugin(Main.class).getDataFolder().getAbsolutePath().contains("skywars")) {
			return "Skywars";
		}
		else if(Main.getPlugin(Main.class).getDataFolder().getAbsolutePath().contains("beta")) {
			return "Dev";
		}
		return "Nenhum";
	}
	

}
