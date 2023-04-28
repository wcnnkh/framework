package io.basc.framework.mapper;

import io.basc.framework.core.ResolvableType;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Elements;
import io.basc.framework.value.Value;

/**
 * 一个字段的定义
 * 
 * @author wcnnkh
 *
 */
public interface Field {

	default boolean isSupportGetter() {
		return !getGetters().isEmpty();
	}

	/**
	 * 在此字段上可使用的Getter方案
	 * 
	 * @return
	 */
	Elements<? extends Getter> getGetters();

	default boolean isSupportSetter() {
		return !getSetters().isEmpty();
	}

	/**
	 * 在此字段上可使用的Setter方案
	 * 
	 * @return
	 */
	Elements<? extends Setter> getSetters();

	/**
	 * 获取参数
	 * 
	 * @param source
	 * @param expectedType 期望类型
	 * @return
	 */
	default Parameter get(Value source, @Nullable ResolvableType expectedType) {
		if (expectedType != null) {
			for (Getter getter : getGetters()) {
				if (getter.getTypeDescriptor().getResolvableType().isAssignableFrom(expectedType)) {
					Object value = getter.get(source);
					return new Parameter(getter.getName(), value, getter.getTypeDescriptor());
				}
			}
		}

		Getter getter = getGetters().first();
		if (getter == null) {
			throw new UnsupportedOperationException();
		}

		Object value = getter.get(source);
		return new Parameter(getter.getName(), value, getter.getTypeDescriptor());
	}

	/**
	 * 设置参数
	 * 
	 * @param target
	 * @param value
	 * @return 返回使用的{@see Setter#set(Value, Object)}
	 */
	default Setter set(Value target, Parameter parameter) {
		for (Setter setter : getSetters()) {
			if (setter.test(parameter)) {
				setter.set(target, parameter.getSource());
				return setter;
			}
		}

		for (Setter setter : getSetters()) {
			// 类型匹配
			if (setter.getTypeDescriptor().getResolvableType()
					.isAssignableFrom(parameter.getTypeDescriptor().getResolvableType())) {
				setter.set(target, parameter.getSource());
				return setter;
			}
		}

		Setter setter = getSetters().first();
		if (setter == null) {
			throw new UnsupportedOperationException();
		}

		Object value = parameter.getAsObject(setter.getTypeDescriptor());
		setter.set(target, value);
		return setter;
	}
}
