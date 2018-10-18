package shuchaowen.core.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 封装一个类的信息
 * @author shuchaowen
 *
 */
public final class ClassInfo {
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
	private Class<?> clz;
	/**
	 * 类的字段
	 */
	private String[] fieldNames;
	private Map<String, FieldInfo> fieldMap = new HashMap<String, FieldInfo>();

	private ClassInfo superInfo;// 父类信息

	public ClassInfo(Class<?> clz) {
		this.clz = clz;
		this.name = clz.getName();
		this.simpleName = clz.getSimpleName();
		List<String> fieldNameList = new ArrayList<String>();
		for (Field field : clz.getDeclaredFields()) {
			Deprecated deprecated = field.getAnnotation(Deprecated.class);
			if(deprecated != null){
				continue;
			}
			
			field.setAccessible(true);
			fieldNameList.add(field.getName());
			FieldInfo fieldInfo = new FieldInfo(clz, field);
			this.fieldMap.put(field.getName(), fieldInfo);
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
	
	public FieldInfo getFieldInfo(String fieldName){
		ClassInfo classInfo = this;
		FieldInfo fieldInfo = classInfo.getFieldMap().get(fieldName);
		while(fieldInfo == null){
			classInfo = classInfo.getSuperInfo();
			if(classInfo == null){
				break;
			}
			
			fieldInfo = classInfo.getFieldMap().get(fieldName);
		}
		return fieldInfo;
	}

	public ClassInfo getSuperInfo() {
		return superInfo;
	}
}