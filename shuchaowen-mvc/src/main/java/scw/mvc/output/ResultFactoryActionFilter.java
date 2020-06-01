package scw.mvc.output;

import scw.beans.BeanFactory;
import scw.core.instance.annotation.Configuration;
import scw.mvc.HttpChannel;
import scw.mvc.action.Action;
import scw.mvc.action.ActionFilter;
import scw.mvc.annotation.ResultFactory;
import scw.mvc.view.View;
import scw.result.Result;

@Configuration(order=Integer.MAX_VALUE)
public final class ResultFactoryActionFilter implements ActionFilter{
	private BeanFactory beanFactory;
	
	public ResultFactoryActionFilter(BeanFactory beanFactory){
		this.beanFactory = beanFactory;
	}
	
	public Object doFilter(Action action, HttpChannel httpChannel)
			throws Throwable {
		Object value = action.doAction(httpChannel);
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
