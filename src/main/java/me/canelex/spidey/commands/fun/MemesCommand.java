package me.canelex.spidey.commands.fun;

import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

@SuppressWarnings("unused")
public class MemesCommand extends Command
{
    public MemesCommand()
    {
        super("memes", new String[]{"meme"}, "Sends a random meme", "memes", Category.FUN, Permission.UNKNOWN, 0, 4);
    }

    @Override
    public void execute(final String[] args, final Message msg) {}
}