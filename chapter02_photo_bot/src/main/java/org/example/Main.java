package org.example;

import java.util.Map;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

public class Main {

  public static void main(String[] args) {

    Map<String, String> env = System.getenv();
    String botToken = env.get("TELEGRAM_BOT_TOKEN");

    try (TelegramBotsLongPollingApplication application = new TelegramBotsLongPollingApplication()) {
      application.registerBot(botToken, new PhotoBot(botToken));
      System.out.println("PhotoBot successfully started!");
      Thread.currentThread().join();
    } catch (Exception ex) {
      throw new RuntimeException(ex.getMessage());
    }
  }
}