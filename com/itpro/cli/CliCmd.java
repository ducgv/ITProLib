/**
 * 
 */
package com.itpro.cli;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import com.itpro.util.Queue;

/**
 * @author Giap Van Duc
 *
 */
public class CliCmd {
	public static final int RESP_FORMAT_TYPE_HASH = 1;
	public static final int RESP_FORMAT_TYPE_JSON = 2;
	private Queue queueResp;
	public String cmd = "";
	private int respFormatType = RESP_FORMAT_TYPE_HASH;
	public int getRespFormatType() {
		return respFormatType;
	}

	public void setRespFormatType(int respFormatType) {
		this.respFormatType = respFormatType;
	}

	public Hashtable<String, String> reqParams = new Hashtable<String, String>();
	private JsonObject respParams = null;
	private JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
	
	public CliCmd(Queue queueResp) {
		// TODO Auto-generated constructor stub
		this.queueResp = queueResp;
	}
	
	public JsonObjectBuilder getJsonObjectBuilder(){
		return jsonObjectBuilder;
	}
	
	public void apply(){
		respParams = jsonObjectBuilder.build();
	}
	
	public void doResponse(){
		queueResp.enqueue(this);
	}
	
	public String toString(){
		String sResult=cmd+":";
		Enumeration<String> keys = reqParams.keys();		
		String sParam="";		
		while (keys.hasMoreElements()){
			String key = keys.nextElement();
			String value = reqParams.get(key);
			sParam+=", "+key+"="+value;				
		}		
		if(!sParam.equals(""))
			sParam=sParam.substring(1);
		sResult+=sParam;
		return sResult;
	}
	
	public String getReqParam(String param){
		return reqParams.get(param);
	}
	
	public void setRespParams(JsonObject respParams){
		this.respParams = respParams;
	}
	public String getRespString(){
		if(respFormatType == RESP_FORMAT_TYPE_JSON){
			return Json.createObjectBuilder().add(cmd+"Resp",respParams).build().toString();
		} else {
			return cmd+"Resp:"+getStringJSONToHash(respParams);
		}
		/*
		String sResult=cmd+"Resp:";
		Enumeration<String> keys = resultHash.keys();
		String sParam="";
		while (keys.hasMoreElements()){
			String key = keys.nextElement();
			String value = resultHash.get(key);
			sParam+=","+key+"="+value;				
		}		
		if(!sParam.equals(""))
			sParam=sParam.substring(1);
		sResult+=sParam;
		return sResult;
		*/
	}
	
	public String getStringJSONToHash(JsonObject jsonObject){
		String result = "";
		for(String key:jsonObject.keySet()){
			result=result+=","+getPropertyString("", jsonObject, key);
		}
		if(result.indexOf(",")==0)
			result=result.substring(1);
		return result;
	}
	
	/*
		,	=> #2C
		'= 	=> #3D
		: 	=> #3A
	*/
	public String getPropertyString(String parentKey, JsonObject currentNode, String key){
		JsonValue value = currentNode.get(key);
		String valueType= value.getValueType().toString();
		String hashKeyDisplay=(parentKey==null||parentKey.equals("")?key:parentKey+"-"+key);
		String result="";
		switch (valueType){
		case "OBJECT":
			JsonObject valueObject = value.asJsonObject();
			for(String subValueKey:valueObject.keySet()){
				result=result+","+getPropertyString(hashKeyDisplay, valueObject, subValueKey);
			}
			if(result.indexOf(",")==0)
				result=result.replaceFirst("[,]", "");
			return result;
		case "ARRAY":
			JsonArray valueArray = value.asJsonArray();
			int arrSize=valueArray.size();
			if(arrSize>0){
			for(int i=0;i<arrSize;i++){
				JsonObject element = valueArray.get(i).asJsonObject();
				for(String elementKey:element.keySet()){
					result=result+","+getPropertyString(hashKeyDisplay+"["+i+"]", element, elementKey);
				}
			}
			}
			else{
				return hashKeyDisplay+"=[]";
			}
			if(result.indexOf(",")==0)
				result=result.replaceFirst("[,]", "");
			
			return result;
		case "STRING":
			return hashKeyDisplay+"="+currentNode.getString(key)
				.replaceAll("[,]", "#2C")
				.replaceAll("=", "#3D")
				.replaceAll("[:]", "#3A");
		case "NUMBER":
			return hashKeyDisplay+"="+currentNode.get(key).toString();
		case "FALSE":
			return hashKeyDisplay+"="+currentNode.get(key).toString();
		case "NULL":
			return hashKeyDisplay+"="+currentNode.get(key).toString();
		case "TRUE":
			return hashKeyDisplay+"="+currentNode.get(key).toString();
		default:
			return hashKeyDisplay+"="+currentNode.get(key).toString();
		}
	}
}
