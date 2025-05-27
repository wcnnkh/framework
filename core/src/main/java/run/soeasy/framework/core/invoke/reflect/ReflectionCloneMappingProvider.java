package run.soeasy.framework.core.invoke.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.lang.ClassMembersLoader;
import run.soeasy.framework.core.lang.ClassUtils;
import run.soeasy.framework.core.lang.ReflectionUtils;
import run.soeasy.framework.core.transform.TemplateMapping;
import run.soeasy.framework.core.transform.property.ObjectPropertyAccessor;
import run.soeasy.framework.core.transform.service.MappingProvider;

class ReflectionCloneMappingProvider implements
		MappingProvider<Object, Object, ObjectPropertyAccessor<ReflectionField>, TemplateMapping<ObjectPropertyAccessor<ReflectionField>>> {

	@Override
	public TemplateMapping<ObjectPropertyAccessor<ReflectionField>> getMapping(@NonNull Object source,
			@NonNull TypeDescriptor requiredType) {
		Elements<ReflectionField> fields = getFields(requiredType.getType())
				.filter((e) -> !Modifier.isStatic(e.getModifiers())).map((field) -> new ReflectionField(field));
		TemplateMapping<ObjectPropertyAccessor<ReflectionField>> mapping = () -> fields
				.map((field) -> new ObjectPropertyAccessor<>(field, source)).map((e) -> KeyValue.of(e.getName(), e));
		return mapping.asMap();

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
