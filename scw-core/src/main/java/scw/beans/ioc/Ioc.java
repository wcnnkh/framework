package scw.beans.ioc;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.NoSuchElementException;

import scw.beans.annotation.Autowired;
import scw.beans.annotation.Destroy;
import scw.beans.annotation.InitMethod;
import scw.beans.annotation.Value;
import scw.core.annotation.AnnotationUtils;
import scw.core.parameter.ParameterUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.mapper.Field;
import scw.mapper.FilterFeature;
import scw.mapper.MapperUtils;
import scw.util.AbstractIterator;
import scw.util.ConcurrentReferenceHashMap;

public final class Ioc {
	private static Logger logger = LoggerUtils.getLogger(Ioc.class);
	private final IocMetadata init = new IocMetadata();
	private final IocMetadata destroy = new IocMetadata();
	private final IocMetadata dependence = new IocMetadata();

	public Ioc() {
	};

	private Ioc(Class<?> targetClass) {
		for (Method method : AnnotationUtils.getAnnoationMethods(targetClass, false, true, InitMethod.class)) {
			method.setAccessible(true);
			init.getIocProcessors().add(new NoArgumentMethodIocProcessor(method));
		}

		for (Method method : AnnotationUtils.getAnnoationMethods(targetClass, false, true, Destroy.class)) {
			method.setAccessible(true);
			destroy.getIocProcessors().add(new NoArgumentMethodIocProcessor(method));
		}

		for (Field field : MapperUtils.getMapper().getFields(targetClass, false, FilterFeature.SUPPORT_SETTER)) {
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

		for (Method method : AnnotationUtils.getAnnoationMethods(targetClass, false, true, Value.class)) {
			if (method.getParameterTypes().length != 1) {
				logger.error("@Value method one parameter must exis: {}", method);
				continue;
			}

			Field field = new Field(null, targetClass, ParameterUtils.getParameterNames(method)[0], null, null, method);
			this.dependence.getIocProcessors().add(new ValueIocProcessor(field));
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

	public static Iterable<Ioc> forClass(Class<?> clazz) {
		return new IocIterable(clazz);
	}

	private static final class IocIterable implements Iterable<Ioc> {
		private Class<?> clazz;

		public IocIterable(Class<?> clazz) {
			this.clazz = clazz;
		}

		public Iterator<Ioc> iterator() {
			return new IocIterator(clazz);
		}
	}

	private static final class IocIterator extends AbstractIterator<Ioc> {
		private Class<?> clazz;

		public IocIterator(Class<?> clazz) {
			this.clazz = clazz;
		}

		public boolean hasNext() {
			if (clazz == null || clazz == Object.class) {
				return false;
			}

			return true;
		}

		public Ioc next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}

			Ioc ioc = iocCache.get(clazz);
			if (ioc == null) {
				ioc = new Ioc(clazz);
				Ioc old = iocCache.putIfAbsent(clazz, ioc);
				if (old != null) {
					ioc = old;
				}
			}

			this.clazz = clazz.getSuperclass();
			return ioc;
		}
	}
}
