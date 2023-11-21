package uk.andrewcarterdevelopments.Discord;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
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

            if (!gameManager.hasStarted()) {

                event.deferReply().queue();

                List failedUsers = gameManager.start();
                String msg = "Game Started!";

                if (failedUsers.getItemCount() != 0) {
                    msg += "\n\nFailed to DM the following users:";
                    for (String failedUser : failedUsers.getItems()) {
                        msg += "\n" + failedUser;
                    }
                }

                event.getHook().sendMessage(msg).queue();

            } else {
                event.reply("The game has already started, you cannot start it again.").queue();
            }

        }

        else if (event.getName().equals("resend-failed")) {

            if (gameManager.hasStarted()) {

                event.deferReply().queue();

                List failedUsers = gameManager.resendFailed();
                String msg = "Attempted to resend failed messages.";

                if (failedUsers.getItemCount() != 0) {
                    msg += "\n\nStill failed to DM the following users:";
                    for (String failedUser : failedUsers.getItems()) {
                        msg += "\n" + failedUser;
                    }
                }

                event.getHook().sendMessage(msg).queue();

            } else {
                event.reply("The game has not started yet.").queue();
            }

        }

        else if (event.getName().equals("add-user")) {

            if (!gameManager.hasStarted()) {

                User user = event.getOption("user").getAsUser();

                gameManager.addUser(user.getId());

                event.reply("Successfully added " + user.getGlobalName() + "!").queue();

            } else {
                event.reply("The game has already started, you cannot add any more users to it.").queue();
            }

        }

        else if (event.getName().equals("remove-user")) {

            if (!gameManager.hasStarted()) {

                User user = event.getOption("user").getAsUser();

                gameManager.removeUser(user.getId());

                event.reply("Successfully removed " + user.getGlobalName() + "!").queue();

            } else {
                event.reply("The game has already started, you cannot remove any users from it.").queue();
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

                gameManager.setDueDate(date);

                event.reply("Set the date to " + dateStr + "!").queue();

            } else {
                event.reply("The game has already started, you cannot add any more users to it.").queue();
            }

        }

    }

}
