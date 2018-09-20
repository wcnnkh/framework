package shuchaowen.web.util.excel.load;

import java.util.HashMap;

import shuchaowen.core.util.StringUtils;

public abstract class LoadRowToMap<K, V> extends HashMap<K, V> implements LoadRow {
	private static final long serialVersionUID = 1L;
	private int beginRowIndex;
	private int keyColumnIndex;

	public LoadRowToMap(int keyColumnIndex, int beginRowIndex) {
		this.keyColumnIndex = keyColumnIndex;
		this.beginRowIndex = beginRowIndex;
	}

	public final void load(int sheetIndex, int rowIndex, String[] contents) {
		if(rowIndex < beginRowIndex){
			return ;
		}

		try {
			for(int i=0; i<contents.length; i++){
				String keyContent = contents[keyColumnIndex];
				if(StringUtils.isNull(keyContent)){
					return ;
				}
				
				K k = getKey(keyContent);
				if(k == null){
					return ;
				}
				
				if(containsKey(k)){
					throw new NullPointerException("role存在相同的ID, sheelIndex="+sheetIndex+", row="+rowIndex+" key=" + k);
				}
				
				V v = getValue(contents);
				if(v == null){
					continue;
				}
				
				put(k, v);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public abstract K getKey(String content);

	public abstract V getValue(String[] contents);
}
