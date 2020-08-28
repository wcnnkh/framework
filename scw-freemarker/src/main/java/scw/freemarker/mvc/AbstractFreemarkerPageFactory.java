package scw.freemarker.mvc;

import scw.mvc.page.Page;
import scw.mvc.page.PageFactory;
import scw.net.MimeType;
import freemarker.template.Configuration;


public abstract class AbstractFreemarkerPageFactory implements PageFactory{

	public abstract Configuration getConfiguration();

	public abstract MimeType getMimeType();

	public Page getPage(String page) {
		return new FreemarkerPage(getConfiguration(), page, getMimeType());
	}
}
