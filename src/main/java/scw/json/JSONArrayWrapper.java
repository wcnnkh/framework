package scw.json;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class JSONArrayWrapper extends JSONArrayReadOnlyWrapper implements
		JSONArray {
	private static final long serialVersionUID = 1L;
	private JSONArray jsonArray;

	public JSONArrayWrapper(JSONArray jsonArray) {
		super(jsonArray);
		this.jsonArray = jsonArray;
	}

	public int size() {
		return jsonArray.size();
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
		return jsonArray.addAll(c);
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
