package scw.orm;

import java.util.Map;

public class NoCacheColumnFactory extends AbstractColumnFactory {

	public Map<String, Column> getColumnMap(Class<?> clazz) {
		return analysisClass(clazz);
	}
}
