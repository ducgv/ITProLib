/**
 * 
 */
package com.itpro.util.list;

/**
 * @author ducgv
 *
 */
public class Entry<K, V> {
	public K key;
	public V value;
	public long timestamp;
	public Entry<K, V> prev = null;
	public Entry<K, V> next = null;
	Entry(K key, V value){
		this.key = key;
		this.value = value;
		this.timestamp = System.currentTimeMillis();
		prev = null;
		next = null;
	}
	public String toString() {
		return key.toString()+":"+value.toString()+"("+timestamp+")";
	}
}
