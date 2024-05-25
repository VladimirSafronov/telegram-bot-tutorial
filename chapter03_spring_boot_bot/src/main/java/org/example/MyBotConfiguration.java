package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.util.TelegramOkHttpClientFactory;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Configuration
public class MyBotConfiguration {

  @Bean(value = "okClient")
  public OkHttpClient OkHttpClient(
      @Value("${hostname}") String hostname,
      @Value("${port}") int port,
      @Value("${username}") String username,
      @Value("${password}") String password
  ) {
    return new TelegramOkHttpClientFactory.ProxyOkHttpClientCreator(
        () -> new Proxy(Type.HTTP, new InetSocketAddress(hostname, port)),
        () -> (route, response) -> {
          String credential = Credentials.basic(username, password);
          return response
              .request()
              .newBuilder()
              .header("Proxy-Authorization", credential)
              .build();
        }
    ).get();
  }

  @Bean(value = "telegramClient")
  public TelegramClient telegramClient(
      @Qualifier("okClient") OkHttpClient okClient,
      @Value("${botToken}") String botToken
  ) {
    return new OkHttpTelegramClient(okClient, botToken);
  }

  @Bean(value = "telegramBotsApplication")
  public TelegramBotsLongPollingApplication telegramBotsApplication(
      @Value("${okClient}") OkHttpClient okClient
  ) {
    return new TelegramBotsLongPollingApplication(ObjectMapper::new, () -> okClient);
  }
}
