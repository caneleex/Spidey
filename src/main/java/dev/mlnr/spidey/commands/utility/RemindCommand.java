package dev.mlnr.spidey.commands.utility;

import dev.mlnr.spidey.Core;
import dev.mlnr.spidey.objects.cache.PrefixCache;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class RemindCommand extends Command
{
    private static final Pattern TIME_REGEX = Pattern.compile("(\\d+)((?:w(?:eek(?:s)?)?)|(?:d(?:ay(?:s)?)?)|(?:h(?:our(?:s)?)?)|(?:m(?:in(?:ute(?:s)?)?)?)|(?:s(?:ec(?:ond(?:s)?)?)?))", Pattern.CASE_INSENSITIVE);

    public RemindCommand()
    {
        super("remind", new String[]{"reminder", "remindme"}, "Reminds you after a specified time interval (max 1 week)", "remind s/m/h/d/w (something)", Category.UTILITY, Permission.UNKNOWN, 2, 10);
    }

    @Override
    public void execute(final String[] args, final Message msg) // TODO database saving
    {
        final var prefix = PrefixCache.retrievePrefix(msg.getGuild().getIdLong());
        final var channel = msg.getTextChannel();
        if (args.length == 0)
        {
            Utils.returnError("Please enter a valid time interval, for example `" + prefix + "remind 2h(our(s)) do the homework`", msg);
            return;
        }
        final var matcher = TIME_REGEX.matcher(args[0]);
        if (!matcher.matches())
        {
            Utils.returnError("Please enter a valid time interval, for example `" + prefix + "remind 2h(our(s)) do the homework`", msg);
            return;
        }
        final var duration = Integer.parseInt(matcher.group(1));
        final var unit = matcher.group(2).toLowerCase();
        if (duration == 0)
        {
            Utils.returnError("Duration can't be 0", msg);
            return;
        }
        if (unit.charAt(0) == 'm' && duration < 5)
        {
            Utils.returnError("Duration can't be less than 5 minutes", msg);
            return;
        }
        final var timeUnit = getUnit(unit);
        final var actualDuration = unit.charAt(0) == 'w' ? duration * 7 : duration;
        if (timeUnit.toSeconds(actualDuration) > 604800)
        {
            Utils.returnError("Duration can't be more than 1 week", msg);
            return;
        }
        final var author = msg.getAuthor();
        final var mention = author.getAsMention();
        final var eb = new EmbedBuilder();
        final var end = args.length == 1 ? "!" : " to **" + args[1] + "**!";
        final var reminder = "Hey, i'm here to remind you" + end;
        eb.setAuthor("REMINDER");
        eb.setDescription(reminder);
        eb.setColor(0xFEFEFE);
        eb.setFooter("Reminder created");
        eb.setTimestamp(msg.getTimeCreated());

        Utils.deleteMessage(msg);
        final var unitName = timeUnit.name().toLowerCase();
        final var interval = unit.charAt(0) == 'w' ? "week" : (duration == 1 ? unitName.substring(0, unitName.length() - 1) : unitName);
        channel.sendMessage("Okay " + mention + ", i'll remind you in **" + duration + " " + interval + "**" + end)
               .delay(Duration.ofSeconds(5))
               .flatMap(Message::delete)
               .queue();
        author.openPrivateChannel()
              .delay(actualDuration, timeUnit, Core.getExecutor())
              .flatMap(ch -> ch.sendMessage(eb.build()))
              .onErrorFlatMap(ignored -> channel.canTalk(), ignored -> channel.sendMessage(mention + " " + reminder))
              .queue();
    }

    private TimeUnit getUnit(final String s)
    {
        switch (s.charAt(0))
        {
            case 's':
                return TimeUnit.SECONDS;
            case 'm':
                return TimeUnit.MINUTES;
            case 'h':
                return TimeUnit.HOURS;
            case 'd':
            case 'w':
                return TimeUnit.DAYS;
            default:
        }
        return TimeUnit.valueOf("");
    }
}