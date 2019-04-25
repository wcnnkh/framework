package scw.core;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.cglib.proxy.Enhancer;
import scw.core.utils.ClassUtils;

/**
 * 封装一个类的信息
 * 
 * @author shuchaowen
 *
 */
public final class ClassInfo {
	private static final String SerialVersionUID_FIELD_NAME = "serialVersionUID";

	/**
	 * 类全名 xx.xx.xx
	 */
	private String name;
	/**
	 * 类名
	 */
	private String simpleName;
	/**
	 * 类
	 */
	private final Class<?> clz;
	/**
	 * 类的字段
	 */
	private String[] fieldNames;
	private Map<String, FieldInfo> fieldMap = new HashMap<String, FieldInfo>();
	private Map<String, FieldInfo> fieldSetterMethodMap = new HashMap<String, FieldInfo>();

	private ClassInfo superInfo;// 父类信息
	private Long serialVersionUID;
	private final Class<?>[] beanListenInterfaces;

	public ClassInfo(Class<?> clz) {
		if (BeanFieldListen.class.isAssignableFrom(clz)) {
			beanListenInterfaces = clz.getInterfaces();
		} else {// 没有自己实现此接口，增加此接口
			Class<?>[] arr = clz.getInterfaces();
			if (arr.length == 0) {
				beanListenInterfaces = new Class[] { BeanFieldListen.class };
			} else {
				beanListenInterfaces = new Class[arr.length + 1];
				System.arraycopy(arr, 0, beanListenInterfaces, 0, arr.length);
				beanListenInterfaces[arr.length] = BeanFieldListen.class;
			}
		}

		this.clz = clz;
		this.name = clz.getName();
		this.simpleName = clz.getSimpleName();

		List<String> fieldNameList = new ArrayList<String>();
		for (Field field : clz.getDeclaredFields()) {
			Deprecated deprecated = field.getAnnotation(Deprecated.class);
			if (deprecated != null) {
				continue;
			}

			field.setAccessible(true);
			fieldNameList.add(field.getName());
			FieldInfo fieldInfo = new FieldInfo(clz, field);
			this.fieldMap.put(field.getName(), fieldInfo);

			if (fieldInfo.getSetter() != null) {
				fieldSetterMethodMap.put(fieldInfo.getSetter().getName(), fieldInfo);
			}
		}

		if (Serializable.class.isAssignableFrom(clz)) {
			try {
				Field field = clz.getDeclaredField(SerialVersionUID_FIELD_NAME);
				if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
					field.setAccessible(true);
					serialVersionUID = (Long) field.get(null);
				}
			} catch (Exception e) {
			}
		}

		this.fieldNames = fieldNameList.toArray(new String[0]);
		Class<?> superClz = clz.getSuperclass();
		if (superClz != null) {
			superInfo = ClassUtils.getClassInfo(superClz);
		}
	}

	public String getName() {
		return name;
	}

	public String getSimpleName() {
		return simpleName;
	}

	public Class<?> getClz() {
		return clz;
	}

	public String[] getFieldNames() {
		return fieldNames;
	}

	public Map<String, FieldInfo> getFieldMap() {
		return fieldMap;
	}

	public FieldInfo getFieldInfo(String fieldName) {
		ClassInfo classInfo = this;
		FieldInfo fieldInfo = classInfo.getFieldMap().get(fieldName);
		while (fieldInfo == null) {
			classInfo = classInfo.getSuperInfo();
			if (classInfo == null) {
				break;
			}

			fieldInfo = classInfo.getFieldMap().get(fieldName);
		}
		return fieldInfo;
	}

	public FieldInfo getFieldInfoBySetterName(String setterName) {
		ClassInfo classInfo = this;
		FieldInfo fieldInfo = classInfo.getFieldSetterMethodMap().get(setterName);
		while (fieldInfo == null) {
			classInfo = classInfo.getSuperInfo();
			if (classInfo == null) {
				break;
			}

			fieldInfo = classInfo.getFieldSetterMethodMap().get(setterName);
		}
		return fieldInfo;
	}

	protected Map<String, FieldInfo> getFieldSetterMethodMap() {
		return fieldSetterMethodMap;
	}

	public ClassInfo getSuperInfo() {
		return superInfo;
	}

	public Long getSerialVersionUID() {
		return serialVersionUID;
	}

	private Enhancer createEnhacer() {
		Enhancer enhancer = new Enhancer();
		enhancer.setInterfaces(beanListenInterfaces);
		if (BeanFieldListen.class.isAssignableFrom(clz)) {
			if (serialVersionUID != null) {
				enhancer.setSerialVersionUID(serialVersionUID);
			}
		} else {
			enhancer.setSerialVersionUID(1L);
		}

		enhancer.setCallback(new FieldListenMethodInterceptor());
		enhancer.setSuperclass(clz);
		return enhancer;
	}

	public Object newFieldListenInstance() {
		BeanFieldListen beanFieldListen = (BeanFieldListen) createEnhacer().create();
		beanFieldListen.start_field_listen();
		return beanFieldListen;
	}

	public Object newFieldListenInstance(Class<?>[] parameterTypes, Object... args) {
		BeanFieldListen beanFieldListen = (BeanFieldListen) createEnhacer().create(parameterTypes, args);
		beanFieldListen.start_field_listen();
		return beanFieldListen;
	}

	public Class<?> createFieldListenProxyClass() {
		Enhancer enhancer = new Enhancer();
		enhancer.setInterfaces(beanListenInterfaces);
		if (BeanFieldListen.class.isAssignableFrom(clz)) {
			if (serialVersionUID != null) {
				enhancer.setSerialVersionUID(serialVersionUID);
			}
		} else {
			enhancer.setSerialVersionUID(1L);
		}

		enhancer.setCallbackType(FieldListenMethodInterceptor.class);
		enhancer.setSuperclass(clz);
		return enhancer.createClass();
	}

}