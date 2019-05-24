package scw.utils.excel.load;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import scw.core.exception.AlreadyExistsException;
import scw.core.reflect.ReflectUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;

public abstract class AbstractLoadRow<T> implements LoadRow {
	private final int nameMappingIndex;
	private final int beginRowIndex;
	private final int endRowIndex;
	private final HashMap<String, Integer> nameMapping = new HashMap<String, Integer>();
	private final Class<T> type;

	/**
	 * @param type
	 * @param nameMappingIndex
	 * @param beginRowIndex
	 * @param endRowIndex
	 *            当此值为-1时不判断结束位置
	 */
	public AbstractLoadRow(Class<T> type, int nameMappingIndex, int beginRowIndex, int endRowIndex) {
		this.nameMappingIndex = nameMappingIndex;
		this.beginRowIndex = beginRowIndex;
		this.endRowIndex = endRowIndex;
		this.type = type;
	}

	public final void load(int sheetIndex, int rowIndex, String[] contents) {
		if (rowIndex == nameMappingIndex) {
			for (int i = 0; i < contents.length; i++) {
				String name = contents[i];
				if (StringUtils.isNull(name)) {
					continue;
				}

				name = name.trim();

				if (nameMapping.containsKey(name)) {
					throw new AlreadyExistsException(name);
				}

				nameMapping.put(name, i);
			}
		} else if (rowIndex >= beginRowIndex && (endRowIndex == -1 || rowIndex <= endRowIndex)) {
			if (nameMapping.isEmpty()) {
				throw new RuntimeException("未加载name的映射关系, nameMappingIndex=" + nameMappingIndex);
			}

			try {
				T obj = ReflectUtils.newInstance(type);
				for (Entry<String, Integer> entry : nameMapping.entrySet()) {
					Field field = ReflectUtils.getField(type, entry.getKey(), true);
					if (field == null) {
						continue;
					}

					Object value = format(entry.getKey(), contents[entry.getValue()], field.getType());
					if (value == null) {
						continue;
					}

					ReflectUtils.setFieldValue(type, field, obj, value);
				}

				if (obj == null) {
					return;
				}
				load(obj);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public Object format(String name, String value, Class<?> type) {
		if (StringUtils.isNull(value)) {
			return null;
		}

		if (ClassUtils.isStringType(type)) {
			return value;
		} else if (ClassUtils.isPrimitiveOrWrapper(type)) {
			return StringUtils.conversion(value, type);
		} else if (JSONObject.class.isAssignableFrom(type)) {
			return JSONObject.parse(value);
		} else if (JSONArray.class.isAssignableFrom(type)) {
			return JSONArray.parse(value);
		}
		return null;
	}

	public abstract void load(T row);
}
