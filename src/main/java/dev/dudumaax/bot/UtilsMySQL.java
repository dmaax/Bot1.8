package dev.dudumaax.bot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class UtilsMySQL {
	
	private Connection connection;
	public String host, port, username, password, database;
	Statement statement = null;

	public void checkConfigMySQL() {
		if (Files.MySQLConfig.getBoolean("use-mysql")) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Usando MySQL");
			SetupMySQL();
			return;
		} else {
			Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Usando config.yml");
			return;
		}
	}

	public void SetupMySQL() {
		host  = Files.MySQLConfig.getString("mysql.host");
		port = Files.MySQLConfig.getString("mysql.port");
		username = Files.MySQLConfig.getString("mysql.user");
		password = Files.MySQLConfig.getString("mysql.password");
		
		
		try {
			
			synchronized (this) {
				if(getConnection() != null && !getConnection().isClosed()) {
					return;
				}
				
				Class.forName("com.mysql.jdbc.Driver");
				String url = "jdbc:mysql://" + host + ":" + port + "?characterEncoding=utf8";
				setConnection(DriverManager.getConnection(url, username, password));
				Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "Conectado com sucesso!");
				
				Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "Verificando Database!");
				statement = connection.createStatement();
				
				String sql = "CREATE DATABASE IF NOT EXISTS pluginbot";				
				statement.execute(sql);
				Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Database checkada/criada com sucesso");
				
				sql = "USE pluginbot";				
				statement.execute(sql);
				Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Usando database pluginbot");
				
				sql = "CREATE TABLE IF NOT EXISTS punishments " +
		                   "(id INTEGER AUTO_INCREMENT, " +
		                   " author VARCHAR(255), " + 
		                   " player VARCHAR(255), " + 
		                   " motive VARCHAR(255), " + 
		                   " PRIMARY KEY ( id ))"; 				
				statement.execute(sql);
				Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Usando tabela punishments");
			}
			
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void punish(String author, String player, String motive) {
		try {
			PreparedStatement statement = this.getConnection()
					.prepareStatement("INSERT INTO punishments(author, player, motive) VALUE (?,?,?)");
			statement.setString(1, author);
			statement.setString(2, player);
			statement.setString(3, motive);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public Connection getConnection() {
		return this.connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

}
