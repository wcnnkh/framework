package scw.servlet.page;

public class JspPageFactory implements PageFactory {

	public Page create(String page) {
		return new Jsp(page);
	}
	
}
