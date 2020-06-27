package me.canelex.spidey;

import me.canelex.spidey.objects.command.Command;
import me.canelex.spidey.utils.EventWaiter;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import static net.dv8tion.jda.api.requests.GatewayIntent.*;

public class Core
{
	protected static final Map<String, Command> commands = new HashMap<>();
	private static final Logger LOG = LoggerFactory.getLogger(Core.class);
	private static final ScheduledExecutorService EXECUTOR = Utils.createScheduledThread("Spidey Misc");
	private static final EventWaiter waiter = new EventWaiter(EXECUTOR, true);

	public static void main(final String[] args)
	{
		try
		{
			JDABuilder.create(System.getenv("SpideyDev"), EnumSet.of(GUILD_BANS, GUILD_INVITES, GUILD_MEMBERS, GUILD_MESSAGES, GUILD_MESSAGE_REACTIONS, GUILD_EMOJIS))
					.disableCache(CacheFlag.MEMBER_OVERRIDES)
					.addEventListeners(new Events(), waiter)
					.setActivity(Activity.watching("myself load.."))
					.setStatus(OnlineStatus.DO_NOT_DISTURB)
					.build()
					.awaitReady();
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

	public static Map<String, Command> getCommands()
	{
		return commands;
	}

	public static ScheduledExecutorService getExecutor()
	{
		return EXECUTOR;
	}
}