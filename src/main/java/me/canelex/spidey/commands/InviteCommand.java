package me.canelex.spidey.commands;

import me.canelex.spidey.objects.category.Category;
import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class InviteCommand implements ICommand {

    @Override
    public final void action(final GuildMessageReceivedEvent e) {

        e.getMessage().delete().queueAfter(5, TimeUnit.SECONDS);
        Utils.sendPrivateMessageFormat(e.getAuthor(), "Link for inviting me: ||" + Utils.getInviteUrl() + "||");
        e.getChannel().sendMessage(":white_check_mark: Sent you an invite link.").queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));

    }

    @Override
    public final String getDescription() { return "Sends you Spidey's invite link to PM"; }
    @Override
    public final boolean isAdmin() { return false; }
    @Override
    public final String getInvoke() { return "invite"; }
    @Override
    public final Category getCategory() { return Category.MISC; }
    @Override
    public final String getUsage() { return "s!invite"; }

}