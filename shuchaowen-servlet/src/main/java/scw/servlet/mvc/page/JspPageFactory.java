package scw.servlet.mvc.page;

import scw.core.instance.annotation.Configuration;
import scw.core.utils.StringUtils;
import scw.mvc.page.Page;
import scw.mvc.page.PageFactory;

@Configuration(order = Integer.MIN_VALUE)
public class JspPageFactory implements PageFactory {
	public final static JspPageFactory instance = new JspPageFactory();

	public Page getPage(String page) {
		return new Jsp(page);
	}

	public boolean isSupport(String page) {
		return StringUtils.endsWithIgnoreCase(page, ".jsp");
	}
}
