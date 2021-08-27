package io.basc.framework.core;

import io.basc.framework.util.Accept;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 迭代器回调
 * @author shuchaowen
 *
 * @param <T>
 */
public interface IteratorCallback<V> {
	/**
	 * 迭代器回调
	 * @param value 迭代数据
	 * @return 是否继续迭代
	 */
	boolean iteratorCallback(V value);
	
	public static class Row<T> implements Serializable{
		private static final long serialVersionUID = 1L;
		private int index;
		private T value;
		
		public Row(int index, T value){
			this.index = index;
			this.value = value;
		}
		
		public int getIndex() {
			return index;
		}

		public T getValue() {
			return value;
		}
	}
	
	public static final class First<V> implements IteratorCallback<V>{
		private final Accept<V> accept;
		private V value;
		
		public First(Accept<V> accept){
			this.accept = accept;
		}
		
		public boolean iteratorCallback(V value) {
			if(accept.accept(value)){
				this.value = value;
				return false;
			}
			return true;
		}

		public V getValue() {
			return value;
		}
	}
	
	public static final class All<V> implements IteratorCallback<V>{
		private final Accept<V> accept;
		private List<V> values;
		
		public All(Accept<V> accept){
			this.accept = accept;
		}
		
		public boolean iteratorCallback(V value) {
			if(accept.accept(value)){
				if(values == null){
					values = new ArrayList<V>();
				}
				values.add(value);
			}
			return true;
		}

		public List<V> getValues() {
			if(values == null){
				return Collections.emptyList();
			}
			
			return Collections.unmodifiableList(values);
		}
	}
}
