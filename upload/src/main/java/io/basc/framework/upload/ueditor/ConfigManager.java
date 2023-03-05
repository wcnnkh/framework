package io.basc.framework.upload.ueditor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import io.basc.framework.json.JsonUtils;
import io.basc.framework.json.JsonArray;
import io.basc.framework.json.JsonObject;
import io.basc.framework.upload.ueditor.define.ActionMap;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;

/**
 * 配置管理器
 * 
 * @author hancong03@baidu.com
 *
 */
public final class ConfigManager {

	private final String rootPath;
	private String originalPath;
	private static final String configFileName = "config.json";
	private String parentPath = null;
	private JsonObject jsonConfig = null;
	// 涂鸦上传filename定义
	private final static String SCRAWL_FILE_NAME = "scrawl";
	// 远程图片抓取filename定义
	private final static String REMOTE_FILE_NAME = "remote";

	/*
	 * 通过一个给定的路径构建一个配置管理器， 该管理器要求地址路径所在目录下必须存在config.properties文件
	 */
	private ConfigManager(String rootPath, String contextPath, String uri) throws FileNotFoundException, IOException {
		this.rootPath = Assert.secureFilePathArgument(rootPath, "rootPath");
		String contextPathToUse = StringUtils.cleanPath(contextPath);
		if (contextPathToUse.length() > 0) {
			this.originalPath = this.rootPath + StringUtils.cleanPath(uri).substring(contextPathToUse.length());
		} else {
			this.originalPath = this.rootPath + StringUtils.cleanPath(uri);
		}
		this.originalPath = Assert.secureFilePath(originalPath);
		this.initEnv();

	}

	/**
	 * 配置管理器构造工厂
	 * 
	 * @param rootPath    服务器根路径
	 * @param contextPath 服务器所在项目路径
	 * @param uri         当前访问的uri
	 * @return 配置管理器实例或者null
	 */
	public static ConfigManager getInstance(String rootPath, String contextPath, String uri) {

		try {
			return new ConfigManager(rootPath, contextPath, uri);
		} catch (Exception e) {
			return null;
		}

	}

	// 验证配置文件加载是否正确
	public boolean valid() {
		return this.jsonConfig != null;
	}

	public JsonObject getAllConfig() {

		return this.jsonConfig;

	}

	public Map<String, Object> getConfig(int type) {

		Map<String, Object> conf = new HashMap<String, Object>();
		String savePath = null;

		switch (type) {

		case ActionMap.UPLOAD_FILE:
			conf.put("isBase64", "false");
			conf.put("maxSize", this.jsonConfig.getAsLong("fileMaxSize"));
			conf.put("allowFiles", this.getArray("fileAllowFiles"));
			conf.put("fieldName", this.jsonConfig.getAsString("fileFieldName"));
			savePath = this.jsonConfig.getAsString("filePathFormat");
			break;

		case ActionMap.UPLOAD_IMAGE:
			conf.put("isBase64", "false");
			conf.put("maxSize", this.jsonConfig.getAsLong("imageMaxSize"));
			conf.put("allowFiles", this.getArray("imageAllowFiles"));
			conf.put("fieldName", this.jsonConfig.getAsString("imageFieldName"));
			savePath = this.jsonConfig.getAsString("imagePathFormat");
			break;

		case ActionMap.UPLOAD_VIDEO:
			conf.put("maxSize", this.jsonConfig.getAsLong("videoMaxSize"));
			conf.put("allowFiles", this.getArray("videoAllowFiles"));
			conf.put("fieldName", this.jsonConfig.getAsString("videoFieldName"));
			savePath = this.jsonConfig.getAsString("videoPathFormat");
			break;

		case ActionMap.UPLOAD_SCRAWL:
			conf.put("filename", ConfigManager.SCRAWL_FILE_NAME);
			conf.put("maxSize", this.jsonConfig.getAsLong("scrawlMaxSize"));
			conf.put("fieldName", this.jsonConfig.getAsString("scrawlFieldName"));
			conf.put("isBase64", "true");
			savePath = this.jsonConfig.getAsString("scrawlPathFormat");
			break;

		case ActionMap.CATCH_IMAGE:
			conf.put("filename", ConfigManager.REMOTE_FILE_NAME);
			conf.put("filter", this.getArray("catcherLocalDomain"));
			conf.put("maxSize", this.jsonConfig.getAsLong("catcherMaxSize"));
			conf.put("allowFiles", this.getArray("catcherAllowFiles"));
			conf.put("fieldName", this.jsonConfig.getAsString("catcherFieldName") + "[]");
			savePath = this.jsonConfig.getAsString("catcherPathFormat");
			break;

		case ActionMap.LIST_IMAGE:
			conf.put("allowFiles", this.getArray("imageManagerAllowFiles"));
			conf.put("dir", this.jsonConfig.getAsString("imageManagerListPath"));
			conf.put("count", this.jsonConfig.getAsInt("imageManagerListSize"));
			break;

		case ActionMap.LIST_FILE:
			conf.put("allowFiles", this.getArray("fileManagerAllowFiles"));
			conf.put("dir", this.jsonConfig.getAsString("fileManagerListPath"));
			conf.put("count", this.jsonConfig.getAsInt("fileManagerListSize"));
			break;

		}

		conf.put("savePath", savePath);
		conf.put("rootPath", this.rootPath);
		return conf;

	}

	private void initEnv() throws FileNotFoundException, IOException {

		File file = new File(this.originalPath);

		if (!file.isAbsolute()) {
			file = new File(file.getAbsolutePath());
		}

		this.parentPath = file.getParent();

		String configContent = this.readFile(this.getConfigPath());

		try {
			JsonObject jsonConfig = JsonUtils.getSupport().parseObject(configContent);
			this.jsonConfig = jsonConfig;
		} catch (Exception e) {
			this.jsonConfig = null;
		}

	}

	private String getConfigPath() {
		return this.parentPath + File.separator + ConfigManager.configFileName;
	}

	private String[] getArray(String key) {

		JsonArray jsonArray = this.jsonConfig.getJsonArray(key);
		String[] result = new String[jsonArray.size()];

		for (int i = 0, len = jsonArray.size(); i < len; i++) {
			result[i] = jsonArray.getAsString(i);
		}

		return result;

	}

	private String readFile(String path) throws IOException {

		StringBuilder builder = new StringBuilder();

		try {

			InputStreamReader reader = new InputStreamReader(new FileInputStream(path), "UTF-8");
			BufferedReader bfReader = new BufferedReader(reader);

			String tmpContent = null;

			while ((tmpContent = bfReader.readLine()) != null) {
				builder.append(tmpContent);
			}

			bfReader.close();

		} catch (UnsupportedEncodingException e) {
			// 忽略
		}

		return this.filter(builder.toString());

	}

	// 过滤输入字符串, 剔除多行注释以及替换掉反斜杠
	private String filter(String input) {

		return input.replaceAll("/\\*[\\s\\S]*?\\*/", "");

	}

}
