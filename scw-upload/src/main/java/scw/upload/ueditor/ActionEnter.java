package scw.upload.ueditor;

import java.util.List;
import java.util.Map;

import scw.upload.ueditor.define.ActionMap;
import scw.upload.ueditor.define.AppInfo;
import scw.upload.ueditor.define.BaseState;
import scw.upload.ueditor.define.State;
import scw.upload.ueditor.hunter.FileManager;
import scw.upload.ueditor.hunter.ImageHunter;
import scw.upload.ueditor.upload.Uploader;
import scw.util.XUtils;
import scw.web.MultiPartServerHttpRequest;
import scw.web.ServerHttpRequest;

public class ActionEnter {

	private ServerHttpRequest request = null;

	private String rootPath = null;
	private String contextPath = null;

	private String actionType = null;

	private ConfigManager configManager = null;

	public ActionEnter(ServerHttpRequest request, String rootPath) {
		this.request = request;
		this.rootPath = rootPath;
		this.actionType = request.getParameterMap().getFirst("action");
		this.contextPath = request.getContextPath();
		this.configManager = ConfigManager.getInstance(this.rootPath, this.contextPath, request.getURI().toString());
	}

	public String invoke() {

		if (actionType == null || !ActionMap.mapping.containsKey(actionType)) {
			return new BaseState(false, AppInfo.INVALID_ACTION).toJSONString();
		}

		if (this.configManager == null || !this.configManager.valid()) {
			return new BaseState(false, AppInfo.CONFIG_ERROR).toJSONString();
		}

		State state = null;

		int actionCode = ActionMap.getType(this.actionType);

		Map<String, Object> conf = null;

		switch (actionCode) {

		case ActionMap.CONFIG:
			return this.configManager.getAllConfig().toString();

		case ActionMap.UPLOAD_IMAGE:
		case ActionMap.UPLOAD_SCRAWL:
		case ActionMap.UPLOAD_VIDEO:
		case ActionMap.UPLOAD_FILE:
			conf = this.configManager.getConfig(actionCode);
			MultiPartServerHttpRequest multiPartServerHttpRequest = XUtils.getDelegate(request,
					MultiPartServerHttpRequest.class);
			if (multiPartServerHttpRequest == null) {
				state = new BaseState(false, AppInfo.NOT_MULTIPART_CONTENT);
			} else {
				state = new Uploader(multiPartServerHttpRequest, conf).doExec();
			}
			break;

		case ActionMap.CATCH_IMAGE:
			conf = configManager.getConfig(actionCode);
			List<String> list = this.request.getParameterMap().get((String) conf.get("fieldName"));
			state = new ImageHunter(conf).capture(list);
			break;

		case ActionMap.LIST_IMAGE:
		case ActionMap.LIST_FILE:
			conf = configManager.getConfig(actionCode);
			int start = this.getStartIndex();
			state = new FileManager(conf).listFile(start);
			break;

		}

		return state.toJSONString();

	}

	public int getStartIndex() {

		String start = this.request.getParameterMap().getFirst("start");

		try {
			return Integer.parseInt(start);
		} catch (Exception e) {
			return 0;
		}

	}

}