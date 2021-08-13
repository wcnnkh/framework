package scw.mapper;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import scw.core.utils.CollectionUtils;
import scw.lang.Nullable;
import scw.util.Accept;
import scw.util.page.Pageables;

public interface Fields extends Pageables<Class<?>, Field> {

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
		AcceptFieldDescriptor acceptFieldDescriptor = new AcceptFieldDescriptor(
				name, type);
		return accept(new Accept<Field>() {

			@Override
			public boolean accept(Field e) {
				return (e.isSupportGetter() && acceptFieldDescriptor.accept(e
						.getGetter()))
						|| (e.isSupportSetter() && acceptFieldDescriptor
								.accept(e.getSetter()));
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

	@Override
	public default Fields shared() {
		return new SharedFields(getCursorId(), this, rows());
	}

	/**
	 * 去重
	 * 
	 * @return
	 */
	default Fields distinct() {
		return new StreamFields(getCursorId(), this, stream().distinct());
	}
	
	/**
	 * 获取实体的所的字段(抽象的字段，不一定存在{@link java.lang.reflect.Field})，即不包含静态字段
	 * @return
	 */
	default Fields entity() {
		return accept(FieldFeature.IGNORE_STATIC).accept(FieldFeature.IGNORE_TRANSIENT);
	}
	
	/**
	 * 严格的字段约定(包含getter setter)
	 * @return
	 */
	default Fields strict(){
		return accept(FieldFeature.STRICT);
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
				return (e.isSupportGetter() && names.contains(e.getGetter()
						.getName()))
						|| (e.isSupportSetter() && names.contains(e.getSetter()
								.getName()));
			}
		});
	}

	default Map<String, Object> getValueMap(Object instance) {
		return getValueMap(instance, false);
	}

	default Map<String, Object> getValueMap(Object instance, boolean nullable) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		for (Field field : this) {
			if (!field.isSupportGetter()) {
				continue;
			}

			String name = field.getGetter().getName();
			if (map.containsKey(name)) {
				continue;
			}

			Object value = field.getValue(instance);
			if (value == null && !nullable) {
				continue;
			}
			map.put(name, value);
		}
		return map;
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
		if(hasNext()) {
			return new StreamFields(getCursorId(), null, this, streamAll());
		}
		return this;
	}

	@Override
	public default Fields next() {
		return jumpTo(getNextCursorId());
	}

	@Override
	Fields jumpTo(Class<?> cursorId);
}
