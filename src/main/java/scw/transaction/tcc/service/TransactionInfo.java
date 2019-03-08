package scw.transaction.tcc.service;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

import scw.beans.BeanFactory;
import scw.common.Logger;
import scw.transaction.tcc.InvokeInfo;
import scw.transaction.tcc.StageType;

public class TransactionInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private InvokeInfo invokeInfo;
	private String name;
	private StageType stageType;

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

	public StageType getStageType() {
		return stageType;
	}

	public void setStageType(StageType stageType) {
		this.stageType = stageType;
	}

	public void invoke(BeanFactory beanFactory) throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		Logger.debug(stageType.name(), "clz=" + getInvokeInfo().getTryMethod().getClz().getName() + ",name=" + name);
		getInvokeInfo().invoke(stageType, beanFactory);
	}

	public boolean hasCanInvoke() {
		return invokeInfo.hasCanInvoke(stageType);
	}
}
