package scw.mvc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.core.Destroy;
import scw.core.ValueFactory;
import scw.core.context.Context;
import scw.core.context.ContextManager;
import scw.core.context.support.ThreadLocalContextManager;
import scw.core.exception.BeansException;
import scw.core.instance.InstanceUtils;
import scw.core.reflect.ReflectUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.XUtils;
import scw.mvc.annotation.Model;
import scw.mvc.annotation.Parameter;

public final class MVCUtils {
	private static final ContextManager MVC_CONTEXT_MANAGER = new ThreadLocalContextManager(true);
	private static final String RESTURL_PATH_PARAMETER = "_resturl_path_parameter";

	public static Channel getContextChannel() {
		Context context = MVC_CONTEXT_MANAGER.getCurrentContext();
		return (Channel) (context == null ? null : context.getResource(Channel.class));
	}

	public static Context getContext() {
		return MVC_CONTEXT_MANAGER.getCurrentContext();
	}

	public static void service(Collection<Filter> filters, Channel channel) throws Throwable {
		FilterChain filterChain = new SimpleFilterChain(filters);
		Context context = MVC_CONTEXT_MANAGER.createContext();
		context.bindResource(Channel.class, channel);
		try {
			channel.write(filterChain.doFilter(channel));
		} finally {
			try {
				if (channel instanceof Destroy) {
					((Destroy) channel).destroy();
				}
			} finally {
				MVC_CONTEXT_MANAGER.release(context);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static Map<String, String> getRestPathParameterMap(AttributeManager attributeManager) {
		return (Map<String, String>) attributeManager.getAttribute(RESTURL_PATH_PARAMETER);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getParameterWrapper(ValueFactory<String> request, Class<T> type, String name) {
		try {
			return (T) privateParameterWrapper(request, type,
					StringUtils.isEmpty(name) ? null : (name.endsWith(".") ? name : name + "."));
		} catch (Exception e) {
			throw new RuntimeException("构造bean失败:" + type.getName(), e);
		}
	}

	public static void parameterWrapper(Object instance, ValueFactory<String> request, Class<?> type, String name) {
		try {
			privateParameterWrapper(instance, request, type,
					StringUtils.isEmpty(name) ? null : (name.endsWith(".") ? name : name + "."));
		} catch (Exception e) {
			throw new RuntimeException("构造bean失败:" + type.getName(), e);
		}
	}

	private static void privateParameterWrapper(Object instance, ValueFactory<String> request, Class<?> type,
			String prefix) throws Exception {
		Class<?> clz = type;
		while (clz != null && clz != Object.class) {
			for (Field field : clz.getDeclaredFields()) {
				if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
					continue;
				}

				// 已经存在值了，可能是通过其他方式注入的
				if (!field.getType().isPrimitive() && field.get(instance) != null) {
					continue;
				}

				String fieldName = field.getName();
				Parameter parameter = field.getAnnotation(Parameter.class);
				if (parameter != null && StringUtils.isNotEmpty(parameter.value())) {
					fieldName = parameter.value();
				}

				String key = StringUtils.isEmpty(prefix) ? fieldName : prefix + fieldName;
				if (String.class.isAssignableFrom(field.getType())
						|| ClassUtils.isPrimitiveOrWrapper(field.getType())) {
					// 如果是基本数据类型
					Object v = XUtils.getValue(request, key, field.getType());
					if (v != null) {
						ReflectUtils.setFieldValue(clz, field, instance, v);
					}
				} else {
					ReflectUtils.setFieldValue(clz, field, instance,
							privateParameterWrapper(request, field.getType(), key + "."));
				}
			}
			clz = clz.getSuperclass();
		}
	}

	private static Object privateParameterWrapper(ValueFactory<String> request, Class<?> type, String prefix)
			throws Exception {
		if (!ReflectUtils.isInstance(type)) {
			return null;
		}

		Object t = InstanceUtils.newInstance(type);
		privateParameterWrapper(t, request, type, prefix);
		return t;
	}

	public static Constructor<?> getModelConstructor(Class<?> type) {
		Constructor<?>[] constructors = type.getDeclaredConstructors();
		Constructor<?> constructor = null;
		if (constructors.length == 1) {
			constructor = constructors[0];
		} else {
			for (int i = 0; i < constructors.length; i++) {
				constructor = constructors[i];
				Model model = constructor.getAnnotation(Model.class);
				if (model == null) {
					continue;
				}

				break;
			}
		}
		return constructor;
	}

	public static ParameterDefinition[] getParameterDefinitions(Method method) {
		String[] names = ClassUtils.getParameterName(method);
		Annotation[][] parameterAnnoatations = method.getParameterAnnotations();
		Type[] parameterGenericTypes = method.getGenericParameterTypes();
		Class<?>[] parameterTypes = method.getParameterTypes();
		ParameterDefinition[] parameterDefinitions = new ParameterDefinition[names.length];
		for (int i = 0; i < names.length; i++) {
			parameterDefinitions[i] = new SimpleParameterDefinition(names.length, names[i], parameterAnnoatations[i],
					parameterTypes[i], parameterGenericTypes[i], i);
		}
		return parameterDefinitions;
	}

	public static ParameterDefinition[] getParameterDefinitions(Constructor<?> constructor) {
		String[] names = ClassUtils.getParameterName(constructor);
		Annotation[][] parameterAnnoatations = constructor.getParameterAnnotations();
		Type[] parameterGenericTypes = constructor.getGenericParameterTypes();
		Class<?>[] parameterTypes = constructor.getParameterTypes();
		ParameterDefinition[] parameterDefinitions = new ParameterDefinition[names.length];
		for (int i = 0; i < names.length; i++) {
			parameterDefinitions[i] = new SimpleParameterDefinition(names.length, names[i], parameterAnnoatations[i],
					parameterTypes[i], parameterGenericTypes[i], i);
		}
		return parameterDefinitions;
	}

	public static Object[] getParameterValues(Channel channel, ParameterDefinition[] parameterDefinitions,
			Collection<ParameterFilter> parameterFilters) throws Throwable {
		Object[] args = new Object[parameterDefinitions.length];
		for (int i = 0; i < parameterDefinitions.length; i++) {
			ParameterDefinition parameterDefinition = parameterDefinitions[i];
			ParameterFilterChain parameterFilterChain = new SimpleParameterParseFilterChain(parameterFilters);
			Object value = parameterFilterChain.doFilter(channel, parameterDefinitions[i]);
			if (value == null) {
				value = channel.getParameter(parameterDefinition);
			}
			args[i] = value;
		}
		return args;
	}

	public static Object getBean(BeanFactory beanFactory, BeanDefinition beanDefinition, Channel channel,
			Constructor<?> constructor, Collection<ParameterFilter> parameterFilters) {
		try {
			return beanFactory.getInstance(beanDefinition.getId(), constructor.getParameterTypes(),
					getParameterValues(channel, getParameterDefinitions(constructor), parameterFilters));
		} catch (Throwable e) {
			throw new BeansException(beanDefinition.getId());
		}
	}
}
