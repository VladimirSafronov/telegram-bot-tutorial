package org.example.util;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

public class RedisIntegrator {

  private RedisClient redisClient;
  private RedisCommands<String, String> syncCommands;
  private StatefulRedisConnection<String, String> redisConnection;

  public RedisIntegrator() {
    this.redisClient = RedisClient.create("redis://localhost:6379/0");
    this.redisConnection = redisClient.connect();
    this.syncCommands = redisConnection.sync();
  }

  public RedisCommands<String, String> getSyncCommands() {
    return syncCommands;
  }

  public void closeConnections() {
    redisConnection.close();
    redisClient.shutdown();
  }
}
