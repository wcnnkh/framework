package scw.json;

public interface JsonArray extends Json<Integer>, Iterable<JsonElement>{
	static final String PREFIX = "[";
	static final String SUFFIX = "]";
	
	JsonElement getValue(Integer index);

	boolean add(Object element);
	
	boolean remove(int index);
}