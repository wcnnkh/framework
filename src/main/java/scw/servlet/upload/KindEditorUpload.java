package scw.servlet.upload;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.alibaba.fastjson.JSONObject;

import scw.common.exception.ShuChaoWenRuntimeException;
import scw.common.utils.XTime;
import scw.servlet.Request;

public final class KindEditorUpload implements Upload {
	private static final HashMap<String, String> extMap = new HashMap<String, String>();
	static {
		extMap.put("image", "gif,jpg,jpeg,png,bmp");
		extMap.put("flash", "swf,flv");
		extMap.put("media", "swf,flv,mp3,wav,wma,wmv,mid,avi,mpg,asf,rm,rmvb");
		extMap.put("file", "doc,docx,xls,xlsx,ppt,htm,html,txt,zip,rar,gz,bz2");
	}

	protected final String rootPath;
	protected final String urlPrefix;

	public KindEditorUpload(String rootPath, String urlPrefix) {
		this.rootPath = rootPath;
		File file = new File(rootPath);
		if(!file.exists()){
			throw new ShuChaoWenRuntimeException("目录不存在：" + rootPath);
		}
		 
		if(!file.isDirectory()){
			throw new ShuChaoWenRuntimeException("这不是一个目录：" + rootPath);
		}
		this.urlPrefix = urlPrefix;
	}

	protected String getSavePath(Request request) {
		return rootPath;
	}

	public void execute(Request request) throws IOException {
		if (!ServletFileUpload.isMultipartContent(request)) {
			request.getResponse().write(getError("请选择文件"));
			return;
		}

		String dirName = request.getParameter("dir");
		if (dirName == null) {
			dirName = "image";
		}
		if (!extMap.containsKey(dirName)) {
			request.getResponse().write(getError("目录名不正确"));
			return;
		}

		StringBuilder url = new StringBuilder();
		url.append(urlPrefix);
		url.append("/");
		StringBuilder sb = new StringBuilder();
		sb.append(getSavePath(request));
		sb.append(File.separator);
		String ymd = XTime.format(System.currentTimeMillis(), "yyyyMMdd");
		sb.append(ymd);
		sb.append(File.separator);

	}

	private String getError(String message) {
		JSONObject obj = new JSONObject();
		obj.put("error", 1);
		obj.put("message", message);
		return obj.toJSONString();
	}
}
