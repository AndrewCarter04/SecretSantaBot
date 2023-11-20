package uk.andrewcarterdevelopments.Discord;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SlashCommands extends ListenerAdapter {

    private GameManager gameManager;

    public SlashCommands() {
        gameManager = new GameManager();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if (event.getName().equals("testing")) {

            event.reply("Testing!").queue();

        }

        else if (event.getName().equals("start")) {

            

        }

        else if (event.getName().equals("add-user")) {

            if (event.getOption("user") != null) {

                if (!gameManager.hasStarted()) {

                    User user = event.getOption("user").getAsUser();

                    event.reply(user.getName()).queue();

                } else {
                    event.reply("The game has already started, you cannot add any more users to it.").queue();
                }

            } else {
                event.reply("You must specify a user to add!").queue();
            }

        }

    }

}
