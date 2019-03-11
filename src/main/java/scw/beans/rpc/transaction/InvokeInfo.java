package scw.beans.rpc.transaction;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import scw.beans.BeanFactory;
import scw.common.Logger;
import scw.common.MethodConfig;
import scw.common.exception.NotFoundException;
import scw.common.utils.CollectionUtils;
import scw.common.utils.StringUtils;

public class InvokeInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private Object tryRtnValue;
	private MethodConfig tryMethod;
	private MethodConfig confirmMethod;
	private MethodConfig cancelMethod;
	private MethodConfig completeMethod;
	private Object[] args;

	/**
	 * 用于序列化
	 */
	protected InvokeInfo() {
	}

	protected InvokeInfo(Object tryRtnValue, MethodConfig tryMethod, MethodConfig confirmMethod,
			MethodConfig cancelMethod, MethodConfig completeMethod, Object[] args) {
		this.tryRtnValue = tryRtnValue;
		this.tryMethod = tryMethod;
		this.confirmMethod = confirmMethod;
		this.cancelMethod = cancelMethod;
		this.completeMethod = completeMethod;
		this.args = args;
	}

	private static Object[] getIndexMapppingArgs(MethodConfig method, Object tryRtn, int resultSetIndex,
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

	private static Object[] getNameMappingArgs(MethodConfig tryMethod, MethodConfig method, Object tryRtn,
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
		String[] tryMethodParameterNames = tryMethod.getParameterNames();
		for (int i = 0; i < tryMethodParameterNames.length; i++) {
			tryNameIndexMap.put(tryMethodParameterNames[i], i);
		}

		LinkedList<Object> params = new LinkedList<Object>();
		String[] methodParameterNames = method.getParameterNames();
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

	private void invoke(BeanFactory beanFactory, MethodConfig methodConfig) throws NoSuchMethodException,
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

		StringBuilder sb = new StringBuilder();
		sb.append("clz=").append(methodConfig.getClz().getName());
		sb.append(",name=").append(StringUtils.isEmpty(stage.name()) ? method.getName() : stage.name());
		Logger.debug(TCC.class.getName(), sb.toString());

		Object obj = beanFactory.get(methodConfig.getClz());
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

	public Object getTryRtnValue() {
		return tryRtnValue;
	}

	public MethodConfig getTryMethod() {
		return tryMethod;
	}

	public MethodConfig getConfirmMethod() {
		return confirmMethod;
	}

	public MethodConfig getCancelMethod() {
		return cancelMethod;
	}

	public MethodConfig getComplateMethod() {
		return completeMethod;
	}

	public Object[] getArgs() {
		return args;
	}
}
