package scw.mvc.parameter;

import scw.beans.annotation.AutoImpl;
import scw.core.parameter.ParameterConfig;
import scw.json.JSONSupport;
import scw.mvc.Channel;

@AutoImpl({DefaultRequestBodyParse.class})
public interface RequestBodyParse {
	Object requestBodyParse(Channel channel, JSONSupport jsonParseSupport, ParameterConfig parameterConfig) throws Exception;
}
