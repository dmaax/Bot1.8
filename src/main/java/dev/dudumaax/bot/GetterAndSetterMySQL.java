package dev.dudumaax.bot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class GetterAndSetterMySQL {

	UtilsMySQL utils = new UtilsMySQL();

	String table = Files.MySQLConfig.getString("mysql.table");
	
	Connection conn = null;
	Statement stmt = null;

	public boolean punishmentExists(Integer id) {

		try {
			PreparedStatement statement = utils.getConnection()
					.prepareStatement("SELECT * FROM " + table + "WHERE id=?");
			statement.setString(1, id.toString());

			ResultSet results = statement.executeQuery();
			if (results.next()) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "Puniçao com id " + id + "encontrada.");
				return true;
			}
			Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "Puniçao com id " + id + " não encontrada.");
		} catch (Exception e) {
			System.out.println("Deu merda pra krl");
			e.printStackTrace();
		}

		return false;
	}

}
