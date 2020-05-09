package scw.orm.sql;

import scw.aop.support.FieldSetterListenUtils;
import scw.core.instance.InstanceUtils;
import scw.core.instance.annotation.Configuration;
import scw.orm.ORMInstanceFactory;
import scw.sql.orm.annotation.Table;

@Configuration(order = Integer.MIN_VALUE)
public final class TableInstanceFactory implements ORMInstanceFactory {

	@SuppressWarnings("unchecked")
	public <T> T newInstance(Class<? extends T> clazz) {
		Table table = clazz.getAnnotation(Table.class);
		if (table != null) {
			return (T) FieldSetterListenUtils.getFieldSetterListenProxy(clazz).create();
		}

		return InstanceUtils.INSTANCE_FACTORY.getInstance(clazz);
	}
}
