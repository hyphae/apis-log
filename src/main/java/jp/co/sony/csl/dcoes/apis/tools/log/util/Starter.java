package jp.co.sony.csl.dcoes.apis.tools.log.util;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import jp.co.sony.csl.dcoes.apis.common.util.vertx.AbstractStarter;
import jp.co.sony.csl.dcoes.apis.tools.log.LogReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Starter extends AbstractStarter {
    private static final Logger log = LoggerFactory.getLogger(Starter.class);

    @Override
    protected void doStart(Handler<AsyncResult<Void>> completionHandler) {
        vertx.deployVerticle(new LogReceiver()).setHandler(resLogReceiver -> {
            if (resLogReceiver.succeeded()) {
                completionHandler.handle(Future.succeededFuture());
            } else {
                completionHandler.handle(Future.failedFuture(resLogReceiver.cause()));
            }
        });
    }
}
