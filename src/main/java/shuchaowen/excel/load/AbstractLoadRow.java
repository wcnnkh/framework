package shuchaowen.excel.load;

import java.util.HashMap;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import shuchaowen.core.exception.AlreadyExistsException;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.core.util.ClassInfo;
import shuchaowen.core.util.ClassUtils;
import shuchaowen.core.util.FieldInfo;
import shuchaowen.core.util.StringUtils;

public abstract class AbstractLoadRow<T> implements LoadRow{
	private final int nameMappingIndex;
	private final int beginRowIndex;
	private final int endRowIndex;
	private final HashMap<String, Integer> nameMapping = new HashMap<String, Integer>();
	private final ClassInfo classInfo;
	
	/**
	 * @param type
	 * @param nameMappingIndex
	 * @param beginRowIndex
	 * @param endRowIndex 当此值为-1时不判断结束位置
	 */
	public AbstractLoadRow(Class<T> type, int nameMappingIndex, int beginRowIndex, int endRowIndex){
		this.nameMappingIndex = nameMappingIndex;
		this.beginRowIndex = beginRowIndex;
		this.endRowIndex = endRowIndex;
		this.classInfo = ClassUtils.getClassInfo(type);
	}

	public final ClassInfo getClassInfo() {
		return classInfo;
	}

	public final void load(int sheetIndex, int rowIndex, String[] contents) {
		if(rowIndex == nameMappingIndex){
			for(int i=0; i<contents.length; i++){
				String name = contents[i];
				if(StringUtils.isNull(name)){
					continue;
				}
				
				name = name.trim();
				
				if(nameMapping.containsKey(name)){
					throw new AlreadyExistsException(name);
				}
				
				nameMapping.put(name, i);
			}
		}else if(rowIndex >= beginRowIndex && (endRowIndex == -1 || rowIndex <= endRowIndex)){
			System.out.println("来了啊。。。。。。");
			if(nameMapping.isEmpty()){
				throw new ShuChaoWenRuntimeException("未加载name的映射关系, nameMappingIndex=" + nameMappingIndex);
			}
			
			try {
				T obj = newInstance();	
				for(Entry<String, Integer> entry : nameMapping.entrySet()){
					FieldInfo fieldInfo = classInfo.getFieldInfo(entry.getKey());
					if(fieldInfo == null){
						continue;
					}
					
					Object value = format(entry.getKey(), contents[entry.getValue()], fieldInfo.getField().getType());
					if(value == null){
						continue;
					}
					
					fieldInfo.set(obj, value);
				}
				
				if(obj == null){
					return ;
				}
				load(obj);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public T newInstance() throws Exception{
		return (T) classInfo.getClz().newInstance();
	}
	
	public Object format(String name, String value, Class<?> type){
		if(StringUtils.isNull(value)){
			return null;
		}
		
		if(ClassUtils.isStringType(type)){
			return value;
		}else if(ClassUtils.isBasicType(type)){
			return StringUtils.conversion(value, type);
		}else if(JSONObject.class.isAssignableFrom(type)){
			return JSONObject.parse(value);
		}else if(JSONArray.class.isAssignableFrom(type)){
			return JSONArray.parse(value);
		}
		return null;
	}
	
	public abstract void load(T row);
}
