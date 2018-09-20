package shuchaowen.web.util.excel.load;

import java.io.File;

import shuchaowen.core.util.ConfigUtils;
import shuchaowen.core.util.Logger;

public abstract class LoadExcel implements Runnable{
	private LoadRow loadRow;
	private File excel;
	private Runnable success;
	
	public LoadExcel(String filePath, LoadRow loadRow){
		String path = ConfigUtils.format(filePath);
		this.excel = new File(path);
		this.loadRow = loadRow;
	}
	
	public LoadExcel(File excel, LoadRow loadRow){
		this.excel = excel;
		this.loadRow = loadRow;
	}
	
	public final void setSuccess(Runnable success) {
		this.success = success;
	}

	public abstract void load(File excel, LoadRow loadRow) throws Exception;
	
	public final void run() {
		try {
			load(excel, loadRow);
			if(success != null){
				new Thread(success).start();
			}
		} catch (Exception e) {
			Logger.error("加载" + excel.getName() + "失败", e);
		}
	}
}
