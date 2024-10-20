package io.basc.framework.autoconfigure.beans.factory;

import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Map;

import io.basc.framework.convert.lang.ValueFactory;
import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.core.type.classreading.MetadataReader;
import io.basc.framework.env.PropertyFactory;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LogManager;
import io.basc.framework.util.match.StringMatchers;

public class EnableConditionUtils {
	private static Logger logger = LogManager.getLogger(EnableConditionUtils.class);

	private EnableConditionUtils() {
	}

	private static boolean enable(String[] patterns, String value) {
		for (String pattern : patterns) {
			if (StringMatchers.SIMPLE.match(pattern, value)) {
				return true;
			}
		}
		return false;
	}

	public static boolean enable(AnnotatedElement annotatedElement, PropertyFactory propertyFactory) {
		EnableCondition enableCondition = AnnotatedElementUtils.getMergedAnnotation(annotatedElement,
				EnableCondition.class);
		if (enableCondition == null) {
			return true;
		}

		String condition = enableCondition.condition();
		if (StringUtils.isEmpty(condition)) {
			if (logger.isTraceEnabled()) {
				logger.trace("[{}] condition is empty", annotatedElement);
			}
			return false;
		}

		String value = propertyFactory.getAsString(condition);
		String[] values = enableCondition.value();
		if (enable(values, value)) {
			return true;
		}

		if (logger.isTraceEnabled()) {
			logger.trace("[{}] condition {} value {} not is {}", annotatedElement, condition, value,
					Arrays.asList(values));
		}
		return false;
	}

	public static boolean enable(MetadataReader metadataReader, ValueFactory<String> propertyFactory) {
		Map<String, Object> attributeMap = metadataReader.getAnnotationMetadata()
				.getAnnotationAttributes(EnableCondition.class.getName());
		if (attributeMap == null) {
			return true;
		}

		String condition = (String) attributeMap.get("condition");
		if (StringUtils.isEmpty(condition)) {
			if (logger.isTraceEnabled()) {
				logger.trace("[{}] condition is empty", metadataReader.getClassMetadata().getClassName());
			}
			return false;
		}

		String value = propertyFactory.getAsString(condition);
		String[] values = (String[]) attributeMap.get("value");
		if (enable(values, value)) {
			return true;
		}

		if (logger.isTraceEnabled()) {
			logger.trace("[{}] condition {} value {} not is {}", metadataReader.getClassMetadata().getClassName(),
					condition, value, values);
		}
		return false;
	}
}
