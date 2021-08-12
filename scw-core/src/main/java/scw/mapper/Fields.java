package scw.mapper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import scw.core.utils.CollectionUtils;
import scw.lang.Nullable;
import scw.util.Accept;
import scw.util.page.Pageables;

public interface Fields extends Pageables<Class<?>, Field>{
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
	default Fields distinct() {
		return new SharedFields(getCursorId(), rows().stream().distinct());
		Set<Field> fields = new LinkedHashSet<Field>();
		for (Field field : this) {
			fields.add(field);
		}
		return new SharedFields(fields);
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
	
	/**
	 * 获取全部字段
	 * @see #stream()
	 * @return
	 */
	default Fields all() {
		return new SharedFields(getCursorId(), stream().collect(Collectors.toList()));
	}
}
