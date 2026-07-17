package jp.co.sony.csl.dcoes.apis.tools.log.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.CoreConstants;

import io.vertx.core.json.JsonObject;
import jp.co.sony.csl.dcoes.apis.common.util.logback.ApisLogPrefixConverter;
import jp.co.sony.csl.dcoes.apis.common.util.logback.JulLevelConverter;
import jp.co.sony.csl.dcoes.apis.common.util.vertx.VertxConfig;


public class MulticastEncoderParserRoundTripTest {

	/** Exactly the pattern intended for the multicast appender's encoder in logback.xml. */
	private static final String PATTERN = "%apisPrefix[%thread] %d{yyyy-MM-dd'T'HH:mm:ss.SSSXXX} %jullevel [%logger]  %msg%n";

	private LoggerContext lc;
	private PatternLayoutEncoder encoder;

	@Before public void setUp() {
		lc = new LoggerContext();
		Map<String, String> rules = new HashMap<>();
		rules.put("apisPrefix", ApisLogPrefixConverter.class.getName());
		rules.put("jullevel", JulLevelConverter.class.getName());
		lc.putObject(CoreConstants.PATTERN_RULE_REGISTRY, rules);

		encoder = new PatternLayoutEncoder();
		encoder.setContext(lc);
		encoder.setPattern(PATTERN);
		encoder.start();
	}

	/** Encodes one event through the production pattern and parses the resulting wire line. */
	private ApisVertxLog roundTrip_(String loggerName, Level level, String message) {
		Logger logger = lc.getLogger(loggerName);
		ILoggingEvent event = new LoggingEvent("fqcn", logger, level, message, null, null);
		byte[] bytes = encoder.encode(event);
		// The datagram carries the encoder output; strip the single trailing line terminator
		// that %n adds (internal newlines in the message are preserved).
		String line = new String(bytes, StandardCharsets.UTF_8).replaceFirst("\\R$", "");
		return ApisVertxLogParser.parse(line);
	}

	@Test public void apisMainWithUnitIdParsesAsV3Main() {
		VertxConfig.config.setJsonObject(new JsonObject().put("programId", "apis-main").put("unitId", "E001"));
		ApisVertxLog log = roundTrip_("jp.co.sony.csl.dcoes.apis.main.app.Helo", Level.INFO, "started : abc-123");
		assertNotNull("parser must accept the emitted line", log);
		assertEquals("apis-main", log.programId);
		assertEquals("E001", log.unitId);
		assertEquals(Thread.currentThread().getName(), log.threadName);
		assertEquals("INFO", log.level);
		assertEquals("jp.co.sony.csl.dcoes.apis.main.app.Helo", log.loggerName);
		assertEquals("started : abc-123", log.message);
		assertTrue("ISO-8601 datetime, got: " + log.dateTime,
				log.dateTime.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}.*"));
	}

	@Test public void toolWithoutUnitIdParsesAsV3Tools() {
		// Tools (apis-ccc/web/log) have no unitId in CONFIG, so no colon: parser yields unitId == null.
		VertxConfig.config.setJsonObject(new JsonObject().put("programId", "apis-ccc"));
		ApisVertxLog log = roundTrip_("jp.co.sony.csl.dcoes.apis.tools.ccc.Some", Level.INFO, "hello");
		assertNotNull(log);
		assertEquals("apis-ccc", log.programId);
		assertNull("tools must not emit a trailing colon (would change unitname downstream)", log.unitId);
		assertEquals("hello", log.message);
	}

	@Test public void emptyIdsBeforeConfigParsed() {
		VertxConfig.config.setJsonObject(null);
		ApisVertxLog log = roundTrip_("io.vertx.core.Starter", Level.INFO, "Starting clustering...");
		assertNotNull(log);
		assertEquals("", log.programId);
		assertNull(log.unitId);
		assertEquals("Starting clustering...", log.message);
	}

	@Test public void levelsAreEmittedAsJulNamesAndParseCleanly() {
		VertxConfig.config.setJsonObject(new JsonObject().put("programId", "apis-main").put("unitId", "E002"));

		ApisVertxLog warn = roundTrip_("jp.co.sony.csl.dcoes.apis.main.app.X", Level.WARN, "careful");
		assertNotNull(warn);
		assertEquals("WARNING", warn.level);
		// The property that matters downstream: MongoDbWriter does java.util.logging.Level.parse(level).
		assertEquals("WARNING", java.util.logging.Level.parse(warn.level).getName());

		ApisVertxLog error = roundTrip_("jp.co.sony.csl.dcoes.apis.main.app.X", Level.ERROR, "boom");
		assertNotNull(error);
		assertEquals("SEVERE", error.level);
		assertEquals("SEVERE", java.util.logging.Level.parse(error.level).getName());
	}

	@Test public void multilineMessageIsPreserved() {
		VertxConfig.config.setJsonObject(new JsonObject().put("programId", "apis-main").put("unitId", "E003"));
		String message = "Failed to decode:Unexpected character\n at [Source: {\n  \"x\" : 1\n}]";
		ApisVertxLog log = roundTrip_("jp.co.sony.csl.dcoes.apis.tools.ccc.ScenarioAcquisition", Level.ERROR, message);
		assertNotNull(log);
		assertEquals("apis-main", log.programId);
		assertEquals("E003", log.unitId);
		assertEquals("SEVERE", log.level);
		assertEquals(message, log.message);
	}

}
