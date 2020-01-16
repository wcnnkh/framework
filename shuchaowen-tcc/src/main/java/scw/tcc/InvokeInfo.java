package scw.tcc;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import scw.core.instance.InstanceFactory;
import scw.core.parameter.ParameterUtils;
import scw.core.reflect.SerializableMethodHolder;
import scw.core.utils.StringUtils;
import scw.lang.NotFoundException;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.tcc.annotation.TCCStage;

public final class InvokeInfo implements Serializable {
	private static Logger logger = LoggerUtils.getLogger(InvokeInfo.class);

	private static final long serialVersionUID = 1L;
	private Object tryRtnValue;
	private SerializableMethodHolder tryMethod;
	private SerializableMethodHolder confirmMethod;
	private SerializableMethodHolder cancelMethod;
	private Object[] args;

	/**
	 * 用于序列化
	 */
	protected InvokeInfo() {
	}

	protected InvokeInfo(Object tryRtnValue, SerializableMethodHolder tryMethod, SerializableMethodHolder confirmMethod,
			SerializableMethodHolder cancelMethod, Object[] args) {
		this.tryRtnValue = tryRtnValue;
		this.tryMethod = tryMethod;
		this.confirmMethod = confirmMethod;
		this.cancelMethod = cancelMethod;
		this.args = args;
	}

	private static Object[] getIndexMapppingArgs(SerializableMethodHolder method, Object tryRtn, int resultSetIndex,
			Object[] args) {
		if (resultSetIndex >= 0) {
			if (method.getParameterCount() - 1 > (args == null ? 0 : args.length)) {
				throw new IndexOutOfBoundsException();
			}
		} else {
			if (method.getParameterCount() - 1 > (args == null ? 0 : args.length)) {
				throw new IndexOutOfBoundsException();
			}
		}

		if (method.getParameterCount() == 0) {
			return new Object[0];
		}

		LinkedList<Object> params = new LinkedList<Object>();
		int index = 0;
		for (int i = 0; i < method.getParameterCount(); i++) {
			if (i == resultSetIndex) {
				params.add(tryRtn);
			} else {
				params.add(args[index++]);
			}
		}
		return params.toArray();
	}

	private static Object[] getNameMappingArgs(SerializableMethodHolder tryMethod, SerializableMethodHolder method, Object tryRtn,
			int resultSetIndex, Object[] args) throws NoSuchMethodException {
		if (resultSetIndex >= 0) {
			if (method.getParameterCount() > args.length) {
				throw new IndexOutOfBoundsException();
			}
		} else {
			if (method.getParameterCount() - 1 > args.length) {
				throw new IndexOutOfBoundsException();
			}
		}

		if (method.getParameterCount() == 0) {
			return new Object[0];
		}

		Map<String, Integer> tryNameIndexMap = new HashMap<String, Integer>();
		String[] tryMethodParameterNames = ParameterUtils.getParameterName(tryMethod.getMethod());
		for (int i = 0; i < tryMethodParameterNames.length; i++) {
			tryNameIndexMap.put(tryMethodParameterNames[i], i);
		}

		LinkedList<Object> params = new LinkedList<Object>();
		String[] methodParameterNames = ParameterUtils.getParameterName(method.getMethod());
		for (int i = 0; i < methodParameterNames.length; i++) {
			if (i == resultSetIndex) {
				params.add(tryRtn);
			} else {
				Integer index = tryNameIndexMap.get(methodParameterNames[i]);
				if (index == null) {
					throw new NotFoundException(methodParameterNames[i]);
				}

				params.add(args[index]);
			}
		}
		return params.toArray();
	}

	private void invoke(InstanceFactory instanceFactory, SerializableMethodHolder methodConfig) throws NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (methodConfig == null) {
			return;
		}

		Method method = methodConfig.getMethod();
		TCCStage tCCStage = method.getAnnotation(TCCStage.class);
		if (tCCStage == null) {
			return;
		}

		Object[] params;
		if (tCCStage.parameterNameMapping()) {
			params = getNameMappingArgs(tryMethod, methodConfig, tryRtnValue, tCCStage.tryResultSetParameterIndex(),
					args);
		} else {
			params = getIndexMapppingArgs(methodConfig, tryRtnValue, tCCStage.tryResultSetParameterIndex(), args);
		}

		logger.debug("clz={},name={}", methodConfig.getBelongClass().getName(),
				StringUtils.isEmpty(tCCStage.name()) ? method.getName() : tCCStage.name());

		Object obj = instanceFactory.getInstance(methodConfig.getBelongClass());
		method.invoke(obj, params);
	}

	public void invoke(StageType stageType, InstanceFactory instanceFactory)
			throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		switch (stageType) {
		case Confirm:
			invoke(instanceFactory, confirmMethod);
			break;
		case Cancel:
			invoke(instanceFactory, cancelMethod);
			break;
		default:
			break;
		}
	}

	/**
	 * 是否可以调用
	 * 
	 * @param confirm
	 * @return
	 */
	public boolean hasCanInvoke(StageType stageType) {
		switch (stageType) {
		case Confirm:
			return confirmMethod != null;
		case Cancel:
			return cancelMethod != null;
		default:
			return false;
		}
	}

	public Object[] getArgs() {
		return args;
	}
}
