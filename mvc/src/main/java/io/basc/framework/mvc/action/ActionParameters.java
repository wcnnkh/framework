package io.basc.framework.mvc.action;

import java.io.Serializable;
import java.util.Optional;

import io.basc.framework.mvc.HttpChannel;

public class ActionParameters implements Serializable {
	private static final long serialVersionUID = 1L;
	private Optional<Object[]> parameters = Optional.empty();

	public ActionParameters() {
	}

	public ActionParameters(ActionParameters parameters) {
		this.parameters = parameters.parameters;
	}

	public ActionParameters(Object[] parameters) {
		this.parameters = Optional.of(parameters);
	}

	/**
	 * 如果未初始化{@link Optional#isPresent()}会返回false
	 * @see #getParameters(HttpChannel, Action)
	 * @return
	 */
	public Optional<Object[]> getParameters() {
		return parameters;
	}
	
	public boolean isPresent() {
		return parameters.isPresent();
	}

	/**
	 * 只有当参数不存在时才会从httpChannel中获取参数<br/>
	 * doAction前会调用此方法{@see ActionInterceptorChain#intercept(HttpChannel, Action,
	 * ActionParameters)}
	 * 
	 * @param httpChannel
	 * @param action
	 * @return
	 */
	public Object[] getParameters(HttpChannel httpChannel, Action action) {
		if (!parameters.isPresent()) {
			parameters = Optional.of(httpChannel.getParameters(action.getParameterDescriptors()));
		}
		return parameters.get();
	}

	/**
	 * 重写当前参数
	 * @param parameters
	 */
	public void setParameters(Object[] parameters) {
		this.parameters = Optional.of(parameters);
	}

	/**
	 * 清除当前参数
	 */
	public void clear() {
		parameters = Optional.empty();
	}
}
