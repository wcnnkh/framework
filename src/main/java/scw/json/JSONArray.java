package scw.json;

public interface JSONArray extends JSONArrayReadOnly, Iterable<Object> {
	JSONArray add(int index, Object element);

	JSONArray add(Object element);

	Object remove(int index);
}
