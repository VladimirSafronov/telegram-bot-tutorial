package org.example;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class SpringBootBot implements LongPollingSingleThreadUpdateConsumer, SpringLongPollingBot {

  private final TelegramClient telegramClient;

  public SpringBootBot() {
    this.telegramClient = new OkHttpTelegramClient(getBotToken());
  }

  @Override
  public String getBotToken() {
    return System.getenv().get("TELEGRAM_BOT_TOKEN");
  }

  @Override
  public LongPollingUpdateConsumer getUpdatesConsumer() {
    return this;
  }

  @Override
  public void consume(Update update) {
    if (update.hasMessage() && update.getMessage().hasText()) {
      String msgText = update.getMessage().getText();
      long chatId = update.getMessage().getChatId();

      SendMessage message = SendMessage
          .builder()
          .chatId(chatId)
          .text(msgText)
          .build();

      try {
        telegramClient.execute(message);
      } catch (TelegramApiException ex) {
        throw new RuntimeException(ex.getMessage());
      }
    }
  }

  @AfterBotRegistration
  public void afterRegistration(BotSession session) {
    System.out.println("Registered bot running state is: " + session.isRunning());
  }
}
