package scw.mvc.parameter;

import scw.beans.annotation.AutoImpl;
import scw.core.parameter.ParameterDescriptor;
import scw.json.JSONSupport;
import scw.mvc.HttpChannel;

@AutoImpl({DefaultRequestBodyParse.class})
public interface RequestBodyParse {
	Object requestBodyParse(HttpChannel httpChannel, JSONSupport jsonParseSupport, ParameterDescriptor parameterDescriptor) throws Exception;
}
