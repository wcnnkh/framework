package scw.mvc.action;

import java.lang.reflect.AnnotatedElement;
import java.util.Collection;

import scw.aop.MethodInvokerWrapper;
import scw.core.parameter.ParameterDescriptors;
import scw.http.server.HttpControllerDescriptor;

public class ActionWrapper extends MethodInvokerWrapper implements Action {
	private static final long serialVersionUID = 1L;
	private final Action action;

	public ActionWrapper(Action action) {
		super(action);
		this.action = action;
	}

	public Action getAction() {
		if (action instanceof ActionWrapper) {
			return ((ActionWrapper) action).getAction();
		}
		return action;
	}

	public AnnotatedElement getAnnotatedElement() {
		return action.getAnnotatedElement();
	}

	public AnnotatedElement getTargetClassAnnotatedElement() {
		return action.getTargetClassAnnotatedElement();
	}

	public AnnotatedElement getMethodAnnotatedElement() {
		return action.getMethodAnnotatedElement();
	}

	public Collection<HttpControllerDescriptor> getHttpControllerDescriptors() {
		return action.getHttpControllerDescriptors();
	}

	public Collection<HttpControllerDescriptor> getTargetClassHttpControllerDescriptors() {
		return action.getTargetClassHttpControllerDescriptors();
	}

	public Collection<HttpControllerDescriptor> getMethodHttpControllerDescriptors() {
		return action.getMethodHttpControllerDescriptors();
	}

	public ParameterDescriptors getParameterDescriptors() {
		return action.getParameterDescriptors();
	}

	public Iterable<? extends ActionFilter> getActionFilter() {
		return action.getActionFilter();
	}
}
