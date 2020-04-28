package scw.beans.ioc;

import java.lang.reflect.Modifier;

import scw.core.reflect.FieldDefinition;

public abstract class FieldIocProcessor extends AbstractIocProcessor {

	public abstract FieldDefinition getFieldDefinition();

	public boolean isGlobal() {
		return Modifier
				.isStatic(getFieldDefinition().getField().getModifiers());
	}

	public void checkField() {
		if (Modifier.isStatic(getFieldDefinition().getField().getModifiers())) {
			logger.warn("class [{}] field [{}] is a static",
					getFieldDefinition().getDeclaringClass(),
					getFieldDefinition().getField().getName());
		}
	}

	protected void existDefaultValueWarnLog(Object obj) throws Exception {
		if (checkExistDefaultValue(obj)) {
			logger.warn("class[{}] fieldName[{}] existence default value",
					getFieldDefinition().getDeclaringClass().getName(),
					getFieldDefinition().getField().getName());
		}
	}

	protected boolean checkExistDefaultValue(Object obj) throws Exception {
		if (getFieldDefinition().getField().getType().isPrimitive()) {// 值类型一定是有默认值的,所以不用判断直接所回false
			return false;
		}
		return getFieldDefinition().get(obj) != null;
	}
}
