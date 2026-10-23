无任何第三方依赖，为自定义框架提供抽象和基础实现。
========
1.pom.xml中引入
-------------------
    <dependency>
		<groupId>run.soeasy.framework</groupId>
		<artifactId>{模块名}</artifactId>
		<version>{你想使用的版本}</version>
	</dependency>
或下载源码maven clean install

2.模块说明
-------------------

 core   			核心模块，无任何依赖，提供常用的抽象
 context 			框架的上下文依赖，目前包含常用工具、基类定义、spring上下文
 apple 				Apple支付接口的相关封装
 jackson     		Jackson的依赖和封装
 mybatis			mybatis的依赖和封装
 mybatis-plus		mybatis-plus的依赖和封装
 pdf			    pdf的相关操作
 redis				redis的依赖和封装
 web                web层的封装
 
--------------------

 此版本为无依赖版本，如果想看早期完整的实现和集成请移步[此分支](https://github.com/wcnnkh/framework/tree/base.io)。  
 此项目起源于2016年，很多东西受限于作者认知和精力不再维护，现仅供参考和学习未经验证请勿在生产中使用。  
 市面上已经有很多优秀的框架(如spring, netty等)我就不再继续重复造轮子了，谢谢！  

