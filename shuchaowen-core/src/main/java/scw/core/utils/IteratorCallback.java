package scw.core.utils;

import java.io.Serializable;

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
}
