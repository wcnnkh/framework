package scw.ibatis.test;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import scw.boot.Application;
import scw.boot.support.MainApplication;
import scw.ibatis.beans.annotation.MapperResources;
import scw.ibatis.beans.annotation.MapperScan;
import scw.ibatis.test.mapper.TestMapper;

@MapperScan("scw.ibatis.test.mapper")
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
