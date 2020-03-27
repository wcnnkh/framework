package scw.orm.sql;

import scw.core.instance.SimpleCacheNoArgsInstanceFactory;
import scw.core.utils.FieldSetterListenUtils;
import scw.orm.sql.annotation.Table;

public final class TableInstanceFactory extends SimpleCacheNoArgsInstanceFactory {
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getInstance(Class<? extends T> clazz) {
		Table table = clazz.getAnnotation(Table.class);
		if (table != null) {
			return (T) FieldSetterListenUtils.newFieldSetterListenInstance(clazz);
		}
		return super.getInstance(clazz);
	}
}
