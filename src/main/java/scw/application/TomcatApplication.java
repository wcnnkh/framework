package scw.application;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import scw.core.instance.InstanceUtils;
import scw.core.utils.ArrayUtils;
import scw.core.utils.ConfigUtils;
import scw.core.utils.StringUtils;
import scw.logger.LoggerUtils;
import scw.servlet.ServletService;
import scw.servlet.ServletUtils;

public class TomcatApplication extends CommonApplication implements Servlet {
	private Tomcat tomcat;
	private ServletService servletService;

	public TomcatApplication(String configXml) {
		super(configXml, false);
	}

	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		servletService.service(req, res);
	}

	protected Tomcat createTomcat() {
		Tomcat tomcat = new Tomcat();
		tomcat.setPort(StringUtils.parseInt(getPropertiesFactory().getValue("servlet.port"), 8080));

		String basedir = getPropertiesFactory().getValue("servlet.basedir");
		if (StringUtils.isEmpty(basedir)) {
			basedir = ConfigUtils.getWorkPath();
		}

		if (!StringUtils.isEmpty(basedir)) {
			tomcat.setBaseDir(basedir);
		}

		String protocol = getPropertiesFactory().getValue("servlet.protocol");
		if (!StringUtils.isEmpty(protocol)) {
			tomcat.setConnector(new Connector(protocol));
		}

		return tomcat;
	}

	@Override
	public void init() {
		super.init();
		this.servletService = ServletUtils.getServletService(getBeanFactory(), getPropertiesFactory(), getConfigPath(),
				getBeanFactory().getFilterNames());

		this.tomcat = createTomcat();
		String contextPath = getPropertiesFactory().getValue("servlet.contextPath");
		contextPath = StringUtils.isEmpty(contextPath) ? "" : contextPath;
		Context context = tomcat.addContext(contextPath, ConfigUtils.getWorkPath());

		String servletName = getPropertiesFactory().getValue("servlet.name");
		servletName = StringUtils.isEmpty(servletName) ? "def" : servletName;
		Tomcat.addServlet(context, servletName, this);

		if (StringUtils.parseBoolean(getPropertiesFactory().getValue("servlet.jsp"))) {
			try {
				context.addServletContainerInitializer((ServletContainerInitializer) InstanceUtils
						.getInstance("org.apache.jasper.servlet.JasperInitializer", true), null);
			} catch (Exception e) {
				// Probably not Tomcat 8
			}

			Tomcat.addServlet(context, "jsp", "org.apache.jasper.servlet.JspServlet");
			context.addServletMapping("*.jsp", "jsp", true);
			context.addServletMapping("*.jspx", "jsp", true);
		}

		String sourceMapping = getPropertiesFactory().getValue("servlet.source");
		if (!StringUtils.isEmpty(sourceMapping)) {
			String[] patternArr = StringUtils.commonSplit(sourceMapping);
			if (!ArrayUtils.isEmpty(patternArr)) {
				Tomcat.addServlet(context, "default", "org.apache.catalina.servlets.DefaultServlet");
				for (String pattern : patternArr) {
					LoggerUtils.info(TomcatApplication.class, "source mapping [{}]", pattern);
					context.addServletMapping(pattern, "default");
				}
			}
		}

		try {
			tomcat.start();
		} catch (LifecycleException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void destroy() {
		try {
			tomcat.destroy();
			super.destroy();
		} catch (LifecycleException e) {
			e.printStackTrace();
		}
	}

	public void init(ServletConfig config) throws ServletException {
	}

	public ServletConfig getServletConfig() {
		return null;
	}

	public String getServletInfo() {
		return null;
	}

	public static void run(String beanXml) {
		if (StringUtils.isEmpty(beanXml)) {
			LoggerUtils.warn(TomcatApplication.class, "No default beans.xml exists");
		}

		TomcatApplication application = new TomcatApplication(beanXml);
		application.init();
	}

	public static void run() {
		run(getDefaultConfigPath());
	}
}

final class TldSkipPatterns {

	private static final Set<String> TOMCAT;

	static {
		// Same as Tomcat
		Set<String> patterns = new LinkedHashSet<String>();
		patterns.add("ant-*.jar");
		patterns.add("aspectj*.jar");
		patterns.add("commons-beanutils*.jar");
		patterns.add("commons-codec*.jar");
		patterns.add("commons-collections*.jar");
		patterns.add("commons-dbcp*.jar");
		patterns.add("commons-digester*.jar");
		patterns.add("commons-fileupload*.jar");
		patterns.add("commons-httpclient*.jar");
		patterns.add("commons-io*.jar");
		patterns.add("commons-lang*.jar");
		patterns.add("commons-logging*.jar");
		patterns.add("commons-math*.jar");
		patterns.add("commons-pool*.jar");
		patterns.add("geronimo-spec-jaxrpc*.jar");
		patterns.add("h2*.jar");
		patterns.add("hamcrest*.jar");
		patterns.add("hibernate*.jar");
		patterns.add("jaxb-runtime-*.jar");
		patterns.add("jmx*.jar");
		patterns.add("jmx-tools-*.jar");
		patterns.add("jta*.jar");
		patterns.add("junit-*.jar");
		patterns.add("httpclient*.jar");
		patterns.add("log4j-*.jar");
		patterns.add("mail*.jar");
		patterns.add("org.hamcrest*.jar");
		patterns.add("slf4j*.jar");
		patterns.add("tomcat-embed-core-*.jar");
		patterns.add("tomcat-embed-logging-*.jar");
		patterns.add("tomcat-jdbc-*.jar");
		patterns.add("tomcat-juli-*.jar");
		patterns.add("tools.jar");
		patterns.add("wsdl4j*.jar");
		patterns.add("xercesImpl-*.jar");
		patterns.add("xmlParserAPIs-*.jar");
		patterns.add("xml-apis-*.jar");
		TOMCAT = Collections.unmodifiableSet(patterns);
	}

	private static final Set<String> ADDITIONAL;

	static {
		// Additional typical for Spring Boot applications
		Set<String> patterns = new LinkedHashSet<String>();
		patterns.add("antlr-*.jar");
		patterns.add("aopalliance-*.jar");
		patterns.add("aspectjrt-*.jar");
		patterns.add("aspectjweaver-*.jar");
		patterns.add("classmate-*.jar");
		patterns.add("dom4j-*.jar");
		patterns.add("ecj-*.jar");
		patterns.add("ehcache-core-*.jar");
		patterns.add("hibernate-core-*.jar");
		patterns.add("hibernate-commons-annotations-*.jar");
		patterns.add("hibernate-entitymanager-*.jar");
		patterns.add("hibernate-jpa-2.1-api-*.jar");
		patterns.add("hibernate-validator-*.jar");
		patterns.add("hsqldb-*.jar");
		patterns.add("jackson-annotations-*.jar");
		patterns.add("jackson-core-*.jar");
		patterns.add("jackson-databind-*.jar");
		patterns.add("jandex-*.jar");
		patterns.add("javassist-*.jar");
		patterns.add("jboss-logging-*.jar");
		patterns.add("jboss-transaction-api_*.jar");
		patterns.add("jcl-over-slf4j-*.jar");
		patterns.add("jdom-*.jar");
		patterns.add("jul-to-slf4j-*.jar");
		patterns.add("log4j-over-slf4j-*.jar");
		patterns.add("logback-classic-*.jar");
		patterns.add("logback-core-*.jar");
		patterns.add("rome-*.jar");
		patterns.add("slf4j-api-*.jar");
		patterns.add("spring-aop-*.jar");
		patterns.add("spring-aspects-*.jar");
		patterns.add("spring-beans-*.jar");
		patterns.add("spring-boot-*.jar");
		patterns.add("spring-core-*.jar");
		patterns.add("spring-context-*.jar");
		patterns.add("spring-data-*.jar");
		patterns.add("spring-expression-*.jar");
		patterns.add("spring-jdbc-*.jar,");
		patterns.add("spring-orm-*.jar");
		patterns.add("spring-oxm-*.jar");
		patterns.add("spring-tx-*.jar");
		patterns.add("snakeyaml-*.jar");
		patterns.add("tomcat-embed-el-*.jar");
		patterns.add("validation-api-*.jar");
		patterns.add("xml-apis-*.jar");
		ADDITIONAL = Collections.unmodifiableSet(patterns);
	}

	static final Set<String> DEFAULT;

	static {
		Set<String> patterns = new LinkedHashSet<String>();
		patterns.addAll(TOMCAT);
		patterns.addAll(ADDITIONAL);
		DEFAULT = Collections.unmodifiableSet(patterns);
	}

	private TldSkipPatterns() {
	}

}
