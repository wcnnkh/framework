package scw.beans;

import java.lang.reflect.Modifier;

import scw.core.reflect.FieldDefinition;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public abstract class AbstractBeanField implements BeanField {
	protected final Logger logger = LoggerUtils.getLogger(getClass());

	protected final FieldDefinition fieldDefinition;

	public AbstractBeanField(FieldDefinition fieldDefinition) {
		this.fieldDefinition = fieldDefinition;
		if (fieldDefinition != null) {
			checkField(fieldDefinition);
		}
	}

	public FieldDefinition getFieldDefinition() {
		return fieldDefinition;
	}

	protected void checkField(FieldDefinition fieldDefinition) {
		if (Modifier.isStatic(fieldDefinition.getField().getModifiers())) {
			logger.warn("class [{}] field [{}] is a static", fieldDefinition.getDeclaringClass(),
					fieldDefinition.getField().getName());
		}
	}

	protected void existDefaultValueWarnLog(Object obj) throws Exception {
		if (checkExistDefaultValue(obj)) {
			logger.warn("class[{}] fieldName[{}] existence default value",
					fieldDefinition.getDeclaringClass().getName(), fieldDefinition.getField().getName());
		}
	}

	protected boolean checkExistDefaultValue(Object obj) throws Exception {
		if (fieldDefinition.getField().getType().isPrimitive()) {// 值类型一定是有默认值的,所以不用判断直接所回false
			return false;
		}
		return fieldDefinition.get(obj) != null;
	}
}
