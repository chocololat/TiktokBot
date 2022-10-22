package org.virep.tiktokbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virep.tiktokbot.utils.Config;

public class Main {
    public static JDA jda;

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException {
        jda = JDABuilder
                .createDefault(Config.get("TOKEN"))
                .enableIntents(GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.DIRECT_MESSAGES)
                .build()
                .awaitReady();

        log.info("Bot has successfully initialized !");

        jda.getPresence().setActivity(Activity.watching("tiktoks"));
    }
}