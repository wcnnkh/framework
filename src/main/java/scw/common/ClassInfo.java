package scw.common;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scw.common.utils.ClassUtils;

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

	public ClassInfo(Class<?> clz){
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
		
		if(Serializable.class.isAssignableFrom(clz)){
			try {
				Field field = clz.getDeclaredField(SerialVersionUID_FIELD_NAME);
				if(Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())){
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
}