package uk.andrewcarterdevelopments.Discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class SecretSantaBot {

    private static JDA jda;

    public static void main(String[] args) throws InterruptedException {

        JDABuilder builder = JDABuilder.createDefault(Private.getToken());

        builder.setActivity(Activity.playing("Secret Santa Setup!"));
        builder.setStatus(OnlineStatus.DO_NOT_DISTURB);
        builder.disableCache(CacheFlag.VOICE_STATE, CacheFlag.SCHEDULED_EVENTS);
        builder.setEnabledIntents(GatewayIntent.DIRECT_MESSAGES, GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_EMOJIS_AND_STICKERS);

        builder.addEventListeners(new SlashCommands());

        jda = builder.build();

        jda.awaitReady();

        jda.updateCommands().addCommands(
                Commands.slash("testing", "Testing command."),
                Commands.slash("add-user", "Add a user to the secret santa.")
                        .addOption(OptionType.USER, "user", "The user to add.", true)
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL, Permission.MODERATE_MEMBERS)),
                Commands.slash("start", "Start the secret santa.")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL, Permission.MODERATE_MEMBERS)),
                Commands.slash("set-date", "Set the deadline date for the secret santa.")
                        .addOption(OptionType.STRING, "date", "Format: dd/mm/yyyy", true)
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL, Permission.MODERATE_MEMBERS))
        ).queue();

    }
}