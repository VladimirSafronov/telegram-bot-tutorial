package org.example;

import org.example.util.PropertiesLoader;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class PhotoBot implements LongPollingSingleThreadUpdateConsumer {

  private final TelegramClient telegramClient;
  private final String SNICKERS_CAPTION = "snickers";

  public PhotoBot(String botToken) {
    this.telegramClient = new OkHttpTelegramClient(botToken);
  }

  @Override
  public void consume(Update update) {

    if (update.hasMessage() && update.getMessage().hasText()) {
      String msgText = update.getMessage().getText();
      long chatId = update.getMessage().getChatId();
      if (msgText.equals("/start")) {
        sendMessage(chatId, msgText);
      } else if (msgText.equals("/pic")) {
        sendPhoto(chatId, "PICTURE_URL", "CAPTURE");
      } else if (msgText.equals("/markup")) {
        SendMessage message = SendMessage
            .builder()
            .chatId(chatId)
            .text(PropertiesLoader.getProperty("ASSORTMENT_TITLE"))
            .build();

        message.setReplyMarkup(ReplyKeyboardMarkup
            .builder()
            .keyboardRow(new KeyboardRow(PropertiesLoader.getProperty("BUTTON_1"),
                PropertiesLoader.getProperty("BUTTON_2")))
            .keyboardRow(new KeyboardRow(PropertiesLoader.getProperty("BUTTON_3"),
                PropertiesLoader.getProperty("BUTTON_4")))
            .build());

        try {
          telegramClient.execute(message);
        } catch (TelegramApiException ex) {
          throw new RuntimeException(ex.getMessage());
        }
      } else if (msgText.equals("nike")) {
        sendPhoto(chatId, "PHOTO_NIKE", SNICKERS_CAPTION);
      } else if (msgText.equals("adidas")) {
        sendPhoto(chatId, "PHOTO_ADIDAS", SNICKERS_CAPTION);
      } else if (msgText.equals("bmi")) {
        sendPhoto(chatId, "PHOTO_BMI", SNICKERS_CAPTION);
      } else if (msgText.equals("new balance")) {
        sendPhoto(chatId, "PHOTO_NEW_BALANCE", SNICKERS_CAPTION);
      } else if (msgText.equals("/hide")) {
        SendMessage message = SendMessage
            .builder()
            .chatId(chatId)
            .text("Keyboard hidden!")
            .replyMarkup(new ReplyKeyboardRemove(true))
            .build();

        try {
          telegramClient.execute(message);
        } catch (TelegramApiException ex) {
          throw new RuntimeException(ex.getMessage());
        }
      } else {
        String textMessage = PropertiesLoader.getProperty("UNKNOWN_COMMAND");
        sendMessage(chatId, textMessage);
      }
    }
  }

  private void sendPhoto(long chatId, String url, String caption) {
    SendPhoto message = SendPhoto
        .builder()
        .chatId(chatId)
        .photo(new InputFile(PropertiesLoader.getProperty(url)))
        .caption(PropertiesLoader.getProperty(caption))
        .build();

    try {
      telegramClient.execute(message);
    } catch (TelegramApiException ex) {
      throw new RuntimeException(ex.getMessage());
    }
  }

  private void sendMessage(long chatId, String messageText) {
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