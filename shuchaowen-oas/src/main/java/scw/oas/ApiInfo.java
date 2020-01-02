package scw.oas;

import java.util.List;

public interface ApiInfo extends ApiDescription {
	String getRequestContentType();

	String getResponseContentType();

	List<? extends ApiParameter> getRequestParameterList();

	List<? extends ApiParameter> getResponseParameterList();
}
