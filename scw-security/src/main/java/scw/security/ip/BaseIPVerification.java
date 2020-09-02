package scw.security.ip;

import java.util.HashSet;
import java.util.List;

import scw.core.Constants;
import scw.core.utils.ArrayUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.io.Resource;
import scw.io.ResourceUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.util.StringMatcher;

public class BaseIPVerification extends HashSet<String> implements IPVerification {
	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerUtils.getLogger(getClass());

	protected void appendIPFile(String path) {
		Resource resource = ResourceUtils.getResourceOperations().getResource(path);
		if (resource.exists()) {
			List<String> contentList = ResourceUtils.getLines(resource, Constants.DEFAULT_CHARSET);
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
			if (StringMatcher.DEFAULT.match(ip, matchIp)) {
				return true;
			}
		}
		return false;
	}
}
