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
 core 				核心模块(除testing模块其他模块都引用此模块)，提供常见的工具的和种抽象  
 dom     			提供dom读写的抽象和java原生实现  
 jdbc				对jdbc操作进行简化抽象  
 json				对json的简单操作(目前仅实现对象转json字符串)  
 messaging			对消息实体和消息转换的抽象，提供了一些简单的实现  
 slf4j				将日志重定向到slf4j(因为这是一个非常流行的日志门面框架) 
 testing	 			单测依赖  
 xml					xml的读写默认实现  

