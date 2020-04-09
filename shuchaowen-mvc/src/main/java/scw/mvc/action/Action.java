package scw.mvc.action;

import scw.core.annotation.AnnotationFactory;
import scw.mvc.Channel;
import scw.mvc.action.filter.ActionFilterChain;

public interface Action extends AnnotationFactory{
	String getController();
	
	Object doAction(Channel channel) throws Throwable;

	/**
	 * 这个action的filter chain
	 * @return 可能为空
	 */
	ActionFilterChain getActionFilterChain();
}
