package scw.security.ip;

import java.util.HashSet;
import java.util.List;

import scw.core.Constants;
import scw.core.resource.ResourceUtils;
import scw.core.utils.ArrayUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public class BaseIPVerification extends HashSet<String> implements IPVerification {
	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerUtils.getLogger(getClass());

	protected void appendIPFile(String path) {
		if (ResourceUtils.isExist(path)) {
			List<String> contentList = ResourceUtils.getFileContentLineList(path, Constants.DEFAULT_CHARSET_NAME);
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

	public boolean verification(String data) {
		String[] arr = StringUtils.commonSplit(data);
		if (ArrayUtils.isEmpty(arr)) {
			return false;
		}

		for (String ip : arr) {
			if (contains(ip)) {
				return true;
			}
		}
		return false;
	}

}
