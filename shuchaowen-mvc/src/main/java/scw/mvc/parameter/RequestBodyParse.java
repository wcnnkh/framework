package scw.mvc.parameter;

import scw.beans.annotation.AutoImpl;
import scw.core.parameter.ParameterDescriptor;
import scw.json.JSONSupport;
import scw.mvc.Channel;

@AutoImpl({DefaultRequestBodyParse.class})
public interface RequestBodyParse {
	Object requestBodyParse(Channel channel, JSONSupport jsonParseSupport, ParameterDescriptor parameterConfig) throws Exception;
}
