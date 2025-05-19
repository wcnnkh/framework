package run.soeasy.framework.core.invoke.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.invoke.field.FieldAccessor;
import run.soeasy.framework.core.invoke.field.FieldDescriptor;
import run.soeasy.framework.core.reflect.ReflectionUtils;
import run.soeasy.framework.core.transform.indexed.IndexedMapping;
import run.soeasy.framework.core.transform.service.MappingProvider;
import run.soeasy.framework.core.type.ClassMembersLoader;
import run.soeasy.framework.core.type.ClassUtils;

public class ReflectionMappingProvider implements
		MappingProvider<Object, Object, FieldAccessor<FieldDescriptor>, IndexedMapping<FieldAccessor<FieldDescriptor>>> {

	@Override
	public IndexedMapping<FieldAccessor<FieldDescriptor>> getMapping(@NonNull Object source,
			@NonNull TypeDescriptor requiredType) {
		Elements<FieldDescriptor> fields = getFields(requiredType.getType())
				.filter((e) -> !Modifier.isStatic(e.getModifiers())).map(ReflectionField::new);
		IndexedMapping<FieldAccessor<FieldDescriptor>> mapping = () -> fields.map(FieldAccessor::new).iterator();
		return mapping.randomAccess();
	}

	@Override
	public boolean hasMapping(@NonNull TypeDescriptor requiredType) {
		return !ClassUtils.isPrimitiveOrWrapper(requiredType.getType())
				|| !ClassUtils.isPrimitiveWrapperArray(requiredType.getType());
	}

	protected ClassMembersLoader<Field> getFields(Class<?> requiredType) {
		return ReflectionUtils.getDeclaredFields(requiredType);
	}
}
