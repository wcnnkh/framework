package shuchaowen.core.multitask;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 多任务同步
 * @author shuchaowen
 *
 * @param <T>
 */
public final class Multitask<T> extends ArrayList<Callable<T>>{
	private static final long serialVersionUID = 1L;
	private CountDownLatch countDownLatch;
	private ExecutorService service;
	
	public Multitask(){
		
	}
	
	public Multitask(ExecutorService service){
		this.service = service;
	}
	
	private void start(ListState<T> listState){
		if(this.size() != 0){
			countDownLatch = new CountDownLatch(this.size());
			for(Callable<T> task : this){
				TaskProcess<T> process = new TaskProcess<T>(countDownLatch, listState, task);
				if(service == null){
					new Thread(process).start();;
				}else{
					service.submit(process);
				}
			}
		}
	}
	
	public ListState<T> execute() throws InterruptedException{
		ListState<T> listState = new ListState<T>();
		start(listState);
		countDownLatch.await();
		return listState;
	}
	
	public ListState<T> execute(long timeout, TimeUnit unit) throws InterruptedException{
		ListState<T> listState = new ListState<T>();
		start(listState);
		countDownLatch.await(timeout, unit);
		return listState;
	}
}

class TaskProcess<T> implements Runnable{
	private CountDownLatch countDownLatch;
	private Callable<T> call;
	private ListState<T> listState;
	
	public TaskProcess(CountDownLatch countDownLatch, ListState<T> listState, Callable<T> call) {
		this.countDownLatch = countDownLatch;
		this.call = call;
	}
	
	public void run() {
		try {
			T t = call.call();
			listState.add(new State<T>(t, null));
		} catch (Exception e) {
			e.printStackTrace();
			listState.add(new State<T>(null, e));
		}finally {
			if(countDownLatch != null){
				countDownLatch.countDown();
			}
		}
	}
}
