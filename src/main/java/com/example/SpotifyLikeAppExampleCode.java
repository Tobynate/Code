package com.example;

import static javax.sound.sampled.AudioSystem.*;

import java.io.*;
import java.util.*;
import javax.sound.sampled.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class SpotifyLikeAppExampleCode {

  private static String basePath = "C:/Users/User/Downloads/Code/src/main/java/com/example/";
  private static Clip audioClip;
  private static Scanner input = new Scanner(System.in);

  public static void main(final String[] args) {
    JSONArray library = readAudioLibrary();

    String userInput = "";
    while (!userInput.equals("q")) {
      menu();
      userInput = input.nextLine().toLowerCase();
      handleMenu(userInput, library);
    }

    input.close();
  }

  public static void menu() {
    System.out.println("---- SpotifyLikeApp ----");
    System.out.println("[H]ome");
    System.out.println("[S]earch by title");
    System.out.println("[L]ibrary");
    System.out.println("[F]avorites");
    System.out.println("[Q]uit");
    System.out.println("\nEnter q to Quit:");
  }

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

  public static void handleFavorites(JSONArray library) {
    System.out.println("-->Favorites<--");
    displayFavorites(library);
    playSelectedFavorite(library);
  }

  public static void displayFavorites(JSONArray library) {
    ArrayList<JSONObject> favorites = getFavorites(library);

    if (favorites.isEmpty()) {
      System.out.println("No favorite songs found.");
    } else {
      displaySearchResults(favorites);
    }
  }

  public static void playSelectedFavorite(JSONArray library) {
    ArrayList<JSONObject> favorites = getFavorites(library);

    if (favorites.isEmpty()) {
      System.out.println("No favorite songs found.");
    } else {
      playSelectedSong(favorites);
    }
  }

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

  public static void searchByTitle(JSONArray library) {
    System.out.print("Enter the title to search: ");
    String searchTitle = input.nextLine().toLowerCase();

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

  public static void displaySearchResults(ArrayList<JSONObject> searchResults) {
    System.out.println("Search Results:");
    for (int i = 0; i < searchResults.size(); i++) {
      JSONObject song = searchResults.get(i);
      System.out.println((i + 1) + ". " + song.get("title"));
    }
  }

  public static void showPlaybackOptions() {
    System.out.println("Playback Options:");
    System.out.println("1. Pause");
    System.out.println("2. Stop");
    System.out.println("3. Rewind 5 seconds");
    System.out.println("4. Forward 5 seconds");
    System.out.println("5. Favorite");
    System.out.println("6. Back");
  }

  public static void handlePlaybackOptions(int option, JSONObject song) {
    switch (option) {
      case 1:
        if (audioClip != null && audioClip.isRunning()) {
          pause();
          showPlaybackOptions();
          handlePlaybackOptions(input.nextInt(), song);
        } else {
          play(song);
          showPlaybackOptions();
          handlePlaybackOptions(input.nextInt(), song);
        }
        break;
      case 2:
        stop();
        break;
      case 3:
        rewind(song);
        showPlaybackOptions();
        handlePlaybackOptions(input.nextInt(), song);
        break;
      case 4:
        forward(song);
        showPlaybackOptions();
        handlePlaybackOptions(input.nextInt(), song);
        break;
      case 5:
        markAsFavorite(song);
        showPlaybackOptions();
        handlePlaybackOptions(input.nextInt(), song);
        break;
      case 6:
        showMenuAndExit();
        break;
      default:
        System.out.println("Invalid option. Please enter a number between 1 and 6.");
        break;
    }
  }

  public static void playSelectedSong(ArrayList<JSONObject> searchResults) {
    System.out.print("Enter the number of the song to play (or 0 to show menu and exit): ");
    int selectedNumber;

    try {
      selectedNumber = input.nextInt();
    } catch (java.util.InputMismatchException e) {
      System.out.println("Invalid input. Please enter a number.");
      input.next(); // Consume the invalid input
      showMenuAndExit();
      return;
    }

    if (selectedNumber == 0 || selectedNumber > searchResults.size()) {
      showMenuAndExit();
    } else {
      playSong(searchResults.get(selectedNumber - 1));
      showPlaybackOptions();
      handlePlaybackOptions(input.nextInt(), searchResults.get(selectedNumber - 1));
    }
  }

  public static void showMenuAndExit() {
    System.out.println("Showing menu and exiting search.");
    menu();
    input.nextLine(); // Consume the newline character
  }

  public static void stop() {
    if (audioClip != null && audioClip.isRunning()) {
      audioClip.stop();
      audioClip.setMicrosecondPosition(0);
      System.out.println("Song stopped.");
    } else {
      System.out.println("No song is currently playing.");
    }
  }

  public static void rewind(JSONObject song) {
    if (audioClip != null && audioClip.isRunning()) {
      long currentPosition = audioClip.getMicrosecondPosition();
      long newPosition = Math.max(0, currentPosition - 5 * 1000000); // Rewind 5 seconds

      audioClip.stop();
      audioClip.setMicrosecondPosition(newPosition);
      audioClip.start();

      System.out.println("Rewinded 5 seconds.");
    } else {
      System.out.println("No song is currently playing.");
    }
  }

  public static void forward(JSONObject song) {
    if (audioClip != null && audioClip.isRunning()) {
      long currentPosition = audioClip.getMicrosecondPosition();

      // Forward 5 seconds
      long newPosition = currentPosition + 5 * 1000000;

      audioClip.stop();
      audioClip.setMicrosecondPosition(newPosition);
      audioClip.start();

      System.out.println("Forwarded 5 seconds.");
    } else {
      System.out.println("No song is currently playing.");
    }
  }

  public static void markAsFavorite(JSONObject song) {
    boolean isFavorite = (boolean) song.get("isFavorite");
    song.put("isFavorite", !isFavorite);

    String action = isFavorite ? "removed from" : "added to";
    System.out.println("Song " + action + " favorites.");
  }

  private static void playSong(JSONObject song) {
    final String filename = (String) song.get("filePath");
    final String filePath = basePath + "/wav/" + filename;
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

  public static void play(JSONObject song) {
    if (audioClip != null) {
      audioClip.close();
    }

    final String filename = (String) song.get("filePath");
    final String filePath = basePath + "/wav/" + filename;
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

  private static long pausedPosition = 0;

  public static void pause() {
    if (audioClip != null && audioClip.isRunning()) {
      // Store the current position before pausing
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
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return dataArray;
  }

  public static JSONArray readAudioLibrary() {
    final String jsonFileName = "audio-library.json";
    final String filePath = basePath + "/" + jsonFileName;

    JSONArray jsonData = readJSONArrayFile(filePath);

    System.out.println("Reading the file " + filePath);

    return jsonData;
  }
}
