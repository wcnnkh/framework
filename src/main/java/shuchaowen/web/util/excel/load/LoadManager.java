package shuchaowen.web.util.excel.load;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public final class LoadManager {
	private List<LoadExcel> loadList = new ArrayList<LoadExcel>();
	private CountDownLatch countDownLatch;
	
	public LoadManager add(LoadExcel loadExcel){
		loadExcel.setSuccess(new Runnable() {
			
			public void run() {
				if(countDownLatch != null){
					countDownLatch.countDown();
				}
			}
		});
		loadList.add(loadExcel);
		return this;
	}
	
	public void load(){
		if(loadList.isEmpty()){
			return;
		}
		
		countDownLatch = new CountDownLatch(loadList.size());
		for(LoadExcel loadExcel : loadList){
			new Thread(loadExcel).start();
		}
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
