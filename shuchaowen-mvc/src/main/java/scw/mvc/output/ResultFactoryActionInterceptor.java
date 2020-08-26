package scw.mvc.output;

import scw.beans.BeanFactory;
import scw.core.instance.annotation.Configuration;
import scw.mvc.HttpChannel;
import scw.mvc.action.Action;
import scw.mvc.action.ActionInterceptor;
import scw.mvc.action.ActionInterceptorAccept;
import scw.mvc.action.ActionInterceptorChain;
import scw.mvc.action.ActionParameters;
import scw.mvc.annotation.ResultFactory;
import scw.mvc.view.View;
import scw.result.Result;

@Configuration(order=Integer.MAX_VALUE)
public final class ResultFactoryActionInterceptor implements ActionInterceptor, ActionInterceptorAccept{
	private BeanFactory beanFactory;
	
	public ResultFactoryActionInterceptor(BeanFactory beanFactory){
		this.beanFactory = beanFactory;
	}
	
	public boolean isAccept(HttpChannel httpChannel, Action action, ActionParameters parameters) {
		return action.getAnnotatedElement().getAnnotation(ResultFactory.class) != null;
	}
	
	public Object intercept(HttpChannel httpChannel, Action action, ActionParameters parameters, ActionInterceptorChain chain)
			throws Throwable {
		Object value = chain.intercept(httpChannel, action, parameters);
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
