package jp.co.sony.csl.dcoes.apis.tools.log.util;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;
import jp.co.sony.csl.dcoes.apis.common.util.vertx.VertxConfig;

public class MongoDbWriter {
    private static final Logger log = LoggerFactory.getLogger(MongoDbWriter.class);
    private static MongoClient client_;
    private static String collection_;

    public static void initialize(Handler<AsyncResult<Void>> completionHandler) {
        // Implementation from original file
    }

    public static void write(JsonObject packet) {
        // Implementation from original file
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
