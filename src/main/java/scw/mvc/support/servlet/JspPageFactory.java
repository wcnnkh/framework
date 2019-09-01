package scw.mvc.support.servlet;

import scw.mvc.support.Page;
import scw.mvc.support.PageFactory;

public class JspPageFactory implements PageFactory {
	public final static JspPageFactory instance = new JspPageFactory();

	public Page getPage(String page) {
		return new Jsp(page);
	}

}
