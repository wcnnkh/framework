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
	 * 获取数据
	 * <p>
	 * 默认情况下和{@link Getter#get(Value)}相同
	 */
	@Override
	default Parameter get(Value source) {
		Getter getter = getGetters().first();
		if (getter == null) {
			throw new UnsupportedException("getter");
		}

		Object value = getter.get(source);
		return new Parameter(getter.getName(), value, getter.getTypeDescriptor());
	}

	/**
	 * 可以存在多种get方案， 默认选择第一个
	 * 
	 * @return
	 */
	Elements<? extends Getter> getGetters();

	/**
	 * 默认情况下和{@link Getter#getName()}相同
	 */
	@Override
	default String getName() {
		Getter getter = getGetters().first();
		if (getter == null) {
			throw new UnsupportedException("getter");
		}
		return getter.getName();
	}

	/**
	 * 可能存在多种set方案,选择其中一种方式插入
	 * 
	 * @return
	 */
	Elements<? extends Setter> getSetters();

	/**
	 * 默认情况下和{@link Getter#getTypeDescriptor()}相同
	 */
	@Override
	default TypeDescriptor getTypeDescriptor() {
		Getter getter = getGetters().first();
		if (getter == null) {
			throw new UnsupportedException("getter");
		}
		return getter.getTypeDescriptor();
	}

	/**
	 * 
	 * 是否可以调用{@link #get(Value)}
	 * 
	 * @return
	 */
	default boolean isSupportGetter() {
		return !getGetters().isEmpty();
	}

	/**
	 * 是否可以调用{@link #set(Value, Parameter)}
	 * 
	 * @return
	 */
	default boolean isSupportSetter() {
		return !getSetters().isEmpty();
	}

	@Override
	Field rename(String name);

	/**
	 * 插入数据
	 * 
	 * @param target
	 * @param parameter
	 * @return
	 */
	@Nullable
	default void set(Value target, Parameter parameter) {
		for (Setter setter : getSetters()) {
			if (setter.getName().equals(parameter.getName())
					&& setter.getTypeDescriptor().isAssignableTo(parameter.getTypeDescriptor())) {
				// 开始插入
				if (hasParent()) {
					// 如果存在父级
					Value instance = target;
					for (Field parent : parents().reverse()) {
						// 获取到父级字段的值
						Object parentValue = parent.get(instance);
						Setter parentSetter = parent.getSetters()
								.filter((e) -> e.getTypeDescriptor().isAssignableTo(parent.getTypeDescriptor()))
								.first();
						parentSetter.set(instance, parentValue);
						instance = Value.of(parentValue, parent.getTypeDescriptor());
					}
				}
				setter.set(target, parameter.getSource());
				return;
			}
		}
	}

	/**
	 * 测试此来源类型是否可以写入
	 */
	@Override
	default boolean test(ParameterDescriptor source) {
		if (source == null) {
			return false;
		}

		String name = getName();
		if (name.equals(source.getName()) || getSetters().anyMatch((e) -> e.getName().equals(name))) {
			return getSetters().anyMatch((e) -> e.test(source));
		}
		return false;
	}
}
