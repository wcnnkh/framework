package io.basc.framework.orm;

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
		super(sourceClass, objectRelationalResolver, parent,
				new PropertiesFunction(objectRelationalResolver, parent, processor));
	}

	public EntityStructure(Members<Property> members) {
		super(members);
	}

	public EntityStructure(Members<? extends Field> members, Function<? super Field, ? extends Property> map) {
		super(members, (e) -> {
			if (e == null) {
				return null;
			}

			if (e instanceof Property) {
				return (Property) e;
			}

			return new Property(e);
		});
	}

	@Override
	public EntityStructure setParentField(Field field) {
		if (field instanceof Property) {
			return setParent((Property) field);
		}

		Property property = field == null ? null : new Property(field, this.objectRelationalResolver);
		return setParent(property);
	}

	@Override
	public EntityStructure jumpTo(Class<?> cursorId) {
		if (objectRelationalResolver != null && objectRelationalResolver instanceof ObjectRelationalFactory) {
			ObjectRelational<? extends Property> objectRelational = ((ObjectRelationalFactory) objectRelationalResolver)
					.getStructure(cursorId);
			return new EntityStructure(objectRelational, (e) -> (Property) e);
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
}
