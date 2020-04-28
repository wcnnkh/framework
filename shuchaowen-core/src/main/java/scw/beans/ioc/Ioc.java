package scw.beans.ioc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import scw.beans.annotation.Autowired;
import scw.beans.annotation.Config;
import scw.beans.annotation.InitMethod;
import scw.beans.annotation.Value;
import scw.core.annotation.AnnotationUtils;
import scw.core.reflect.DefaultFieldDefinition;
import scw.core.reflect.FieldDefinition;
import scw.core.reflect.ReflectionUtils;

public class Ioc {
	private final IocMetadata init = new IocMetadata();
	private final IocMetadata destroy = new IocMetadata();
	private final IocMetadata autowired = new IocMetadata();

	public Ioc() {
	};

	public Ioc(Class<?> targetClass) {
		for (Method method : AnnotationUtils.getAnnoationMethods(targetClass,
				true, true, InitMethod.class)) {
			method.setAccessible(true);
			init.getIocProcessors().add(
					new NoArgumentMethodIocProcessor(method));
		}

		for (Method method : AnnotationUtils.getAnnoationMethods(targetClass,
				true, true, InitMethod.class)) {
			method.setAccessible(true);
			destroy.getIocProcessors().add(
					new NoArgumentMethodIocProcessor(method));
		}

		Class<?> clz = targetClass;
		while (clz != null && clz != Object.class) {
			for (final Field field : ReflectionUtils.getDeclaredFields(clz)) {
				if (AnnotationUtils.isDeprecated(field)) {
					continue;
				}

				field.setAccessible(true);
				FieldDefinition fieldDefinition = createFieldDefinition(clz,
						field);
				Autowired autowired = fieldDefinition.getAnnotatedElement()
						.getAnnotation(Autowired.class);
				if (autowired != null) {
					this.autowired.getIocProcessors().add(
							new AutowiredIocProcessor(fieldDefinition));
				}

				Config config = field.getAnnotation(Config.class);
				if (config != null) {
					this.autowired.getIocProcessors().add(
							new ConfigIocProcessor(fieldDefinition));
				}

				Value value = field.getAnnotation(Value.class);
				if (value != null) {
					this.autowired.getIocProcessors().add(
							new ValueIocProcessor(fieldDefinition));
				}
			}
			clz = clz.getSuperclass();
		}
	}

	protected FieldDefinition createFieldDefinition(Class<?> clazz, Field field) {
		return new DefaultFieldDefinition(clazz, field, false, false, false);
	}

	public final IocMetadata getInit() {
		return init;
	}

	public final IocMetadata getDestroy() {
		return destroy;
	}

	public final IocMetadata getAutowired() {
		return autowired;
	}
}
