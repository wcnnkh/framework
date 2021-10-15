package io.basc.framework.context.annotation;

import java.lang.reflect.AnnotatedElement;
import java.util.Map;

import io.basc.framework.core.type.classreading.MetadataReader;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.StringUtils;
import io.basc.framework.value.PropertyFactory;

public class EnableConditionUtils {
	private static Logger logger = LoggerFactory.getLogger(EnableConditionUtils.class);
	private EnableConditionUtils() {
	}
	
	public static boolean enable(AnnotatedElement annotatedElement, PropertyFactory propertyFactory) {
		EnableCondition enableCondition = annotatedElement.getAnnotation(EnableCondition.class);
		if(enableCondition == null) {
			return true;
		}
		
		String condition = enableCondition.condition();
		if(StringUtils.isEmpty(condition)) {
			if(logger.isTraceEnabled()) {
				logger.trace("[{}] condition is empty", annotatedElement);
			}
			return false;
		}
		
		String value = propertyFactory.getString(condition);
		String conditionValue = enableCondition.value();
		if(!conditionValue.equals(value)) {
			if(logger.isTraceEnabled()) {
				logger.trace("[{}] condition {} value {} not is {}", annotatedElement, condition, value, conditionValue);
			}
			return false;
		}
		return true;
	}
	
	public static boolean enable(MetadataReader metadataReader, PropertyFactory propertyFactory) {
		Map<String, Object> attributeMap = metadataReader.getAnnotationMetadata().getAnnotationAttributes(EnableCondition.class.getName());
		if(attributeMap == null) {
			return true;
		}

		String condition = (String) attributeMap.get("condition");
		if(StringUtils.isEmpty(condition)) {
			if(logger.isTraceEnabled()) {
				logger.trace("[{}] condition is empty", metadataReader.getClassMetadata().getClassName());
			}
			return false;
		}
		
		String value = propertyFactory.getString(condition);
		Object conditionValue = attributeMap.get("value");
		if(!ObjectUtils.nullSafeEquals(conditionValue, value)) {			
			if(logger.isTraceEnabled()) {
				logger.trace("[{}] condition {} value {} not is {}", metadataReader.getClassMetadata().getClassName(), condition, value, conditionValue);
			}
			return false;
		}
		return true;
	}
}
