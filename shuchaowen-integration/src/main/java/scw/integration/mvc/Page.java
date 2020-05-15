package scw.integration.mvc;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;
import scw.core.GlobalPropertyFactory;
import scw.core.utils.StringUtils;
import scw.lang.NotFoundException;
import scw.mvc.page.AbstractPage;
import scw.mvc.servlet.page.Jsp;
import scw.net.MimeType;
import scw.net.MimeTypeUtils;
import scw.net.http.server.mvc.HttpChannel;
import scw.net.http.server.mvc.MVCUtils;

/**
 * 不再推荐使用，下个版本弃用
 * 
 * @author shuchaowen
 *
 */
public class Page extends AbstractPage {
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

	private boolean freemarkerAppendAttrs = false;
	private boolean appendParams = false;

	public static synchronized void initFreemarker(Version version, String default_encoding, String rootPath) {
		if (freemarkerConfiguration == null) {
			freemarkerConfiguration = new Configuration(version);
		}

		freemarkerConfiguration.setDefaultEncoding(default_encoding);
		String workPath = GlobalPropertyFactory.getInstance().getWorkPath();
		if (workPath == null) {
			throw new NotFoundException("找不到WEB-INF目录");
		}

		try {
			freemarkerConfiguration.setDirectoryForTemplateLoading(new File(StringUtils.isNull(rootPath) ? workPath
					: GlobalPropertyFactory.getInstance().format(rootPath, true)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		freemarker_default_encoding = default_encoding;
		freemarkerConfiguration.setObjectWrapper(new DefaultObjectWrapper(version));
	}

	public static void initFreemarker(final String rootPath) {
		String workPath = GlobalPropertyFactory.getInstance().getWorkPath();
		if (workPath == null) {
			throw new NotFoundException("找不到WEB-INF目录");
		}

		initFreemarker(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS, freemarker_default_encoding,
				StringUtils.isNull(rootPath) ? workPath : GlobalPropertyFactory.getInstance().format(rootPath, true));
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

			String workPath = GlobalPropertyFactory.getInstance().getWorkPath();
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
		super(page);
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

	public void render(HttpChannel httpChannel) throws IOException {
		String realPage = getPage();
		if (StringUtils.isEmpty(realPage)) {
			realPage = httpChannel.getRequest().getController();
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
			httpChannel.getLogger().error("not found page type :" + realPage);
			return;
		}

		switch (pageType) {
		case JSP:
			if (httpChannel.getResponse().getContentType() == null) {
				httpChannel.getResponse().setContentType(MimeTypeUtils.TEXT_HTML_VALUE);
			}

			Jsp jsp = new Jsp(realPage);
			jsp.putAll(this);
			jsp.render(httpChannel);
			break;
		case FREEMARKER:
			if (freemarkerConfiguration == null) {
				defaultInitFreemarker();
			}

			if (httpChannel.getResponse().getContentType() == null) {
				httpChannel.getResponse()
						.setContentType(new MimeType(MimeTypeUtils.TEXT_HTML_VALUE, freemarker_default_encoding));
			}

			if (freemarkerAppendAttrs) {
				Enumeration<?> enumeration = httpChannel.getRequest().getAttributeNames();
				while (enumeration.hasMoreElements()) {
					Object obj = enumeration.nextElement();
					if (obj != null) {
						String name = obj.toString();
						if (MVCUtils.isSystemAttribute(name)) {
							continue;
						}

						put(name, httpChannel.getRequest().getAttribute(name));
					}
				}
			}

			if (appendParams) {
				putAll(httpChannel.getRequest().getParameterMap());
			}

			Template template = freemarkerConfiguration.getTemplate(realPage);
			try {
				template.process(this, httpChannel.getResponse().getWriter());
			} catch (TemplateException e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}

		if (httpChannel.isLogEnabled()) {
			httpChannel.log("{}:{}", pageType, realPage);
		}
	}
}
