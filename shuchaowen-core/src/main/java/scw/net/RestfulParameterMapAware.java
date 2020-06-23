package scw.net;

import scw.util.MultiValueMap;

public interface RestfulParameterMapAware {
	MultiValueMap<String, String> getRestfulParameterMap();
	
	void setRestfulParameterMap(MultiValueMap<String, String> parameterMap);
}
