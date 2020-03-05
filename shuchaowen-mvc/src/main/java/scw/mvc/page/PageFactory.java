package scw.mvc.page;

import scw.beans.annotation.AutoImpl;

@AutoImpl(ConfigurationPageFactory.class)
public interface PageFactory {
	Page getPage(String page);
}
