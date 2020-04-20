package scw.async;

import scw.core.instance.definition.InstanceFactoryAccessor;
import scw.json.JSONSupport;
import scw.json.JSONUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public abstract class AbstractAsyncRunnable extends InstanceFactoryAccessor implements AsyncRunnable {
	private static final long serialVersionUID = 1L;
	protected transient final Logger logger = LoggerUtils.getLogger(getClass());

	public JSONSupport getJsonSupport() {
		return JSONUtils.DEFAULT_JSON_SUPPORT;
	}
}
