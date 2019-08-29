package scw.json;

@SuppressWarnings("rawtypes")
public interface JSONArray extends JSONArrayReadOnly, Iterable {
	void add(Object element);

	void remove(int index);
}
