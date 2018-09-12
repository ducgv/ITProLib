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
	public static final String THREAD_STATUS = "ThreadStatus";
	public static final String DB_CONNECTION_STATUS = "DbConnection";
	public static final String STATUS_OK = "OK";
	public static final String STATUS_FAILED = "FAIL";
	public Queue queueResp;
	public String threadName;
	public ProcessingThreadWithStatus threadObject;
	public JsonObject threadStatusInfo;
	private JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
	public void doResponse(){
		queueResp.enqueue(this);
	}
	public JsonObjectBuilder getThreadStatusInfoBuilder() {
		return jsonObjectBuilder;
	}
	public void apply(){
		threadStatusInfo = jsonObjectBuilder.build();
	}
}
