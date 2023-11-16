package com.example.demo.src.main.java.com.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * This is the SpotifyLikeAppExampleCode class. This class is responsible for...
 * <p>
 * You can provide more details about the class here such as its
 * responsibilities,
 * any important algorithms it uses, any patterns this class is part of, etc.
 */
class SpotifyLikeAppExampleCode {
    private static final String BASE_PATH = "C:/Users/User/Downloads/Code/src/main/java/com/example/";
    private static Clip audioClip;
    private static final Scanner INPUT = new Scanner(System.in);
    private static long pausedPosition;

    /**
   * The main method that serves as the entry point for the application.
   * It reads the audio library, displays the menu, and handles user input.
   *
   * @param args the command-line arguments
   */
    public static void main(String[] args) {
        JSONArray library = readAudioLibrary();

        String userInput = "";
        while (!userInput.equals("q")) {
            menu();
            userInput = INPUT.nextLine().toLowerCase();
            handleMenu(userInput, library);
        }

        INPUT.close();
    }

    /**
   * Displays the main menu to the user.
   */
    public static void menu() {
        System.out.println("---- SpotifyLikeApp ----");
        System.out.println("[H]ome");
        System.out.println("[S]earch by title");
        System.out.println("[L]ibrary");
        System.out.println("[F]avorites");
        System.out.println("[Q]uit");
        System.out.println("\nEnter q to Quit:");
    }

    /**
   * Handles the user's menu selection.
   *
   * @param userInput the user's menu selection
   * @param library   the JSONArray containing the audio library
   */
    public static void handleMenu(String userInput, JSONArray library) {
        switch (userInput) {
            case "h":
                System.out.println("-->Home<--");
                break;
            case "s":
                System.out.println("-->Search by title<--");
                searchByTitle(library);
                break;
            case "l":
                System.out.println("-->Library<--");
                break;
            case "f":
                handleFavorites(library);
                break;
            case "p":
                System.out.println("-->Play<--");
                play((JSONObject) library.get(0));
                break;
            case "q":
                System.out.println("-->Quit<--");
                break;
            default:
                break;
        }
    }

    /**
   * Handles the Favorites menu option.
   * Displays the favorite songs and plays the selected favorite song.
   *
   * @param library the JSONArray containing the audio library
   */
    public static void handleFavorites(JSONArray library) {
        System.out.println("-->Favorites<--");
        displayFavorites(library);
        playSelectedFavorite(library);
    }

    /**
   * Displays the favorite songs to the user.
   *
   * @param library the JSONArray containing the audio library
   */
    public static void displayFavorites(JSONArray library) {
        ArrayList<JSONObject> favorites = getFavorites(library);

        if (favorites.isEmpty()) {
            System.out.println("No favorite songs found.");
        } else {
            displaySearchResults(favorites);
        }
    }

    /**
   * Plays the selected favorite song.
   *
   * @param library the JSONArray containing the audio library
   */
    public static void playSelectedFavorite(JSONArray library) {
        ArrayList<JSONObject> favorites = getFavorites(library);

        if (favorites.isEmpty()) {
            System.out.println("No favorite songs found.");
        } else {
            playSelectedSong(favorites);
        }
    }

    /**
   * Returns an ArrayList of favorite songs.
   *
   * @param library the JSONArray containing the audio library
   * @return an ArrayList of JSONObject where each JSONObject is a favorite song
   */
    public static ArrayList<JSONObject> getFavorites(JSONArray library) {
        ArrayList<JSONObject> favorites = new ArrayList<>();

        for (Object obj : library) {
            JSONObject song = (JSONObject) obj;
            boolean isFavorite = (boolean) song.get("isFavorite");

            if (isFavorite) {
                favorites.add(song);
            }
        }

        return favorites;
    }

    /**
   * Searches for songs by title in the library.
   * Displays the search results and plays the selected song.
   *
   * @param library the JSONArray containing the audio library
   */
    public static void searchByTitle(JSONArray library) {
        System.out.print("Enter the title to search: ");
        String searchTitle = INPUT.nextLine().toLowerCase();

        ArrayList<JSONObject> searchResults = new ArrayList<>();

        for (Object obj : library) {
            JSONObject song = (JSONObject) obj;
            String title = ((String) song.get("title")).toLowerCase();

            if (title.contains(searchTitle)) {
                searchResults.add(song);
            }
        }

        if (searchResults.isEmpty()) {
            System.out.println("No matching songs found.");
        } else {
            displaySearchResults(searchResults);
            playSelectedSong(searchResults);
        }
    }

    /**
   * Displays the search results to the user.
   *
   * @param searchResults an ArrayList of JSONObject where each JSONObject is a
   *                      search result
   */
    public static void displaySearchResults(ArrayList<JSONObject> searchResults) {
        System.out.println("Search Results:");
        for (int i = 0; i < searchResults.size(); i++) {
            JSONObject song = searchResults.get(i);
            System.out.println((i + 1) + ". " + song.get("title"));
        }
    }

    /**
   * Displays the playback options to the user.
   */
    public static void showPlaybackOptions() {
        System.out.println("Playback Options:");
        System.out.println("1. Pause");
        System.out.println("2. Stop");
        System.out.println("3. Rewind 5 seconds");
        System.out.println("4. Forward 5 seconds");
        System.out.println("5. Favorite");
        System.out.println("6. Back");
    }

    /**
   * Handles the user's playback option.
   *
   * @param option the user's playback option
   * @param song   the JSONObject representing the song being played
   */
    public static void handlePlaybackOptions(int option, JSONObject song) {
        switch (option) {
            case 1:
                if (audioClip != null && audioClip.isRunning()) {
                    pause();
                    showPlaybackOptions();
                    handlePlaybackOptions(INPUT.nextInt(), song);
                } else {
                    play(song);
                    showPlaybackOptions();
                    handlePlaybackOptions(INPUT.nextInt(), song);
                }
                break;
            case 2:
                stop();
                break;
            
            case 3:
                rewind(song);
                showPlaybackOptions();
                handlePlaybackOptions(INPUT.nextInt(), song);
                break;
            case 4:
                forward(song);
                showPlaybackOptions();
                handlePlaybackOptions(INPUT.nextInt(), song);
                break;
            case 5:
                markAsFavorite(song);
                showPlaybackOptions();
                handlePlaybackOptions(INPUT.nextInt(), song);
                break;
            case 6:
                showMenuAndExit();
                break;
            default:
                System.out.println("Invalid option. Please enter a number between 1 and 6.");
                break;
        }
    }

    /**
   * Plays the selected song from the search results.
   *
   * @param searchResults an ArrayList of JSONObject where each JSONObject is a
   *                      search result
   */
    public static void playSelectedSong(ArrayList<JSONObject> searchResults) {
        System.out.print("Enter the number of the song to play (or 0 to show menu and exit): ");
        int selectedNumber;

        try {
            selectedNumber = INPUT.nextInt();
        } catch (java.util.InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            INPUT.next(); // Consume the invalid input
            showMenuAndExit();
            return;
        }

        if (selectedNumber == 0 || selectedNumber > searchResults.size()) {
            showMenuAndExit();
        } else {
            playSong(searchResults.get(selectedNumber - 1));
            showPlaybackOptions();
            handlePlaybackOptions(INPUT.nextInt(), searchResults.get(selectedNumber - 1));
        }
    }

     /**
   * Shows the main menu and exits the search.
   */
    public static void showMenuAndExit() {
        System.out.println("Showing menu and exiting search.");
        menu();
        INPUT.nextLine(); // Consume the newline character
    }

    /**
   * Stops the currently playing song.
   */
    public static void stop() {
        if (audioClip != null && audioClip.isRunning()) {
            audioClip.stop();
            audioClip.setMicrosecondPosition(0);
            System.out.println("Song stopped.");
        } else {
            System.out.println("No song is currently playing.");
        }
    }

     /**
   * Rewinds the currently playing song by 5 seconds.
   *
   * @param song the JSONObject representing the song being played
   */
    public static void rewind(JSONObject song) {
        if (audioClip != null && audioClip.isRunning()) {
            long currentPosition = audioClip.getMicrosecondPosition();
            long newPosition = Math.max(0, currentPosition - 5 * 1000000);
            audioClip.stop();
            audioClip.setMicrosecondPosition(newPosition);
            audioClip.start();
            System.out.println("Rewinded 5 seconds.");
        } else {
            System.out.println("No song is currently playing.");
        }
    }

      /**
   * Forwards the currently playing song by 5 seconds.
   *
   * @param song the JSONObject representing the song being played
   */
    public static void forward(JSONObject song) {
        if (audioClip != null && audioClip.isRunning()) {
            long currentPosition = audioClip.getMicrosecondPosition();
            long newPosition = currentPosition + 5 * 1000000;
            audioClip.stop();
            audioClip.setMicrosecondPosition(newPosition);
            audioClip.start();
            System.out.println("Forwarded 5 seconds.");
        } else {
            System.out.println("No song is currently playing.");
        }
    }

    /**
   * Marks a song as a favorite or removes it from favorites.
   *
   * @param song the JSONObject representing the song to be marked or unmarked
   *     as
   *             a favorite
   */
    public static void markAsFavorite(JSONObject song) {
        boolean isFavorite = (boolean) song.get("isFavorite");
        song.put("isFavorite", !isFavorite);
        String action = isFavorite ? "removed from" : "added to";
        System.out.println("Song " + action + " favorites.");
    }

    /**
   * Plays a song.
   *
   * @param song the JSONObject representing the song to be played
   */
    private static void playSong(JSONObject song) {
        final String filename = (String) song.get("filePath");
        final String filePath = BASE_PATH + "/wav/" + filename;
        final File file = new File(filePath);

        System.out.println(filePath);

        if (audioClip != null) {
            audioClip.close();
        }

        try {
            audioClip = AudioSystem.getClip();
            final AudioInputStream in = AudioSystem.getAudioInputStream(file);

            audioClip.open(in);
            audioClip.setMicrosecondPosition(0);
            audioClip.loop(Clip.LOOP_CONTINUOUSLY);

            // Add a delay before playing the next song
            Thread.sleep(5000); // You can adjust the delay as needed

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // private static long pausedPosition = 0;

    /**
   * Plays a song.
   *
   * @param song the JSONObject representing the song to be played
   */
    public static void play(JSONObject song) {
        if (audioClip != null) {
            audioClip.close();
        }

        final String filename = (String) song.get("filePath");
        final String filePath = BASE_PATH + "/wav/" + filename;
        final File file = new File(filePath);

        System.out.println(filePath);

        try {
            audioClip = AudioSystem.getClip();
            final AudioInputStream in = AudioSystem.getAudioInputStream(file);

            audioClip.open(in);

            // Set the stored position if resuming from pause
            audioClip.setMicrosecondPosition(pausedPosition);

            audioClip.loop(Clip.LOOP_CONTINUOUSLY);

            System.out.println("Song playing. Press 1 to pause.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
   * Pauses the currently playing song.
   */
    public static void pause() {
        if (audioClip != null && audioClip.isRunning()) {
            pausedPosition = audioClip.getMicrosecondPosition();
            audioClip.stop();
            System.out.println("Song paused. Press 1 to resume.");
        } else {
            System.out.println("No song is currently playing.");
        }
    }

    private static JSONArray readJSONArrayFile(String fileName) {
        JSONParser jsonParser = new JSONParser();

        JSONArray dataArray = null;

        try (FileReader reader = new FileReader(fileName)) {
            Object obj = jsonParser.parse(reader);

            dataArray = (JSONArray) obj;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return dataArray;
    }

    private static JSONArray readAudioLibrary() {
        final String jsonFileName = "audio-library.json";
        final String filePath = BASE_PATH + "/" + jsonFileName;

        JSONArray jsonData = readJSONArrayFile(filePath);

        System.out.println("Reading the file " + filePath);

        return jsonData;
    }
}
