package scw.embed.servlet;

import scw.application.MainApplication;

/**
 * 推荐使用{@link MainApplication}
 * @author shuchaowen
 *
 */
@Deprecated
public class ServletEmbeddedApplication extends MainApplication {

	public ServletEmbeddedApplication(Class<?> mainClass, String[] args) {
		super(mainClass, args);
	}

}
