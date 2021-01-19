package jp.co.sony.csl.dcoes.apis.tools.log.util;

import io.vertx.core.json.JsonObject;

/**
 * Holds received APIS log.
 * @author OES Project
 * 受信した APIS のログを保持する.
 * @author OES Project
 */
public class ApisVertxLog {

	public final String programId;
	public final String unitId;
	public final String threadName;
	public final String dateTime;
	public final String level;
	public final String loggerName;
	public final String message;

	/**
	 * Creates instance.
	 * @param programId Program ID
	 * @param unitId Unit ID
	 * @param threadName Thread name
	 * @param dateTime Datetime string
	 * @param level Log level
	 * @param loggerName Logger name
	 * @param message Log message
	 * インスタンスを作成する.
	 * @param programId プログラム ID
	 * @param unitId ユニット ID
	 * @param threadName スレッド名
	 * @param dateTime 日付文字列
	 * @param level ログレベル
	 * @param loggerName ロガー名
	 * @param message ログメッセージ
	 */
	public ApisVertxLog(String programId, String unitId, String threadName, String dateTime, String level, String loggerName, String message) {
		this.programId = programId;
		this.unitId = unitId;
		this.threadName = threadName;
		this.dateTime = dateTime;
		this.level = level;
		this.loggerName = loggerName;
		this.message = message;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override public String toString() {
		return new JsonObject().put("programId", programId).put("unitId", unitId).put("threadName", threadName).put("dateTime", dateTime).put("level", level).put("loggerName", loggerName).put("message", message).encodePrettily();
	}

}
