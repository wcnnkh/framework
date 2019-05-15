package scw.json.support.fastjson;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public final class FastJSONArray implements scw.json.JSONArray {
	private static final long serialVersionUID = 1L;
	private com.alibaba.fastjson.JSONArray jsonArray;
	
	//用于序列化
	protected FastJSONArray(){};

	public FastJSONArray(com.alibaba.fastjson.JSONArray jsonArray) {
		this.jsonArray = jsonArray;
	}

	public String getString(int index) {
		return jsonArray.getString(index);
	}

	public byte getByteValue(int index) {
		return jsonArray.getByteValue(index);
	}

	public Byte getByte(int index) {
		return jsonArray.getByte(index);
	}

	public short getShortValue(int index) {
		return jsonArray.getShortValue(index);
	}

	public Short getShort(int index) {
		return jsonArray.getShort(index);
	}

	public int getIntValue(int index) {
		return jsonArray.getIntValue(index);
	}

	public Integer getInteger(int index) {
		return jsonArray.getInteger(index);
	}

	public long getLongValue(int index) {
		return jsonArray.getLongValue(index);
	}

	public Long getLong(int index) {
		return jsonArray.getLong(index);
	}

	public float getFloatValue(int index) {
		return jsonArray.getFloatValue(index);
	}

	public Float getFloat(int index) {
		return jsonArray.getFloat(index);
	}

	public double getDoubleValue(int index) {
		return jsonArray.getDoubleValue(index);
	}

	public Double getDouble(int index) {
		return jsonArray.getDouble(index);
	}

	public scw.json.JSONObject getJSONObject(int index) {
		com.alibaba.fastjson.JSONObject jsonObject = jsonArray.getJSONObject(index);
		return jsonObject == null ? null : new FastJSONObject(jsonObject);
	}

	public scw.json.JSONArray getJSONArray(int index) {
		com.alibaba.fastjson.JSONArray jarr = jsonArray.getJSONArray(index);
		return jarr == null ? null : new FastJSONArray(jarr);
	}

	public <T> T getObject(int index, Class<T> type) {
		return jsonArray.getObject(index, type);
	}

	public int size() {
		return jsonArray.size();
	}

	public String toJSONString() {
		return jsonArray.toJSONString();
	}

	@Override
	public String toString() {
		return toJSONString();
	}

	public boolean isEmpty() {
		return jsonArray.isEmpty();
	}

	public boolean contains(Object o) {
		return jsonArray.contains(o);
	}

	public Iterator<Object> iterator() {
		return jsonArray.iterator();
	}

	public Object[] toArray() {
		return jsonArray.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return jsonArray.toArray(a);
	}

	public boolean add(Object e) {
		return jsonArray.add(e);
	}

	public boolean remove(Object o) {
		return jsonArray.remove(o);
	}

	public boolean containsAll(Collection<?> c) {
		return jsonArray.containsAll(c);
	}

	public boolean addAll(Collection<? extends Object> c) {
		return jsonArray.addAll(c);
	}

	public boolean addAll(int index, Collection<? extends Object> c) {
		return jsonArray.addAll(index, c);
	}

	public boolean removeAll(Collection<?> c) {
		return jsonArray.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return jsonArray.retainAll(c);
	}

	public void clear() {
		jsonArray.clear();
	}

	public Object get(int index) {
		return jsonArray.get(index);
	}

	public Object set(int index, Object element) {
		return jsonArray.set(index, element);
	}

	public void add(int index, Object element) {
		jsonArray.add(index, element);
	}

	public Object remove(int index) {
		return jsonArray.remove(index);
	}

	public int indexOf(Object o) {
		return jsonArray.indexOf(o);
	}

	public int lastIndexOf(Object o) {
		return jsonArray.lastIndexOf(o);
	}

	public ListIterator<Object> listIterator() {
		return jsonArray.listIterator();
	}

	public ListIterator<Object> listIterator(int index) {
		return jsonArray.listIterator(index);
	}

	public List<Object> subList(int fromIndex, int toIndex) {
		return jsonArray.subList(fromIndex, toIndex);
	}
}
