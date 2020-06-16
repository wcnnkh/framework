package scw.beans.event;

import scw.beans.BeanFactory;

/**
 * 刷新依赖事件
 * @author shuchaowen
 *
 */
public class DependenceRefreshEvent extends BeanEvent{
	private static final long serialVersionUID = 1L;

	public DependenceRefreshEvent(Object source, BeanFactory beanFactory) {
		super(source, beanFactory);
	}
}
