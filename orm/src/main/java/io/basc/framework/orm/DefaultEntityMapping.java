package io.basc.framework.orm;

import java.util.function.Function;

import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.Mapping;
import io.basc.framework.mapper.support.DefaultMapping;
import io.basc.framework.util.Assert;
import io.basc.framework.util.LinkedMultiValueMap;
import io.basc.framework.util.MultiValueMap;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DefaultEntityMapping<T extends Property> extends DefaultMapping<T> implements EntityMapping<T> {
	private String comment;
	private String charsetName;

	public DefaultEntityMapping() {
	}

	public DefaultEntityMapping(Class<?> sourceClass, EntityMappingResolver relationalResolver) {
		Assert.requiredArgument(sourceClass != null, "sourceClass");
		Assert.requiredArgument(relationalResolver != null, "relationalResolver");
		setName(relationalResolver.getName(sourceClass));
		setAliasNames(relationalResolver.getAliasNames(sourceClass));
		setCharsetName(relationalResolver.getCharsetName(sourceClass));
		setComment(relationalResolver.getComment(sourceClass));
	}

	public <S extends Field> DefaultEntityMapping(Class<?> sourceClass, EntityMappingResolver relationalResolver,
			Mapping<? extends S> mapping, Function<? super S, ? extends T> converter) {
		this(sourceClass, relationalResolver);
		Assert.requiredArgument(mapping != null, "mapping");
		Assert.requiredArgument(converter != null, "converter");
		MultiValueMap<String, T> propertyMap = new LinkedMultiValueMap<>();
		for (S field : mapping.getElements()) {
			T property = converter.apply(field);
			propertyMap.add(property.getName(), property);
		}
		setElementMap(propertyMap);
	}
}
