package scw.mvc.parameter;

import scw.beans.annotation.AutoImpl;
import scw.core.parameter.ParameterConfig;
import scw.json.JSONParseSupport;
import scw.mvc.Channel;

@AutoImpl({DefaultRequestBodyParse.class})
public interface RequestBodyParse {
	Object requestBodyParse(Channel channel, JSONParseSupport jsonParseSupport, ParameterConfig parameterConfig) throws Exception;
}
