package dev.mlnr.spidey.objects.command;

import dev.mlnr.spidey.objects.I18n;
import dev.mlnr.spidey.utils.ArgumentUtils;
import dev.mlnr.spidey.utils.Emojis;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class CommandContext
{
    private final String[] args;
    private final GuildMessageReceivedEvent event;
    private final I18n i18n;

    public CommandContext(final String[] args, final GuildMessageReceivedEvent event, final I18n i18n)
    {
        this.args = args;
        this.event = event;
        this.i18n = i18n;
    }

    public Message getMessage()
    {
        return event.getMessage();
    }

    public User getAuthor()
    {
        return event.getAuthor();
    }

    public Member getMember()
    {
        return event.getMember();
    }

    public TextChannel getTextChannel()
    {
        return event.getChannel();
    }

    public Guild getGuild()
    {
        return event.getGuild();
    }

    public JDA getJDA()
    {
        return event.getJDA();
    }

    public I18n getI18n()
    {
        return this.i18n;
    }

    // interaction methods

    public void reply(final EmbedBuilder embedBuilder)
    {
        Utils.sendMessage(getTextChannel(), embedBuilder.build(), getMessage());
    }

    public void reply(final String content)
    {
        reply(content, MessageAction.getDefaultMentions());
    }

    public void reply(final String content, final Set<Message.MentionType> allowedMentions)
    {
        Utils.sendMessage(getTextChannel(), content, allowedMentions, getMessage());
    }

    public void replyError(final String error)
    {
        replyError(error, Emojis.CROSS);
    }

    public void replyError(final String error, final String failureEmoji)
    {
        Utils.returnError(error, getMessage(), failureEmoji);
    }

    public void reactLike()
    {
        Utils.addReaction(getMessage(), Emojis.LIKE);
    }

    // arg stuff

    public void getArgumentAsUnsignedInt(final int argIndex, final IntConsumer consumer)
    {
        ArgumentUtils.parseArgumentAsUnsignedInt(args[argIndex], this, consumer);
    }

    public void getArgumentAsChannel(final int argIndex, final Consumer<TextChannel> consumer)
    {
        ArgumentUtils.parseArgumentAsTextChannel(args[argIndex], this, consumer);
    }

    public void getArgumentAsRole(final int argIndex, final Consumer<Role> consumer)
    {
        ArgumentUtils.parseArgumentAsRole(args[argIndex], this, consumer);
    }

    public void getArgumentAsUser(final int argIndex, final Consumer<User> consumer)
    {
        ArgumentUtils.parseArgumentAsUser(args[argIndex], this, consumer);
    }
}