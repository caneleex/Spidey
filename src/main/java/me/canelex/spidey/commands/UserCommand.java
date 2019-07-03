package me.canelex.spidey.commands;

import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

@SuppressWarnings("unused")
public class UserCommand implements ICommand {

	private final Locale locale = new Locale("en", "EN");
	private final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/London"));
	private final SimpleDateFormat date = new SimpleDateFormat("EEEE, d.LLLL Y", locale);
	private final SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss", locale);

	@Override
	public final void action(final GuildMessageReceivedEvent e) {

		final EmbedBuilder eb = Utils.createEmbedBuilder(e.getAuthor());
		final List<User> musers = e.getMessage().getMentionedUsers();
		final User u = musers.isEmpty() ? e.getAuthor() : musers.get(0);
		final Member m = e.getGuild().getMember(u);
		
		eb.setAuthor("USER INFO - " + u.getAsTag());
		eb.setColor(Color.WHITE);
		eb.setThumbnail(u.getEffectiveAvatarUrl());
		eb.addField("ID", u.getId(), false);

		if (Objects.requireNonNull(m).getNickname() != null) {
			eb.addField("Nickname for this guild", m.getNickname(), false);
		}

		cal.setTimeInMillis(u.getTimeCreated().toInstant().toEpochMilli());
		final String creatdate = date.format(cal.getTime());
		final String creattime = time.format(cal.getTime());

		eb.addField("Account created", String.format( "%s | %s UTC", creatdate, creattime), false);

		cal.setTimeInMillis(m.getTimeJoined().toInstant().toEpochMilli());
		final String joindate = date.format(cal.getTime());
		final String jointime = time.format(cal.getTime());

		eb.addField("User joined", String.format( "%s | %s UTC", joindate, jointime), false);

		if (e.getGuild().getBoosters().contains(m)) {
			cal.setTimeInMillis(Objects.requireNonNull(m.getTimeBoosted()).toInstant().toEpochMilli());
			final String boostdate = date.format(cal.getTime());
			final String boosttime = time.format(cal.getTime());
			eb.addField("Boosting since", String.format("%s | %s UTC", boostdate, boosttime), false);
		}

		if (!m.getRoles().isEmpty()) {

			int i = 0;
			StringBuilder s = new StringBuilder();

			for (final Role role : m.getRoles()) {
				i++;
				if (i == m.getRoles().size()) {
					s.append(role.getName());
				}
				else {
					s.append(role.getName()).append(", ");
				}
			}

			eb.addField("Roles [**" + i + "**]", s.toString(), false);

		}

		Utils.sendMessage(e.getChannel(), eb.build());

	}

	@Override
	public final String getDescription() { return "Shows info about you or mentioned user"; }
	@Override
	public final boolean isAdmin() { return false; }
	@Override
	public final String getInvoke() { return "user"; }
	@Override
	public final Category getCategory() { return Category.INFORMATIVE; }
	@Override
	public final String getUsage() { return "s!user (@someone)"; }

}