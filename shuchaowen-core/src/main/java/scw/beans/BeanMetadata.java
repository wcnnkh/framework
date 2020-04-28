package scw.beans;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;

import scw.beans.annotation.Autowired;
import scw.beans.annotation.Config;
import scw.beans.annotation.InitMethod;
import scw.beans.annotation.Value;
import scw.beans.config.ConfigBeanField;
import scw.beans.property.ValueBeanField;
import scw.core.annotation.AnnotationUtils;
import scw.core.reflect.DefaultFieldDefinition;
import scw.core.reflect.FieldDefinition;
import scw.core.reflect.ReflectionUtils;

public class BeanMetadata {
	protected final LinkedList<BeanMethod> initMethods = new LinkedList<BeanMethod>();
	protected final LinkedList<BeanMethod> destroyMethods = new LinkedList<BeanMethod>();
	protected final LinkedList<BeanField> autowritedBeanFields = new LinkedList<BeanField>();

	public BeanMetadata(Class<?> targetClass) {
		for (Method method : AnnotationUtils.getAnnoationMethods(targetClass, true, true, InitMethod.class)) {
			method.setAccessible(true);
			initMethods.add(new NoArgumentBeanMethod(method));
		}

		for (Method method : AnnotationUtils.getAnnoationMethods(targetClass, true, true, InitMethod.class)) {
			method.setAccessible(true);
			destroyMethods.add(new NoArgumentBeanMethod(method));
		}

		Class<?> clz = targetClass;
		while (clz != null && clz != Object.class) {
			for (final Field field : ReflectionUtils.getDeclaredFields(clz)) {
				if (AnnotationUtils.isDeprecated(field)) {
					continue;
				}

				field.setAccessible(true);
				FieldDefinition fieldDefinition = createFieldDefinition(clz, field);
				Autowired autowired = fieldDefinition.getAnnotatedElement().getAnnotation(Autowired.class);
				if (autowired != null) {
					autowritedBeanFields.add(new AutowiredBeanField(fieldDefinition));
				}

				Config config = field.getAnnotation(Config.class);
				if (config != null) {
					autowritedBeanFields.add(new ConfigBeanField(fieldDefinition));
				}

				Value value = field.getAnnotation(Value.class);
				if (value != null) {
					autowritedBeanFields.add(new ValueBeanField(fieldDefinition));
				}
			}
			clz = clz.getSuperclass();
		}
	}

	protected FieldDefinition createFieldDefinition(Class<?> clazz, Field field) {
		return new DefaultFieldDefinition(clazz, field, false, false, false);
	}

	public LinkedList<BeanMethod> getInitMethods() {
		return initMethods;
	}

	public LinkedList<BeanMethod> getDestroyMethods() {
		return destroyMethods;
	}

	public LinkedList<BeanField> getAutowritedBeanFields() {
		return autowritedBeanFields;
	}
}
