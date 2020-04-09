package scw.servlet.mvc.page;

import scw.core.instance.annotation.Configuration;
import scw.core.utils.StringUtils;
import scw.mvc.page.PageFactoryAdapter;

@Configuration(order=Integer.MAX_VALUE)
public final class JspPageFactoryAdapter extends JspPageFactory implements PageFactoryAdapter{

	public boolean isAdapte(String page) {
		return StringUtils.endsWithIgnoreCase(page, ".jsp");
	}
}
