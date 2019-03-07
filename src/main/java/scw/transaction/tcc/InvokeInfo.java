package scw.transaction.tcc;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import scw.beans.BeanFactory;
import scw.common.MethodConfig;
import scw.common.exception.NotFoundException;
import scw.common.utils.CollectionUtils;

public class InvokeInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private Object tryRtnValue;
	private MethodConfig tryMethod;
	private MethodConfig confirmMethod;
	private MethodConfig cancelMethod;
	private Object[] args;

	/**
	 * 用于序列化
	 */
	protected InvokeInfo() {
	}

	protected InvokeInfo(Object tryRtnValue, MethodConfig tryMethod, MethodConfig confirmMethod,
			MethodConfig cancelMethod, Object[] args) {
		this.tryRtnValue = tryRtnValue;
		this.tryMethod = tryMethod;
		this.confirmMethod = confirmMethod;
		this.cancelMethod = cancelMethod;
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
	
	public void invokeConfirm(Object obj) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		if (confirmMethod == null) {
			return;
		}

		Method method = confirmMethod.getMethod();
		Confirm confirm = method.getAnnotation(Confirm.class);
		if (confirm == null) {
			return;
		}

		Object[] params;
		if (confirm.parameterNameMapping()) {
			params = getNameMappingArgs(tryMethod, confirmMethod, tryRtnValue, confirm.tryResultSetParameterIndex(),
					args);
		} else {
			params = getIndexMapppingArgs(confirmMethod, tryRtnValue, confirm.tryResultSetParameterIndex(), args);
		}

		method.invoke(obj, params);
	}
	
	public void invokeConfirm(BeanFactory beanFactory) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (confirmMethod == null) {
			return;
		}

		Method method = confirmMethod.getMethod();
		Confirm confirm = method.getAnnotation(Confirm.class);
		if (confirm == null) {
			return;
		}

		Object[] params;
		if (confirm.parameterNameMapping()) {
			params = getNameMappingArgs(tryMethod, confirmMethod, tryRtnValue, confirm.tryResultSetParameterIndex(),
					args);
		} else {
			params = getIndexMapppingArgs(confirmMethod, tryRtnValue, confirm.tryResultSetParameterIndex(), args);
		}

		Object obj = beanFactory.get(confirmMethod.getClz());
		method.invoke(obj, params);
	}
	
	public void invokeCacnel(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		if (cancelMethod == null) {
			return;
		}

		Method method = cancelMethod.getMethod();
		Cancel cancel = method.getAnnotation(Cancel.class);
		if (cancel == null) {
			return;
		}

		Object[] params;
		if (cancel.parameterNameMapping()) {
			params = getNameMappingArgs(tryMethod, confirmMethod, tryRtnValue, cancel.tryResultSetParameterIndex(),
					args);
		} else {
			params = getIndexMapppingArgs(confirmMethod, tryRtnValue, cancel.tryResultSetParameterIndex(), args);
		}

		method.invoke(obj, params);
	}

	public void invokeCacnel(BeanFactory beanFactory) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (cancelMethod == null) {
			return;
		}

		Method method = cancelMethod.getMethod();
		Cancel cancel = method.getAnnotation(Cancel.class);
		if (cancel == null) {
			return;
		}

		Object[] params;
		if (cancel.parameterNameMapping()) {
			params = getNameMappingArgs(tryMethod, confirmMethod, tryRtnValue, cancel.tryResultSetParameterIndex(),
					args);
		} else {
			params = getIndexMapppingArgs(confirmMethod, tryRtnValue, cancel.tryResultSetParameterIndex(), args);
		}

		Object obj = beanFactory.get(cancelMethod.getClz());
		method.invoke(obj, params);
	}

	public void invoke(boolean confirm, BeanFactory beanFactory) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (confirm) {
			invokeConfirm(beanFactory);
		} else {
			invokeCacnel(beanFactory);
		}
	}
	
	public void invoke(boolean confirm, Object obj) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		if (confirm) {
			invokeConfirm(obj);
		} else {
			invokeCacnel(obj);
		}
	}

	/**
	 * 是否可以调用
	 * 
	 * @param confirm
	 * @return
	 */
	public boolean hasCanInvoke(boolean confirm) {
		if (confirm) {
			return confirmMethod != null;
		} else {
			return cancelMethod != null;
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

	public Object[] getArgs() {
		return args;
	}
}
