package scw.beans.property;

import java.lang.reflect.Modifier;

import scw.beans.BeanFactory;
import scw.beans.annotation.Value;
import scw.core.PropertyFactory;
import scw.core.reflect.FieldDefinition;

public final class ValueWired {
	private final Object obj;
	private final Value value;
	private final FieldDefinition fieldDefinition;

	/**
	 * @param obj
	 *            如果是静态字段就是空
	 * @param field
	 * @param value
	 */
	public ValueWired(Object obj, FieldDefinition fieldDefinition, Value value) {
		this.value = value;
		this.fieldDefinition = fieldDefinition;
		this.obj = obj;
	}

	public Value getValueAnnotation() {
		return value;
	}

	public boolean isCanRefresh() {
		return !Modifier.isFinal(fieldDefinition.getField().getModifiers()) && value.period() >= 0;
	}

	public void wired(BeanFactory beanFactory, PropertyFactory propertyFactory) throws Exception {
		Object v = beanFactory.getInstance(value.format()).format(beanFactory, propertyFactory, fieldDefinition.getField(),
				value.value());
		if (v != null) {
			fieldDefinition.set(obj, v);
		}
	}
}
