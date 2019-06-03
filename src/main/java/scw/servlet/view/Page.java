package scw.servlet.view;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;
import scw.core.exception.NotFoundException;
import scw.core.logger.DebugLogger;
import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;
import scw.core.net.ContentType;
import scw.core.utils.ConfigUtils;
import scw.core.utils.StringUtils;
import scw.servlet.Request;
import scw.servlet.Response;
import scw.servlet.ServletUtils;
import scw.servlet.View;

/**
 * freemarker
 * 
 * @author shuchaowen
 *
 */
public class Page extends HashMap<String, Object> implements View {
	private static Logger logger = LoggerFactory.getLogger(Page.class);

	private static Map<String, PageType> suffixMap = new HashMap<String, PageType>();
	private static final long serialVersionUID = 1L;
	private static Configuration freemarkerConfiguration;
	private static String freemarker_default_encoding = "utf-8";

	static {
		suffixMap.put(".jsp", PageType.JSP);
		suffixMap.put(".ftl", PageType.FREEMARKER);
		suffixMap.put(".html", PageType.FREEMARKER);
	}

	public static void setPageType(String suffix, PageType pageType) {
		Iterator<Entry<String, PageType>> iterator = suffixMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, PageType> entry = iterator.next();
			if (entry.getKey().endsWith(suffix) || suffix.endsWith(entry.getKey())) {
				iterator.remove();
			}
		}
		suffixMap.put(suffix, pageType);
	}

	private String page;
	private boolean freemarkerAppendAttrs = false;
	private boolean appendParams = false;

	public static synchronized void initFreemarker(Version version, String default_encoding, String rootPath) {
		if (freemarkerConfiguration == null) {
			freemarkerConfiguration = new Configuration(version);
		}

		freemarkerConfiguration.setDefaultEncoding(default_encoding);
		String workPath = ConfigUtils.getWorkPath();
		if (workPath == null) {
			throw new NotFoundException("找不到WEB-INF目录");
		}

		try {
			freemarkerConfiguration.setDirectoryForTemplateLoading(
					new File(StringUtils.isNull(rootPath) ? workPath : ConfigUtils.format(rootPath)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		freemarker_default_encoding = default_encoding;
		freemarkerConfiguration.setObjectWrapper(new DefaultObjectWrapper(version));
	}

	public static void initFreemarker(final String rootPath) {
		String workPath = ConfigUtils.getWorkPath();
		if (workPath == null) {
			throw new NotFoundException("找不到WEB-INF目录");
		}

		initFreemarker(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS, freemarker_default_encoding,
				StringUtils.isNull(rootPath) ? workPath : ConfigUtils.format(rootPath));
	}

	public static Configuration getFreemarkerConfiguration() {
		if (freemarkerConfiguration == null) {
			defaultInitFreemarker();
		}
		return freemarkerConfiguration;
	}

	private synchronized static void defaultInitFreemarker() {
		if (freemarkerConfiguration == null) {
			freemarkerConfiguration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
			freemarkerConfiguration.setDefaultEncoding(freemarker_default_encoding);

			String workPath = ConfigUtils.getWorkPath();
			if (workPath == null) {
				throw new NotFoundException("找不到WEB-INF目录");
			}

			try {
				freemarkerConfiguration.setDirectoryForTemplateLoading(new File(workPath));
			} catch (IOException e) {
				e.printStackTrace();
			}
			freemarkerConfiguration
					.setObjectWrapper(new DefaultObjectWrapper(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS));
		}
	}

	public Page() {
		this(null);
	}

	public Page(String page) {
		this.page = page;
	}

	public void setFreemarkerAppendAttrs(boolean appendAttrs) {
		this.freemarkerAppendAttrs = appendAttrs;
	}

	public void setAppendParams(boolean appendParams) {
		this.appendParams = appendParams;
	}

	public Page put(String name, Object value) {
		super.put(name, value);
		return this;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public void render(Request request, Response response) throws Exception {
		String realPage = getPage();
		if (StringUtils.isEmpty(realPage) && request instanceof HttpServletRequest) {
			realPage = ((HttpServletRequest) request).getServletPath();
		}

		Iterator<String> iterator = suffixMap.keySet().iterator();
		PageType pageType = null;
		while (iterator.hasNext()) {
			String suffix = iterator.next();
			if (realPage.endsWith(suffix)) {
				pageType = suffixMap.get(suffix);
				break;
			}
		}

		if (pageType == null) {
			logger.error("not found page type :" + realPage);
			return;
		}

		switch (pageType) {
		case JSP:
			if (response.getContentType() == null) {
				response.setContentType(ContentType.TEXT_HTML);
			}

			for (Entry<String, Object> entry : entrySet()) {
				request.setAttribute(entry.getKey(), entry.getValue());
			}

			ServletUtils.jsp(request, response, realPage);
			break;
		case FREEMARKER:
			if (freemarkerConfiguration == null) {
				defaultInitFreemarker();
			}

			response.setCharacterEncoding(freemarker_default_encoding);
			if (response.getContentType() == null) {
				response.setContentType(ContentType.TEXT_HTML);
			}

			if (freemarkerAppendAttrs) {
				Enumeration<?> enumeration = request.getAttributeNames();
				while (enumeration.hasMoreElements()) {
					Object obj = enumeration.nextElement();
					if (obj != null) {
						String name = obj.toString();
						put(name, request.getAttribute(name));
					}
				}
			}

			if (appendParams) {
				putAll(request.getParameterMap());
			}

			Template template = freemarkerConfiguration.getTemplate(realPage);
			try {
				template.process(this, response.getWriter());
			} catch (TemplateException e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}

		if (response instanceof DebugLogger) {
			if (((DebugLogger) response).isDebugEnabled()) {
				StringBuilder sb = new StringBuilder();
				sb.append(pageType);
				sb.append(":");
				sb.append(realPage);
				((DebugLogger) response).debug(sb.toString());
			}
		}
	}
}
