package uk.andrewcarterdevelopments.Discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.util.Date;

public class GameManager {

    private final String FILE_NAME = "data/secretsanta.json";

    private void set(String key, Object data) {

        

    }

    private Object get(String key) {
        return null;
    }



    public boolean hasStarted() {
        return false;
    }

    public void start() {

    }

    public void addUser(String userID) {

    }

    public void setDate(Date date) {

    }

    public MessageEmbed getUserStartDM(User user) {

        EmbedBuilder builder = new EmbedBuilder();

        builder.setColor(Color.red);
        builder.setTitle("Secret Santa - Start");


        return builder.build();
    }

}
