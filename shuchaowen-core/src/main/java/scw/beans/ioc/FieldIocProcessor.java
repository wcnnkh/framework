package scw.beans.ioc;

import java.lang.reflect.Modifier;

import scw.mapper.FieldContext;

public abstract class FieldIocProcessor extends AbstractIocProcessor {

	public abstract FieldContext getFieldContext();

	public boolean isGlobal() {
		return Modifier
				.isStatic(getFieldContext().getField().getSetter().getModifiers());
	}

	public void checkField() {
		if (Modifier.isStatic(getFieldContext().getField().getSetter().getModifiers())) {
			logger.warn("class [{}] field [{}] is a static",
					getFieldContext().getField().getSetter().getDeclaringClass(),
					getFieldContext().getField().getSetter().getName());
		}
	}

	protected void existDefaultValueWarnLog(Object obj) throws Exception {
		if (checkExistDefaultValue(obj)) {
			logger.warn("class[{}] fieldName[{}] existence default value",
					getFieldContext().getField().getSetter().getDeclaringClass().getName(),
					getFieldContext().getField().getSetter().getName());
		}
	}

	protected boolean checkExistDefaultValue(Object obj) throws Exception {
		if(!getFieldContext().getField().isSupportGetter()){
			return false;
		}
		
		if (getFieldContext().getField().getGetter().getType().isPrimitive()) {// 值类型一定是有默认值的,所以不用判断直接所回false
			return false;
		}
		return getFieldContext().getField().getGetter().get(obj) != null;
	}
}
