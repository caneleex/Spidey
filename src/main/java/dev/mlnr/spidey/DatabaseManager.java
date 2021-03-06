package dev.mlnr.spidey;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.mlnr.spidey.jooq.tables.records.GuildsRecord;
import dev.mlnr.spidey.jooq.tables.records.SettingsMiscRecord;
import dev.mlnr.spidey.jooq.tables.records.SettingsMusicRecord;
import dev.mlnr.spidey.objects.settings.guild.GuildGeneralSettings;
import dev.mlnr.spidey.objects.settings.guild.GuildMiscSettings;
import dev.mlnr.spidey.objects.settings.guild.GuildMusicSettings;
import dev.mlnr.spidey.objects.settings.guild.IGuildSettings;
import org.jooq.*;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

import static dev.mlnr.spidey.jooq.Tables.*;

public class DatabaseManager {
	private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
	private final Configuration configuration;

	public DatabaseManager() {
		var hikariConfig = new HikariConfig();
		hikariConfig.setDriverClassName("org.postgresql.Driver");
		hikariConfig.setJdbcUrl("jdbc:postgresql:spidey");
		hikariConfig.setUsername("sebo");
		hikariConfig.setPassword(System.getenv("db"));

		var defaultConfig = new DefaultConfiguration();
		defaultConfig.setDataSource(new HikariDataSource(hikariConfig));
		defaultConfig.setSQLDialect(SQLDialect.POSTGRES);
		this.configuration = defaultConfig;
	}

	// getting settings

	public IGuildSettings retrieveGuildSettings(Table<? extends Record> table, long guildId, Function<Record, IGuildSettings> defaultSettingsTransformer,
	                                            Function<Record, IGuildSettings> transformer) {
		try (var selectStep = getCtx().selectFrom(table)) {
			try {
				var result = selectStep.where(guildIdEquals(table, guildId)).fetch();
				if (result.isEmpty()) {
					return defaultSettingsTransformer.apply(null);
				}
				return transformer.apply(result.get(0));
			}
			catch (DataAccessException ex) {
				logger.error("There was an error while fetching the {} for guild {}", table.getName(), guildId, ex);
			}
		}
		return defaultSettingsTransformer.apply(null);
	}

	public GuildGeneralSettings retrieveGuildGeneralSettings(long guildId) {
		return (GuildGeneralSettings) retrieveGuildSettings(GUILDS, guildId,
				defaultRecord -> new GuildGeneralSettings(guildId, this),
				settingsRecord ->
		{
			var casted = (GuildsRecord) settingsRecord;
			return new GuildGeneralSettings(guildId, casted.getVip(), this);
		});
	}

	public GuildMiscSettings retrieveGuildMiscSettings(long guildId, Spidey spidey) {
		return (GuildMiscSettings) retrieveGuildSettings(SETTINGS_MISC, guildId,
				defaultRecord -> new GuildMiscSettings(guildId, spidey),
				settingsRecord ->
		{
			var casted = (SettingsMiscRecord) settingsRecord;
			return new GuildMiscSettings(guildId, casted.getLogChannelId(), casted.getJoinRoleId(), casted.getLanguage(),
					casted.getSnipingEnabled(), spidey);
		});
	}

	public GuildMusicSettings retrieveGuildMusicSettings(long guildId, Spidey spidey) {
		return (GuildMusicSettings) retrieveGuildSettings(SETTINGS_MUSIC, guildId,
				defaultRecord -> new GuildMusicSettings(guildId, spidey),
				settingsRecord ->
		{
			var casted = (SettingsMusicRecord) settingsRecord;
			return new GuildMusicSettings(guildId, casted.getDefaultVolume(), casted.getDjRoleId(), casted.getSegmentSkippingEnabled(),
					casted.getFairQueueEnabled(), casted.getFairQueueThreshold(), spidey);
		});
	}

	// registering/removing guild

	public void registerGuild(long guildId) {
		try (var valueStep = getCtx().insertInto(GUILDS).columns(GUILDS.GUILD_ID).values(guildId)) {
			try {
				valueStep.onConflictDoNothing().execute();
			}
			catch (DataAccessException ex) {
				logger.error("There was an error while registering guild {}", guildId, ex);
			}
		}
	}

	public void removeGuild(long guildId) {
		try (var deleteStep = getCtx().deleteFrom(GUILDS)) {
			try {
				deleteStep.where(GUILDS.GUILD_ID.eq(guildId)).execute();
			}
			catch (DataAccessException ex) {
				logger.error("There was an error while removing the entry for guild {}", guildId, ex);
			}
		}
	}

	// helper set methods

	public <T> void executeSetQuery(Table<? extends Record> table, Field<T> column, T value, long guildId) {
		try (var insertStep = getCtx().insertInto(table).columns(GUILDS.GUILD_ID, column)) {
			try (var setStep = insertStep.values(guildId, value).onDuplicateKeyUpdate().set(column, value)) {
				try {
					setStep.execute();
				}
				catch (DataAccessException ex) {
					logger.error("There was an error while setting the {} column in table {} for guild {}", column.getName(), table.getName(), guildId, ex);
				}
			}
		}
	}

	public <T> void executeGeneralSetQuery(Field<T> column, T value, long guildId) {
		executeSetQuery(GUILDS, column, value, guildId);
	}

	public <T> void executeMiscSetQuery(Field<T> column, T value, long guildId) {
		executeSetQuery(SETTINGS_MISC, column, value, guildId);
	}

	public <T> void executeMusicSetQuery(Field<T> column, T value, long guildId) {
		executeSetQuery(SETTINGS_MUSIC, column, value, guildId);
	}

	// guild general setters

	public void setVip(long guildId, boolean vip) {
		executeGeneralSetQuery(GUILDS.VIP, vip, guildId);
	}

	// guild misc setters

	public void setLogChannelId(long guildId, long channelId) {
		executeMiscSetQuery(SETTINGS_MISC.LOG_CHANNEL_ID, channelId, guildId);
	}

	public void setJoinRoleId(long guildId, long roleId) {
		executeMiscSetQuery(SETTINGS_MISC.JOIN_ROLE_ID, roleId, guildId);
	}

	//	public void setLanguage(long guildId, String language) {
	//		executeMiscSetQuery(SETTINGS_MISC.LANGUAGE, language, guildId);
	//	}

	public void setSnipingEnabled(long guildId, boolean enabled) {
		executeMiscSetQuery(SETTINGS_MISC.SNIPING_ENABLED, enabled, guildId);
	}

	// guild music setters

	public void setDefaultVolume(long guildId, int defaultVolume) {
		executeMusicSetQuery(SETTINGS_MUSIC.DEFAULT_VOLUME, defaultVolume, guildId);
	}

	public void setDJRoleId(long guildId, long djRoleId) {
		executeMusicSetQuery(SETTINGS_MUSIC.DJ_ROLE_ID, djRoleId, guildId);
	}

	public void setFairQueueEnabled(long guildId, boolean enabled) {
		executeMusicSetQuery(SETTINGS_MUSIC.FAIR_QUEUE_ENABLED, enabled, guildId);
	}

	public void setFairQueueThreshold(long guildId, int threshold) {
		executeMusicSetQuery(SETTINGS_MUSIC.FAIR_QUEUE_THRESHOLD, threshold, guildId);
	}

	public void setSegmentSkippingEnabled(long guildId, boolean enabled) {
		executeMusicSetQuery(SETTINGS_MUSIC.SEGMENT_SKIPPING_ENABLED, enabled, guildId);
	}

	// jooq

	public DSLContext getCtx() {
		return DSL.using(this.configuration);
	}

	// helper methods

	private Condition guildIdEquals(Table<? extends Record> table, long guildId) {
		return table.field("guild_id", Long.class).eq(guildId);
	}
}
