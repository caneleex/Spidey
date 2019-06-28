package me.canelex.spidey.utils;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import me.canelex.spidey.Core;
import me.canelex.spidey.objects.command.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class Utils extends Core {

	private static final String INVITE_LINK = "https://discordapp.com/oauth2/authorize?client_id=468523263853592576&scope=bot&permissions=268446900";
	private static final Logger logger = LoggerFactory.getLogger(Utils.class);
	private static final ClassGraph graph = new ClassGraph().whitelistPackages("me.canelex.spidey.commands").enableAllInfo().ignoreClassVisibility();

	private Utils(){
		super();
	}

	public static boolean hasPerm(final Member toCheck, final Permission perm) {
		return toCheck.hasPermission(perm);
	}

	public static void sendMessage(final TextChannel ch, final String toSend, final boolean isSpoiler) {
		if (isSpoiler) {
			ch.sendMessage("||" + toSend + "||").queue();
		}
		else {
			ch.sendMessage(toSend).queue();
		}
	}

	public static void sendMessage(final TextChannel ch, final MessageEmbed embed) {
		ch.sendMessage(embed).queue();
	}

	private static void sendPrivateMessage(final User user, final String toSend) {
		user.openPrivateChannel().queue(channel -> channel.sendMessage(toSend).queue());
	}

	public static void deleteMessage(final Message msg) {
		msg.delete().queue();
	}

	public static boolean canSetVanityUrl(final Guild g) {
		return g.getFeatures().contains("VANITY_URL");
	}

	public static String replaceLast(final String text, final String regex, final String replacement) {
		return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
	}

	public static EmbedBuilder createEmbedBuilder(final User u) { //by maasterkoo
		return new EmbedBuilder().setFooter("Command executed by " + u.getAsTag(), u.getEffectiveAvatarUrl());
	}

	public static String getInviteUrl(final long guildId) {
		return String.format("https://discordapp.com/oauth2/authorize?client_id=468523263853592576&guild_id=%s&scope=bot&permissions=268446900", guildId);
	}

	public static String getInviteUrl() {
		return INVITE_LINK;
	}

	public static void sendPrivateMessageFormat(final User u, final String message, final Object... args) {
		sendPrivateMessage(u, String.format(message, args));
	}

	public static boolean isWeb(final Member member) {
		return member.getOnlineStatus(ClientType.WEB) != OnlineStatus.OFFLINE;
	}

	public static boolean isDesktop(final Member member) {
		return member.getOnlineStatus(ClientType.DESKTOP) != OnlineStatus.OFFLINE;
	}

	public static boolean isMobile(final Member member) {
		return member.getOnlineStatus(ClientType.MOBILE) != OnlineStatus.OFFLINE;
	}

	public static void returnError(final String errMsg, final Message origin) {
		origin.addReaction(Emojis.CROSS).queue();
		origin.getTextChannel().sendMessage(String.format(":no_entry: %s.", errMsg)).queue(m -> {
			origin.delete().queueAfter(5, TimeUnit.SECONDS, null, userGone -> {
			});
			m.delete().queueAfter(5, TimeUnit.SECONDS, null, botGone -> {
			});
		});

	}

	public static String generateSuccess(final int count, final User u) {
		return ":white_check_mark: Successfully deleted **" + count + "** message" + (count > 1 ? "s" : "") + (u == null ? "." : String.format(" by user **%s**.", u.getAsTag()));
	}

	public static void registerCommands() {
		Core.commands.clear();
		try (final ScanResult result = graph.scan()) {
			for (final ClassInfo cls : result.getClassesImplementing("me.canelex.spidey.objects.command.ICommand")) {
				final ICommand cmd = (ICommand) cls.loadClass().getDeclaredConstructor().newInstance();
				Core.commands.put(cmd.invoke(), cmd);
				cmd.aliases().forEach(alias -> Core.commands.put(alias, cmd));
			}
		}
		catch (final Exception e) {
			logger.error("Exception!", e);
		}
	}

	public static String getSiteContent(String url) throws IOException {
		final URL obj = new URL(url);
		final HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		con.setRequestMethod("GET");

		final String userAgent = "me.canelex.spidey";
		con.setRequestProperty("User-Agent", userAgent);

		final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		final StringBuilder response = new StringBuilder();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		return response.toString();
	}

	public static DataObject getJson(String url) throws IOException {
		return DataObject.fromJson(getSiteContent(url));
	}

}