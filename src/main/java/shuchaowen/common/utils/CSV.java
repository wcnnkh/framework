package shuchaowen.common.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

public class CSV {
	private BufferedWriter writer;
	
	public CSV(BufferedWriter writer){
		this.writer = writer;
	}
	
	public void addRow(Object ...colValues) throws IOException{
		StringBuilder sb = new StringBuilder();
		for(Object val : colValues){
			sb.append("\"");
			sb.append(val);
			sb.append("\",");
		}
		writer.write(sb.toString());
		writer.newLine();
		writer.flush();
	}
	
	public void addRow(List<Object> colValues) throws IOException{
		StringBuilder sb = new StringBuilder();
		for(Object val : colValues){
			sb.append("\"");
			sb.append(val);
			sb.append("\",");
		}
		writer.write(sb.toString());
		writer.newLine();
		writer.flush();
	}
}
