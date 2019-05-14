package scw.core;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.cglib.proxy.Enhancer;
import scw.core.utils.ClassUtils;

/**
 * 封装一个类的信息
 * 
 * @author shuchaowen
 *
 */
public final class ClassInfo {
	/**
	 * 类
	 */
	private final Class<?> source;
	/**
	 * 类的字段
	 */
	private final Map<String, FieldInfo> fieldMap;
	private final Map<String, FieldInfo> fieldSetterMethodMap;
	private final Class<?>[] beanListenInterfaces;
	private final boolean serializer;

	public ClassInfo(Class<?> clazz) {
		this.source = clazz;
		this.serializer = Serializable.class.isAssignableFrom(clazz);
		if (BeanFieldListen.class.isAssignableFrom(source)) {
			beanListenInterfaces = source.getInterfaces();
		} else {// 没有自己实现此接口，增加此接口
			Class<?>[] arr = source.getInterfaces();
			if (arr.length == 0) {
				beanListenInterfaces = new Class[] { BeanFieldListen.class };
			} else {
				beanListenInterfaces = new Class[arr.length + 1];
				System.arraycopy(arr, 0, beanListenInterfaces, 0, arr.length);
				beanListenInterfaces[arr.length] = BeanFieldListen.class;
			}
		}

		Map<String, FieldInfo> fieldSetterMethodMap = new HashMap<String, FieldInfo>();
		fieldMap = new LinkedHashMap<String, FieldInfo>(clazz.getDeclaredFields().length, 1);
		for (Field field : clazz.getDeclaredFields()) {
			Deprecated deprecated = field.getAnnotation(Deprecated.class);
			if (deprecated != null) {
				continue;
			}

			field.setAccessible(true);
			FieldInfo fieldInfo = new FieldInfo(clazz, field);
			fieldMap.put(field.getName(), fieldInfo);

			if (fieldInfo.getSetter() != null) {
				fieldSetterMethodMap.put(fieldInfo.getSetter().getName(), fieldInfo);
			}
		}
		this.fieldSetterMethodMap = new HashMap<String, FieldInfo>(fieldSetterMethodMap.size(), 1);
		this.fieldSetterMethodMap.putAll(fieldSetterMethodMap);
	}

	public boolean isSerializer() {
		return serializer;
	}

	public Class<?> getSource() {
		return source;
	}

	public FieldInfo getFieldInfo(String fieldName, boolean searchSuper) {
		if (searchSuper) {
			ClassInfo classInfo = this;
			FieldInfo fieldInfo = classInfo.getFieldInfo(fieldName, false);
			while (fieldInfo == null) {
				classInfo = classInfo.getSuperInfo();
				if (classInfo == null) {
					break;
				}

				fieldInfo = classInfo.getFieldInfo(fieldName, false);
			}
			return fieldInfo;
		} else {
			return fieldMap.get(fieldName);
		}
	}

	public FieldInfo getFieldInfoBySetterName(String setterName, boolean searchSuper) {
		if (searchSuper) {
			ClassInfo classInfo = this;
			FieldInfo fieldInfo = classInfo.getFieldInfoBySetterName(setterName, false);
			while (fieldInfo == null) {
				classInfo = classInfo.getSuperInfo();
				if (classInfo == null) {
					break;
				}

				fieldInfo = classInfo.getFieldInfoBySetterName(setterName, false);
			}
			return fieldInfo;
		} else {
			return fieldSetterMethodMap.get(setterName);
		}
	}

	public ClassInfo getSuperInfo() {
		Class<?> superClz = source.getSuperclass();
		return superClz == null ? null : ClassUtils.getClassInfo(superClz);
	}

	@SuppressWarnings("unchecked")
	public <T> T newFieldListenInstance() {
		Enhancer enhancer = new Enhancer();
		enhancer.setInterfaces(beanListenInterfaces);
		enhancer.setCallback(new FieldListenMethodInterceptor());
		enhancer.setSuperclass(source);
		if (serializer) {
			enhancer.setSerialVersionUID(1L);
		}

		BeanFieldListen beanFieldListen = (BeanFieldListen) enhancer.create();
		beanFieldListen.start_field_listen();
		return (T) beanFieldListen;
	}

	public Set<Entry<String, FieldInfo>> getFieldEntrySet() {
		return fieldMap.entrySet();
	}

	public Collection<FieldInfo> getFieldInfos() {
		return fieldMap.values();
	}

	@SuppressWarnings("unchecked")
	public Class<? extends BeanFieldListen> getFieldListenProxyClass() {
		Enhancer enhancer = new Enhancer();
		enhancer.setInterfaces(beanListenInterfaces);
		enhancer.setCallbackType(FieldListenMethodInterceptor.class);
		enhancer.setSuperclass(source);
		if (serializer) {
			enhancer.setSerialVersionUID(1L);
		}
		return enhancer.createClass();
	}

}