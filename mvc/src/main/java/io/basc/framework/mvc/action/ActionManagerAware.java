package io.basc.framework.mvc.action;

/**
 * 为了防止循环引用，应该使用此方法注入ActionManager
 * @author shuchaowen
 *
 */
public interface ActionManagerAware {
	void setActionManager(ActionManager actionManager);
}
