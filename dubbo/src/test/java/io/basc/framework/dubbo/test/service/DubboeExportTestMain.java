package io.basc.framework.dubbo.test.service;

import io.basc.framework.boot.support.MainApplication;

public class DubboeExportTestMain {
	public static void main(String[] args) {
		MainApplication.run(DubboeExportTestMain.class, new String[] { "--io.basc.framework.beans.xml=export.xml" });
	}
}
