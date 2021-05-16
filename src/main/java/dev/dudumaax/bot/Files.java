package dev.dudumaax.bot;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Files {
	
	public static File configFile;
	public static FileConfiguration config;
	
	public static File MySQLFile;
	public static FileConfiguration MySQLConfig;
	
	public static File HorarioFile;
	public static FileConfiguration HorarioConfig;
	
	public static void base(Main main) {
		if(!main.getDataFolder().exists()) {
			main.getDataFolder().mkdir();
		}
		
		configFile = new File(main.getDataFolder(), "config.yml");
		if(!configFile.exists()) {
			main.saveResource("config.yml", false);
		}
		config = YamlConfiguration.loadConfiguration(configFile);
		
		MySQLFile = new File(main.getDataFolder(), "mysql.yml");
		if(!MySQLFile.exists()) {
			main.saveResource("mysql.yml", false);
		}
		MySQLConfig = YamlConfiguration.loadConfiguration(MySQLFile);
		
		HorarioFile = new File(main.getDataFolder(), "horario.yml");
		if(!HorarioFile.exists()) {
			main.saveResource("horario.yml", false);
		}
		HorarioConfig = YamlConfiguration.loadConfiguration(HorarioFile);
	}

}
