package dev.mlnr.spidey;

import dev.mlnr.spidey.objects.guild.GuildSettings;
import dev.mlnr.spidey.objects.user.UserSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class DatabaseManager
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseManager.class);

	private DatabaseManager() {}

	private static Connection initializeConnection()
	{
		try
		{
			return DriverManager.getConnection("jdbc:postgresql:spidey", "postgres", System.getenv("spidey_db_password"));
		}
		catch (final Exception ex)
		{
			LOGGER.error("There was an error establishing the connection to the database!", ex);
		}
		return null;
	}

	// guild stuff

	public static GuildSettings retrieveGuildSettings(final long guildId)
	{
		try (final var db = initializeConnection(); final var ps = db.prepareStatement("SELECT * FROM guilds WHERE guild_id=?"))
		{
			ps.setLong(1, guildId);
			try (final var rs = ps.executeQuery())
			{
				return rs.next() ? new GuildSettings(guildId, rs.getLong("log_channel_id"), rs.getLong("join_role_id"), rs.getString("prefix"),
						rs.getBoolean("sniping"), rs.getBoolean("vip"), rs.getLong("music_dj_role_id"), rs.getBoolean("music_segment_skipping"),
						rs.getInt("music_default_volume"), rs.getBoolean("music_fair_queue_enabled"), rs.getInt("music_fair_queue_threshold"))
						 : new GuildSettings(guildId, 0, 0, "s!", true, false, 0, false, 100, true, 3); // default settings
			}
		}
		catch (final SQLException ex)
		{
			LOGGER.error("There was an error while requesting the guild settings for guild {}!", guildId, ex);
		}
		return null;
	}

	private static <T> void executeGuildSetQuery(final String property, final long guildId, final T value)
	{
		insert(property, guildId, value, false);
	}

	public static void removeGuild(final long guildId)
	{
		try (final var db = initializeConnection(); final var ps = db.prepareStatement("DELETE FROM guilds WHERE guild_id=" + guildId))
		{
			ps.executeUpdate();
		}
		catch (final SQLException ex)
		{
			LOGGER.error("There was an error while removing the entry for guild {}!", guildId, ex);
		}
	}

	// user stuff

	public static UserSettings retrieveUserSettings(final long userId)
	{
//		try (final var db = initializeConnection(); final var ps = db.prepareStatement("SELECT * FROM users WHERE user_id=?"))
//		{
//			ps.setLong(1, userId);
//			try (final var rs = ps.executeQuery())
//			{
//				final var favorites = retrieveMusicFavorites(userId);
//				return rs.next() ? new UserSettings(userId, favorites)
//						 : new UserSettings(userId, favorites);
//			}
//		}
//		catch (final SQLException ex)
//		{
//			LOGGER.error("There was an error while requesting the user settings for user {}!", userId, ex);
//		}
//		return null; // TODO change this back after adding user properties
		return new UserSettings(userId, retrieveMusicFavorites(userId));
	}

//	private static <T> void executeUserSetQuery(final String property, final long userId, final T value)
//	{
//		insert(property, userId, value, true);
//	}

	// helper method

	private static <T> void insert(final String property, final long id, final T value, final boolean user)
	{
		final var table = user ? "users" : "guilds";
		final var query = "INSERT INTO " + table + " (%s, " + property + ") VALUES (?, ?) ON CONFLICT (%s) DO UPDATE SET " + property + "='" + value + "'";
		final var idType = user ? "user_id" : "guild_id";
		try (final var db = initializeConnection(); final var ps = db.prepareStatement(String.format(query, idType, idType)))
		{
			ps.setLong(1, id);
			ps.setObject(2, value);
			ps.executeUpdate();
		}
		catch (final SQLException ex)
		{
			LOGGER.error("There was an error while setting the {} property for {} {}!", property, user ? "user" : "guild", id, ex);
		}
	}

	// guild setters

	public static void setLogChannelId(final long guildId, final long channelId)
	{
		executeGuildSetQuery("log_channel_id", guildId, channelId);
	}

	public static void setJoinRoleId(final long guildId, final long roleId)
	{
		executeGuildSetQuery("join_role_id", guildId, roleId);
	}

	public static void setPrefix(final long guildId, final String prefix)
	{
		executeGuildSetQuery("prefix", guildId, prefix);
	}

	public static void setSnipingEnabled(final long guildId, final boolean enabled)
	{
		executeGuildSetQuery("sniping", guildId, enabled);
	}

	public static void setVip(final long guildId, final boolean vip)
	{
		executeGuildSetQuery("vip", guildId, vip);
	}


	// music setters

	public static void setDJRoleId(final long guildId, final long djRoleId)
	{
		executeGuildSetQuery("music_dj_role_id", guildId, djRoleId);
	}

	public static void setSegmentSkippingEnabled(final long guildId, final boolean enabled)
	{
		executeGuildSetQuery("music_segment_skipping", guildId, enabled);
	}

	public static void setDefaultVolume(final long guildId, final int defaultVolume)
	{
		executeGuildSetQuery("music_default_volume", guildId, defaultVolume);
	}

	// fair queue setters

	public static void setFairQueueEnabled(final long guildId, final boolean enabled)
	{
		executeGuildSetQuery("music_fair_queue_enabled", guildId, enabled);
	}

	public static void setFairQueueThreshold(final long guildId, final int threshold)
	{
		executeGuildSetQuery("music_fair_queue_threshold", guildId, threshold);
	}

	// user favorites stuff

	private static List<String> retrieveMusicFavorites(final long userId)
	{
		try (final var con = initializeConnection(); final var ps = con.prepareStatement("SELECT * FROM favorites WHERE user_id=?"))
		{
			ps.setLong(1, userId);
			try (final var rs = ps.executeQuery())
			{
				final var favorites = new ArrayList<String>();
				while (rs.next())
					favorites.add(rs.getString("query"));
				return favorites;
			}
		}
		catch (final SQLException ex)
		{
			LOGGER.error("There was an error while retrieving the music favorites for user {}!", userId, ex);
		}
		return Collections.emptyList();
	}

	private static void manageMusicFavorite(final long userId, final String query, final boolean add)
	{
		final var sql = add ? "INSERT INTO favorites (user_id, query) VALUES (?, ?)" : "DELETE FROM favorites WHERE user_id=? AND query=?";
		try (final var con = initializeConnection(); final var ps = con.prepareStatement(sql))
		{
			ps.setLong(1, userId);
			ps.setString(2, query);
			ps.executeUpdate();
		}
		catch (final SQLException ex)
		{
			LOGGER.error("There was an error while {} favorite \"{}\" for user {}!", add ? "adding" : "removing", query, userId, ex);
		}
	}

	public static void addMusicFavorite(final long userId, final String query)
	{
		manageMusicFavorite(userId, query, true);
	}

	public static void removeMusicFavorite(final long userId, final String query)
	{
		manageMusicFavorite(userId, query, false);
	}
}