package scw.beans.tcc.service;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

import scw.beans.tcc.InvokeInfo;
import scw.beans.tcc.StageType;
import scw.core.instance.InstanceFactory;

public final class TransactionInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private InvokeInfo invokeInfo;
	private StageType stageType;
	
	protected TransactionInfo(){};
	
	protected TransactionInfo(InvokeInfo invokeInfo, StageType stageType){
		this.invokeInfo = invokeInfo;
		this.stageType = stageType;
	}

	public InvokeInfo getInvokeInfo() {
		return invokeInfo;
	}

	public StageType getStageType() {
		return stageType;
	}

	public void invoke(InstanceFactory instanceFactory) throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		invokeInfo.invoke(stageType, instanceFactory);
	}

	public boolean hasCanInvoke() {
		return invokeInfo.hasCanInvoke(stageType);
	}
}
