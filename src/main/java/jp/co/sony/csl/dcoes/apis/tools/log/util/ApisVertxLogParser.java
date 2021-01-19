package jp.co.sony.csl.dcoes.apis.tools.log.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * Parses received APIS log.
 * Corresponds to {@link jp.co.sony.csl.dcoes.apis.common.util.vertx.ApisLoggerFormatter} and {@link jp.co.sony.csl.dcoes.apis.main.util.ApisMainLoggerFormatter}.
 * It might be correct to place alongside {@link jp.co.sony.csl.d-coes.apis.common.util.vertx.ApisLoggerFormatter}.
 * @author OES Project
 * 受信した APIS のログをパースする.
 * {@link jp.co.sony.csl.dcoes.apis.common.util.vertx.ApisLoggerFormatter} や {@link jp.co.sony.csl.dcoes.apis.main.util.ApisMainLoggerFormatter} に対応.
 * {@link jp.co.sony.csl.dcoes.apis.common.util.vertx.ApisLoggerFormatter} と並べて置いておくのが正しいのかもしれない.
 * @author OES Project
 */
public class ApisVertxLogParser {
	private static final Logger log = LoggerFactory.getLogger(ApisVertxLogParser.class);

	/**
	 * Parses log and gets {@link ApisVertxLog}.
	 * @param value One line of log
	 * @return Parsing results {@link ApisVertxLog}.
	 *         {@code null} if parsing fails.
	 * ログをパースし {@link ApisVertxLog} を取得する.
	 * @param value ログ１行の文字列
	 * @return パース結果 {@link ApisVertxLog}.
	 *         パースが失敗したら {@code null}.
	 */
	public static ApisVertxLog parse(String value) {
		ApisVertxLog result = null;
		result = parse_v3_main_(value);
		if (result != null) return result;
		result = parse_v3_tools_(value);
		if (result != null) return result;
		result = parse_v2_(value);
		if (result != null) return result;
		result = parse_v1_(value);
		if (result != null) return result;
		log.error("pattern matching failed, value : " + value);
		return null;
	}

	// New type (apis-main) : [[[apis-main:E001]]] [vert.x-eventloop-thread-0] 2020-...
	// 新型 (apis-main) : [[[apis-main:E001]]] [vert.x-eventloop-thread-0] 2020-...
	private static Pattern PATTERN_V3_MAIN_ = Pattern.compile("^\\[\\[\\[(.*?):(.*)\\]\\]\\] \\[(.*?)\\] (.*?) (.*?) \\[(.*?)\\]  ([\\s\\S]*)$");
	private static ApisVertxLog parse_v3_main_(String value) {
		Matcher matcher = PATTERN_V3_MAIN_.matcher(value);
		if (matcher.find()) {
			String programId = matcher.group(1);
			String unitId = matcher.group(2);
			String threadName = matcher.group(3);
			String dateTime = matcher.group(4);
			String level = matcher.group(5);
			String loggerName = matcher.group(6);
			String message = matcher.group(7);
			return new ApisVertxLog(programId, unitId, threadName, dateTime, level, loggerName, message);
		}
		return null;
	}

	// New type (apis-tools) : [[[apis-ccc]]] [vert.x-eventloop-thread-0] 2020-...
	// 新型 (apis-tools) : [[[apis-ccc]]] [vert.x-eventloop-thread-0] 2020-...
	private static Pattern PATTERN_V3_TOOLS_ = Pattern.compile("^\\[\\[\\[(.*)\\]\\]\\] \\[(.*?)\\] (.*?) (.*?) \\[(.*?)\\]  ([\\s\\S]*)$");
	private static ApisVertxLog parse_v3_tools_(String value) {
		Matcher matcher = PATTERN_V3_TOOLS_.matcher(value);
		if (matcher.find()) {
			String programId = matcher.group(1);
			String threadName = matcher.group(2);
			String dateTime = matcher.group(3);
			String level = matcher.group(4);
			String loggerName = matcher.group(5);
			String message = matcher.group(6);
			return new ApisVertxLog(programId, null, threadName, dateTime, level, loggerName, message);
		}
		return null;
	}

	// Old type (apis-main) : [[E001]] [vert.x-eventloop-thread-0] 2020-...
	// Old type (apis-tools) : [[apis-ccc]] [vert.x-eventloop-thread-0] 2020-...
	// 旧型 (apis-main) : [[E001]] [vert.x-eventloop-thread-0] 2020-...
	// 旧型 (apis-tools) : [[apis-ccc]] [vert.x-eventloop-thread-0] 2020-...
	private static Pattern PATTERN_V2_ = Pattern.compile("^\\[\\[(.*)\\]\\] \\[(.*?)\\] (.*?) (.*?) \\[(.*?)\\]  ([\\s\\S]*)$");
	private static ApisVertxLog parse_v2_(String value) {
		Matcher matcher = PATTERN_V2_.matcher(value);
		if (matcher.find()) {
			String unitId = matcher.group(1);
			String threadName = matcher.group(2);
			String dateTime = matcher.group(3);
			String level = matcher.group(4);
			String loggerName = matcher.group(5);
			String message = matcher.group(6);
			return new ApisVertxLog(null, unitId, threadName, dateTime, level, loggerName, message);
		}
		return null;
	}

	// Vert.x default : [vert.x-eventloop-thread-0] 2020-...
	// Vert.x デフォルト : [vert.x-eventloop-thread-0] 2020-...
	private static Pattern PATTERN_V1_ = Pattern.compile("^.*\\[(.*?)\\] (.*?) (.*?) \\[(.*?)\\]  ([\\s\\S]*)$");
	private static ApisVertxLog parse_v1_(String value) {
		Matcher matcher = PATTERN_V1_.matcher(value);
		if (matcher.find()) {
			String threadName = matcher.group(1);
			String dateTime = matcher.group(2);
			String level = matcher.group(3);
			String loggerName = matcher.group(4);
			String message = matcher.group(5);
			return new ApisVertxLog(null, null, threadName, dateTime, level, loggerName, message);
		}
		return null;
	}

}
