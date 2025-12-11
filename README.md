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

 aop   				面向切面编程，抽象了切面操作，默认实现jdk代理  
 beans 				对java bean的相关操作  
 core 				核心模块，提供常见的工具和各种抽象  
 dom     			提供dom读写的抽象和java原生实现  
 jdbc				对jdbc操作进行简化抽象  
 json				对json的简单操作(目前仅实现对象转json字符串)  
 messaging			对消息实体和消息转换的抽象，提供了一些简单的实现  
 xml					对xml的读写默认实现  
 
--------------------

 此版本为无依赖版本，如果想看早期完整的实现和集成请移步[此分支](https://github.com/wcnnkh/framework/tree/base.io)。  
 此项目起源于2016年，很多东西受限于作者认知和精力不再维护，现仅供参考和学习未经验证请勿在生产中使用。  
 市面上已经有很多优秀的框架(如spring, netty等)我就不再继续重复造轮子了，谢谢！  

