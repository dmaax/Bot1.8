package dev.dudumaax.bot.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;

import dev.dudumaax.bot.Files;
import dev.dudumaax.bot.Main;
import net.md_5.bungee.api.ChatColor;

public class SQLGetter {

	private Main plugin;

	public SQLGetter(Main plugin) {
		this.plugin = plugin;
	}

	String database = Files.MySQLConfig.getString("mysql.database");
	String table = Files.MySQLConfig.getString("mysql.table");

	public void createDefaults() {
		PreparedStatement ps;

		String sql = "CREATE DATABASE IF NOT EXISTS " + database;
		try {
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.BLUE + "Usando database " + database + ".");
			ps = plugin.SQL.getConnection().prepareStatement(sql);
			ps.executeUpdate();

			sql = "USE pluginbot";
			ps = plugin.SQL.getConnection().prepareStatement(sql);
			ps.executeUpdate();

			sql = "CREATE TABLE IF NOT EXISTS " + table + " " + "(id INTEGER AUTO_INCREMENT, "
					+ " author VARCHAR(255), " + " player VARCHAR(255), " + " motive VARCHAR(255), "
					+ " PRIMARY KEY ( id ))";

			ps = plugin.SQL.getConnection().prepareStatement(sql);
			ps.executeUpdate();

			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "Tabela " + table + " configurada.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// This is the Read function from CRUD.
	public String getPunishment(Integer id) {
		try {
			if (plugin.SQL.isConnected()) {
				PreparedStatement ps = plugin.SQL.getConnection()
						.prepareStatement("SELECT * FROM " + table + " WHERE id=?");
				ps.setInt(1, id);
				ResultSet rs = ps.executeQuery();
				String nome = "";
				if (rs.next()) {
					nome = rs.getString("motive");
					return nome;
				}
				ps.executeUpdate();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "Nenhum";
	}

	// This is the Create function from CRUD.
	public void punish(String author, String player, String motive) {
		try {
			String query = "INSERT INTO punishments(author, player, motive)" + " values (?, ?, ?)";
			PreparedStatement ps = plugin.SQL.getConnection().prepareStatement(query);
			ps.setString(1, author);
			ps.setString(2, player);
			ps.setString(3, motive);

			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// This is the Delete function from a CRUD.
	public void removeAllData() {
		try {
			PreparedStatement statement = plugin.SQL.getConnection().prepareStatement("DELETE FROM punishments");
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int totalPunishments() throws Exception {
		Connection connect = plugin.SQL.getConnection();

		Statement statement = connect.createStatement();
		ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM " + table);

		while (resultSet.next()) {
			return resultSet.getInt(1);
		}
		return 0;
	}

}
