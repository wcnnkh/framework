package scw.mvc.page;

import java.util.Map;

import scw.mvc.view.View;

public interface Page extends Map<String, Object>, View {
	static final String CONTEXT_PATH_NAME = "_contextPath";
	
	String getPage();
}
