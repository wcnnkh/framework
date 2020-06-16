package scw.beans.ioc;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.List;

import scw.beans.annotation.Autowired;
import scw.beans.annotation.Config;
import scw.beans.annotation.InitMethod;
import scw.beans.annotation.Value;
import scw.core.annotation.AnnotationUtils;
import scw.mapper.Field;
import scw.mapper.FieldFilter;
import scw.mapper.MapperUtils;
import scw.util.ConcurrentReferenceHashMap;

public final class Ioc {
	private static ConcurrentReferenceHashMap<Class<?>, Ioc> iocCache = new ConcurrentReferenceHashMap<Class<?>, Ioc>();

	private final IocMetadata init = new IocMetadata();
	private final IocMetadata destroy = new IocMetadata();
	private final IocMetadata dependence = new IocMetadata();

	public Ioc() {
	};

	public Ioc(Class<?> targetClass) {
		for (Method method : AnnotationUtils.getAnnoationMethods(targetClass, true, true, InitMethod.class)) {
			method.setAccessible(true);
			init.getIocProcessors().add(new NoArgumentMethodIocProcessor(method));
		}

		for (Method method : AnnotationUtils.getAnnoationMethods(targetClass, true, true, InitMethod.class)) {
			method.setAccessible(true);
			destroy.getIocProcessors().add(new NoArgumentMethodIocProcessor(method));
		}

		List<Field> autowrites = MapperUtils.getMapper().getFields(targetClass, null, new FieldFilter() {

			public boolean accept(Field field) {
				if (!field.isSupportSetter()) {
					return false;
				}

				AnnotatedElement annotatedElement = field.getSetter().getAnnotatedElement();
				if (AnnotationUtils.isDeprecated(annotatedElement)) {
					return false;
				}
				return true;
			}
		});

		for (Field field : autowrites) {
			AnnotatedElement annotatedElement = field.getSetter().getAnnotatedElement();
			Autowired autowired = annotatedElement.getAnnotation(Autowired.class);
			if (autowired != null) {
				this.dependence.getIocProcessors().add(new AutowiredIocProcessor(field));
			}

			Config config = annotatedElement.getAnnotation(Config.class);
			if (config != null) {
				this.dependence.getIocProcessors().add(new ConfigIocProcessor(field));
			}

			Value value = annotatedElement.getAnnotation(Value.class);
			if (value != null) {
				this.dependence.getIocProcessors().add(new ValueIocProcessor(field));
			}
		}
	}

	public IocMetadata getInit() {
		return init;
	}

	public IocMetadata getDestroy() {
		return destroy;
	}

	public IocMetadata getDependence() {
		return dependence;
	}

	public static Ioc forClass(Class<?> clazz) {
		Ioc ioc = iocCache.get(clazz);
		if (ioc == null) {
			ioc = new Ioc(clazz);
			Ioc old = iocCache.putIfAbsent(clazz, ioc);
			if (old != null) {
				ioc = old;
			}
		}
		return ioc;
	}
}
