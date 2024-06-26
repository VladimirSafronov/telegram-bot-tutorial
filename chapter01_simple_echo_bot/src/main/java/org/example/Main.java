package org.example;

import java.util.Map;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

public class Main {

  public static void main(String[] args) {

    Map<String, String> env = System.getenv();
    String botToken = env.get("TELEGRAM_BOT_TOKEN");

    try (TelegramBotsLongPollingApplication telegramApp = new TelegramBotsLongPollingApplication()) {
      telegramApp.registerBot(botToken, new MyAmazingBot(botToken));
      System.out.println("MyAmazingBot successfully started!");
      Thread.currentThread().join();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}