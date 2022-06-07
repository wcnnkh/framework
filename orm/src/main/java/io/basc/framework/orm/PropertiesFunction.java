package io.basc.framework.orm;

import java.lang.reflect.Modifier;
import java.util.function.Function;
import java.util.stream.Stream;

import io.basc.framework.mapper.AccessibleField;
import io.basc.framework.mapper.Field;

public class PropertiesFunction implements Function<Class<?>, Stream<Property>> {
	private final Function<Class<?>, ? extends Stream<? extends AccessibleField>> processor;
	private final ObjectRelationalResolver objectRelationalResolver;
	private final Property parent;

	public PropertiesFunction(ObjectRelationalResolver objectRelationalResolver, Property parnet,
			Function<Class<?>, ? extends Stream<? extends AccessibleField>> processor) {
		this.objectRelationalResolver = objectRelationalResolver;
		this.parent = parnet;
		this.processor = processor;
	}

	@Override
	public Stream<Property> apply(Class<?> t) {
		return processor.apply(t)
				.filter((o) -> (o.isSupportGetter() && !Modifier.isStatic(o.getGetter().getModifiers()))
						|| (o.isSupportSetter() && !Modifier.isStatic(o.getSetter().getModifiers())))
				.map((o) -> new Field(parent, t, o)).map((o) -> new Property(o, objectRelationalResolver));
	}

	public Function<Class<?>, ? extends Stream<? extends AccessibleField>> getProcessor() {
		return processor;
	}

	public ObjectRelationalResolver getObjectRelationalResolver() {
		return objectRelationalResolver;
	}

	public Property getParent() {
		return parent;
	}
}
