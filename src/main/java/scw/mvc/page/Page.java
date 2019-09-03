package scw.mvc.page;

import java.util.Map;

import scw.mvc.View;

public interface Page extends Map<String, Object>, View{
	String getPage();
}
