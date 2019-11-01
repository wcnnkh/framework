package scw.mvc.parameter;

import scw.core.parameter.ParameterConfig;
import scw.json.JSONParseSupport;
import scw.mvc.Channel;

public interface RequestBodyParse {
	Object requestBodyParse(Channel channel, JSONParseSupport jsonParseSupport, ParameterConfig parameterConfig) throws Exception;
}
