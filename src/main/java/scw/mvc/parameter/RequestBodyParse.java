package scw.mvc.parameter;

import scw.beans.annotation.AutoImpl;
import scw.core.parameter.ParameterConfig;
import scw.json.JsonSupport;
import scw.mvc.Channel;

@AutoImpl({DefaultRequestBodyParse.class})
public interface RequestBodyParse {
	Object requestBodyParse(Channel channel, JsonSupport jsonParseSupport, ParameterConfig parameterConfig) throws Exception;
}
