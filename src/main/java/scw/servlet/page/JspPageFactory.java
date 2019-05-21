package scw.servlet.page;

public class JspPageFactory implements PageFactory {
	public final static JspPageFactory instance = new JspPageFactory();

	public Page create(String page) {
		return new Jsp(page);
	}

}
