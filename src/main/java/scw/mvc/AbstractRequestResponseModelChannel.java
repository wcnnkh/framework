package scw.mvc;

import java.util.Collection;

import scw.beans.BeanFactory;
import scw.json.JSONParseSupport;
import scw.mvc.parameter.ParameterFilter;

public abstract class AbstractRequestResponseModelChannel<R extends Request, P extends Response>
		extends AbstractParameterChannel implements RequestResponseModelChannel<R, P> {

	public AbstractRequestResponseModelChannel(BeanFactory beanFactory, Collection<ParameterFilter> parameterFilters,
			JSONParseSupport jsonParseSupport) {
		super(beanFactory, parameterFilters, jsonParseSupport);
	}
}
