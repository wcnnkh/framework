package scw.core.multivalue;

import java.util.Enumeration;
import java.util.Map;

public interface MultiValueParameterFactory {
	String getParameter(String name);

	Enumeration<String> getParameterNames();

	String[] getParameterValues(String name);

	Map<String, String[]> getParameterMap();
}
