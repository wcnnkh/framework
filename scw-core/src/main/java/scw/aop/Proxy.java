package scw.aop;

import scw.instance.InstanceCreator;

/**
  *  代理
 * @author shuchaowen
 *
 */
public interface Proxy extends InstanceCreator<Object>{
	/**
	  *  被代理的原始类
	 * @return
	 */
	Class<?> getTargetClass();
}
