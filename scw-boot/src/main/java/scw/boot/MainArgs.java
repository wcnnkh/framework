package scw.boot;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import scw.core.utils.StringUtils;
import scw.util.KeyValuePair;
import scw.value.AnyValue;
import scw.value.Value;

public class MainArgs {
	private final String[] args;

	public MainArgs(String[] args) {
		this.args = args == null ? new String[0] : args;
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

	public Value get(int index) {
		return new AnyValue(args[index]);
	}

	public int length() {
		return args.length;
	}

	public String[] getArgs() {
		return args.clone();
	}

	public Value getInstruction(String instructionName) {
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

	public Map<String, String> getParameterMap() {
		if (args.length == 0) {
			return Collections.emptyMap();
		}

		Map<String, String> map = new HashMap<String, String>(8);
		for (String value : args) {
			if (value.startsWith("--") && value.indexOf("=") != -1) {
				KeyValuePair<String, String> keyValuePair = StringUtils.parseKV(value.substring(2), "=");
				if (keyValuePair != null) {
					map.put(keyValuePair.getKey(), keyValuePair.getValue());
				}
			}
		}
		return map;
	}
}
