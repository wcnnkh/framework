package io.basc.framework.mapper;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Accept;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.LinkedMultiValueMap;
import io.basc.framework.util.MultiValueMap;
import io.basc.framework.util.page.Pageables;

public interface Fields extends Pageables<Class<?>, Field> {

	default Fields find(String name) {
		AcceptFieldDescriptor acceptFieldDescriptor = new AcceptFieldDescriptor(name, null);
		return accept(new Accept<Field>() {

			@Override
			public boolean accept(Field e) {
				return (e.isSupportGetter() && acceptFieldDescriptor.accept(e.getGetter()))
						|| (e.isSupportSetter() && acceptFieldDescriptor.accept(e.getSetter()));
			}
		});
	}

	@Nullable
	default Field find(String name, @Nullable Type type) {
		return accept(name, type).first();
	}

	@Nullable
	default Field findGetter(String name, @Nullable Type type) {
		return acceptGetter(name, type).first();
	}

	@Nullable
	default Field findSetter(String name, @Nullable Type type) {
		return acceptSetter(name, type).first();
	}

	default Fields acceptGetter(String name, @Nullable Type type) {
		return acceptGetter(new AcceptFieldDescriptor(name, type));
	}

	default Fields acceptSetter(String name, @Nullable Type type) {
		return acceptSetter(new AcceptFieldDescriptor(name, type));
	}

	default Fields accept(String name, @Nullable Type type) {
		AcceptFieldDescriptor acceptFieldDescriptor = new AcceptFieldDescriptor(name, type);
		return accept(new Accept<Field>() {

			@Override
			public boolean accept(Field e) {
				return (e.isSupportGetter() && acceptFieldDescriptor.accept(e.getGetter()))
						|| (e.isSupportSetter() && acceptFieldDescriptor.accept(e.getSetter()));
			}
		});
	}

	default Fields acceptGetter(Accept<FieldDescriptor> accept) {
		if (accept == null) {
			return this;
		}

		return accept(new Accept<Field>() {
			@Override
			public boolean accept(Field e) {
				return e.isSupportGetter() && accept.accept(e.getGetter());
			}
		});
	}

	default Fields acceptSetter(Accept<FieldDescriptor> accept) {
		if (accept == null) {
			return this;
		}

		return accept(new Accept<Field>() {

			@Override
			public boolean accept(Field e) {
				return e.isSupportSetter() && accept.accept(e.getSetter());
			}
		});
	}

	public default Fields shared() {
		return new SharedFields(getCursorId(), this, rows());
	}

	/**
	 * 去重
	 * 
	 * @return
	 */
	default Fields distinct() {
		return new StreamFields(getCursorId(), this, () -> stream().distinct());
	}

	/**
	 * 支持getter的
	 * 
	 * @return
	 */
	default Fields getters() {
		return accept(FieldFeature.SUPPORT_GETTER);
	}

	/**
	 * 支持setter的
	 * 
	 * @return
	 */
	default Fields setters() {
		return accept(FieldFeature.SUPPORT_SETTER);
	}

	/**
	 * 获取实体类字段(抽象的字段，不一定存在{@link java.lang.reflect.Field})
	 * 
	 * @see #ignoreStatic()
	 * @see #ignoreTransient()
	 * @see #strict()
	 * @return
	 */
	default Fields entity() {
		return ignoreStatic().ignoreTransient().strict();
	}

	/**
	 * 忽略transient描述的字段
	 * 
	 * @return
	 */
	default Fields ignoreTransient() {
		return accept(FieldFeature.IGNORE_TRANSIENT);
	}

	/**
	 * 严格的，必须包含getter和setter
	 * 
	 * @return
	 */
	default Fields strict() {
		return accept(FieldFeature.STRICT);
	}

	/**
	 * 忽略常量
	 * 
	 * @return
	 */
	default Fields ignoreFinal() {
		return accept(FieldFeature.IGNORE_FINAL);
	}

	default Fields ignoreStatic() {
		return accept(FieldFeature.IGNORE_STATIC);
	}

	default Fields accept(Accept<Field> accept) {
		return new AcceptFields(this, accept);
	}

	/**
	 * 排除一些字段
	 * 
	 * @param accept
	 * @return
	 */
	default Fields exclude(Accept<Field> accept) {
		if (accept == null) {
			return this;
		}

		return accept(accept.negate());
	}

	/**
	 * 排除一些字段
	 * 
	 * @param names
	 * @return
	 */
	default Fields exclude(Collection<String> names) {
		if (CollectionUtils.isEmpty(names)) {
			return this;
		}

		return exclude(new Accept<Field>() {

			public boolean accept(Field e) {
				return (e.isSupportGetter() && names.contains(e.getGetter().getName()))
						|| (e.isSupportSetter() && names.contains(e.getSetter().getName()));
			}
		});
	}

	/**
	 * 获取字段的值
	 * 
	 * @param instance
	 * @return 子类和父类可能存在相同的字段名
	 */
	default MultiValueMap<String, Object> getMultiValueMap(Object instance) {
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		for (Field field : this) {
			if (!field.isSupportGetter()) {
				continue;
			}

			Object value = field.getValue(instance);
			if (value == null) {
				continue;
			}

			map.add(field.getGetter().getName(), value);
		}
		return map;
	}

	default Map<String, Object> getValueMap(Object instance) {
		return getMultiValueMap(instance).toSingleValueMap();
	}

	@Override
	default boolean hasNext() {
		Class<?> next = getNextCursorId();
		return next != null && next != Object.class;
	}

	@Override
	default Class<?> getNextCursorId() {
		return getCursorId().getSuperclass();
	}

	/**
	 * 获取全部字段
	 * 
	 * @see StreamFields
	 * @see Fields#streamAll()
	 * @return
	 */
	default Fields all() {
		if (hasNext()) {
			return new StreamFields(getCursorId(), null, this, () -> streamAll());
		}
		return this;
	}

	default Fields merge(Fields fields) {
		return new MergeFields(this, fields);
	}

	@Override
	public default Fields next() {
		return jumpTo(getNextCursorId());
	}

	@Override
	Fields jumpTo(Class<?> cursorId);
}
