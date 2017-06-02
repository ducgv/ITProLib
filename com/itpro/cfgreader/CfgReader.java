/**
 * 
 */
package com.itpro.cfgreader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * @author Giap Van Duc
 *
 */
public class CfgReader {

	public Hashtable<String, Hashtable<String, String>> mapGroups;
	public Vector<String> lstGroups;
	private String currentGroup;
	Hashtable<String, Vector<String>> mapComments;
	private boolean isChanged = false;

	public CfgReader() {
		// TODO Auto-generated constructor stub
		mapGroups = new Hashtable<String, Hashtable<String,String>>();
		lstGroups = new Vector<String>();	
		mapComments = new Hashtable<String, Vector<String>>();
	}

	public boolean load(String fileName){
		File file=new File(fileName);
		if(!file.exists())
			return false;
		long len = file.length(); 
		if(len==0)
			return false;

		String csLine;
		String csGroup="";
		String csKey,csValue;
		byte[] buff = new byte[(int) len];
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
			fis.read(buff);
			fis.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}

		String content = new String(buff);
		content.replaceAll("\r\n", "\n");
		String[] arrLine = content.split("\n");
		Vector<String> lstNewComments = new Vector<String>();
		for(int i=0;i<arrLine.length;i++)
		{
			int index1,index2;		
			csLine=arrLine[i].trim();
			int commentIndex = csLine.indexOf("#");
			if(commentIndex==0){
				String csComment = csLine;
				csLine = "";
				lstNewComments.add(csComment);
			}
			else if(commentIndex>0){
				String csComment = csLine.substring(commentIndex,csLine.length());
				csLine=csLine.substring(0,commentIndex);				
				lstNewComments.add(csComment);
			}
			if(!csLine.equals("")){		

				index1=csLine.indexOf("[");		
				//Co phai la Group khong
				if(index1>=0)
				{
					index2=csLine.indexOf("]");
					if(index2>index1)
					{
						csGroup=csLine.substring(index1+1,index2);
						csGroup = csGroup.trim();
						mapComments.put(csGroup, lstNewComments);
						lstNewComments = new Vector<String>();
						Hashtable<String, String> newGroup = new Hashtable<String, String>();
						mapGroups.put(csGroup, newGroup);
						lstGroups.add(csGroup);
					}
				}
				//Khong phai la Group
				else
				{
					int nEqual = csLine.indexOf("=");
					if(nEqual > 0 ){    
						csKey = csLine.substring(0,nEqual);
						csValue = csLine.substring(nEqual+1,csLine.length());
						csKey = csKey.trim();
						csValue = csValue.trim();			
						setGroup(csGroup);
						setKeyValue(csKey,csValue);
					}
				}
			}
		}		
		return true;
	}
	
	public boolean load(String fileName, String commentCharacter){
		File file=new File(fileName);
		if(!file.exists())
			return false;
		long len = file.length(); 
		if(len==0)
			return false;

		String csLine;
		String csGroup="";
		String csKey,csValue;
		byte[] buff = new byte[(int) len];
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
			fis.read(buff);
			fis.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}

		String content = new String(buff);
		content.replaceAll("\r\n", "\n");
		String[] arrLine = content.split("\n");
		Vector<String> lstNewComments = new Vector<String>();
		for(int i=0;i<arrLine.length;i++)
		{
			int index1,index2;		
			csLine=arrLine[i].trim();
			int commentIndex = csLine.indexOf(commentCharacter);
			if(commentIndex==0){
				String csComment = csLine;
				csLine = "";
				lstNewComments.add(csComment);
			}
			else if(commentIndex>0){
				String csComment = csLine.substring(commentIndex,csLine.length());
				csLine=csLine.substring(0,commentIndex);				
				lstNewComments.add(csComment);
			}
			if(!csLine.equals("")){		

				index1=csLine.indexOf("[");		
				//Co phai la Group khong
				if(index1>=0)
				{
					index2=csLine.indexOf("]");
					if(index2>index1)
					{
						csGroup=csLine.substring(index1+1,index2);
						csGroup = csGroup.trim();
						mapComments.put(csGroup, lstNewComments);
						lstNewComments = new Vector<String>();
						Hashtable<String, String> newGroup = new Hashtable<String, String>();
						mapGroups.put(csGroup, newGroup);
						lstGroups.add(csGroup);
					}
				}
				//Khong phai la Group
				else
				{
					int nEqual = csLine.indexOf("=");
					if(nEqual > 0 ){    
						csKey = csLine.substring(0,nEqual);
						csValue = csLine.substring(nEqual+1,csLine.length());
						csKey = csKey.trim();
						csValue = csValue.trim();			
						setGroup(csGroup);
						setKeyValue(csKey,csValue);
					}
				}
			}
		}		
		return true;
	}
	
	public String getCurrentGroupString(){
		return currentGroup;
	}
	public void setKeyValue(String csKey, String csValue) {
		// TODO Auto-generated method stub
		Hashtable<String, String> mapKeyValue = mapGroups.get(currentGroup);
		if(mapKeyValue!=null){
			if(mapKeyValue.get(csKey)!=null)
				isChanged = true;
			mapKeyValue.put(csKey, csValue);
		}
		else{
			mapKeyValue = new Hashtable<String, String>();
			mapKeyValue.put(csKey, csValue);
			mapGroups.put(currentGroup, mapKeyValue);
			lstGroups.add(currentGroup);
		}		
	}
	public void setGroup(String csGroup) {
		// TODO Auto-generated method stub
		currentGroup = csGroup;

	}
	private Hashtable<String, String> getCurrentGroup(){		
		return mapGroups.get(currentGroup);
	}

	public String getString(String csKey,String defaultValue){
		Hashtable<String, String> group = getCurrentGroup();
		if(group==null){
			group = new Hashtable<String, String>();
			group.put(csKey, defaultValue);
			mapGroups.put(currentGroup, group);
			lstGroups.add(currentGroup);
			isChanged  = true;
			return defaultValue;
		}
		else{
			String result = group.get(csKey);
			if(result==null){
				group.put(csKey, defaultValue);
				isChanged = true;
				return defaultValue;				
			}
			else{
				return group.get(csKey);
			}
		}
	}

	public int getInt(String csKey,int defaultValue){
		Hashtable<String, String> group = getCurrentGroup();
		if(group==null){
			group = new Hashtable<String, String>();
			group.put(csKey, ((Integer)defaultValue).toString());
			mapGroups.put(currentGroup, group);
			lstGroups.add(currentGroup);
			isChanged  = true;
			return ((Integer)defaultValue).intValue();
		}
		else{
			String csTemp = group.get(csKey);
			try{
				Integer result = Integer.parseInt(csTemp);
				return result.intValue();
			}catch (NumberFormatException e){
				group.put(csKey, ((Integer)defaultValue).toString());
				isChanged  = true;
				return ((Integer)defaultValue).intValue();
			}						
		}
	}

	public byte getByte(String csKey,byte defaultValue){
		Hashtable<String, String> group = getCurrentGroup();
		if(group==null){
			group = new Hashtable<String, String>();
			group.put(csKey, ((Byte)defaultValue).toString());
			mapGroups.put(currentGroup, group);
			lstGroups.add(currentGroup);
			isChanged  = true;
			return ((Byte)defaultValue).byteValue();
		}
		else{
			String csTemp = group.get(csKey);
			try{
				Byte result = Byte.parseByte(csTemp);
				return result.byteValue();
			}catch (NumberFormatException e){
				group.put(csKey, ((Byte)defaultValue).toString());
				isChanged  = true;
				return ((Byte)defaultValue).byteValue();
			}						
		}
	}
	public boolean isChanged(){
		return isChanged;
	}
	public boolean save(String fileName){
		File file = new File(fileName);
		if(!file.exists()){
			try {
				if(!file.createNewFile()){
					return false;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			String output = "";
			for(int i=0;i<lstGroups.size();i++){			
				//ghi comment len dau
				Vector<String> lstComments = mapComments.get(lstGroups.get(i));	
				if(lstComments!=null){
					for(int j=0;j<lstComments.size();j++){
						output += lstComments.get(j)+"\n";
					}
				}
				//ghi group
				output +="["+lstGroups.get(i)+"]\n";
				//ghi list key=value
				Hashtable<String, String> mapKeyValues = mapGroups.get(lstGroups.get(i));
				if(mapKeyValues!=null&&mapKeyValues.size()>0){
					Enumeration<String> keys = mapKeyValues.keys();
					while (keys.hasMoreElements()) {
						String key = (String) keys.nextElement();
						String value = mapKeyValues.get(key);
						output+="  "+key+" = "+value+"\n";
					}
				}
				output+="\n";
			}
			
			fos.write(output.getBytes());
			fos.close();
				
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
}
