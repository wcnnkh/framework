package scw.core.reflect;

import java.io.Serializable;
import java.lang.reflect.Field;

public class SerializableField implements Serializable{
	private static final long serialVersionUID = 1L;
	private volatile transient Field field;
	private final Class<?> targetClass;
	private final String fieldName;
	
	public SerializableField(Class<?> targetClass, Field field){
		this.field = field;
		this.targetClass = targetClass;
		this.fieldName = field == null? null:field.getName();
	}
	
	public Field getField(){
		if(field == null){
			synchronized (this) {
				if(field == null){
					field = ReflectionUtils.findField(targetClass, fieldName);
				}
			}
		}
		return field;
	}

	public Class<?> getTargetClass() {
		return targetClass;
	}
	
	public String getFieldName() {
		return fieldName;
	}
}
