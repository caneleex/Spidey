package dev.mlnr.spidey;

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import dev.mlnr.blh.api.BLHBuilder;
import dev.mlnr.blh.api.BLHEventListener;
import dev.mlnr.blh.api.BotList;
import dev.mlnr.spidey.events.ReadyEvents;
import dev.mlnr.spidey.handlers.command.CommandHandler;
import dev.mlnr.spidey.objects.I18n;
import dev.mlnr.spidey.utils.ConcurrentUtils;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.GatewayEncoding;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.EnumSet;

import static net.dv8tion.jda.api.requests.GatewayIntent.*;
import static net.dv8tion.jda.api.utils.cache.CacheFlag.*;

public class Spidey {
	private static final Logger logger = LoggerFactory.getLogger(Spidey.class);
	private final JDA jda;

	private final DatabaseManager databaseManager = new DatabaseManager();

	public Spidey() throws LoginException, InterruptedException {
		I18n.loadLanguages();
		CommandHandler.loadCommands();
		MusicUtils.registerSources();

		RestAction.setDefaultFailure(null);
		MessageAction.setDefaultMentions(EnumSet.noneOf(Message.MentionType.class));

		var blh = new BLHBuilder().setDevModePredicate(jdaO -> jdaO.getSelfUser().getIdLong() != 772446532560486410L)
				.setSuccessLoggingEnabled(false)
				.setUnavailableEventsEnabled(false)
				.addBotList(BotList.TOP_GG, System.getenv("topgg"))
				.addBotList(BotList.BOTLIST_SPACE, System.getenv("botlistspace"))
				.addBotList(BotList.DBOATS, System.getenv("dboats"))
				.addBotList(BotList.DSERVICES, System.getenv("dservices"))
				.addBotList(BotList.DBOTS_GG, System.getenv("dbotsgg"))
				.addBotList(BotList.DBL, System.getenv("dbl"))
				.build();

		jda = JDABuilder.create(System.getenv("Spidey"),
				GUILD_BANS,
				GUILD_INVITES,
				GUILD_MEMBERS,
				GUILD_MESSAGES,
				GUILD_MESSAGE_REACTIONS,
				GUILD_EMOJIS,
				GUILD_VOICE_STATES)
			.disableCache(
				MEMBER_OVERRIDES,
				ROLE_TAGS,
				ACTIVITY,
				CLIENT_STATUS
			)
			.setMemberCachePolicy(MemberCachePolicy.VOICE)
			.setChunkingFilter(ChunkingFilter.NONE)
			.addEventListeners(new ReadyEvents(this), ConcurrentUtils.getEventWaiter(), new BLHEventListener(blh))
			.setActivity(Activity.watching("myself load"))
			.setStatus(OnlineStatus.DO_NOT_DISTURB)
			.setGatewayEncoding(GatewayEncoding.ETF)
			.setAudioSendFactory(new NativeAudioSendFactory())
			.build()
			.awaitReady();
	}

	public static void main(String[] args) {
		try {
			new Spidey();
		}
		catch (Exception e) {
			logger.error("There was an error while building JDA", e);
		}
	}

	public JDA getJDA() {
		return jda;
	}

	public DatabaseManager getDatabaseManager() {
		return databaseManager;
	}
}