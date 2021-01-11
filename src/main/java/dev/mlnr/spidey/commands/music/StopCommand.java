package dev.mlnr.spidey.commands.music;

import dev.mlnr.spidey.cache.music.MusicPlayerCache;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.MusicUtils;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class StopCommand extends Command
{
    public StopCommand()
    {
        super("stop", new String[]{"disconnect", "dis"}, Category.MUSIC, Permission.UNKNOWN, 0, 0);
    }

    @Override
    public void execute(final String[] args, final CommandContext ctx)
    {
        final var guild = ctx.getGuild();
        final var musicPlayer = MusicPlayerCache.getMusicPlayer(guild);
        final var i18n = ctx.getI18n();
        if (musicPlayer == null)
        {
            ctx.replyError(i18n.get("music.messages.failure.no_music"));
            return;
        }
        if (!MusicUtils.canInteract(ctx.getMember()))
        {
            ctx.replyError(i18n.get("music.messages.failure.cant_interact", "stop the playback"));
            return;
        }
        MusicPlayerCache.disconnectFromChannel(guild);
        Utils.addReaction(ctx.getMessage(), "\uD83D\uDC4B"); // wave
    }
}