package org.example;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * Class must implement LongPollingSingleThreadUpdateConsumer
 */
public class MyAmazingBot implements LongPollingSingleThreadUpdateConsumer {

  private final TelegramClient telegramClient;

  public MyAmazingBot(String botToken) {
    this.telegramClient = new OkHttpTelegramClient(botToken);
  }

  @Override
  public void consume(Update update) {
    if (update.hasMessage() && update.getMessage().hasText()) {
      String messageText = update.getMessage().getText();
      long chatId = update.getMessage().getChatId();

      SendMessage message = SendMessage
          .builder()
          .chatId(chatId)
          .text(messageText)
          .build();

      try {
        telegramClient.execute(message);
      } catch (TelegramApiException ex) {
        throw new RuntimeException(ex.getMessage());
      }
    }
  }
}
