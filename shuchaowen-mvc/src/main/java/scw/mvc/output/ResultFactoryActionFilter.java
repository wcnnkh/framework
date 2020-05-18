package scw.mvc.output;

import scw.beans.BeanFactory;
import scw.core.instance.annotation.Configuration;
import scw.json.JSONUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.mvc.HttpChannel;
import scw.mvc.action.Action;
import scw.mvc.action.ActionFilter;
import scw.mvc.action.ActionService;
import scw.mvc.annotation.ResultFactory;
import scw.mvc.view.View;
import scw.result.Result;

@Configuration(order=Integer.MAX_VALUE)
public final class ResultFactoryActionFilter implements ActionFilter{
	private static Logger logger = LoggerUtils.getLogger(ResultFactoryActionFilter.class);
	private BeanFactory beanFactory;
	
	public ResultFactoryActionFilter(BeanFactory beanFactory){
		this.beanFactory = beanFactory;
	}
	
	public Object doFilter(HttpChannel httpChannel, Action action, ActionService service)
			throws Throwable {
		Object value = service.doAction(httpChannel, action);
		if(value != null && value instanceof View){
			return value;
		}
		
		if (value != null && httpChannel.getLogger().isErrorEnabled() && value instanceof Result && ((Result) value).isError()) {
			logger.error("fail:{}, result={}", httpChannel.toString(), JSONUtils.toJSONString(value));
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
