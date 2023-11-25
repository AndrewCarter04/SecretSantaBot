package uk.andrewcarterdevelopments.Discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @author Andrew Carter
 */
public class GameManager {

    private final String FILE_PATH = "data/secretsanta.json";

    /**
     * Read the whole JSON Object from the file.
     * @return The file contents as a JSONObject
     */
    private JSONObject readJSONObject() {
        File jsonFile = new File(FILE_PATH);
        if (!jsonFile.getParentFile().exists()) {
            jsonFile.getParentFile().mkdirs();
        }
        try (FileReader reader = new FileReader(FILE_PATH)) {
            return new JSONObject(new JSONTokener(reader));
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    /**
     * Set data inside the JSON file, and save it
     * @param key   The key for the data
     * @param data  The data itself
     */
    private void set(String key, Object data) {
        JSONObject json = readJSONObject();
        json.put(key, data);
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            writer.write(json.toString(4));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Remove data from the JSON file, using the key
     * @param key   The key for the data to remove
     */
    private void remove(String key) {
        JSONObject json = readJSONObject();
        json.remove(key);
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            writer.write(json.toString(4));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Get data from the JSON file, using the key
     * @param key   The key for the data to get
     * @return  The data itself
     */
    private Object get(String key) {
        return readJSONObject().get(key);
    }

    /**
     * Get if the JSON file contains data for the provided key
     * @param key   The key to check
     * @return  If the JSON file contains anything with the specified key
     */
    private boolean contains(String key) {
        return readJSONObject().has(key);
    }


    /**
     * Get if the game has started
     * @return  If the game has started
     */
    public boolean hasStarted() {
        return contains("game");
    }

    /**
     * Add a user to the game
     * @param userID    ID of the user to add
     */
    public void addUser(String userID) {
        JSONArray users;
        if (contains("users")) {
            users = (JSONArray) get("users");
        } else {
            users = new JSONArray();
        }
        users.put(userID);
        set("users", users);
    }

    /**
     * Remove a user from the game
     * @param userID    ID of the user to remove
     */
    public void removeUser(String userID) {
        JSONArray users;
        if (contains("users")) {
            users = (JSONArray) get("users");
        } else {
            users = new JSONArray();
        }
        for (int i = 0; i < users.length(); i++) {
            if (users.get(i).equals(userID)) users.remove(i);
        }
        set("users", users);
    }

    /**
     * Get all the users in the game
     * @return  An array of all the user IDs in the game
     */
    private String[] getUsers() {
        JSONArray users = (JSONArray) get("users");
        return users.toList().stream().map(Object::toString).toArray(String[]::new);
    }

    /**
     * Set the due date for the game
     * @param date  The due date for the game
     */
    public void setDueDate(Date date) {
        set("due-date", date);
    }

    /**
     * Get the due date for the game
     * @return  The due date for the game
     */
    private Date getDueDate() {
        String strDate = get("due-date").toString();
        SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        try {
            return inputFormat.parse(strDate);
        } catch (ParseException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Get the embedded message to send the user for when the game is starting
     * @param user  The User the message will be sent to
     * @return  The MessageEmbed containing the data to be sent
     */
    private MessageEmbed getUserStartDM(User user) {

        DateFormat ukFormat = new SimpleDateFormat("dd/MM/yyyy");

        EmbedBuilder builder = new EmbedBuilder();

        builder.setColor(Color.red);
        builder.setThumbnail("https://cdn-icons-png.flaticon.com/512/6235/6235092.png");
        builder.setTitle("You are buying for...");
        builder.addField(user.getName(), user.getAsMention(), false); //.getGlobalName
        builder.addBlankField(false);
        builder.addField("Reveal by date", ukFormat.format(getDueDate()), false);
        builder.addBlankField(false);
        builder.setFooter("Good luck!", SecretSantaBot.getJDA().getSelfUser().getAvatarUrl());

        return builder.build();
    }

    /**
     * Start the game
     * @return  The List of user IDs the bot failed to DM
     */
    public List start() {

        set("start-date", new Date());

        String[] users = getUsers();

        if (contains("game")) {
            remove("game");
        }

        String[] users1 = users.clone();
        String[] users2 = users.clone();

        while (containsSameIndex(users1, users2)) {
            shuffleArray(users1);
            shuffleArray(users2);
        }

        JSONObject game = new JSONObject();
        JSONObject whoHasWho = new JSONObject();

        for (int i = 0; i < users1.length; i++) {
            System.out.println(users1[i] + " - " + users2[i]);
            whoHasWho.put(users1[i], users2[i]);
        }

        game.put("who-has-who", whoHasWho);

        List failedUsers = new List();
        JSONArray failedJSON = new JSONArray();

        for (int i = 0; i < users1.length; i++) {

            String userID = users1[i];

            User user = SecretSantaBot.getJDA().retrieveUserById(userID).complete();
            User user2 = SecretSantaBot.getJDA().retrieveUserById(users2[i]).complete();

            MessageEmbed msg = getUserStartDM(user2);

            try {
                user.openPrivateChannel().complete().sendMessageEmbeds(msg).complete();
            } catch (Exception exception) {
                failedUsers.add(user.getName() + " (" + user.getAsMention() + ")");
                failedJSON.put(userID);
            }


        }

        for (String failedUser : failedUsers.getItems()) {
            System.err.println("Failed to DM user " + failedUser);
        }

        game.put("failed", failedJSON);

        set("game", game);

        return failedUsers;

    }

    /**
     * End the game
     */
    public void end() {

        if (contains("game")) {
            remove("game");
        }

    }

    /**
     * Resend the previously failed messages from when the game started
     * @return  The List of user IDs the bot failed to DM
     */
    public List resendFailed() {

        JSONObject game = (JSONObject) get("game");
        JSONArray failedJSON = (JSONArray) game.get("failed");
        JSONObject whoHasWho = (JSONObject) game.get("who-has-who");

        List failedUsers = new List();

        for (int i = 0; i < failedJSON.length(); i++) {

            String userID = failedJSON.getString(i);
            String user2ID = whoHasWho.getString(userID);

            User user = SecretSantaBot.getJDA().retrieveUserById(userID).complete();
            User user2 = SecretSantaBot.getJDA().retrieveUserById(user2ID).complete();

            MessageEmbed msg = getUserStartDM(user2);

            try {
                user.openPrivateChannel().complete().sendMessageEmbeds(msg).complete();
            } catch (Exception exception) {
                failedUsers.add(user.getName() + " (" + user.getAsMention() + ")");
            }

        }

        return failedUsers;

    }

    /**
     * Randomly shuffle an array
     * @param array Array to shuffle
     */
    private void shuffleArray(String[] array) {
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            String temp = array[i];
            array[i] = array[index];
            array[index] = temp;
        }
    }

    /**
     * Check if 2 arrays contain a same value at the same position in the arrays
     * @param array1    The first array
     * @param array2    The second array
     * @return  If the arrays contain a same value at the same position in the arrays
     */
    private boolean containsSameIndex(String[] array1, String[] array2) {
        boolean sameIndex = false;
        for (int i = 0; i < array1.length; i++) {
            if (array1[i].equals(array2[i])) sameIndex = true;
        }
        return sameIndex;
    }

}
