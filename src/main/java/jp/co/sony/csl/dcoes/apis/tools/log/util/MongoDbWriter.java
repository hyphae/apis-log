package jp.co.sony.csl.dcoes.apis.tools.log.util;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.co.sony.csl.dcoes.apis.common.util.vertx.VertxConfig;

public class MongoDbWriter {
    private static final Logger log = LoggerFactory.getLogger(MongoDbWriter.class);
    private static MongoClient client_;
    private static String collection_;

    public static void initialize(Vertx vertx, Handler<AsyncResult<Void>> completionHandler) {
        boolean enabled = VertxConfig.config.getBoolean(false, "mongoDbWriter", "enabled");
        if (!enabled) {
            completionHandler.handle(Future.succeededFuture());
            return;
        }
        String host = VertxConfig.config.getString("localhost", "mongoDbWriter", "host");
        int port = VertxConfig.config.getInteger(27017, "mongoDbWriter", "port");
        boolean ssl = VertxConfig.config.getBoolean(false, "mongoDbWriter", "ssl");
        String dbName = VertxConfig.config.getString("apis", "mongoDb", "database");
        collection_ = VertxConfig.config.getString("log", "mongoDb", "collection");

        JsonObject config = new JsonObject()
            .put("host", host)
            .put("port", port)
            .put("db_name", dbName)
            .put("useSSL", ssl);
        
        client_ = MongoClient.createShared(vertx, config);
        completionHandler.handle(Future.succeededFuture());
    }

    public static void write(JsonObject packet) {
        if (client_ != null) {
            write_(packet, res -> {});
        }
    }

    public static void write_(JsonObject value, Handler<AsyncResult<Void>> completionHandler) {
        if (client_ == null) {
            completionHandler.handle(Future.succeededFuture());
            return;
        }
        client_.insert(collection_, value).onComplete(res -> {
            if (res.succeeded()) {
                completionHandler.handle(Future.succeededFuture());
            } else {
                log.error("Communication failed with MongoDB ; " + res.cause());
                completionHandler.handle(Future.failedFuture(res.cause()));
            }
        });
    }
}
