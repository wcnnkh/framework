package shuchaowen.core.logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import shuchaowen.common.utils.XTime;

public class FileLoggerProcess extends LoggerProcess{
	public FileLoggerProcess(){
		this(System.getProperty("user.dir") + File.separator + "log" + File.separator);
	}
	
	public FileLoggerProcess(String logRootPath) {
		this.logRootPath = logRootPath;
	}

	private String logRootPath;
	
	private Map<Level, PrintWriter> writeMap = new HashMap<Level, PrintWriter>();
	
	
	
	private PrintWriter createPrintWrite(File file) throws FileNotFoundException{
		return new PrintWriter(new FileOutputStream(file, true), true);
	}
	
	private PrintWriter newPrintWriter(LogMsg msg) throws IOException{
		StringBuilder sb = new StringBuilder();
		sb.append(logRootPath);
		sb.append(File.separator);
		File rootPath = new File(sb.toString());
		if(!rootPath.exists()){
			rootPath.mkdirs();
		}
		
		sb.append(msg.getLevel().toString().toLowerCase());
		
		if(XTime.isToday(msg.getCts())){
			String nowFilePath = sb.toString() + ".txt";
			File file = new File(nowFilePath);
			if(file.exists()){
				String lastYMD = XTime.format(file.lastModified(), "yyyy-MM-dd");
				if(!msg.getYMD().equals(lastYMD)){
					//不是同一天的日志
					sb.append(".").append(lastYMD).append(".txt");
					File lastFile = new File(sb.toString());
					if(lastFile.exists()){//如果上一次的文件已经存在，迁移
						PrintWriter printWriter = createPrintWrite(lastFile);
						BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
						String line;
						while((line = br.readLine()) != null){
							printWriter.println(line);
						}
						printWriter.flush();
						br.close();
						printWriter.close();
						file.delete();
						file.createNewFile();
					}else{//如果不存在就重命名
						file.renameTo(lastFile);
						file = new File(nowFilePath);
					}
				}
			}else{
				file.createNewFile();
			}
			
			return createPrintWrite(file);
		}else{
			sb.append(".").append(msg.getYMD()).append(".txt");
			File file = new File(sb.toString());
			if(!file.exists()){
				file.createNewFile();
			}
			return createPrintWrite(file);
		}
	}
	
	private PrintWriter getPrintWriter(LogMsg msg) throws IOException{
		PrintWriter write = writeMap.get(msg.getLevel());
		if(write == null){
			write = newPrintWriter(msg);
			writeMap.put(msg.getLevel(), write);
		}else if(write.checkError() || !XTime.isToday(msg.getCts())){
			write.close();
			write = newPrintWriter(msg);
			writeMap.put(msg.getLevel(), write);
		}
		return write;
	}
	
	@Override
	public void console(LogMsg msg) throws Exception{
		PrintWriter printWriter = getPrintWriter(msg);
		String str = msg.toString();
		switch (msg.getLevel()) {
		case ERROR:
			System.err.println(str);
			break;
		default:
			System.out.println(str);
			break;
		}
		
		printWriter.println(str);
		if(msg.getThrowable() != null){
			msg.getThrowable().printStackTrace();
			msg.getThrowable().printStackTrace(printWriter);
		}
		printWriter.flush();
	}
}