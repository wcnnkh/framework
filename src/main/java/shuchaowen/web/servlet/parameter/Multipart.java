package shuchaowen.web.servlet.parameter;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;

import shuchaowen.web.servlet.RequestParameter;
import shuchaowen.common.Logger;
import shuchaowen.web.servlet.Request;

public class Multipart extends RequestParameter{
	private Map<String, List<String>> paramMap = new HashMap<String, List<String>>();
	private Map<String, List<FileItem>> fileItemMap = new HashMap<String, List<FileItem>>();
	private List<String> keys = new ArrayList<String>();
	private List<FileItem> fileItemList = new ArrayList<FileItem>();
	private HttpServletRequest httpServletRequest;
	
	public Multipart(Request request) throws IOException{
		super(request);
		init(request, request.isDebug());
	}
	
	private void init(HttpServletRequest httpServletRequest, boolean debug){
		RequestContext requestContext;
		DiskFileItemFactory factory;
		ServletFileUpload upload;
		List<FileItem> items;
		Iterator<FileItem> iterator;
		FileItem fileItem;
		List<String> values;
		try {
			requestContext = new ServletRequestContext(httpServletRequest);
			if (FileUpload.isMultipartContent(requestContext)) {
				factory = new DiskFileItemFactory();
				upload = new ServletFileUpload(factory);
				items = upload.parseRequest(requestContext);
				iterator = items.iterator();
				while (iterator.hasNext()) {
					fileItem = iterator.next();
					if (fileItem == null) {
						continue;
					}

					if (fileItem.isFormField()) {
						Logger.debug("form表单字段[name=" + fileItem.getFieldName() + "]");
						values = paramMap.get(fileItem.getFieldName());
						if (values == null) {
							values = new ArrayList<String>();
							keys.add(fileItem.getFieldName());
						}

						String value = fileItem.getString();
						values.add(value);
						paramMap.put(fileItem.getFieldName(), values);

						if (debug) {
							StringBuilder sb = new StringBuilder();
							sb.append("form表单字段name=");
							sb.append(fileItem.getFieldName());
							sb.append(",value=");
							sb.append(value);
							Logger.debug(sb.toString());
						}
					} else {
						fileItemList.add(fileItem);
						List<FileItem> fList = fileItemMap.get(fileItem.getFieldName());
						if (fList == null) {
							fList = new ArrayList<FileItem>();
							keys.add(fileItem.getFieldName());
						}
						fList.add(fileItem);
						fileItemMap.put(fileItem.getFieldName(), fList);

						if (debug) {
							StringBuilder sb = new StringBuilder();
							sb.append("form表单文件[name=");
							sb.append(fileItem.getFieldName());
							sb.append(", size=");
							sb.append(fileItem.getSize());
							sb.append(", fileName=");
							sb.append(fileItem.getName());
							sb.append("]");
							Logger.debug(sb.toString());
						}
					}
				}
			} else {
				Logger.debug("请求类型异常");
			}
		} catch (Exception e) {
			Logger.error("REQUEST", "获取上传文件请求内容异常！！", e);
		}
	}
	
	protected String getValue(String key) {
		String value = httpServletRequest.getParameter(key);
		if (value == null) {
			return getTextValue(key, httpServletRequest.getCharacterEncoding());
		}
		return value;
	}

	/**
	 * 根据FileItemName来查找文件，如果找不到就使用全部的
	 * 
	 * @param key
	 * @return
	 */
	protected List<FileItem> getMyFileItemList(String key) {
		List<FileItem> list = getFileItemList(key);
		if (list == null) {
			return getFileItemList();
		}
		return list;
	}

	public Map<String, List<String>> getParamMap() {
		return paramMap;
	}

	public List<FileItem> getFileItemList() {
		return fileItemList;
	}

	public List<String> getTextValues(String key) {
		return paramMap.get(key);
	}

	public String getTextValue(String key) {
		List<String> values = paramMap.get(key);
		if (values == null) {
			return null;
		}

		return values.get(0);
	}

	public String getTextValueToUTF_8(String key) {
		String v = getValue(key);
		if (v != null) {
			try {
				return new String(v.getBytes("ISO-8859-1"), "UTF-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return v;
	}

	public String getTextValue(String key, String charsetName) {
		String value = getValue(key);
		if (value != null) {
			try {
				return new String(charsetName.getBytes("iso-8859-1"), charsetName);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public List<String> getKeys() {
		return keys;
	}

	public static void save(String toPath, FileItem... fileItems) {
		for (FileItem fileItem : fileItems) {
			File file;
			try {
				file = new File(toPath + fileItem.getName());
				fileItem.write(file);
			} catch (Exception e) {
				Logger.error("upload", "保存上传的文件异常,路径" + toPath + ",文件名：" + fileItem.getName(), e);
			}
		}
	}

	public List<FileItem> getFileItemList(String key) {
		return fileItemMap.get(key);
	}

	public FileItem getFileItem(String key) {
		List<FileItem> list = getFileItemList(key);
		if (list == null || list.size() == 0) {
			return null;
		}
		return list.get(0);
	}

	public FileItem getFirstFileItem() {
		if (fileItemList != null && fileItemList.size() != 0) {
			return fileItemList.get(0);
		}
		return null;
	}
}
