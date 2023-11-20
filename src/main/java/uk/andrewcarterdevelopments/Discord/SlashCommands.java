package uk.andrewcarterdevelopments.Discord;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

            gameManager.start();

            event.reply("Starting game!").queue();

        }

        else if (event.getName().equals("add-user")) {

            if (!gameManager.hasStarted()) {

                User user = event.getOption("user").getAsUser();

                gameManager.addUser(user.getId());

                event.reply(String.format("Sucessfully added {0}!", user.getGlobalName())).queue();

            } else {
                event.reply("The game has already started, you cannot add any more users to it.").queue();
            }

        }

        else if (event.getName().equals("set-date")) {

            if (!gameManager.hasStarted()) {

                DateFormat ukFormat = new SimpleDateFormat("dd/MM/yyyy");
                String dateStr = event.getOption("date").getAsString();
                Date date;

                try {
                    date = ukFormat.parse(dateStr);
                } catch (ParseException e) {
                    event.reply("That is not a valid date! Format: dd/mm/yyyy").queue();
                    return;
                }

                gameManager.setDate(date);

                event.reply(String.format("Set the date to {0}!", dateStr)).queue();

            } else {
                event.reply("The game has already started, you cannot add any more users to it.").queue();
            }

        }

    }

}
