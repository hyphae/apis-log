package jp.co.sony.csl.dcoes.apis.tools.log.util;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoDbWriter {
    private static final Logger log = LoggerFactory.getLogger(MongoDbWriter.class);
    private static MongoClient client_;
    private static String collection_;

    public static void initialize(Handler<AsyncResult<Void>> completionHandler) {
        // Implementation here
    }

    public static void write(JsonObject packet) {
        // Implementation here
    }

    public static void write_(JsonObject value, Handler<AsyncResult<Void>> completionHandler) {
        client_.insert(collection_, value, res -> {
            if (res.succeeded()) {
                completionHandler.handle(Future.succeededFuture());
            } else {
                log.error("Communication failed with MongoDB ; " + res.cause());
                completionHandler.handle(Future.failedFuture(res.cause()));
            }
        });
    }
}
