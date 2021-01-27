package scw.security.ip;

import java.util.HashSet;
import java.util.List;

import scw.core.utils.ArrayUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.env.Environment;
import scw.io.Resource;
import scw.io.ResourceUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.util.DefaultStringMatcher;
import scw.util.StringMatcher;

public class BaseIPVerification extends HashSet<String> implements IPVerification {
	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerUtils.getLogger(getClass());
	private StringMatcher matcher = DefaultStringMatcher.getInstance();
	
	public StringMatcher getMatcher() {
		return matcher;
	}

	public void setMatcher(StringMatcher matcher) {
		this.matcher = matcher;
	}

	protected void appendIPFile(Environment environment, String path) {
		Resource resource = environment.getResource(path);
		if (resource != null && resource.exists()) {
			List<String> contentList = ResourceUtils.getLines(resource, environment.getCharsetName());
			if (!CollectionUtils.isEmpty(contentList)) {
				for (String content : contentList) {
					if (StringUtils.isEmpty(content)) {
						continue;
					}

					String[] arr = StringUtils.commonSplit(content);
					if (ArrayUtils.isEmpty(arr)) {
						continue;
					}

					for (String ip : arr) {
						if (StringUtils.isEmpty(ip)) {
							continue;
						}

						ip = ip.trim();
						if (logger.isDebugEnabled()) {
							logger.debug("add ip {}", ip);
						}
						add(ip);
					}
				}
			}
		}
	}

	public boolean verification(String ip) {
		return contains(ip) || testIp(ip);
	}

	protected final boolean testIp(String matchIp) {
		for (String ip : this) {
			if (getMatcher().match(ip, matchIp)) {
				return true;
			}
		}
		return false;
	}
}
