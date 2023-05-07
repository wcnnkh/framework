package io.basc.framework.mapper.support;

import java.util.Iterator;

import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.Mapping;
import io.basc.framework.mapper.MappingException;
import io.basc.framework.mapper.ObjectAccess;
import io.basc.framework.mapper.ObjectMapper;
import io.basc.framework.util.Assert;
import io.basc.framework.value.Value;

public final class SetFilterChain {
	private final Iterator<? extends SetFilter> iterator;
	private final SetFilterChain chain;

	public SetFilterChain(Iterator<? extends SetFilter> iterator) {
		this(iterator, null);
	}

	public SetFilterChain(Iterator<? extends SetFilter> iterator, @Nullable SetFilterChain chain) {
		Assert.requiredArgument(iterator != null, "iterator");
		this.iterator = iterator;
		this.chain = chain;
	}

	public <T extends Field> void set(ObjectMapper objectMapper, Value source, Mapping<? extends T> sourceMapping,
			T sourceField, Value target, Mapping<? extends T> targetMapping, T targetField) throws MappingException {
		if (iterator.hasNext()) {
			iterator.next().set(objectMapper, source, sourceMapping, sourceField, target, targetMapping, targetField,
					this);
		} else if (chain != null) {
			chain.set(objectMapper, source, sourceMapping, sourceField, target, targetMapping, targetField);
		}
	}

	public <T extends Field> void set(ObjectMapper objectMapper, ObjectAccess sourceField, Value target,
			Mapping<? extends T> targetMapping, T targetField) throws MappingException {
		if (iterator.hasNext()) {
			iterator.next().set(objectMapper, sourceField, target, targetMapping, targetField, this);
		} else if (chain != null) {
			chain.set(objectMapper, sourceField, target, targetMapping, targetField);
		}
	}

	public <T extends Field> void set(ObjectMapper objectMapper, Value source, Mapping<? extends T> sourceMapping,
			T sourceField, ObjectAccess targetAccess) throws MappingException {
		if (iterator.hasNext()) {
			iterator.next().set(objectMapper, source, sourceMapping, sourceField, targetAccess, this);
		} else if (chain != null) {
			chain.set(objectMapper, source, sourceMapping, sourceField, targetAccess);
		}
	}

	public void set(ObjectMapper objectMapper, ObjectAccess sourceAccess, ObjectAccess targetAccess, String name)
			throws MappingException {
		if (iterator.hasNext()) {
			iterator.next().set(objectMapper, sourceAccess, targetAccess, name, this);
		} else if (chain != null) {
			chain.set(objectMapper, sourceAccess, targetAccess, name);
		}
	}
}
