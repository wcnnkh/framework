package scw.sql.orm;

import java.lang.annotation.Annotation;

import scw.core.instance.InstanceUtils;
import scw.core.reflect.AnnotationFactory;
import scw.core.reflect.SimpleAnnotationFactory;
import scw.core.utils.FieldSetterListenUtils;
import scw.sql.orm.annotation.Table;

public abstract class AbstractTableInfo implements TableInfo {
	private final String name;
	private final Class<?> source;
	private final boolean table;
	private final AnnotationFactory annotationFactory;

	public AbstractTableInfo(Class<?> clz) {
		this.source = clz;
		this.name = ORMUtils.getAnnotationTableName(source);
		this.annotationFactory = new SimpleAnnotationFactory(clz);
		Table table = annotationFactory.getAnnotation(Table.class);
		this.table = table != null;
	}

	public final String getDefaultName() {
		return name;
	}

	public final boolean isTable() {
		return table;
	}

	public final Class<?> getSource() {
		return source;
	}

	@SuppressWarnings("unchecked")
	public final <T> T newInstance() {
		if (table) {
			try {
				return (T) FieldSetterListenUtils.newFieldSetterListenInstance(source);
			} catch (Throwable e) {
				return InstanceUtils.newInstance(source);
			}
		} else {
			return InstanceUtils.newInstance(source);
		}
	}

	public final <T extends Annotation> T getAnnotation(Class<T> type) {
		return annotationFactory.getAnnotation(type);
	}

	public final String getName(Object bean) {
		if (bean instanceof TableName) {
			return ((TableName) bean).tableName();
		}

		return name;
	}
}
