package jp.co.sony.csl.dcoes.apis.tools.log.util;

import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class ApisVertxLogParserTest {

	public ApisVertxLogParserTest() {
		super();
	}

	private void parseAndTest_(TestContext context, String logString, JsonObject expected) {
		System.out.println("logString : " + logString);
		ApisVertxLog result = ApisVertxLogParser.parse(logString);
		System.out.println("result : " + result);
		context.assertNotNull(result, "result");
		context.assertEquals(expected.getValue("programId"), result.programId, "programId");
		context.assertEquals(expected.getValue("unitId"), result.unitId, "unitId");
		context.assertEquals(expected.getValue("threadName"), result.threadName, "threadName");
		context.assertEquals(expected.getValue("dateTime"), result.dateTime, "dateTime");
		context.assertEquals(expected.getValue("level"), result.level, "level");
		context.assertEquals(expected.getValue("loggerName"), result.loggerName, "loggerName");
		context.assertEquals(expected.getValue("message"), result.message, "message");
	}

	@Test public void v3(TestContext context) {
		parseAndTest_(context, "[[[apis-web]]] [main] 2016-06-28T16:58:13.959+09:00 INFO [io.vertx.core.Starter]  Starting clustering...", new JsonObject()
				.put("programId", "apis-web")
				.put("unitId", (String) null)
				.put("threadName", "main")
				.put("dateTime", "2016-06-28T16:58:13.959+09:00")
				.put("level", "INFO")
				.put("loggerName", "io.vertx.core.Starter")
				.put("message", "Starting clustering...")
		);
		parseAndTest_(context, "[[[]]] [vert.x-worker-thread-0] 2016-06-28T16:58:22.118+09:00 INFO [com.hazelcast.core.LifecycleService]  [192.168.0.234]:5701 [dev-test] [3.5.2] Address[192.168.0.234]:5701 is STARTED", new JsonObject()
				.put("programId", "")
				.put("unitId", (String) null)
				.put("threadName", "vert.x-worker-thread-0")
				.put("dateTime", "2016-06-28T16:58:22.118+09:00")
				.put("level", "INFO")
				.put("loggerName", "com.hazelcast.core.LifecycleService")
				.put("message", "[192.168.0.234]:5701 [dev-test] [3.5.2] Address[192.168.0.234]:5701 is STARTED")
		);
		parseAndTest_(context, "[[[:]]] [vert.x-worker-thread-0] 2016-06-28T16:58:22.118+09:00 INFO [com.hazelcast.core.LifecycleService]  [192.168.0.234]:5701 [dev-test] [3.5.2] Address[192.168.0.234]:5701 is STARTED", new JsonObject()
				.put("programId", "")
				.put("unitId", "")
				.put("threadName", "vert.x-worker-thread-0")
				.put("dateTime", "2016-06-28T16:58:22.118+09:00")
				.put("level", "INFO")
				.put("loggerName", "com.hazelcast.core.LifecycleService")
				.put("message", "[192.168.0.234]:5701 [dev-test] [3.5.2] Address[192.168.0.234]:5701 is STARTED")
		);
		parseAndTest_(context, "[[[apis-main:]]] [vert.x-eventloop-thread-4] 2016-06-28T16:58:22.847+09:00 FINEST [jp.co.sony.csl.dcoes.apis.main.app.Helo]  started : 44d12ed8-14b6-4693-8042-69c23fa0c91e", new JsonObject()
				.put("programId", "apis-main")
				.put("unitId", "")
				.put("threadName", "vert.x-eventloop-thread-4")
				.put("dateTime", "2016-06-28T16:58:22.847+09:00")
				.put("level", "FINEST")
				.put("loggerName", "jp.co.sony.csl.dcoes.apis.main.app.Helo")
				.put("message", "started : 44d12ed8-14b6-4693-8042-69c23fa0c91e")
		);
		parseAndTest_(context, "[[[apis-main:E0[0]3]]] [vert.x-eventloop-thread-1] 2016-05-11T18:14:47.113+09:00 WARNING [jp.co.sony.csl.dcoes.apis.main.app.gridmaster.ErrorCollection]  (HARDWARE:GLOBAL:WARN:E003) illegal member list : [\"E002\",\"E003\",\"E004\"], policy.memberUnitIds : [E001, E002, E003, E004] [jp.co.sony.csl.dcoes.apis.main.evaluation.safety.GlobalSafetyEvaluation.checkMemberUnitIds_(GlobalSafetyEvaluation.java:76)]", new JsonObject()
				.put("programId", "apis-main")
				.put("unitId", "E0[0]3")
				.put("threadName", "vert.x-eventloop-thread-1")
				.put("dateTime", "2016-05-11T18:14:47.113+09:00")
				.put("level", "WARNING")
				.put("loggerName", "jp.co.sony.csl.dcoes.apis.main.app.gridmaster.ErrorCollection")
				.put("message", "(HARDWARE:GLOBAL:WARN:E003) illegal member list : [\"E002\",\"E003\",\"E004\"], policy.memberUnitIds : [E001, E002, E003, E004] [jp.co.sony.csl.dcoes.apis.main.evaluation.safety.GlobalSafetyEvaluation.checkMemberUnitIds_(GlobalSafetyEvaluation.java:76)]")
		);
		parseAndTest_(context, "[[[apis-main:E0:04]]] [vert.x-eventloop-thread-1] 2016-06-28T12:15:17.382+09:00 SEVERE [jp.co.sony.csl.dcoes.apis.tools.ccc.ScenarioAcquisition]  Failed to decode:Unexpected character ('}' (code 125)): was expecting double-quote to start field name\n at [Source: {\n        \"#\" : \"place this file at the path defined by 'scenarioFile' in config file\",\n\n        \"refreshingPeriodMsec\" : 30000,\n...", new JsonObject()
				.put("programId", "apis-main")
				.put("unitId", "E0:04")
				.put("threadName", "vert.x-eventloop-thread-1")
				.put("dateTime", "2016-06-28T12:15:17.382+09:00")
				.put("level", "SEVERE")
				.put("loggerName", "jp.co.sony.csl.dcoes.apis.tools.ccc.ScenarioAcquisition")
				.put("message", "Failed to decode:Unexpected character ('}' (code 125)): was expecting double-quote to start field name\n at [Source: {\n        \"#\" : \"place this file at the path defined by 'scenarioFile' in config file\",\n\n        \"refreshingPeriodMsec\" : 30000,\n...")
		);
	}

	@Test public void v2(TestContext context) {
		parseAndTest_(context, "[[null]] [main] 2016-06-28T16:58:13.959+09:00 INFO [io.vertx.core.Starter]  Starting clustering...", new JsonObject()
				.put("programId", (String) null)
				.put("unitId", "null")
				.put("threadName", "main")
				.put("dateTime", "2016-06-28T16:58:13.959+09:00")
				.put("level", "INFO")
				.put("loggerName", "io.vertx.core.Starter")
				.put("message", "Starting clustering...")
		);
		parseAndTest_(context, "[[]] [vert.x-worker-thread-0] 2016-06-28T16:58:22.118+09:00 INFO [com.hazelcast.core.LifecycleService]  [192.168.0.234]:5701 [dev-test] [3.5.2] Address[192.168.0.234]:5701 is STARTED", new JsonObject()
				.put("programId", (String) null)
				.put("unitId", "")
				.put("threadName", "vert.x-worker-thread-0")
				.put("dateTime", "2016-06-28T16:58:22.118+09:00")
				.put("level", "INFO")
				.put("loggerName", "com.hazelcast.core.LifecycleService")
				.put("message", "[192.168.0.234]:5701 [dev-test] [3.5.2] Address[192.168.0.234]:5701 is STARTED")
		);
		parseAndTest_(context, "[[apis-ccc]] [vert.x-eventloop-thread-4] 2016-06-28T16:58:22.847+09:00 FINEST [jp.co.sony.csl.dcoes.apis.main.app.Helo]  started : 44d12ed8-14b6-4693-8042-69c23fa0c91e", new JsonObject()
				.put("programId", (String) null)
				.put("unitId", "apis-ccc")
				.put("threadName", "vert.x-eventloop-thread-4")
				.put("dateTime", "2016-06-28T16:58:22.847+09:00")
				.put("level", "FINEST")
				.put("loggerName", "jp.co.sony.csl.dcoes.apis.main.app.Helo")
				.put("message", "started : 44d12ed8-14b6-4693-8042-69c23fa0c91e")
		);
		parseAndTest_(context, "[[E0[0]3]] [vert.x-eventloop-thread-1] 2016-05-11T18:14:47.113+09:00 WARNING [jp.co.sony.csl.dcoes.apis.main.app.gridmaster.ErrorCollection]  (HARDWARE:GLOBAL:WARN:E003) illegal member list : [\"E002\",\"E003\",\"E004\"], policy.memberUnitIds : [E001, E002, E003, E004] [jp.co.sony.csl.dcoes.apis.main.evaluation.safety.GlobalSafetyEvaluation.checkMemberUnitIds_(GlobalSafetyEvaluation.java:76)]", new JsonObject()
				.put("programId", (String) null)
				.put("unitId", "E0[0]3")
				.put("threadName", "vert.x-eventloop-thread-1")
				.put("dateTime", "2016-05-11T18:14:47.113+09:00")
				.put("level", "WARNING")
				.put("loggerName", "jp.co.sony.csl.dcoes.apis.main.app.gridmaster.ErrorCollection")
				.put("message", "(HARDWARE:GLOBAL:WARN:E003) illegal member list : [\"E002\",\"E003\",\"E004\"], policy.memberUnitIds : [E001, E002, E003, E004] [jp.co.sony.csl.dcoes.apis.main.evaluation.safety.GlobalSafetyEvaluation.checkMemberUnitIds_(GlobalSafetyEvaluation.java:76)]")
		);
		parseAndTest_(context, "[[E0:04]] [vert.x-eventloop-thread-1] 2016-06-28T12:15:17.382+09:00 SEVERE [jp.co.sony.csl.dcoes.apis.tools.ccc.ScenarioAcquisition]  Failed to decode:Unexpected character ('}' (code 125)): was expecting double-quote to start field name\n at [Source: {\n        \"#\" : \"place this file at the path defined by 'scenarioFile' in config file\",\n\n        \"refreshingPeriodMsec\" : 30000,\n...", new JsonObject()
				.put("programId", (String) null)
				.put("unitId", "E0:04")
				.put("threadName", "vert.x-eventloop-thread-1")
				.put("dateTime", "2016-06-28T12:15:17.382+09:00")
				.put("level", "SEVERE")
				.put("loggerName", "jp.co.sony.csl.dcoes.apis.tools.ccc.ScenarioAcquisition")
				.put("message", "Failed to decode:Unexpected character ('}' (code 125)): was expecting double-quote to start field name\n at [Source: {\n        \"#\" : \"place this file at the path defined by 'scenarioFile' in config file\",\n\n        \"refreshingPeriodMsec\" : 30000,\n...")
		);
	}

	@Test public void v1(TestContext context) {
		parseAndTest_(context, "[main] 2016-06-28T17:22:01.903+09:00 INFO [io.vertx.core.Starter]  Starting clustering...", new JsonObject()
				.put("programId", (String) null)
				.put("unitId", (String) null)
				.put("threadName", "main")
				.put("dateTime", "2016-06-28T17:22:01.903+09:00")
				.put("level", "INFO")
				.put("loggerName", "io.vertx.core.Starter")
				.put("message", "Starting clustering...")
		);
		parseAndTest_(context, " [vert.x-worker-thread-0] 2016-06-28T17:22:10.008+09:00 INFO [com.hazelcast.core.LifecycleService]  [192.168.0.234]:5701 [dev-test] [3.5.2] Address[192.168.0.234]:5701 is STARTED", new JsonObject()
				.put("programId", (String) null)
				.put("unitId", (String) null)
				.put("threadName", "vert.x-worker-thread-0")
				.put("dateTime", "2016-06-28T17:22:10.008+09:00")
				.put("level", "INFO")
				.put("loggerName", "com.hazelcast.core.LifecycleService")
				.put("message", "[192.168.0.234]:5701 [dev-test] [3.5.2] Address[192.168.0.234]:5701 is STARTED")
		);
		parseAndTest_(context, "[vert.x-eventloop-thread-1] 2016-05-11T18:14:47.113+09:00 WARNING [jp.co.sony.csl.dcoes.apis.main.app.gridmaster.ErrorCollection]  (HARDWARE:GLOBAL:WARN:E003) illegal member list : [\"E002\",\"E003\",\"E004\"], policy.memberUnitIds : [E001, E002, E003, E004] [jp.co.sony.csl.dcoes.apis.main.evaluation.safety.GlobalSafetyEvaluation.checkMemberUnitIds_(GlobalSafetyEvaluation.java:76)]", new JsonObject()
				.put("programId", (String) null)
				.put("unitId", (String) null)
				.put("threadName", "vert.x-eventloop-thread-1")
				.put("dateTime", "2016-05-11T18:14:47.113+09:00")
				.put("level", "WARNING")
				.put("loggerName", "jp.co.sony.csl.dcoes.apis.main.app.gridmaster.ErrorCollection")
				.put("message", "(HARDWARE:GLOBAL:WARN:E003) illegal member list : [\"E002\",\"E003\",\"E004\"], policy.memberUnitIds : [E001, E002, E003, E004] [jp.co.sony.csl.dcoes.apis.main.evaluation.safety.GlobalSafetyEvaluation.checkMemberUnitIds_(GlobalSafetyEvaluation.java:76)]")
		);
		parseAndTest_(context, " [vert.x-eventloop-thread-1] 2016-06-28T12:15:17.382+09:00 SEVERE [jp.co.sony.csl.dcoes.apis.tools.ccc.ScenarioAcquisition]  Failed to decode:Unexpected character ('}' (code 125)): was expecting double-quote to start field name\n at [Source: {\n        \"#\" : \"place this file at the path defined by 'scenarioFile' in config file\",\n\n        \"refreshingPeriodMsec\" : 30000,\n...", new JsonObject()
				.put("programId", (String) null)
				.put("unitId", (String) null)
				.put("threadName", "vert.x-eventloop-thread-1")
				.put("dateTime", "2016-06-28T12:15:17.382+09:00")
				.put("level", "SEVERE")
				.put("loggerName", "jp.co.sony.csl.dcoes.apis.tools.ccc.ScenarioAcquisition")
				.put("message", "Failed to decode:Unexpected character ('}' (code 125)): was expecting double-quote to start field name\n at [Source: {\n        \"#\" : \"place this file at the path defined by 'scenarioFile' in config file\",\n\n        \"refreshingPeriodMsec\" : 30000,\n...")
		);
	}

}
