package scw.embed.tomcat;

import java.util.ArrayList;

import org.apache.tomcat.util.descriptor.web.ErrorPage;

import scw.aop.annotation.AopEnable;

@AopEnable(false)
public class ErrorPages extends ArrayList<ErrorPage> {
	private static final long serialVersionUID = 1L;
}
