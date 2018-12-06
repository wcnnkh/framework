package shuchaowen.common;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.cglib.proxy.Enhancer;
import shuchaowen.common.utils.ClassUtils;
import shuchaowen.core.beans.BeanFieldListen;
import shuchaowen.core.beans.BeanFilter;
import shuchaowen.core.beans.BeanMethodInterceptor;

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
	private Class<?>[] beanListenInterfaces;
	private Long serialVersionUID;

	public ClassInfo(Class<?> clz){
		this.clz = clz;
		this.name = clz.getName();
		this.simpleName = clz.getSimpleName();

		Class<?>[] arr = clz.getInterfaces();
		beanListenInterfaces = new Class<?>[arr == null ? 1 : arr.length + 1];
		System.arraycopy(arr, 0, beanListenInterfaces, 0, arr.length);
		beanListenInterfaces[beanListenInterfaces.length - 1] = BeanFieldListen.class;

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

	public Class<?>[] getBeanListenInterfaces() {
		return beanListenInterfaces;
	}

	public Long getSerialVersionUID() {
		return serialVersionUID;
	}
	
	public Enhancer createEnhancer(Class<?>[] interfaces, List<BeanFilter> beanFilterList){
		Enhancer enhancer = new Enhancer();
		Class<?>[] newArr;
		if(interfaces != null && interfaces.length != 0){
			newArr = new Class<?>[beanListenInterfaces.length + interfaces.length];
			System.arraycopy(beanListenInterfaces, 0, newArr, 0, beanListenInterfaces.length);
			System.arraycopy(interfaces, 0, newArr, beanListenInterfaces.length, interfaces.length);
		}else{
			newArr = beanListenInterfaces;
		}
		
		if(newArr.length != 0){
			enhancer.setInterfaces(newArr);
		}
		
		if(serialVersionUID != null){
			enhancer.setSerialVersionUID(serialVersionUID);
		}
		
		enhancer.setCallback(new BeanMethodInterceptor(beanFilterList));
		enhancer.setSuperclass(clz);
		return enhancer;
	}
	
	/**
	 * 可以监听属性变化
	 * @return
	 */
	public Object newFieldListenInstance(){
		return createEnhancer(null, null).create();
	}
	
	public Class<?> getFieldListenProxyClass(){
		Enhancer enhancer = new Enhancer();
		if(!BeanFieldListen.class.isAssignableFrom(clz)){
			enhancer.setInterfaces(beanListenInterfaces);
		}
		
		if(serialVersionUID != null){
			enhancer.setSerialVersionUID(serialVersionUID);
		}
		
		enhancer.setCallbackType(BeanMethodInterceptor.class);
		enhancer.setSuperclass(clz);
		return enhancer.createClass();
	}
}