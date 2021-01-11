package dev.mlnr.spidey.commands.informative;

import dev.mlnr.spidey.cache.GuildSettingsCache;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class InfoCommand extends Command
{
	public InfoCommand()
	{
		super("info", new String[]{}, Category.INFORMATIVE, Permission.UNKNOWN, 0, 0);
	}

	@Override
	public void execute(final String[] args, final CommandContext ctx)
	{
		final var eb = Utils.createEmbedBuilder(ctx.getAuthor());
		final var avatar = ctx.getJDA().getSelfUser().getEffectiveAvatarUrl();
		final var i18n = ctx.getI18n();

		eb.setAuthor("Spidey", null, avatar);
		eb.setThumbnail(avatar);
		eb.addField(i18n.get("commands.info.about.title"), i18n.get("commands.info.about.text", "cane#0570"), false);
		eb.addField(i18n.get("commands.info.commands.title"),
				i18n.get("commands.info.commands.text", GuildSettingsCache.getPrefix(ctx.getGuild().getIdLong())), false);
		eb.addField(i18n.get("commands.info.commands.info.title"), i18n.get("commands.info.commands.text", JDAInfo.VERSION), false);
		eb.addField(i18n.get("commands.info.commands.links"), "[`Website`](https://spidey.mlnr.dev)\n[`Discord`](https://discord.gg/uJCw7B9fxZ)" +
				"\n[`GitHub`](https://github.com/caneleex/Spidey)", false);
		eb.addField(i18n.get("commands.info.commands.support.title"), i18n.get("commands.info.commands.support.text"),false);
		ctx.reply(eb);
	}
}