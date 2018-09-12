/**
 * 
 */
package com.itpro.util.monitor;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.itpro.util.Queue;

/**
 * @author ducgv
 *
 */
public class CmdQueryThreadStatus {
	public static final String FIELD_THREAD_RUNNING_STATUS = "ThreadStatus";
	public static final String FIELD_DB_CONNECTION_STATUS = "DbConnection";
	public static final String STATUS_OK = "OK";
	public static final String STATUS_FAIL = "FAIL";
	public Queue queueResp;
	public String threadName;
	public ProcessingThreadWithStatus threadObject;
	public JsonObject threadStatusInfo;
	private JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
	public void doResponse(){
		queueResp.enqueue(this);
	}
	public JsonObjectBuilder getJsonObjectBuilder() {
		return jsonObjectBuilder;
	}
	public void apply(){
		threadStatusInfo = jsonObjectBuilder.build();
	}
}
