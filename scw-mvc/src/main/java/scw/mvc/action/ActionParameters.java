package scw.mvc.action;

import java.io.Serializable;

import scw.mvc.HttpChannel;

public class ActionParameters implements Serializable {
	private static final long serialVersionUID = 1L;
	private Object[] parameters;

	public ActionParameters() {
	}

	public ActionParameters(ActionParameters parameters) {
		this(parameters.parameters);
	}

	public ActionParameters(Object[] parameters) {
		this.parameters = parameters;
	}

	public Object[] getParameters() {
		return parameters;
	}

	/**
	 * 只有当参数不存在时才会从httpChannel中获取参数<br/>
	 * doAction前会调用此方法{@see ActionInterceptorChain#intercept(HttpChannel, Action, ActionParameters)}
	 * 
	 * @param httpChannel
	 * @param action
	 * @return
	 */
	public Object[] getParameters(HttpChannel httpChannel, Action action) {
		if (parameters == null) {
			parameters = httpChannel.getParameters(action.getParameterDescriptors());
		}
		return parameters;
	}

	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}

	public boolean isNull() {
		return parameters == null;
	}

	public int length() {
		return parameters == null ? 0 : parameters.length;
	}

	public void clear() {
		parameters = null;
	}
}
