package io.basc.framework.console.scanner;

import java.util.Scanner;

import io.basc.framework.console.AbstractConsoleProcessor;

public abstract class ScannerConsoleProcessor extends AbstractConsoleProcessor<Scanner> {

	public ScannerConsoleProcessor(String name) {
		super(name);
	}
}
