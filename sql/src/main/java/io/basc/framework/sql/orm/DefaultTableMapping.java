package io.basc.framework.sql.orm;

import java.util.function.Function;

import io.basc.framework.orm.DefaultEntityMapping;
import io.basc.framework.orm.EntityMapping;
import io.basc.framework.orm.Property;
import io.basc.framework.util.Assert;
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

	public <S extends Property> DefaultTableMapping(EntityMapping<? extends S> mapping,
			Function<? super S, ? extends T> converter, Class<?> sourceClass, TableResolver tableResolver) {
		super(mapping, converter);
		Assert.requiredArgument(sourceClass != null, "sourceClass");
		Assert.requiredArgument(tableResolver != null, "tableResolver");
		this.engine = tableResolver.getEngine(sourceClass);
		this.rowFormat = tableResolver.getRowFormat(sourceClass);
		this.autoCreate = tableResolver.isAutoCreate(sourceClass);
	}

	public <S extends Column> DefaultTableMapping(TableMapping<? extends S> mapping,
			Function<? super S, ? extends T> converter) {
		super(mapping, converter);
		this.engine = mapping.getEngine();
		this.rowFormat = mapping.getRowFormat();
		this.autoCreate = mapping.isAutoCreate();
	}
}
