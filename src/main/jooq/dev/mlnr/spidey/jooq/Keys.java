/*
 * This file is generated by jOOQ.
 */
package dev.mlnr.spidey.jooq;


import dev.mlnr.spidey.jooq.tables.*;
import dev.mlnr.spidey.jooq.tables.records.*;
import org.jooq.ForeignKey;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables in 
 * public.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<GuildsRecord> GUILDS_PKEY = Internal.createUniqueKey(Guilds.GUILDS, DSL.name("guilds_pkey"), new TableField[] { Guilds.GUILDS.GUILD_ID }, true);
    public static final UniqueKey<SettingsBlacklistedChannelsRecord> SETTINGS_BLACKLISTED_CHANNELS_PKEY = Internal.createUniqueKey(SettingsBlacklistedChannels.SETTINGS_BLACKLISTED_CHANNELS, DSL.name("settings_blacklisted_channels_pkey"), new TableField[] { SettingsBlacklistedChannels.SETTINGS_BLACKLISTED_CHANNELS.GUILD_ID }, true);
    public static final UniqueKey<SettingsFiltersRecord> SETTINGS_FILTERS_PKEY = Internal.createUniqueKey(SettingsFilters.SETTINGS_FILTERS, DSL.name("settings_filters_pkey"), new TableField[] { SettingsFilters.SETTINGS_FILTERS.GUILD_ID }, true);
    public static final UniqueKey<SettingsMiscRecord> SETTINGS_MISC_PKEY = Internal.createUniqueKey(SettingsMisc.SETTINGS_MISC, DSL.name("settings_misc_pkey"), new TableField[] { SettingsMisc.SETTINGS_MISC.GUILD_ID }, true);
    public static final UniqueKey<SettingsMusicRecord> SETTINGS_MUSIC_PKEY = Internal.createUniqueKey(SettingsMusic.SETTINGS_MUSIC, DSL.name("settings_music_pkey"), new TableField[] { SettingsMusic.SETTINGS_MUSIC.GUILD_ID }, true);
    public static final UniqueKey<SettingsWhitelistedChannelsRecord> SETTINGS_WHITELISTED_CHANNELS_PKEY = Internal.createUniqueKey(SettingsWhitelistedChannels.SETTINGS_WHITELISTED_CHANNELS, DSL.name("settings_whitelisted_channels_pkey"), new TableField[] { SettingsWhitelistedChannels.SETTINGS_WHITELISTED_CHANNELS.GUILD_ID }, true);

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------

    public static final ForeignKey<InviteFilterIgnoredRolesRecord, GuildsRecord> INVITE_FILTER_IGNORED_ROLES__INVITE_FILTER_IGNORED_ROLES_GUILD_ID_FKEY = Internal.createForeignKey(InviteFilterIgnoredRoles.INVITE_FILTER_IGNORED_ROLES, DSL.name("invite_filter_ignored_roles_guild_id_fkey"), new TableField[] { InviteFilterIgnoredRoles.INVITE_FILTER_IGNORED_ROLES.GUILD_ID }, Keys.GUILDS_PKEY, new TableField[] { Guilds.GUILDS.GUILD_ID }, true);
    public static final ForeignKey<InviteFilterIgnoredUsersRecord, GuildsRecord> INVITE_FILTER_IGNORED_USERS__INVITE_FILTER_IGNORED_USERS_GUILD_ID_FKEY = Internal.createForeignKey(InviteFilterIgnoredUsers.INVITE_FILTER_IGNORED_USERS, DSL.name("invite_filter_ignored_users_guild_id_fkey"), new TableField[] { InviteFilterIgnoredUsers.INVITE_FILTER_IGNORED_USERS.GUILD_ID }, Keys.GUILDS_PKEY, new TableField[] { Guilds.GUILDS.GUILD_ID }, true);
    public static final ForeignKey<SettingsBlacklistedChannelsRecord, GuildsRecord> SETTINGS_BLACKLISTED_CHANNELS__SETTINGS_BLACKLISTED_CHANNELS_GUILD_ID_FKEY = Internal.createForeignKey(SettingsBlacklistedChannels.SETTINGS_BLACKLISTED_CHANNELS, DSL.name("settings_blacklisted_channels_guild_id_fkey"), new TableField[] { SettingsBlacklistedChannels.SETTINGS_BLACKLISTED_CHANNELS.GUILD_ID }, Keys.GUILDS_PKEY, new TableField[] { Guilds.GUILDS.GUILD_ID }, true);
    public static final ForeignKey<SettingsFiltersRecord, GuildsRecord> SETTINGS_FILTERS__SETTINGS_FILTERS_GUILD_ID_FKEY = Internal.createForeignKey(SettingsFilters.SETTINGS_FILTERS, DSL.name("settings_filters_guild_id_fkey"), new TableField[] { SettingsFilters.SETTINGS_FILTERS.GUILD_ID }, Keys.GUILDS_PKEY, new TableField[] { Guilds.GUILDS.GUILD_ID }, true);
    public static final ForeignKey<SettingsMiscRecord, GuildsRecord> SETTINGS_MISC__SETTINGS_MISC_GUILD_ID_FKEY = Internal.createForeignKey(SettingsMisc.SETTINGS_MISC, DSL.name("settings_misc_guild_id_fkey"), new TableField[] { SettingsMisc.SETTINGS_MISC.GUILD_ID }, Keys.GUILDS_PKEY, new TableField[] { Guilds.GUILDS.GUILD_ID }, true);
    public static final ForeignKey<SettingsMusicRecord, GuildsRecord> SETTINGS_MUSIC__SETTINGS_MUSIC_GUILD_ID_FKEY = Internal.createForeignKey(SettingsMusic.SETTINGS_MUSIC, DSL.name("settings_music_guild_id_fkey"), new TableField[] { SettingsMusic.SETTINGS_MUSIC.GUILD_ID }, Keys.GUILDS_PKEY, new TableField[] { Guilds.GUILDS.GUILD_ID }, true);
    public static final ForeignKey<SettingsWhitelistedChannelsRecord, GuildsRecord> SETTINGS_WHITELISTED_CHANNELS__SETTINGS_WHITELISTED_CHANNELS_GUILD_ID_FKEY = Internal.createForeignKey(SettingsWhitelistedChannels.SETTINGS_WHITELISTED_CHANNELS, DSL.name("settings_whitelisted_channels_guild_id_fkey"), new TableField[] { SettingsWhitelistedChannels.SETTINGS_WHITELISTED_CHANNELS.GUILD_ID }, Keys.GUILDS_PKEY, new TableField[] { Guilds.GUILDS.GUILD_ID }, true);
}
