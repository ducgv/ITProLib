/**
 * 
 */
package com.itpro.util;

import javax.json.JsonObject;

import com.itpro.util.Queue;

/**
 * @author ducgv
 *
 */
public class CmdQueryThreadStatus {
	public Queue queueResp;
	public String threadName;
	public JsonObject threadStatusInfo;
	public void doResponse(){
		queueResp.enqueue(this);
	}
}
