package io.basc.framework.orm;

import java.lang.reflect.Modifier;
import java.util.function.Function;
import java.util.stream.Stream;

import io.basc.framework.core.Members;
import io.basc.framework.mapper.AccessibleField;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.Fields;

/**
 * 实体结构
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public final class EntityStructure extends ObjectRelationalDecorator<Property, EntityStructure> {

	public EntityStructure(Class<?> sourceClass) {
		this(sourceClass, null, null);
	}

	public EntityStructure(Class<?> sourceClass, ObjectRelationalResolver objectRelationalResolver, Property parent) {
		this(sourceClass, objectRelationalResolver, parent, Fields.DEFAULT);
	}

	public EntityStructure(Class<?> sourceClass, ObjectRelationalResolver objectRelationalResolver, Property parent,
			Function<Class<?>, ? extends Stream<? extends AccessibleField>> processor) {
		super(sourceClass, objectRelationalResolver, (e) -> processor.apply(e)
				.filter((o) -> (o.isSupportGetter() && !Modifier.isStatic(o.getGetter().getModifiers()))
						|| (o.isSupportSetter() && !Modifier.isStatic(o.getSetter().getModifiers())))
				.map((o) -> new Field(parent, sourceClass, o)).map((o) -> new Property(o, objectRelationalResolver)));
	}

	public EntityStructure(ObjectRelational<Property> members) {
		super(members);
	}

	@Override
	public EntityStructure setParentField(Field field) {
		if (field instanceof Property) {
			return setParent((Property) field);
		}
		return super.setParentField(new Property(field, this.objectRelationalResolver));
	}

	@Override
	public EntityStructure jumpTo(Class<?> cursorId) {
		if (objectRelationalResolver != null && objectRelationalResolver instanceof ObjectRelationalMapper) {
			ObjectRelational<? extends Property> objectRelational = ((ObjectRelationalMapper) objectRelationalResolver)
					.getStructure(cursorId);
			Members<Property> use = objectRelational.map((e) -> (Property) e);
			return decorate(decorate(decorate(use)));
		}
		return super.jumpTo(cursorId);
	}

	@Override
	protected EntityStructure decorate(ObjectRelational<Property> structure) {
		if (structure instanceof EntityStructure) {
			return (EntityStructure) structure;
		}

		EntityStructure entityStructure = new EntityStructure(structure);
		entityStructure.objectRelationalResolver = this.objectRelationalResolver;
		return entityStructure;
	}

	@Override
	protected Property clone(Property source) {
		return source.clone();
	}

	@Override
	public ObjectRelational<Property> setParentProperty(Property parent) {
		return setParent(parent);
	}
}
