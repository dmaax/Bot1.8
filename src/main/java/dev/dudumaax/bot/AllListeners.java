package dev.dudumaax.bot;

import java.awt.Color;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import dev.dudumaax.bot.ServerListPing17.StatusResponse;
import dev.dudumaax.bot.sql.SQLGetter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AllListeners extends ListenerAdapter implements Listener {

	JDA bot;
	TextChannel channelLoginAndLogout;
	TextChannel channelPunishments;
	GetterAndSetterMySQL sql = new GetterAndSetterMySQL();
	Crawler c = new Crawler();
	UtilsMySQL utils = new UtilsMySQL();
	public SQLGetter data;

	public AllListeners(JDA bot) {
		this.bot = bot;
		this.data = new SQLGetter(Main.plugin);
		channelLoginAndLogout = bot.getTextChannelById(772595266158723123L);
		channelPunishments = bot.getTextChannelById(772594907772092466L);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		String playerName = e.getPlayer().getName();
		if (e.getPlayer().hasPermission("essentials.kick")) {
			Files.config.set("entrada-e-saida." + playerName, "");
			Files.config.set("entrada-e-saida." + playerName + ".lastLogin", BrasilHour());
			Files.config.set("entrada-e-saida." + playerName + ".lastLogout", "");
			try {
				Files.config.save(Files.configFile);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			if (Main.plugin.SQL.isConnected()) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "Backend funcionando!");
			}
			//channelLoginAndLogout.sendMessage("**" + playerName + " Entrou: " + BrasilHour() + "**").queue();
		}

	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) throws ParseException {
		String playerName = e.getPlayer().getName();
		if (e.getPlayer().isOp()) {
			Files.config.set("entrada-e-saida." + playerName + ".lastLogout", BrasilHour());
			try {
				Files.config.save(Files.configFile);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			EmbedBuilder eb = new EmbedBuilder();
			String entrada = Files.config.getString("entrada-e-saida." + playerName + ".lastLogin");
			String saida = Files.config.getString("entrada-e-saida." + playerName + ".lastLogout");

			String[] finalArray = dateDifference(entrada, saida, "HH:mm:ss").split(" ");
			Integer numeroHoras = Integer.parseInt(finalArray[0]);
			if (numeroHoras >= 2) {
				eb.setColor(Color.green);
			} else {
				eb.setColor(new Color(195, 114, 218));
			}
			eb.setTitle("Equipe Log");
			eb.setThumbnail("https://crafthead.net/avatar/" + playerName + ".png");
			eb.addField("Nick", playerName, true);
			eb.addField("Entrada", entrada, true);
			eb.addField("Saída", saida, true);
			eb.addField("Servidor", Main.getServerName(), true);

			try {
				eb.addField("Resumo", dateDifference(entrada, saida, "HH:mm:ss"), true);
			} catch (ParseException ex) {
				ex.printStackTrace();
			}
			eb.setFooter("Developed by Dudumaax - All Rights Reserved ©", "https://crafthead.net/avatar/Dudumaax.png");
			channelLoginAndLogout.sendMessage(eb.build()).queue();
			
			if(!Files.HorarioConfig.contains(BrasilDay())) {
				Files.HorarioConfig.createSection(BrasilDay());
				try {
					Files.HorarioConfig.save(Files.HorarioFile);
				} catch(Exception e2) {
					e2.printStackTrace();
				}
			}
			
			if(!Files.HorarioConfig.getConfigurationSection(BrasilDay()).contains(e.getPlayer().getName())) {
				Files.HorarioConfig.set(BrasilDay() + "." + e.getPlayer().getName(), dateDifferenceRaw(entrada, saida, "HH:mm:ss"));
				try {
					Files.HorarioConfig.save(Files.HorarioFile);
				} catch(Exception e2) {
					e2.printStackTrace();
				}
			} else {
				//Ja tem um horario desse staff. temos que somar.
				//String tempo = Files.HorarioConfig.getString(BrasilDay() + "." + e.getPlayer().getName());
				
			}
			
		}
	}
	 

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e) {
		String playerName = e.getPlayer().getName();
		if (e.getMessage().toLowerCase().startsWith("/kick")) {
			if (!e.getPlayer().hasPermission("essentials.kick"))
				return;
			String[] args = e.getMessage().split(" ");
			if (args.length < 3) {
				return;
			}
			String punidoName = args[1];
			String motivo = "";
			for (int i = 2; i < args.length; i++) {
				motivo = motivo + " " + args[i];
			}
			if (Main.plugin.SQL.isConnected()) {
				Main.plugin.data.punish(playerName, punidoName, motivo);
			}
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("Punição");
			eb.addField("Autor", playerName, true);
			eb.addField("Tipo", "Kick", true);
			eb.addField("Punido", punidoName, true);
			eb.addField("Motivo", motivo, true);
			try {
				eb.addField("ID", "#" + Main.plugin.data.totalPunishments(), true);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			eb.setColor(new Color(222, 108, 131));
			for (String arg : args) {
				if (arg.contains("https://prnt.sc/")) {
					eb.setImage(c.returnImage(arg));
				}
			}
			eb.setThumbnail("https://minotar.net/avatar/" + punidoName + ".png");
			// https://prnt.sc/vittp8
			eb.setFooter("Developed by Dudumaax - All Rights Reserved ©", "https://minotar.net/avatar/Dudumaax.png");
			channelPunishments.sendMessage(eb.build()).queue();
		}

		else if (e.getMessage().toLowerCase().startsWith("/ebanip")) {
			if (!e.getPlayer().hasPermission("essentials.tempban"))
				return;
			String[] args = e.getMessage().split(" ");
			if (args.length < 3) {
				return;
			}
			String punidoName = args[1];
			String motivo = "";
			for (int i = 2; i < args.length; i++) {
				motivo = motivo + " " + args[i];
			}
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("F " + playerName + " DEU EBANIP");
			eb.addField("Quem fez: ", playerName, true);
			eb.setColor(Color.RED);
			eb.setThumbnail("https://minotar.net/avatar/" + punidoName + ".png");
			eb.setFooter("Developed by Dudumaax - All Rights Reserved ©", "https://minotar.net/avatar/Dudumaax.png");
			channelPunishments.sendMessage(eb.build()).queue();
			channelPunishments.sendMessage("@everyone").queue();
		}

		else if (e.getMessage().toLowerCase().startsWith("/eban")) {
			if (!e.getPlayer().hasPermission("essentials.ban"))
				return;
			String[] args = e.getMessage().split(" ");
			if (args.length < 3) {
				return;
			}
			String punidoName = args[1];
			String motivo = "";
			for (int i = 2; i < args.length; i++) {
				motivo = motivo + " " + args[i];
			}
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("Punição");
			eb.addField("Autor", playerName, true);
			eb.addField("Tipo", "Ban", true);
			eb.addField("Punido", punidoName, true);
			eb.addField("Motivo", motivo, true);
			eb.setColor(Color.RED);
			eb.setThumbnail("https://minotar.net/avatar/" + punidoName + ".png");
			// eb.setImage(args[args.length - 1]);
			eb.setFooter("Developed by Dudumaax - All Rights Reserved ©", "https://minotar.net/avatar/Dudumaax.png");
			channelPunishments.sendMessage(eb.build()).queue();
		}

		else if (e.getMessage().toLowerCase().startsWith("/etempban")) {
			if (!e.getPlayer().hasPermission("essentials.tempban"))
				return;
			String[] args = e.getMessage().split(" ");
			if (args.length < 3) {
				return;
			}
			String punidoName = args[1];
			String motivo = "";
			for (int i = 2; i < args.length; i++) {
				motivo = motivo + " " + args[i];
			}
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("Punição");
			eb.addField("Autor", playerName, true);
			eb.addField("Tipo", "Tempban", true);
			eb.addField("Punido", punidoName, true);
			eb.addField("Motivo", motivo, true);
			eb.setColor(Color.ORANGE);
			eb.setThumbnail("https://minotar.net/avatar/" + punidoName + ".png");
			// eb.setImage(args[args.length - 1]);
			eb.setFooter("Developed by Dudumaax - All Rights Reserved ©", "https://minotar.net/avatar/Dudumaax.png");
			channelPunishments.sendMessage(eb.build()).queue();
		}

	}

	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		if (e.getMessage().getContentRaw().startsWith(".info")) {

			String[] args = e.getMessage().getContentRaw().split(" ");
			if (args.length != 3) {
				e.getChannel().sendMessage("**Uso correto:** .info <servidor> <porta>").queue();
				return;
			}

			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle(args[1] + " status:");

			try {
				Integer.parseInt(args[2]);
			} catch (NumberFormatException ex) {
				eb.addField("Erro", "Porta inválida.", false);
				eb.setColor(Color.RED);
				e.getChannel().sendMessage(eb.build()).queue();
			}

			eb.setColor(Color.GREEN);
			ServerListPing17 slp = new ServerListPing17();
			InetSocketAddress server = new InetSocketAddress(args[1], Integer.parseInt(args[2]));
			slp.setAddress(server);
			slp.setTimeout(7000);

			try {
				StatusResponse response = slp.fetchData();
				eb.addField("Online", "" + response.getPlayers().getOnline() + "/" + response.getPlayers().getMax(),
						true);
				eb.setThumbnail("https://mc-api.net/v3/server/favicon/" + args[1]);
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			String[] latencia = latency(args[1], Integer.parseInt(args[2])).split(" ");
			int ms = Integer.parseInt(latencia[0]);
			if (ms > 800) {
				e.getChannel().sendMessage("Algo inesperado ocorreu.").queue();;
				return;
			} else {

				eb.addField("Ping", "" + ms, true);

				e.getChannel().sendMessage(eb.build()).queue();
				return;

			}

		}
		else if(e.getMessage().getContentRaw().startsWith(".status")) {
			String[] args = e.getMessage().getContentRaw().split(" ");
			if (args.length != 1) {
				e.getChannel().sendMessage("**Uso correto:** .status").queue();
				return;
			}
			
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("Status");
			eb.setColor(Color.GREEN);
			
			ServerListPing17 slp = new ServerListPing17();
			InetSocketAddress server = new InetSocketAddress("playnetwork.com.br", 25565);
			slp.setAddress(server);
			slp.setTimeout(7000);

			try {
				StatusResponse response = slp.fetchData();
				eb.addField("Online", "" + response.getPlayers().getOnline() + "/" + response.getPlayers().getMax(),
						true);
				eb.setThumbnail("https://mc-api.net/v3/server/favicon/playnetwork.com.br");
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			String[] latencia = latency("playnetwork.com.br", 25565).split(" ");
			int ms = Integer.parseInt(latencia[0]);
			if (ms > 800) {
				e.getChannel().sendMessage("Algo inesperado ocorreu.").queue();;
				return;
			} else {

				eb.addField("Ping", "" + ms, true);

				e.getChannel().sendMessage(eb.build()).queue();
				return;

			}
			
		}
	}

	private String latency(String host, Integer port) {
		Socket s = new Socket();
		SocketAddress a = new InetSocketAddress(host, port);
		int timeoutMillis = 1000;
		long start = System.currentTimeMillis();
		try {
			s.connect(a, timeoutMillis);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		long stop = System.currentTimeMillis();

		try {
			s.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return "" + (stop - start) + " ms";
	}

	private String dateDifference(String date1, String date2, String pattern) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		Date d1 = sdf.parse(date1);
		Date d2 = sdf.parse(date2);
		long diffInMillis = d2.getTime() - d1.getTime();
		long dateDiffInDays = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);

		long dateDiffInHours = TimeUnit.HOURS.convert(diffInMillis - (dateDiffInDays * 24 * 60 * 60 * 1000),
				TimeUnit.MILLISECONDS);

		long dateDiffInMinutes = TimeUnit.MINUTES.convert(
				diffInMillis - (dateDiffInDays * 24 * 60 * 60 * 1000) - (dateDiffInHours * 60 * 60 * 1000),
				TimeUnit.MILLISECONDS);

		long dateDiffInSeconds = TimeUnit.SECONDS.convert(diffInMillis - (dateDiffInDays * 24 * 60 * 60 * 1000)
				- (dateDiffInHours * 60 * 60 * 1000) - (dateDiffInMinutes * 60 * 1000), TimeUnit.MILLISECONDS);

		final String tempo = dateDiffInHours + " Hora(s) " + dateDiffInMinutes + " Minuto(s) " + dateDiffInSeconds
				+ " Segundo(s)";
		return tempo;
	}
	
	private String dateDifferenceRaw(String date1, String date2, String pattern) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		Date d1 = sdf.parse(date1);
		Date d2 = sdf.parse(date2);
		long diffInMillis = d2.getTime() - d1.getTime();
		long dateDiffInDays = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);

		long dateDiffInHours = TimeUnit.HOURS.convert(diffInMillis - (dateDiffInDays * 24 * 60 * 60 * 1000),
				TimeUnit.MILLISECONDS);

		long dateDiffInMinutes = TimeUnit.MINUTES.convert(
				diffInMillis - (dateDiffInDays * 24 * 60 * 60 * 1000) - (dateDiffInHours * 60 * 60 * 1000),
				TimeUnit.MILLISECONDS);

		long dateDiffInSeconds = TimeUnit.SECONDS.convert(diffInMillis - (dateDiffInDays * 24 * 60 * 60 * 1000)
				- (dateDiffInHours * 60 * 60 * 1000) - (dateDiffInMinutes * 60 * 1000), TimeUnit.MILLISECONDS);

		final String tempo = dateDiffInHours + "h" + dateDiffInMinutes + "m" + dateDiffInSeconds
				+ "s";
		return tempo;
	}
	
	private String dateSoma(String date1, String date2) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
		Date d1 = sdf.parse(date1);
		Date d2 = sdf.parse(date2);
		long diffInMillis = d2.getTime() + d1.getTime();
		long dateDiffInDays = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);

		long dateDiffInHours = TimeUnit.HOURS.convert(diffInMillis + (dateDiffInDays * 24 * 60 * 60 * 1000),
				TimeUnit.MILLISECONDS);

		long dateDiffInMinutes = TimeUnit.MINUTES.convert(
				diffInMillis + (dateDiffInDays * 24 * 60 * 60 * 1000) + (dateDiffInHours * 60 * 60 * 1000),
				TimeUnit.MILLISECONDS);

		long dateDiffInSeconds = TimeUnit.SECONDS.convert(diffInMillis + (dateDiffInDays * 24 * 60 * 60 * 1000)
				- (dateDiffInHours * 60 * 60 * 1000) - (dateDiffInMinutes * 60 * 1000), TimeUnit.MILLISECONDS);

		final String tempo = dateDiffInHours + "h" + dateDiffInMinutes + "m" + dateDiffInSeconds
				+ "s";
		return tempo;
	}

	public static String BrasilHour() {
		TimeZone tz = TimeZone.getTimeZone("America/Brasil");
		Calendar calendar = GregorianCalendar.getInstance(tz);
		SimpleDateFormat date = new SimpleDateFormat("HH:mm:ss");
		return date.format(calendar.getTime());
	}
	
	public static String BrasilDay() {
		TimeZone tz = TimeZone.getTimeZone("America/Brasil");
		Calendar calendar = GregorianCalendar.getInstance(tz);
		SimpleDateFormat date = new SimpleDateFormat("dd:MM:yyyy");
		return date.format(calendar.getTime());
	}

}
