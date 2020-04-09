package scw.mvc.action.support;

import scw.beans.BeanFactory;
import scw.beans.annotation.Configuration;
import scw.json.JSONUtils;
import scw.mvc.Channel;
import scw.mvc.View;
import scw.mvc.action.Action;
import scw.mvc.action.filter.ActionFilter;
import scw.mvc.action.filter.ActionFilterChain;
import scw.mvc.annotation.ResultFactory;
import scw.result.Result;

@Configuration(order=Integer.MAX_VALUE)
public final class ResultFactoryActionFilter implements ActionFilter{
	private BeanFactory beanFactory;
	
	public ResultFactoryActionFilter(BeanFactory beanFactory){
		this.beanFactory = beanFactory;
	}
	
	public Object doFilter(Channel channel, Action action, ActionFilterChain chain)
			throws Throwable {
		Object value = chain.doFilter(channel, action);
		if(value != null && value instanceof View){
			return value;
		}
		
		if (value != null && channel.getLogger().isErrorEnabled() && value instanceof Result && ((Result) value).isError()) {
			channel.getLogger().error("fail:{}, result={}", channel.toString(), JSONUtils.toJSONString(value));
		}
		
		ResultFactory resultFactory = action.getAnnotation(ResultFactory.class);
		if (resultFactory != null && resultFactory.enable()) {
			if (value != null && value instanceof Result) {
				return value;
			}
			return beanFactory.getInstance(resultFactory.value())
					.success(value);
		}
		return value;
	}

}
