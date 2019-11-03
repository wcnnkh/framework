package scw.mvc;

import java.util.Collection;

import scw.beans.BeanFactory;
import scw.core.parameter.ParameterConfig;
import scw.json.JSONParseSupport;
import scw.mvc.parameter.ParameterFilter;

public abstract class AbstractRequestResponseModelChannel<R extends Request, P extends Response> extends AbstractChannel
		implements RequestResponseModelChannel<R, P> {

	public AbstractRequestResponseModelChannel(BeanFactory beanFactory, Collection<ParameterFilter> parameterFilters,
			JSONParseSupport jsonParseSupport) {
		super(beanFactory, parameterFilters, jsonParseSupport);
	}

	@Override
	public Object getParameter(ParameterConfig parameterConfig) {
		if(Request.class.isAssignableFrom(parameterConfig.getType())){
			return getRequest();
		}else if(Response.class.isAssignableFrom(parameterConfig.getType())){
			return getResponse();
		}
		return super.getParameter(parameterConfig);
	}
}
