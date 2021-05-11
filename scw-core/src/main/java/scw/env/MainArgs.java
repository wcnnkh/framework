package scw.env;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import scw.core.Ordered;
import scw.core.utils.StringUtils;
import scw.util.AbstractIterator;
import scw.util.Pair;
import scw.value.AnyValue;
import scw.value.PropertyFactory;
import scw.value.StringValue;
import scw.value.Value;

public class MainArgs implements PropertyFactory, Ordered{
	private final String[] args;

	public MainArgs(String[] args) {
		this.args = args == null ? new String[0] : args.clone();
	}

	public int indexOf(String value) {
		for (int i = 0; i < args.length; i++) {
			if (value.equals(args[i])) {
				return i;
			}
		}
		return -1;
	}
	
	public boolean contains(String value){
		return indexOf(value) != -1;
	}

	/**
	 * 默认的最高优先级
	 */
	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}

	public Value get(int index) {
		return new AnyValue(args[index]);
	}

	public int length() {
		return args.length;
	}

	public String[] getArgs() {
		return args.clone();
	}

	/**
	 * 获取指定内容的下一个值
	 * @param instructionName
	 * @return
	 */
	public Value getNextValue(String instructionName) {
		int index = indexOf(instructionName);
		if (index == -1) {
			return null;
		}

		if (index < 0 || index >= args.length - 1) {
			return null;
		}

		return get(index + 1);
	}

	@Override
	public String toString() {
		return Arrays.toString(args);
	}
	
	protected boolean isProperty(String text){
		return text.startsWith("--") && text.indexOf("=") != -1;
	}
	
	protected Pair<String, String> toProperty(String text){
		if(text.startsWith("--")){
			return StringUtils.parseKV(text.substring(2), "=");
		}
		return StringUtils.parseKV(text, "=");
	}
	
	public String getProperty(String key){
		for (String value : args) {
			if(isProperty(value)){
				Pair<String, String> keyValuePair = toProperty(value);
				if(keyValuePair != null && keyValuePair.getKey().equals(key)){
					return keyValuePair.getValue();
				}
			}
		}
		return null;
	}
	
	public Value getValue(String key) {
		String value = getProperty(key);
		return value == null? null:new StringValue(value);
	}

	public Iterator<String> iterator() {
		return new PropertyIterator();
	}

	public boolean containsKey(String key) {
		return getProperty(key) != null;
	}
	
	private final class PropertyIterator extends AbstractIterator<String>{
		private int index = 0;
		
		public boolean hasNext() {
			while(index < args.length){
				String value = args[index];
				if(value == null){
					continue;
				}
				
				return isProperty(value);
			}
			return false;
		}

		public String next() {
			if(!hasNext()){
				throw new NoSuchElementException();
			}
			
			Pair<String, String> keyValuePair = toProperty(args[index]);
			index ++;
			return keyValuePair.getKey();
		}
		
	}
}
