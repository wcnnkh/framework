package io.basc.framework.security.ip;

import java.util.HashSet;

import io.basc.framework.env.Environment;
import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourceUtils;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.ArrayUtils;
import io.basc.framework.util.StringMatcher;
import io.basc.framework.util.StringMatchers;
import io.basc.framework.util.StringUtils;

public class BaseIPVerification extends HashSet<String> implements IPVerification {
	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(getClass());
	private StringMatcher matcher = StringMatchers.SIMPLE;

	public StringMatcher getMatcher() {
		return matcher;
	}

	public void setMatcher(StringMatcher matcher) {
		this.matcher = matcher;
	}

	protected void appendIPFile(Environment environment, Resource resource) {
		if (resource != null && resource.exists()) {
			ResourceUtils.readLines(resource, environment.getCharsetName()).forEachRemaining((content) -> {
				if (StringUtils.isEmpty(content)) {
					return;
				}

				String[] arr = StringUtils.splitToArray(content);
				if (ArrayUtils.isEmpty(arr)) {
					return;
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
			});
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
