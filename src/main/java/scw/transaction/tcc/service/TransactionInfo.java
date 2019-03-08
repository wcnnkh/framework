package scw.transaction.tcc.service;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

import scw.beans.BeanFactory;
import scw.common.Logger;
import scw.transaction.tcc.Cancel;
import scw.transaction.tcc.Confirm;
import scw.transaction.tcc.InvokeInfo;

public class TransactionInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private InvokeInfo invokeInfo;
	private String name;
	private boolean confirm;

	public InvokeInfo getInvokeInfo() {
		return invokeInfo;
	}

	public void setInvokeInfo(InvokeInfo invokeInfo) {
		this.invokeInfo = invokeInfo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isConfirm() {
		return confirm;
	}

	public void setConfirm(boolean confirm) {
		this.confirm = confirm;
	}

	public void invoke(BeanFactory beanFactory) throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		if (confirm) {
			Logger.debug(Confirm.class.getName(),
					"clz=" + getInvokeInfo().getTryMethod().getClz().getName() + ",name=" + name);
		} else {
			Logger.debug(Cancel.class.getName(),
					"clz=" + getInvokeInfo().getTryMethod().getClz().getName() + ",name=" + name);
		}
		getInvokeInfo().invoke(confirm, beanFactory);
	}

	public boolean hasCanInvoke() {
		return invokeInfo.hasCanInvoke(confirm);
	}
}
