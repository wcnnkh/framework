package scw.mvc.action.filter.support;

import scw.beans.BeanFactory;
import scw.beans.annotation.Configuration;
import scw.json.JSONUtils;
import scw.mvc.Channel;
import scw.mvc.View;
import scw.mvc.action.Action;
import scw.mvc.action.filter.Filter;
import scw.mvc.action.filter.FilterChain;
import scw.mvc.annotation.ResultFactory;
import scw.result.Result;

@Configuration(order=Integer.MAX_VALUE)
public final class ResultFactoryFilter implements Filter{
	private BeanFactory beanFactory;
	
	public ResultFactoryFilter(BeanFactory beanFactory){
		this.beanFactory = beanFactory;
	}
	
	public Object doFilter(Channel channel, Action action, FilterChain chain)
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
