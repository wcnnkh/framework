package shuchaowen.core.db.storage.async;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.sql.format.SQLFormat;
import shuchaowen.core.db.storage.AbstractExecuteStorage;
import shuchaowen.core.db.storage.ExecuteInfo;
import shuchaowen.core.util.ConfigUtils;
import shuchaowen.core.util.Logger;
import shuchaowen.core.util.StringUtils;
import shuchaowen.core.util.XUtils;

/**
 * 此方式只能在单机环境一使用，因为在集群下无法保证执行顺序
 * 
 * @author shuchaowen
 *
 */
public class MemoryAsyncExecuteStorage extends AbstractExecuteStorage {
	private static final String QUEUE_LOCAL_PATH = "QUEUE_LOCAL_PATH";
	private static final String FILE_SUFFIX = ".queue";
	private static final long STORAGE_TIMEOUT = 1000L;
	private LinkedBlockingQueue<ExecuteInfo> queue = new LinkedBlockingQueue<ExecuteInfo>();
	private final String queueLocalPath;
	private volatile String writeFileName;// 当前正在操作的文件名
	
	public MemoryAsyncExecuteStorage(AbstractDB db){
		super(db, DEFAULT_SQL_FORMAT);
		
		String path = ConfigUtils.getSystemProperty(QUEUE_LOCAL_PATH);
		if(StringUtils.isNull(path)){
			path = System.getProperty("java.io.tmpdir");
		}
		
		if(StringUtils.isNull(path)){
			throw new NullPointerException("not found queueLocalPath");
		}
		
		this.queueLocalPath = path;
		Logger.info("MemoryAsyncExecuteStorage", path);
	}

	public MemoryAsyncExecuteStorage(AbstractDB db, String queueLocalPath) {
		this(db, DEFAULT_SQL_FORMAT, queueLocalPath);
	}

	public MemoryAsyncExecuteStorage(AbstractDB db, SQLFormat sqlFormat, String queueLocalPath) {
		super(db, sqlFormat);
		if (queueLocalPath == null) {
			throw new NullPointerException("queueLocalPath Can't be null");
		}
		this.queueLocalPath = queueLocalPath;
		File file = new File(queueLocalPath);
		if (!file.exists()) {
			file.mkdirs();
		}
	}
	
	private File getFile(){
		StringBuilder sb = new StringBuilder(queueLocalPath);
		sb.append(File.separator);
		sb.append(System.currentTimeMillis()/STORAGE_TIMEOUT);
		sb.append(FILE_SUFFIX);
		return new File(sb.toString());
	}

	private void writeFile(ExecuteInfo executeInfo) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			File file = getFile();
			if (!file.exists()) {
				file.createNewFile();
			}
			
			writeFileName = file.getName();
			fos = new FileOutputStream(file, true);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(executeInfo);
			oos.flush();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			XUtils.close(oos, fos);
			writeFileName = null;
		}
	}
	
	private void flushToDB(){
		List<Double> list = new ArrayList<Double>();
		try {
			File file = new File(queueLocalPath);
			for (String f : file.list()) {
				if(f.length() <= FILE_SUFFIX.length()){
					continue;
				}
				
				if(f.endsWith(FILE_SUFFIX)){
					String str = f.substring(0, f.length() - FILE_SUFFIX.length());
					try {
						list.add(Double.parseDouble(str));
					} catch (Exception e) {
					}
				}
			}
			
			list.sort(new Comparator<Double>() {

				public int compare(Double o1, Double o2) {
					if(o1 < 02){
						return -1;
					}else if(o1 > o2){
						return 1;
					}else{
						return 0;
					}
				}
			});
			
			for(double id : list){
				synchronized (writeFileName) {
					String fileName = id + FILE_SUFFIX;
					if(fileName.equals(writeFileName)){
						break;
					}
					
					File f = new File(queueLocalPath + File.separator + fileName);
					if(f.exists()){
						FileInputStream fis = null;
						ObjectInputStream ois = null;
						try {
							fis = new FileInputStream(file);
							ois = new ObjectInputStream(fis);
							
							ExecuteInfo executeInfo = (ExecuteInfo) ois.readObject();
							if(executeInfo != null){
								getDb().execute(getSqlList(executeInfo));
							}
						} catch (Exception e) {
							e.printStackTrace();
						}finally {
							XUtils.close(ois, fis);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void start() {
		new Thread(new Runnable() {

			public void run() {
				while (true) {
					try {
						ExecuteInfo executeInfo = queue.poll();
						if (executeInfo == null) {
							continue;
						}

						writeFile(executeInfo);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();

		new Thread(new Runnable() {

			public void run() {
				while (true){
					try {
						Thread.sleep(STORAGE_TIMEOUT);
					} catch (InterruptedException e) {
						e.printStackTrace();
						break;
					}
					
					flushToDB();
				}
			}
		}).start();

	}

	@Override
	public void execute(ExecuteInfo executeInfo) {
		queue.offer(executeInfo);
	}
	
	public static void main(String[] args) {
		List<Integer> list = new ArrayList<Integer>();
		list.add(0);
		list.add(10);
		list.add(1);
		list.sort(new Comparator<Integer>() {

			public int compare(Integer o1, Integer o2) {
				if(o1 < o2){
					return -1;
				}else if(o1 > o2){
					return 1;
				}else{
					return 0;
				}
			}
		});
		System.out.println(Arrays.toString(list.toArray()));
	}
}
