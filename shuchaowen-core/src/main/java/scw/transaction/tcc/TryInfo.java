package scw.transaction.tcc;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;

import scw.core.parameter.ParameterUtils;
import scw.core.utils.StringUtils;
import scw.transaction.tcc.annotation.TccStage;

public class TryInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private final Class<?> targetClass;
	private final Object tryResult;
	private final LinkedHashMap<String, Object> parameterMap;

	public TryInfo(Class<?> targetClass, Object tryResult, Method method, Object[] args) {
		this(targetClass, tryResult, ParameterUtils.getParameterMap(method, args));
	}

	public TryInfo(Class<?> targetClass, Object tryResult, LinkedHashMap<String, Object> parameterMap) {
		this.targetClass = targetClass;
		this.tryResult = tryResult;
		this.parameterMap = parameterMap;
	}

	public Class<?> getTargetClass() {
		return targetClass;
	}

	public Object getTryResult() {
		return tryResult;
	}

	public LinkedHashMap<String, Object> getParameterMap() {
		return parameterMap;
	}

	public Method getStageMethod(String stageName) {
		if (StringUtils.isEmpty(stageName)) {
			return null;
		}

		for (Method method : targetClass.getDeclaredMethods()) {
			TccStage tccStage = method.getAnnotation(TccStage.class);
			if (tccStage == null) {
				continue;
			}

			String name = StringUtils.isEmpty(tccStage.value()) ? method.getName() : tccStage.value();
			if (stageName.equals(name)) {
				return method;
			}
		}
		return null;
	}
}
