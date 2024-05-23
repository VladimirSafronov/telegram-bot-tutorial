package org.example;

import java.util.Properties;
import org.example.util.PropertiesLoader;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

public class Main {

  public static void main(String[] args) {
    Properties properties = PropertiesLoader.getProperties();
    String botToken = properties.getProperty("bot.token");

    try (TelegramBotsLongPollingApplication telegramApp = new TelegramBotsLongPollingApplication()) {
      telegramApp.registerBot(botToken, new MyAmazingBot(botToken));
      System.out.println("MyAmazingBot successfully started!");
      Thread.currentThread().join();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}