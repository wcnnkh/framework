package io.basc.framework.timer.support;

import java.io.Serializable;

public class BeanTaskConfig implements Serializable {
	private static final long serialVersionUID = 1L;
	private String beanName;
	private String methodName;

	public String getBeanName() {
		return beanName;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
}
