package scw.application;

import java.util.Arrays;

import scw.core.utils.ArrayUtils;
import scw.value.StringValue;
import scw.value.Value;

public class MainArgs {
	private final Value[] args;

	public MainArgs(String[] args) {
		if (ArrayUtils.isEmpty(args)) {
			this.args = new Value[0];
		} else {
			this.args = new Value[args.length];
			for (int i = 0; i < args.length; i++) {
				this.args[i] = new StringValue(args[i]);
			}
		}
	}

	public MainArgs(Value[] args) {
		this.args = args;
	}

	public int indexOf(String value) {
		return indexOf(new StringValue(value));
	}

	public int indexOf(Value value) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals(value)) {
				return i;
			}
		}
		return -1;
	}

	public Value get(int index) {
		return args[index];
	}

	public int length() {
		return args.length;
	}

	public Value[] getArgs() {
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

		return args[index + 1];
	}
	
	@Override
	public String toString() {
		return Arrays.toString(args);
	}
}
