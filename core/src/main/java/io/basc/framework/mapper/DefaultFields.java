package io.basc.framework.mapper;

import java.util.function.Function;

import io.basc.framework.core.ResolvableType;
import io.basc.framework.util.Elements;
import lombok.NonNull;

public class DefaultFields<E extends Field, R extends Fields<E, R>> extends Fields<E, R> {
	private final Function<? super Fields<E, R>, ? extends R> fieldsWrapper;

	public DefaultFields(Function<? super Fields<E, R>, ? extends R> fieldsWrapper, @NonNull ResolvableType source,
			@NonNull Elements<E> elements) {
		super(source, elements);
		this.fieldsWrapper = fieldsWrapper;
	}

	@Override
	public Function<? super Fields<E, R>, ? extends R> getFieldsWrapper() {
		return fieldsWrapper;
	}

}
