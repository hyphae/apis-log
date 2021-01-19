package jp.co.sony.csl.dcoes.apis.tools.log.util;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.datagram.DatagramPacket;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;

import java.util.logging.Level;

import jp.co.sony.csl.dcoes.apis.common.util.vertx.JsonObjectUtil;
import jp.co.sony.csl.dcoes.apis.common.util.vertx.VertxConfig;

/**
 * Stores received APIS log in MongoDB.
 * @author OES Project
 * 受信した APIS のログを MongoDB に保存する.
 * @author OES Project
 */
public class MongoDbWriter {
	private static final Logger log = LoggerFactory.getLogger(MongoDbWriter.class);

	private static final Boolean DEFAULT_ENABLED = Boolean.FALSE;
	private static final JsonObjectUtil.DefaultString DEFAULT_LEVEL = JsonObjectUtil.defaultString("INFO");
	private static final JsonObjectUtil.DefaultString DEFAULT_HOST = JsonObjectUtil.defaultString("localhost");
	private static final Boolean DEFAULT_SSL = Boolean.FALSE;
	private static final Boolean DEFAULT_SSL_TRUST_ALL = Boolean.FALSE;
	private static final Integer DEFAULT_PORT = Integer.valueOf(27017);

	private static Level level_ = null;
	private static MongoClient client_ = null;
	private static String collection_ = null;

	/**
	 * Carries out initialization.
	 * Gets settings from CONFIG and initializes.
	 * - CONFIG.mongoDbWriter.enabled : Enable flag [{@link Boolean}]
	 * - CONFIG.mongoDbWriter.level : Stored log level [{@link String}]
	 * - CONFIG.mongoDbWriter.host : Host name [{@link String}]
	 * - CONFIG.mongoDbWriter.port : Host [{@link Integer}]
	 * - CONFIG.mongoDbWriter.ssl : SSL flag [{@link Boolean}]
	 * - CONFIG.mongoDbWriter.sslTrustAll : OK flag for any SSL [{@link Boolean}]
	 * - CONFIG.mongoDbWriter.database : Database name [{@link String}]
	 * - CONFIG.mongoDbWriter.collection : Collection name [{@link String}]
	 * @param vertx vertx object
	 * @param completionHandler The completion handler
	 * 初期化.
	 * CONFIG から設定を取得し初期化する.
	 * - CONFIG.mongoDbWriter.enabled : 有効フラグ [{@link Boolean}]
	 * - CONFIG.mongoDbWriter.level : 保存ログレベル [{@link String}]
	 * - CONFIG.mongoDbWriter.host : ホスト名 [{@link String}]
	 * - CONFIG.mongoDbWriter.port : ポート [{@link Integer}]
	 * - CONFIG.mongoDbWriter.ssl : SSL フラグ [{@link Boolean}]
	 * - CONFIG.mongoDbWriter.sslTrustAll : SSL なんでも OK フラグ [{@link Boolean}]
	 * - CONFIG.mongoDbWriter.database : データベース名 [{@link String}]
	 * - CONFIG.mongoDbWriter.collection : コレクション名 [{@link String}]
	 * @param vertx vertx オブジェクト
	 * @param completionHandler the completion handler
	 */
	public static void initialize(Vertx vertx, Handler<AsyncResult<Void>> completionHandler) {
		Boolean enabled_ = VertxConfig.config.getBoolean(DEFAULT_ENABLED, "mongoDbWriter", "enabled");
		if (enabled_) {
			try {
				level_ = Level.parse(VertxConfig.config.getString(DEFAULT_LEVEL, "mongoDbWriter", "level"));
			} catch (Exception e) {
				log.error(e);
				completionHandler.handle(Future.failedFuture(e));
				return;
			}
			String host = VertxConfig.config.getString(DEFAULT_HOST, "mongoDbWriter", "host");
			Integer port = VertxConfig.config.getInteger(DEFAULT_PORT, "mongoDbWriter", "port");
			Boolean ssl = VertxConfig.config.getBoolean(DEFAULT_SSL, "mongoDbWriter", "ssl");
			Boolean sslTrustAll = VertxConfig.config.getBoolean(DEFAULT_SSL_TRUST_ALL, "mongoDbWriter", "sslTrustAll");
			String database = VertxConfig.config.getString("mongoDbWriter", "database");
			JsonObject config = new JsonObject().put("host", host).put("port", port).put("ssl", ssl).put("db_name", database);
			if (ssl) config.put("trustAll", sslTrustAll);
			client_ = MongoClient.createShared(vertx, config);
			collection_ = VertxConfig.config.getString("mongoDbWriter", "collection");
			if (log.isInfoEnabled()) log.info("level : " + level_);
			if (log.isInfoEnabled()) log.info("host : " + host);
			if (log.isInfoEnabled()) log.info("port : " + port);
			if (log.isInfoEnabled()) log.info("ssl : " + ssl);
			if (ssl) if (log.isInfoEnabled()) log.info("sslTrustAll : " + sslTrustAll);
			if (log.isInfoEnabled()) log.info("database : " + database);
			if (log.isInfoEnabled()) log.info("collection : " + collection_);
		}
		completionHandler.handle(Future.succeededFuture());
	}

	/**
	 * Stores received packet in MongoDB.
	 * This version just delivers the packets for processing and returns immediately without waiting for processing to complete.
	 * @param value {@link DatagramPacket} object
	 * 受信したパケットを MongoDB に保存する.
	 * 処理を投げっぱなしですぐに戻るバージョン.
	 * @param value {@link DatagramPacket} オブジェクト
	 */
	public static void write(DatagramPacket value) {
		write(value, r -> {});
	}
	/**
	 * Stores received packet in MongoDB.
	 * This version waits until processing is completed.
	 * @param value Received {@link DatagramPacket} object
	 * @param completionHandler The completion handler
	 * 受信したパケットを MongoDB に保存する.
	 * 処理が終わるまでまつバージョン.
	 * @param value 受信した {@link DatagramPacket} オブジェクト
	 * @param completionHandler the completion handler
	 */
	public static void write(DatagramPacket value, Handler<AsyncResult<Void>> completionHandler) {
		if (client_ != null) {
			JsonObject json;
			Level level;
			String loggername;
			try {
				json = buildJson_(value);
				level = level_(json);
				loggername = json.getString("loggername");
			} catch (Exception e) {
				log.error(e);
				completionHandler.handle(Future.failedFuture(e));
				return;
			}
			if (level != null && level_.intValue() <= level.intValue()) {
				// Does not save because if you try to save a MongoDB error, an infinite error loop can occur (lame)    
				// MongoDB のエラーを保存しようとすると無限にエラーが起きてしまう可能性があるので保存しない ( ダサい )
				if (loggername == null || !loggername.startsWith("org.mongodb.")) {
					write_(json, completionHandler);
				}
			} else {
				completionHandler.handle(Future.succeededFuture());
			}
		} else {
			completionHandler.handle(Future.succeededFuture());
		}
	}
	/**
	 * Creates a {@link JsonObject} object for storing information in MongoDB from the received packet.
	 * @param value Receive {@link DatagramPacket} object
	 * @return {@link JsonObject} object to be stored in MongoDB
	 * 受信したパケットから MongoDB に保存するための {@link JsonObject} オブジェクトを生成する.
	 * @param value 受信した {@link DatagramPacket} オブジェクト
	 * @return MongoDB に保存する {@link JsonObject} オブジェクト
	 */
	private static JsonObject buildJson_(DatagramPacket value) {
		String address = String.valueOf(value.sender());
		JsonObject result = new JsonObject().put("address", address);
		result.put("communityId", VertxConfig.communityId());
		result.put("clusterId", VertxConfig.clusterId());
		String content = String.valueOf(value.data()).trim();
		// Parses the content of packet and extracts log information
		// パケットの中身をパースしログ情報を取り出す
		ApisVertxLog log = ApisVertxLogParser.parse(content);
		if (log != null) {
			result.put("programId", log.programId);
			if (log.unitId != null) {
				result.put("unitId", log.unitId);
				result.put("unitname", log.unitId);
			} else {
				result.put("unitname", log.programId);
			}
			result.put("thread", log.threadName);
			result.put("datetime", new JsonObject().put("$date", log.dateTime));
			result.put("loglevel", log.level);
			result.put("loggername", log.loggerName);
			result.put("message", log.message);
		} else {
			result.put("message", content);
		}
		return result;
	}
	/**
	 * Parses the log level.
	 * @param value {@link JsonObject} object parsed from the received packet
	 * @return Log level
	 * ログレベルをパースする.
	 * @param value 受信したパケットをパースした {@link JsonObject} オブジェクト
	 * @return ログレベル
	 */
	private static Level level_(JsonObject value) throws IllegalArgumentException {
		String loglevel = value.getString("loglevel");
		if (loglevel != null) {
			return Level.parse(loglevel);
		}
		return Level.SEVERE;
	}
	/**
	 * Stores {@link JsonObject} in MongoDB.
	 * @param value {@link JsonObject} data
	 * @param completionHandler The completion handler
	 * {@link JsonObject} を MongoDB に保存する.
	 * @param value {@link JsonObject} データ
	 * @param completionHandler the completion handler
	 */
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
