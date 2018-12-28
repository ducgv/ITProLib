/**
 * 
 */
package com.itpro.util.list;

import java.util.Hashtable;
import com.itpro.util.Queue;

/**
 * @author ducgv
 *
 */
public class ListCheckingTimeout<K, V> {
	private int size = 0;
	private Queue queueElementsTimedOut = null;
	private Queue queueElementsCleared = null;
	private Entry<K, V> first = null;
	private Entry<K, V> last = null;
	
	public void setQueueElementsTimedOut(Queue queueElementsTimedOut) {
		this.queueElementsTimedOut = queueElementsTimedOut;
	}
	public void setQueueElementsCleared(Queue queueElementsCleared) {
		this.queueElementsCleared = queueElementsCleared;
	}

	private Hashtable<K, Entry<K, V>> hash = new Hashtable<K, Entry<K, V>>();
	
	public int size() {
		return size;
	}
	public boolean isEmpty() {
		return (size==0);
	}
	
	public V getFirst() {
		if(first!=null)
			return first.value;
		else return null;
	}
	
	public Entry<K, V> getFirstEntry() {
		return first;
	}
	
	public boolean contains(K key) {
		return hash.containsKey(key);
	}
	
	public V removeFirst() {
		if(size == 0)
			return null;
		Entry<K, V> entry = first;
		hash.remove(entry.key);
		size--;
		V r = first.value;
		if (first.next != null)
			first.next.prev = null;
		else
			last = null;
		first = first.next;
		return r;
	}
	
	public V remove(K key) {
		if(size == 0)
			return null;
		Entry<K, V> entry = hash.get(key);
		if(entry==null)
			return null;
		hash.remove(entry.key);
		size--;
		V r = entry.value;
		if (entry.next != null)
			entry.next.prev = entry.prev;
		else {
			last = entry.prev;
		}
		
		if (entry.prev != null)
			entry.prev.next = entry.next;
		else {
			first = entry.next;
		}
		return r;
	}
	
	public void put(K key, V value) {
		if(hash.containsKey(key))
			return;
		Entry<K, V> entry = new Entry<K, V>(key, value);
		hash.put(key, entry);
		if (size == 0) {
			first = entry;
			last = entry;
		}
		else{
			entry.prev = last;
			last.next = entry;
			last = entry;
		}
		size++;
	}
	
	public void put(K key, V value, long timestamp) {
		if(hash.containsKey(key))
			return;
		Entry<K, V> entry = new Entry<K, V>(key, value);
		entry.timestamp = timestamp;
		hash.put(key, entry);
		if (size == 0) {
			first = entry;
			last = entry;
		}
		else{
			entry.prev = last;
			last.next = entry;
			last = entry;
		}
		size++;
	}
	
	public V get(K key) {
		Entry<K, V> entry = hash.get(key);
		if(entry!=null)
			return entry.value;
		else
			return null;
	}
	
	public void clearElementsTimedOut(long intevalMiliseconds) {
		long currentTimestamp = System.currentTimeMillis();
		Entry<K, V> entry = getFirstEntry();
		while(entry!=null&&currentTimestamp-entry.timestamp>intevalMiliseconds) {
			removeFirst();
			if(queueElementsTimedOut!=null)
				queueElementsTimedOut.enqueue(entry.value);
			entry = getFirstEntry();
		}
	}

	public void clearAll() {
		Entry<K, V> entry = getFirstEntry();
		while(entry!=null) {
			removeFirst();
			if(queueElementsCleared!=null)
				queueElementsCleared.enqueue(entry.value);
			entry = getFirstEntry();
		}
	}
	
	public String toString() {
		String result = "";
		Entry<K, V> entry = first;
		while(entry!=null) {
			result=result+",{"+entry.toString()+"}";
			entry = entry.next;
		}
		if(size>0) {
			result=result.substring(1);
		}
		return result;
	}
}
