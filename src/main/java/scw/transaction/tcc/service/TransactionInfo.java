package scw.transaction.tcc.service;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

import scw.beans.BeanFactory;
import scw.transaction.tcc.InvokeInfo;
import scw.transaction.tcc.StageType;

public final class TransactionInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private InvokeInfo invokeInfo;
	private StageType stageType;

	public InvokeInfo getInvokeInfo() {
		return invokeInfo;
	}

	public void setInvokeInfo(InvokeInfo invokeInfo) {
		this.invokeInfo = invokeInfo;
	}

	public StageType getStageType() {
		return stageType;
	}

	public void setStageType(StageType stageType) {
		this.stageType = stageType;
	}

	public void invoke(BeanFactory beanFactory) throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		invokeInfo.invoke(stageType, beanFactory);
	}

	public boolean hasCanInvoke() {
		return invokeInfo.hasCanInvoke(stageType);
	}
}
