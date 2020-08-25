package scw.mvc.output;

import scw.beans.BeanFactory;
import scw.core.instance.annotation.Configuration;
import scw.mvc.HttpChannel;
import scw.mvc.action.Action;
import scw.mvc.action.ActionFilter;
import scw.mvc.action.ActionFilterAccept;
import scw.mvc.action.ActionFilterChain;
import scw.mvc.annotation.ResultFactory;
import scw.mvc.view.View;
import scw.result.Result;

@Configuration(order=Integer.MAX_VALUE)
public final class ResultFactoryActionFilter implements ActionFilter, ActionFilterAccept{
	private BeanFactory beanFactory;
	
	public ResultFactoryActionFilter(BeanFactory beanFactory){
		this.beanFactory = beanFactory;
	}
	
	public boolean isAccept(HttpChannel httpChannel, Action action, Object[] args) {
		return action.getAnnotatedElement().getAnnotation(ResultFactory.class) != null;
	}
	
	public Object doFilter(HttpChannel httpChannel, Action action, Object[] args, ActionFilterChain filterChain)
			throws Throwable {
		Object value = filterChain.doFilter(httpChannel, action, args);
		if(value != null && value instanceof View){
			return value;
		}
		
		ResultFactory resultFactory = action.getAnnotatedElement().getAnnotation(ResultFactory.class);
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
