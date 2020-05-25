package scw.beans.ioc;

import java.lang.reflect.Modifier;

import scw.mapper.Field;
import scw.mapper.MapperUtils;

public abstract class FieldIocProcessor extends AbstractIocProcessor {

	public abstract Field getField();

	public boolean isGlobal() {
		return Modifier.isStatic(getField().getSetter().getModifiers());
	}

	public void checkField() {
		if (Modifier.isStatic(getField().getSetter().getModifiers())) {
			logger.warn("class [{}] field [{}] is a static", getField()
					.getSetter().getDeclaringClass(), getField().getSetter()
					.getName());
		}
	}

	protected void existDefaultValueWarnLog(Object obj) throws Exception {
		if (MapperUtils.isExistValue(getField(), obj)) {
			logger.warn("class[{}] fieldName[{}] existence default value",
					getField().getSetter().getDeclaringClass().getName(),
					getField().getSetter().getName());
		}
	}
}
