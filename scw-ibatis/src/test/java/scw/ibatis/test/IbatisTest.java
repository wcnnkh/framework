package scw.ibatis.test;

import java.util.concurrent.ExecutionException;

import scw.boot.Application;
import scw.boot.support.MainApplication;

public class IbatisTest {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		Application application = MainApplication.run(IbatisTest.class, args).get();
	}
}
