package io.basc.framework.sql.orm;

import java.util.function.Function;

import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.Mapping;
import io.basc.framework.orm.DefaultEntityMapping;
import io.basc.framework.orm.EntityMappingResolver;
import io.basc.framework.util.Assert;
import io.basc.framework.util.LinkedMultiValueMap;
import io.basc.framework.util.MultiValueMap;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DefaultTableMapping<T extends Column> extends DefaultEntityMapping<T> implements TableMapping<T> {
	private String engine;
	private String rowFormat;
	private boolean autoCreate;

	public DefaultTableMapping() {
	}

	public DefaultTableMapping(Class<?> sourceClass, EntityMappingResolver relationalResolver,
			TableResolver tableResolver) {
		super(sourceClass, relationalResolver);
		this.engine = tableResolver.getEngine(sourceClass);
		this.rowFormat = tableResolver.getRowFormat(sourceClass);
		this.autoCreate = tableResolver.isAutoCreate(sourceClass);
	}

	public <S extends Field> DefaultTableMapping(Class<?> sourceClass, EntityMappingResolver relationalResolver,
			TableResolver tableResolver, Mapping<? extends S> mapping, Function<? super S, ? extends T> converter) {
		this(sourceClass, relationalResolver, tableResolver);
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
