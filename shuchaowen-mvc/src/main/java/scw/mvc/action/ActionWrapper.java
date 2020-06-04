package scw.mvc.action;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Collection;

import scw.mvc.HttpChannel;

public class ActionWrapper implements Action {
	private Action action;

	public Action getAction() {
		if(action instanceof ActionWrapper){
			return ((ActionWrapper) action).getAction();
		}
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public ActionWrapper(Action action) {
		this.action = action;
	}

	public AnnotatedElement getAnnotatedElement() {
		return action.getAnnotatedElement();
	}

	public Class<?> getTargetClass() {
		return action.getTargetClass();
	}

	public AnnotatedElement getTargetClassAnnotatedElement() {
		return action.getTargetClassAnnotatedElement();
	}

	public Method getMethod() {
		return action.getMethod();
	}

	public AnnotatedElement getMethodAnnotatedElement() {
		return action.getMethodAnnotatedElement();
	}

	public Object doAction(HttpChannel httpChannel) throws Throwable {
		return action.doAction(httpChannel);
	}

	public Collection<ControllerDescriptor> getControllerDescriptors() {
		return action.getControllerDescriptors();
	}

	public Collection<ControllerDescriptor> getTargetClassControllerDescriptors() {
		return action.getTargetClassControllerDescriptors();
	}

	public Collection<ControllerDescriptor> getMethodControllerDescriptors() {
		return action.getMethodControllerDescriptors();
	}

}