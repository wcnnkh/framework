package shuchaowen.core.beans;

import java.util.List;

public final class InitMethodInfo {
	private final String methodName;
	private final List<BeanMethodParameter> beanMethodParameters;
	
	public InitMethodInfo(String  methodName, List<BeanMethodParameter> beanMethodParameters){
		this. methodName =  methodName;
		this.beanMethodParameters = beanMethodParameters;
	}

	public String getMethodName() {
		return methodName;
	}

	public List<BeanMethodParameter> getBeanMethodParameters() {
		return beanMethodParameters;
	}
}
