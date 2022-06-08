package io.basc.framework.sql.orm;

import java.lang.reflect.Modifier;
import java.util.function.Function;
import java.util.stream.Stream;

import io.basc.framework.mapper.AccessibleField;
import io.basc.framework.mapper.Field;
import io.basc.framework.orm.ObjectRelationalResolver;
import io.basc.framework.orm.Property;

public class ColumnsFunction implements Function<Class<?>, Stream<Column>> {
	private final Function<Class<?>, ? extends Stream<? extends AccessibleField>> processor;
	private final ObjectRelationalResolver objectRelationalResolver;
	private final Column parent;

	public ColumnsFunction(ObjectRelationalResolver objectRelationalResolver, Column parnet,
			Function<Class<?>, ? extends Stream<? extends AccessibleField>> processor) {
		this.objectRelationalResolver = objectRelationalResolver;
		this.parent = parnet;
		this.processor = processor;
	}

	@Override
	public Stream<Column> apply(Class<?> t) {
		return processor.apply(t)
				.filter((o) -> (o.isSupportGetter() && !Modifier.isStatic(o.getGetter().getModifiers()))
						&& (o.isSupportSetter() && !Modifier.isStatic(o.getSetter().getModifiers())))
				.map((o) -> new Field(parent, t, o)).map((o) -> new Property(o, objectRelationalResolver))
				.map((o) -> new Column(o));
	}

	public Function<Class<?>, ? extends Stream<? extends AccessibleField>> getProcessor() {
		return processor;
	}

	public ObjectRelationalResolver getObjectRelationalResolver() {
		return objectRelationalResolver;
	}

	public Column getParent() {
		return parent;
	}
}
