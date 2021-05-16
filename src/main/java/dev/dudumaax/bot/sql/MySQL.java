package dev.dudumaax.bot.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import dev.dudumaax.bot.Files;

public class MySQL {
	
	private String host = Files.MySQLConfig.getString("mysql.host");
	private String port = Files.MySQLConfig.getString("mysql.port");
	private String user = Files.MySQLConfig.getString("mysql.user");
	private String password = Files.MySQLConfig.getString("mysql.password");
	
	private Connection connection;
	
	public boolean isConnected() {
		return (connection == null ? false : true);
	}
	
	public void connect() throws ClassNotFoundException, SQLException {
		if(!isConnected()) {
		String url = "jdbc:mysql://" + host + ":" + port + "?characterEncoding=utf8";
		connection = DriverManager.getConnection(url, user, password);
		}
	}
	
	public void disconnect() {
		if(isConnected()) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	public void checkConfigMySQL() {
		if (Files.MySQLConfig.getBoolean("use-mysql")) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Usando MySQL");
			try {
				connect();
			} catch (ClassNotFoundException | SQLException e) {
				Bukkit.getLogger().info("Database not connected");
			}
			return;
		} else {
			Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Usando config.yml");
			return;
		}
	}
}
