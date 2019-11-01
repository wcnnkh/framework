package scw.mvc.parameter;

import scw.core.parameter.ParameterConfig;
import scw.json.JSONParseSupport;
import scw.mvc.Channel;

public interface ParameterTrans {
	Object parameterTransformation(Channel channel, JSONParseSupport jsonParseSupport, ParameterConfig parameterConfig, String name) throws Exception;
}
