package io.basc.framework.sql.orm;

import java.lang.reflect.Modifier;
import java.util.function.Function;

import io.basc.framework.mapper.AccessibleField;
import io.basc.framework.mapper.Field;
import io.basc.framework.orm.EntityMappingResolver;
import io.basc.framework.orm.Property;
import io.basc.framework.util.Elements;

public class ColumnsFunction implements Function<Class<?>, Elements<Column>> {
	private final Function<Class<?>, ? extends Elements<? extends AccessibleField>> processor;
	private final EntityMappingResolver objectRelationalResolver;
	private final Column parent;

	public ColumnsFunction(EntityMappingResolver objectRelationalResolver, Column parnet,
			Function<Class<?>, ? extends Elements<? extends AccessibleField>> processor) {
		this.objectRelationalResolver = objectRelationalResolver;
		this.parent = parnet;
		this.processor = processor;
	}

	@Override
	public Elements<Column> apply(Class<?> t) {
		return processor.apply(t)
				.filter((o) -> (o.isSupportGetter() && !Modifier.isStatic(o.getGetter().getModifiers()))
						&& (o.isSupportSetter() && !Modifier.isStatic(o.getSetter().getModifiers())))
				.map((o) -> new Field(parent, t, o)).map((o) -> new Property(o, objectRelationalResolver))
				.map((o) -> new Column(o));
	}

	public Function<Class<?>, ? extends Elements<? extends AccessibleField>> getProcessor() {
		return processor;
	}

	public EntityMappingResolver getObjectRelationalResolver() {
		return objectRelationalResolver;
	}

	public Column getParent() {
		return parent;
	}
}
