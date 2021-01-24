package scw.value.factory;

import java.util.Iterator;


public interface PropertyFactory extends ValueFactory<String>, Iterable<String>{
	Iterator<String> iterator();

	boolean containsKey(String key);
}
