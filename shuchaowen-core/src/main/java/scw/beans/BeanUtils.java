package scw.beans;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import scw.aop.Filter;
import scw.beans.annotation.Bean;
import scw.beans.xml.XmlBeanParameter;
import scw.core.GlobalPropertyFactory;
import scw.core.instance.InstanceFactory;
import scw.core.instance.InstanceUtils;
import scw.core.parameter.ParameterUtils;
import scw.core.utils.ObjectUtils;
import scw.core.utils.StringUtils;
import scw.util.value.property.PropertyFactory;

public final class BeanUtils {
	private BeanUtils() {
	};

	private static XmlBeanParameter[] sortParameters(String[] paramNames, Class<?>[] parameterTypes,
			XmlBeanParameter[] beanMethodParameters) {
		XmlBeanParameter[] methodParameters = new XmlBeanParameter[beanMethodParameters.length];
		Class<?>[] types = new Class<?>[methodParameters.length];
		for (int i = 0; i < methodParameters.length; i++) {
			XmlBeanParameter beanMethodParameter = beanMethodParameters[i].clone();
			if (!StringUtils.isNull(beanMethodParameter.getDisplayName())) {
				for (int a = 0; a < paramNames.length; a++) {
					if (paramNames[a].equals(beanMethodParameter.getDisplayName())) {
						types[a] = parameterTypes[a];
						methodParameters[a] = beanMethodParameters[i].clone();
						methodParameters[a].setType(parameterTypes[a]);
					}
				}
			} else if (beanMethodParameter.getType() != null) {
				methodParameters[i] = beanMethodParameter;
				types[i] = beanMethodParameter.getType();
			} else {
				types[i] = parameterTypes[i];
				methodParameters[i] = beanMethodParameter;
				methodParameters[i].setType(types[i]);
			}
		}

		return ObjectUtils.equals(Arrays.asList(parameterTypes), Arrays.asList(types)) ? methodParameters : null;
	}

	/**
	 * 对参数重新排序
	 * 
	 * @param executable
	 * @param beanMethodParameters
	 * @return
	 */
	public static XmlBeanParameter[] sortParameters(Method method, XmlBeanParameter[] beanMethodParameters) {
		if (method.getParameterTypes().length != beanMethodParameters.length) {
			return null;
		}

		return sortParameters(ParameterUtils.getParameterName(method), method.getParameterTypes(),
				beanMethodParameters);
	}

	public static XmlBeanParameter[] sortParameters(Constructor<?> constructor,
			XmlBeanParameter[] beanMethodParameters) {
		if (constructor.getParameterTypes().length != beanMethodParameters.length) {
			return null;
		}

		return sortParameters(ParameterUtils.getParameterName(constructor), constructor.getParameterTypes(),
				beanMethodParameters);
	}

	public static Object[] getBeanMethodParameterArgs(XmlBeanParameter[] beanParameters,
			InstanceFactory instanceFactory, PropertyFactory propertyFactory) throws Exception {
		Object[] args = new Object[beanParameters.length];
		for (int i = 0; i < args.length; i++) {
			XmlBeanParameter xmlBeanParameter = beanParameters[i];
			args[i] = xmlBeanParameter.parseValue(instanceFactory, propertyFactory);
		}
		return args;
	}

	public static boolean isSingletion(Class<?> type, AnnotatedElement annotatedElement) {
		Bean bean = annotatedElement.getAnnotation(Bean.class);
		return bean == null ? true : bean.singleton();
	}

	public static boolean isProxy(Class<?> type, AnnotatedElement annotatedElement) {
		if (Modifier.isFinal(type.getModifiers())) {// final修饰的类无法代理
			return false;
		}

		if (Filter.class.isAssignableFrom(type)) {
			return false;
		}

		Bean bean = annotatedElement.getAnnotation(Bean.class);
		if (bean == null) {
			return true;
		}
		return bean.proxy();
	}

	public static String getScanAnnotationPackageName() {
		return GlobalPropertyFactory.getInstance().getValue("scw.scan.beans.package", String.class,
				InstanceUtils.getScanAnnotationPackageName());
	}
}
