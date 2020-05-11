package scw.beans.ioc;

import java.lang.reflect.Modifier;

import scw.mapper.Field;

public abstract class FieldIocProcessor extends AbstractIocProcessor {

	public abstract Field getField();

	public boolean isGlobal() {
		return Modifier
				.isStatic(getField().getSetter().getModifiers());
	}

	public void checkField() {
		if (Modifier.isStatic(getField().getSetter().getModifiers())) {
			logger.warn("class [{}] field [{}] is a static",
					getField().getSetter().getDeclaringClass(),
					getField().getSetter().getName());
		}
	}

	protected void existDefaultValueWarnLog(Object obj) throws Exception {
		if (checkExistDefaultValue(obj)) {
			logger.warn("class[{}] fieldName[{}] existence default value",
					getField().getSetter().getDeclaringClass().getName(),
					getField().getSetter().getName());
		}
	}

	protected boolean checkExistDefaultValue(Object obj) throws Exception {
		if(!getField().isSupportGetter()){
			return false;
		}
		
		if (getField().getGetter().getType().isPrimitive()) {// 值类型一定是有默认值的,所以不用判断直接所回false
			return false;
		}
		
		return getField().getGetter().get(obj) != null;
	}
}
