package scw.beans.property;

import java.lang.reflect.Field;

import scw.beans.BeanFactory;
import scw.beans.annotation.Value;
import scw.core.PropertyFactory;
import scw.core.reflect.ReflectUtils;
import scw.core.utils.StringUtils;

public final class ValueWired {
	private final Object obj;
	private final Value value;
	private final Field field;
	private final Object id;

	/**
	 * @param obj
	 *            如果是静态字段就是空
	 * @param field
	 * @param value
	 */
	public ValueWired(Object id, Object obj, Field field, Value value) {
		this.value = value;
		this.field = field;
		this.obj = obj;
		this.id = id;
		ReflectUtils.setAccessibleField(field);
	}

	public Value getValueAnnotation() {
		return value;
	}

	public Field getField() {
		return field;
	}

	public Object getId() {
		return id;
	}

	public boolean isCanRefresh() {
		return value.period() >= 0;
	}

	public void wired(BeanFactory beanFactory, PropertyFactory propertyFactory) throws Throwable {
		String valueFormat = StringUtils.isEmpty(getValueAnnotation().formatName())
				? getValueAnnotation().format().getName() : getValueAnnotation().formatName();
		Object v = ((ValueFormat) beanFactory.getInstance(valueFormat)).format(beanFactory, propertyFactory, field,
				value.value());
		if (v != null) {
			field.set(obj, v);
		}
	}
}
