package run.soeasy.framework.core.invoke.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.transform.property.ObjectTemplateFactory;
import run.soeasy.framework.core.transform.property.PropertyTemplate;
import run.soeasy.framework.core.type.ClassMembersLoader;
import run.soeasy.framework.core.type.ClassUtils;
import run.soeasy.framework.core.type.ReflectionUtils;

public class ReflectionFieldTemplateFactory implements ObjectTemplateFactory<ReflectionField> {

	@Override
	public boolean hasMapping(@NonNull TypeDescriptor requiredType) {
		return !ClassUtils.isPrimitiveOrWrapper(requiredType.getType())
				|| !ClassUtils.isPrimitiveWrapperArray(requiredType.getType());
	}

	protected ClassMembersLoader<Field> getFields(Class<?> requiredType) {
		return ReflectionUtils.getDeclaredFields(requiredType);
	}

	@Override
	public PropertyTemplate<ReflectionField> getTemplate(Class<?> objectClass) {
		Elements<ReflectionField> fields = getFields(objectClass).filter((e) -> !Modifier.isStatic(e.getModifiers()))
				.map((field) -> new ReflectionField(field));
		PropertyTemplate<ReflectionField> template = () -> fields.iterator();
		return template.asMap(true);
	}
}
