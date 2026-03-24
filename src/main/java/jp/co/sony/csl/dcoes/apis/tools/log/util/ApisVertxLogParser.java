package jp.co.sony.csl.dcoes.apis.tools.log.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApisVertxLogParser {
    private static final Logger log = LoggerFactory.getLogger(ApisVertxLogParser.class);

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
