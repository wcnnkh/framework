package shuchaowen.excel.load;

import java.util.ArrayList;

public abstract class LoadRowToList<E> extends ArrayList<E> implements LoadRow{
	private static final long serialVersionUID = 1L;
	private int beginRowIndex;

	public LoadRowToList(int beginRowIndex) {
		this.beginRowIndex = beginRowIndex;
	}

	public final void load(int sheetIndex, int rowIndex, String[] contents) {
		if(rowIndex < beginRowIndex){
			return ;
		}

		try {
			E v = getValue(contents);
			if(v == null){
				return ;
			}
			add(v);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public abstract E getValue(String[] contents) throws Exception;
}
