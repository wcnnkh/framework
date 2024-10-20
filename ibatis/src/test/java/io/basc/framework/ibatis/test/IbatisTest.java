package io.basc.framework.ibatis.test;

import io.basc.framework.boot.Application;
import io.basc.framework.boot.support.MainApplication;
import io.basc.framework.ibatis.beans.annotation.MapperResources;
import io.basc.framework.ibatis.beans.annotation.MapperScan;
import io.basc.framework.ibatis.test.mapper.TestMapper;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

@MapperScan("io.basc.framework.ibatis.test.mapper")
@MapperResources("classpath:/mapper/*.xml")
public class IbatisTest {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		Application application = MainApplication.run(IbatisTest.class, args).get();
		SqlSessionFactory sqlSessionFactory = application.getInstance(SqlSessionFactory.class);
		SqlSession sqlSession = sqlSessionFactory.openSession();
		try {
			TestMapper testMapper = sqlSession.getMapper(TestMapper.class);
			List<String> list = testMapper.query();
			System.out.println(list);
		} finally {
			sqlSession.close();
		}
	}
}
