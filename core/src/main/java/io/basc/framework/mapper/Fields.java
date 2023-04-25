package io.basc.framework.mapper;

import java.util.function.Function;

import io.basc.framework.core.DefaultStructure;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.util.Elements;
import io.basc.framework.util.LinkedMultiValueMap;
import io.basc.framework.util.MultiValueMap;
import lombok.NonNull;

public abstract class Fields<E extends Field, R extends Fields<E, R>> extends DefaultStructure<E, R> {
	private final Function<? super DefaultStructure<E, R>, ? extends R> membersWrapper = (members) -> {
		Fields<E, R> fields = new DefaultFields<>(getFieldsWrapper(), getSource(), getElements());
		return getFieldsWrapper().apply(fields);
	};

	public Fields(@NonNull ResolvableType source, @NonNull Elements<E> elements) {
		super(source, elements);
	}

	public Fields(Fields<E, R> fields) {
		super(fields);
	}

	@Override
	public final Function<? super DefaultStructure<E, R>, ? extends R> getMembersWrapper() {
		return membersWrapper;
	}

	public abstract Function<? super Fields<E, R>, ? extends R> getFieldsWrapper();

	/**
	 * 获取字段的值
	 * 
	 * @param instance
	 * @return 可能存在相同的字段名
	 */
	public MultiValueMap<String, Object> getMultiValueMap(Object instance) {
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		for (Field field : this.getElements()) {
			if (!field.isSupportGetter()) {
				continue;
			}

			Object value = field.get(instance);
			if (value == null) {
				continue;
			}

			map.add(field.getGetter().getName(), value);
		}
		return map;
	}
}
