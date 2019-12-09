package scw.orm.support;

import java.util.Map;

import scw.orm.AbstractColumnFactory;
import scw.orm.Column;

public class NoCacheColumnFactory extends AbstractColumnFactory {

	public Map<String, Column> getColumnMap(Class<?> clazz) {
		return analysisClass(clazz);
	}
}
