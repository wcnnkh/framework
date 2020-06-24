package scw.net;

import scw.util.MultiValueMap;

public interface RestfulParameterMapAware {
	void setRestfulParameterMap(MultiValueMap<String, String> parameterMap);
}
