package dev.mlnr.spidey;

import dev.mlnr.spidey.utils.EventWaiter;
import net.dv8tion.jda.api.GatewayEncoding;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.internal.utils.config.ThreadingConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.concurrent.ScheduledExecutorService;

import static net.dv8tion.jda.api.requests.GatewayIntent.*;

public class Core
{
	private static final Logger LOG = LoggerFactory.getLogger(Core.class);
	private static final ScheduledExecutorService EXECUTOR = ThreadingConfig.newScheduler(1, () -> "Spidey", "Misc");
	private static final EventWaiter waiter = new EventWaiter(EXECUTOR, true);

	private Core() {}

	public static void main(final String[] args)
	{
		try
		{
			JDABuilder.create(System.getenv("SpideyDev"),
					EnumSet.of(
							GUILD_BANS,
							GUILD_INVITES,
							GUILD_MEMBERS,
							GUILD_MESSAGES,
							GUILD_MESSAGE_REACTIONS,
							GUILD_EMOJIS
					))
					.disableCache(CacheFlag.MEMBER_OVERRIDES)
					.addEventListeners(new Events(), waiter)
					.setActivity(Activity.watching("myself load.."))
					.setStatus(OnlineStatus.DO_NOT_DISTURB)
					.setGatewayEncoding(GatewayEncoding.ETF)
					.build()
					.awaitReady();
			RestAction.setDefaultFailure(null);
		}
		catch (final Exception e)
		{
			LOG.error("There was an error while building JDA!", e);
		}
	}

	public static EventWaiter getWaiter()
	{
		return waiter;
	}

	public static ScheduledExecutorService getExecutor()
	{
		return EXECUTOR;
	}
}