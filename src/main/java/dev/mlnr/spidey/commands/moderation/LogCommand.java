package dev.mlnr.spidey.commands.moderation;

import dev.mlnr.spidey.objects.cache.Cache;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

@SuppressWarnings("unused")
public class LogCommand extends Command
{
	public LogCommand()
	{
		super ("log", new String[]{}, "Sets log channel", "log", Category.MODERATION, Permission.ADMINISTRATOR, 0, 2);
	}

	@Override
	public final void execute(final String[] args, final Message message)
	{
		final var guild = message.getGuild();
		final var guildId = guild.getIdLong();
		final var channel = message.getTextChannel();

		if (guild.getSystemChannel() != null)
			guild.getManager().setSystemChannel(null).queue();

		final var channelId = channel.getIdLong();
		if (Cache.retrieveLogChannel(guildId) == channelId)
		{
			Cache.removeLogChannel(guildId);
			Utils.sendMessage(channel, ":white_check_mark: The log channel has been reset!");
		}
		else
		{
			Cache.setLogChannel(guildId, channelId);
			Utils.sendMessage(channel, ":white_check_mark: The log channel has been set to <#" + channelId + ">!");
		}
	}
}