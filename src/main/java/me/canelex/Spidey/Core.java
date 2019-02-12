package me.canelex.Spidey;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

public class Core {	
	
	public static JDA jda;
							
	public static void main( String[] args ) throws Exception {
    	
		jda = new JDABuilder(AccountType.BOT)
    			.setToken(Secrets.token)
    			.addEventListeners(new Events())
    			.setActivity(Activity.streaming("discord.gg/cnAgKrv", "https://twitch.tv/canelex_"))    			
    			.build().awaitReady();               
        
    }                        
    
}
