package scw.mvc.servlet.page;

import scw.mvc.page.Page;
import scw.mvc.page.PageFactory;

public class JspPageFactory implements PageFactory {
	public final static JspPageFactory instance = new JspPageFactory();

	public Page getPage(String page) {
		return new Jsp(page);
	}

}
