package scw.beans.ioc;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

import scw.beans.annotation.Autowired;
import scw.beans.annotation.Destroy;
import scw.beans.annotation.InitMethod;
import scw.beans.annotation.Value;
import scw.core.annotation.AnnotationUtils;
import scw.mapper.Field;
import scw.mapper.FilterFeature;
import scw.mapper.MapperUtils;
import scw.util.ConcurrentReferenceHashMap;

public final class Ioc {
	private final IocMetadata init = new IocMetadata();
	private final IocMetadata destroy = new IocMetadata();
	private final IocMetadata dependence = new IocMetadata();

	public Ioc() {
	};

	private Ioc(Class<?> targetClass) {
		for (Method method : AnnotationUtils.getAnnoationMethods(targetClass, true, true, InitMethod.class)) {
			method.setAccessible(true);
			init.getIocProcessors().add(new NoArgumentMethodIocProcessor(method));
		}

		for (Method method : AnnotationUtils.getAnnoationMethods(targetClass, true, true, Destroy.class)) {
			method.setAccessible(true);
			destroy.getIocProcessors().add(new NoArgumentMethodIocProcessor(method));
		}

		for (Field field : MapperUtils.getMapper().getFields(targetClass, FilterFeature.SUPPORT_SETTER)) {
			AnnotatedElement annotatedElement = field.getSetter().getAnnotatedElement();
			Autowired autowired = annotatedElement.getAnnotation(Autowired.class);
			if (autowired != null) {
				this.dependence.getIocProcessors().add(new AutowiredIocProcessor(field));
			}

			Value value = annotatedElement.getAnnotation(Value.class);
			if (value != null) {
				this.dependence.getIocProcessors().add(new ValueIocProcessor(field));
			}
		}

		readyOnly();
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

	public void readyOnly() {
		init.readyOnly();
		dependence.readyOnly();
		destroy.readyOnly();
	}

	private static ConcurrentReferenceHashMap<Class<?>, Ioc> iocCache = new ConcurrentReferenceHashMap<Class<?>, Ioc>();

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
