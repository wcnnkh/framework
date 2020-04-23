package scw.beans;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;

import scw.beans.annotation.Autowired;
import scw.beans.annotation.Config;
import scw.beans.annotation.InitMethod;
import scw.beans.annotation.Value;
import scw.core.annotation.AnnotationUtils;
import scw.core.reflect.DefaultFieldDefinition;
import scw.core.reflect.FieldDefinition;
import scw.core.reflect.ReflectionUtils;

public class BeanMetadata {
	protected final LinkedList<BeanMethod> initMethods = new LinkedList<BeanMethod>();
	protected final LinkedList<BeanMethod> destroyMethods = new LinkedList<BeanMethod>();
	protected final LinkedList<FieldDefinition> autowriteFieldDefinitions = new LinkedList<FieldDefinition>();

	public BeanMetadata(Class<?> targetClass) {
		for (Method method : AnnotationUtils.getAnnoationMethods(targetClass,
				true, true, InitMethod.class)) {
			if (Modifier.isStatic(method.getModifiers())) {
				continue;
			}

			method.setAccessible(true);
			initMethods.add(new NoArgumentBeanMethod(method));
		}

		for (Method method : AnnotationUtils.getAnnoationMethods(targetClass,
				true, true, InitMethod.class)) {
			if (Modifier.isStatic(method.getModifiers())) {
				continue;
			}

			method.setAccessible(true);
			destroyMethods.add(new NoArgumentBeanMethod(method));
		}

		Class<?> clz = targetClass;
		while (clz != null && clz != Object.class) {
			for (final Field field : ReflectionUtils.getDeclaredFields(clz)) {
				if (AnnotationUtils.isDeprecated(field)) {
					continue;
				}

				Autowired autowired = field.getAnnotation(Autowired.class);
				Config config = field.getAnnotation(Config.class);
				Value value = field.getAnnotation(Value.class);
				if (autowired == null && config == null && value == null) {
					continue;
				}

				field.setAccessible(true);
				if (Modifier.isStatic(field.getModifiers())) {
					continue;
				}

				autowriteFieldDefinitions
						.add(createFieldDefinition(clz, field));
			}
			clz = clz.getSuperclass();
		}
	}

	protected FieldDefinition createFieldDefinition(Class<?> clazz, Field field) {
		return new DefaultFieldDefinition(clazz, field, false, false, true);
	}

	public LinkedList<BeanMethod> getInitMethods() {
		return initMethods;
	}

	public LinkedList<BeanMethod> getDestroyMethods() {
		return destroyMethods;
	}

	public LinkedList<FieldDefinition> getAutowriteFieldDefinitions() {
		return autowriteFieldDefinitions;
	}
}
