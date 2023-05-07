package io.basc.framework.sql.orm;

import java.util.function.Function;

import io.basc.framework.convert.TypeDescriptor;
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

	public DefaultTableMapping(TypeDescriptor source, EntityMappingResolver relationalResolver,
			TableResolver tableResolver) {
		super(source, relationalResolver);
		this.engine = tableResolver.getEngine(source);
		this.rowFormat = tableResolver.getRowFormat(source);
		this.autoCreate = tableResolver.isAutoCreate(source);
	}

	public <S extends Field> DefaultTableMapping(TypeDescriptor source, EntityMappingResolver relationalResolver,
			TableResolver tableResolver, Mapping<? extends S> mapping, Function<? super S, ? extends T> converter) {
		this(source, relationalResolver, tableResolver);
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
