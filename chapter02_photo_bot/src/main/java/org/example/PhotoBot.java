package org.example;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vdurmont.emoji.EmojiParser;
import io.lettuce.core.api.sync.RedisCommands;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.bson.Document;
import org.example.util.PropertiesLoader;
import org.example.util.RedisIntegrator;
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
  private final static String SNICKERS_CAPTION = "snickers";

  public PhotoBot(String botToken) {
    this.telegramClient = new OkHttpTelegramClient(botToken);
  }

  @Override
  public void consume(Update update) {

    if (update.hasMessage() && update.getMessage().hasText()) {
      long chatId = update.getMessage().getChatId();
      String msgText = update.getMessage().getText();
      String firstName = update.getMessage().getChat().getFirstName();
      String lastName = update.getMessage().getChat().getLastName();
      long userId = update.getMessage().getChat().getId();
      String userName = update.getMessage().getChat().getUserName();
      log(firstName, lastName, userId, msgText);

      switch (msgText) {
        case "/start" -> {
//          checkUserExists(firstName, lastName, userId, userName);
          sendMessage(chatId, msgText);
        }
        case "/redis" -> {
          RedisIntegrator redisIntegrator = new RedisIntegrator();
          RedisCommands<String, String> syncCommands = redisIntegrator.getSyncCommands();
          syncCommands.set(String.valueOf(userId), userName);
          String msgRedis = syncCommands.get(String.valueOf(userId)) + " and Redis works!";
          redisIntegrator.closeConnections();

          sendMessage(chatId, msgRedis);
        }
        case "/pic" -> sendPhoto(chatId, "PICTURE_URL", "CAPTURE");
        case "/markup" -> {
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
          break;
        }
        case "nike" -> sendPhoto(chatId, "PHOTO_NIKE", SNICKERS_CAPTION);
        case "adidas" -> sendPhoto(chatId, "PHOTO_ADIDAS", SNICKERS_CAPTION);
        case "bmi" -> sendPhoto(chatId, "PHOTO_BMI", SNICKERS_CAPTION);
        case "new balance" -> sendPhoto(chatId, "PHOTO_NEW_BALANCE", SNICKERS_CAPTION);
        case "/hide" -> {
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
          break;
        }
        default -> {
          String textMessage = PropertiesLoader.getProperty("UNKNOWN_COMMAND");
          String emojiTextMessage = EmojiParser.parseToUnicode(textMessage + " \uD83D\uDC80");
          sendMessage(chatId, emojiTextMessage);
        }
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

  private void log(String firstName, String lastName, long userId, String message) {
    System.out.println("\n ----------------------------");
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    System.out.println(dateFormat.format(new Date()));
    System.out.println(
        "Message from: " + firstName + " " + lastName + " with userId: " + userId + "\n" +
            "Text: " + message);
  }

  private void checkUserExists(String firstName, String lastName, long userId, String userName) {

    String uri = "mongodb://localhost:27017";
    try (MongoClient mongoClient = MongoClients.create(uri)) {
      MongoDatabase database = mongoClient.getDatabase("mongoDB");
      MongoCollection<Document> collection = database.getCollection("users");

      long found = collection.countDocuments(Document.parse("{id : " + userId + "}"));
      if (found == 0) {
        Document doc = new Document("first_name", firstName)
            .append("last_name", lastName)
            .append("id", userId)
            .append("username", userName);
        collection.insertOne(doc);
        System.out.println("User doesn't exist in database. Written.");
      } else {
        System.out.println("User exists in database");
      }
    }
  }
}
