package io.basc.framework.mapper;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.util.Elements;
import io.basc.framework.util.ParentDiscover;
import io.basc.framework.value.Value;

/**
 * 一个字段的定义
 * 
 * @author wcnnkh
 *
 */
public interface Field extends Getter, ParentDiscover<Field> {

	/**
	 * 默认情况下和{@link Getter#getName()}相同
	 */
	@Override
	default String getName() {
		Getter getter = getters().first();
		if (getter == null) {
			throw new UnsupportedException("getter");
		}
		return getter.getName();
	}

	/**
	 * 默认情况下和{@link Getter#getTypeDescriptor()}相同
	 */
	@Override
	default TypeDescriptor getTypeDescriptor() {
		Getter getter = getters().first();
		if (getter == null) {
			throw new UnsupportedException("getter");
		}
		return getter.getTypeDescriptor();
	}

	/**
	 * 默认情况下和{@link Getter#get(Value)}相同
	 */
	@Override
	default Parameter get(Value source) {
		Getter getter = getters().first();
		if (getter == null) {
			throw new UnsupportedException("getter");
		}

		Object value = getter.get(source);
		return new Parameter(getter.getName(), value, getter.getTypeDescriptor());
	}

	@Override
	Field rename(String name);

	/**
	 * 默认是判断是否存在Getter
	 * 
	 * @see #getter()
	 * @return
	 */
	default boolean isSupportGetter() {
		return !getters().isEmpty();
	}

	/**
	 * 默认选择第一个
	 * 
	 * @return
	 */
	Elements<? extends Getter> getters();

	/**
	 * 默认是判断是否存在Setter
	 * 
	 * @see #setters()
	 * @return
	 */
	default boolean isSupportSetter() {
		return !setters().isEmpty();
	}

	/**
	 * 根据名称选择Setter执行
	 * 
	 * @param target
	 * @param parameter
	 * @return 如果为空说明找不到对应的Setter
	 */
	@Nullable
	default Setter set(Value target, Parameter parameter) {
		for (Setter setter : setters()) {
			if (setter.getName().equals(parameter.getName())
					&& setter.getTypeDescriptor().isAssignableTo(parameter.getTypeDescriptor())) {
				// 开始插入
				if (hasParent()) {
					// 如果存在父级
					Value instance = target;
					for (Field parent : parents().reverse()) {
						// 获取到父级字段的值
						Object parentValue = parent.get(instance);
						Setter parentSetter = parent.setters()
								.filter((e) -> e.getTypeDescriptor().isAssignableTo(parent.getTypeDescriptor()))
								.first();
						parentSetter.set(instance, parentValue);
						instance = Value.of(parentValue, parent.getTypeDescriptor());
					}
				}
				setter.set(target, parameter.getSource());
				return setter;
			}
		}
		return null;
	}

	/**
	 * 可能存在多种set方案,选择其中一种方式插入
	 * 
	 * @return
	 */
	Elements<? extends Setter> setters();

	/**
	 * 测试此来源类型是否可以写入
	 */
	@Override
	default boolean test(ParameterDescriptor source) {
		if (source == null) {
			return false;
		}

		String name = getName();
		if (name.equals(source.getName()) || setters().anyMatch((e) -> e.getName().equals(name))) {
			return setters().anyMatch((e) -> e.test(source));
		}
		return false;
	}
}
