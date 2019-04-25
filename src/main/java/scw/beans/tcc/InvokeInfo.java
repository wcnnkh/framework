package scw.beans.tcc;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import scw.beans.BeanFactory;
import scw.beans.annotation.Stage;
import scw.core.exception.NotFoundException;
import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;
import scw.core.reflect.SerializableMethod;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;

public final class InvokeInfo implements Serializable {
	private static Logger logger = LoggerFactory.getLogger(InvokeInfo.class);

	private static final long serialVersionUID = 1L;
	private Object tryRtnValue;
	private SerializableMethod tryMethod;
	private SerializableMethod confirmMethod;
	private SerializableMethod cancelMethod;
	private SerializableMethod completeMethod;
	private Object[] args;

	/**
	 * 用于序列化
	 */
	protected InvokeInfo() {
	}

	protected InvokeInfo(Object tryRtnValue, SerializableMethod tryMethod, SerializableMethod confirmMethod,
			SerializableMethod cancelMethod, SerializableMethod completeMethod, Object[] args) {
		this.tryRtnValue = tryRtnValue;
		this.tryMethod = tryMethod;
		this.confirmMethod = confirmMethod;
		this.cancelMethod = cancelMethod;
		this.completeMethod = completeMethod;
		this.args = args;
	}

	private static Object[] getIndexMapppingArgs(SerializableMethod method, Object tryRtn, int resultSetIndex,
			Object[] args) {
		if (resultSetIndex >= 0) {
			if (method.getParameterCount() - 1 > args.length) {
				throw new IndexOutOfBoundsException();
			}
		} else {
			if (method.getParameterCount() - 1 > args.length) {
				throw new IndexOutOfBoundsException();
			}
		}

		if (method.getParameterCount() == 0) {
			return CollectionUtils.EMPTY_ARRAY;
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

	private static Object[] getNameMappingArgs(SerializableMethod tryMethod, SerializableMethod method, Object tryRtn,
			int resultSetIndex, Object[] args) throws NoSuchMethodException, SecurityException {
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
			return CollectionUtils.EMPTY_ARRAY;
		}

		Map<String, Integer> tryNameIndexMap = new HashMap<String, Integer>();
		String[] tryMethodParameterNames = ClassUtils.getParameterName(tryMethod.getMethod());
		for (int i = 0; i < tryMethodParameterNames.length; i++) {
			tryNameIndexMap.put(tryMethodParameterNames[i], i);
		}

		LinkedList<Object> params = new LinkedList<Object>();
		String[] methodParameterNames = ClassUtils.getParameterName(method.getMethod());
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

	private void invoke(BeanFactory beanFactory, SerializableMethod methodConfig) throws NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (methodConfig == null) {
			return;
		}

		Method method = methodConfig.getMethod();
		Stage stage = method.getAnnotation(Stage.class);
		if (stage == null) {
			return;
		}

		Object[] params;
		if (stage.parameterNameMapping()) {
			params = getNameMappingArgs(tryMethod, methodConfig, tryRtnValue, stage.tryResultSetParameterIndex(), args);
		} else {
			params = getIndexMapppingArgs(methodConfig, tryRtnValue, stage.tryResultSetParameterIndex(), args);
		}

		logger.debug("clz={},name={}", methodConfig.getBelongClass().getName(),
				StringUtils.isEmpty(stage.name()) ? method.getName() : stage.name());

		Object obj = beanFactory.get(methodConfig.getBelongClass());
		method.invoke(obj, params);
	}

	public void invoke(StageType stageType, BeanFactory beanFactory) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		switch (stageType) {
		case Confirm:
			invoke(beanFactory, confirmMethod);
			break;
		case Cancel:
			invoke(beanFactory, cancelMethod);
			break;
		case Complete:
			invoke(beanFactory, completeMethod);
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
		case Complete:
			return completeMethod != null;
		default:
			return false;
		}
	}

	public Object[] getArgs() {
		return args;
	}
}
