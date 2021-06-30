package scw.mapper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import scw.core.utils.CollectionUtils;
import scw.lang.Nullable;
import scw.util.Accept;

public interface Fields extends Iterable<Field> {
	/**
	 * 获取第一个字段
	 * 
	 * @return
	 */
	@Nullable
	default Field first() {
		for (Field field : this) {
			return field;
		}
		return null;
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

	/**
	 * 去重
	 * 
	 * @return
	 */
	default Fields duplicateRemoval() {
		Set<Field> fields = new LinkedHashSet<Field>();
		for (Field field : this) {
			fields.add(field);
		}
		return new SharedFields(fields);
	}

	/**
	 * 可共享的
	 * 
	 * @return
	 */
	default Fields shared() {
		List<Field> fields = new ArrayList<Field>();
		for (Field field : this) {
			fields.add(field);
		}
		return new SharedFields(fields);
	}

	/**
	 * 获取字段数量，在非shared下字段的数量通过遍历获取的,所以推荐先调用shared再获取数量
	 * 
	 * @see SharedFields
	 */
	default int size() {
		int size = 0;
		for (@SuppressWarnings("unused")
		Field field : this) {
			size++;
		}
		return size;
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

	default Stream<Field> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

	default Fields merge(Fields fields) {
		return new MultiFields(this, fields);
	}
}
