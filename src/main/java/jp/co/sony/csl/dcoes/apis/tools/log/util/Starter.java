package jp.co.sony.csl.dcoes.apis.tools.log.util;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import jp.co.sony.csl.dcoes.apis.common.util.vertx.AbstractStarter;
import jp.co.sony.csl.dcoes.apis.tools.log.LogReceiver;

/**
 * This is the main Verticle of apis-log.
 * It is specified by maven-shade-plugin's {@literal <Main-Verticle>} in pom.xml.
 * Starts the Verticle below.
 * - {@link LogReceiver} : Verticle that receives and processes APIS program logs, which are multicast by UDP
 * @author OES Project
 * apis-log の親玉 Verticle.
 * pom.xml の maven-shade-plugin の {@literal <Main-Verticle>} で指定してある.
 * 以下の Verticle を起動する.
 * - {@link LogReceiver} : UDP でマルチキャストされる APIS プログラムのログを受信し処理する Verticle
 * @author OES Project

 */
public class Starter extends AbstractStarter {

	/**
	 * Called from {@link AbstractStarter#start(Future)} during startup.
	 * 起動時に {@link AbstractStarter#start(Future)} から呼び出される.
	 */
	@Override protected void doStart(Handler<AsyncResult<Void>> completionHandler) {
		vertx.deployVerticle(new LogReceiver(), resLogReceiver -> {
			if (resLogReceiver.succeeded()) {
				completionHandler.handle(Future.succeededFuture());
			} else {
				completionHandler.handle(Future.failedFuture(resLogReceiver.cause()));
			}
		});
	}

}
