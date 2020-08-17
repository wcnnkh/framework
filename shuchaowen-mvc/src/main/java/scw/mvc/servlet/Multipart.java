package scw.mvc.servlet;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;

import scw.core.Constants;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.util.LinkedMultiValueMap;
import scw.util.MultiValueMap;

public final class Multipart extends LinkedMultiValueMap<String, FileItem> {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerUtils.getLogger(Multipart.class);
	private HttpServletRequest request;

	public Multipart(HttpServletRequest request) throws IOException {
		this.request = request;
		RequestContext requestContext;
		DiskFileItemFactory factory;
		ServletFileUpload upload;
		List<FileItem> items;
		Iterator<FileItem> iterator;
		FileItem fileItem;
		try {
			requestContext = new ServletRequestContext(request);
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

					add(fileItem.getFieldName(), fileItem);
					if (fileItem.isFormField()) {
						logger.info("form表单字段name={}, value=",
								fileItem.getFieldName(), fileItem.toString());
					} else {
						logger.info("form表单文件[name={}, size={}, fileName={}]",
								fileItem.getFieldName(), fileItem.getSize(),
								fileItem.getName());
					}
				}
			} else {
				logger.error("请求类型异常" + request.getServletPath() + ",method="
						+ request.getMethod());
			}
		} catch (Exception e) {
			logger.error(e, "获取上传文件请求内容异常！！");
		}
	}

	public MultiValueMap<String, FileItem> getFieldItemMap(boolean formField,
			boolean checkSize) {
		MultiValueMap<String, FileItem> map = new LinkedMultiValueMap<String, FileItem>();
		for (Entry<String, List<FileItem>> entry : entrySet()) {
			List<FileItem> list = entry.getValue();
			if (CollectionUtils.isEmpty(list)) {
				continue;
			}

			List<FileItem> values = new ArrayList<FileItem>(list.size());
			for (FileItem item : list) {
				if (item == null) {
					continue;
				}

				if (checkSize && item.getSize() == 0) {
					continue;
				}

				if (formField) {
					if (item.isFormField()) {
						values.add(item);
					}
				} else {
					if (!item.isFormField()) {
						values.add(item);
					}
				}
			}

			if (CollectionUtils.isEmpty(values)) {
				continue;
			}

			map.put(entry.getKey(), values);
		}
		return map;
	}

	public FileItem getFirstFileItem(boolean formField) {
		for (List<FileItem> fileItemList : values()) {
			for (FileItem fileItem : fileItemList) {
				if (fileItem == null) {
					continue;
				}

				if (formField) {
					if (fileItem.isFormField()) {
						return fileItem;
					}
				} else {
					if (!fileItem.isFormField()) {
						return fileItem;
					}
				}
			}
		}
		return null;
	}

	/**
	 * 获取所有字段
	 * 
	 * @param formField
	 * @param checkSize
	 * @return
	 */
	public Collection<FileItem> getAllFileItemList(boolean formField,
			boolean checkSize) {
		LinkedList<FileItem> list = new LinkedList<FileItem>();
		for (List<FileItem> fileItemList : values()) {
			for (FileItem fileItem : fileItemList) {
				if (fileItem == null) {
					continue;
				}

				if (checkSize && fileItem.getSize() == 0) {
					continue;
				}

				if (formField) {
					if (fileItem.isFormField()) {
						list.add(fileItem);
					}
				} else {
					if (!fileItem.isFormField()) {
						list.add(fileItem);
					}
				}
			}
		}
		return list;
	}

	public List<FileItem> getFieldItemList(String name, boolean formField,
			boolean checkSize) {
		List<FileItem> fileItems = get(name);
		if (CollectionUtils.isEmpty(fileItems)) {
			return null;
		}

		List<FileItem> list = new ArrayList<FileItem>(fileItems.size());
		for (FileItem fileItem : fileItems) {
			if (fileItem == null) {
				continue;
			}

			if (checkSize && fileItem.getSize() == 0) {
				continue;
			}

			if (formField) {
				if (fileItem.isFormField()) {
					list.add(fileItem);
				}
			} else {
				if (!fileItem.isFormField()) {
					list.add(fileItem);
				}
			}
		}
		return list;
	}

	public String getParameter(String name) {
		List<FileItem> list = getFieldItemList(name, true, false);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}

		return list.get(0).toString();
	}

	public String getTextValue(String key) {
		return getTextValue(key, request.getCharacterEncoding());
	}

	public String getTextValue(String key, String charsetName) {
		String value = getParameter(key);
		if (StringUtils.isEmpty(value)) {
			return value;
		}
		try {
			return new String(
					value.getBytes("iso-8859-1"),
					StringUtils.isEmpty(charsetName) ? Constants.DEFAULT_CHARSET_NAME
							: charsetName);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void save(String toPath, Collection<FileItem> fileItems) {
		if (CollectionUtils.isEmpty(fileItems)) {
			return;
		}

		for (FileItem fileItem : fileItems) {
			try {
				fileItem.write(new File(toPath + fileItem.getName()));
			} catch (Exception e) {
				logger.error(
						"保存上传的文件异常,路径" + toPath + ",文件名：" + fileItem.getName(),
						e);
			}
		}
	}
}
