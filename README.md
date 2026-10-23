为自定义框架提供抽象和基础实现。
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

<table>
  <tr>
    <th>模块</th>
    <th>说明</th>
  </tr>
  <tr>
    <td>core</td>
    <td>核心模块，无任何依赖，提供常用的抽象</td>
  </tr>
  <tr>
    <td>context</td>
    <td>框架的上下文依赖，目前包含常用工具、基类定义、spring上下文</td>
  </tr>
  <tr>
    <td>apple</td>
    <td>Apple支付接口的相关封装</td>
  </tr>
  <tr>
    <td>jackson</td>
    <td>Jackson的依赖和封装</td>
  </tr>
  <tr>
    <td>mybatis</td>
    <td>mybatis的依赖和封装</td>
  </tr>
  <tr>
    <td>mybatis-plus</td>
    <td>mybatis-plus的依赖和封装</td>
  </tr>
  <tr>
    <td>pdf</td>
    <td>pdf的依赖和封装</td>
  </tr>
  <tr>
    <td>redis</td>
    <td>redis的依赖和封装</td>
  </tr>
  <tr>
    <td>web</td>
    <td>web的依赖和封装</td>
  </tr>
</table>
 
--------------------

 此版本为无依赖版本，如果想看早期完整的实现和集成请移步[此分支](https://github.com/wcnnkh/framework/tree/base.io)。  
 此项目起源于2016年，很多东西受限于作者认知和精力不再维护，现仅供参考和学习未经验证请勿在生产中使用。  
 市面上已经有很多优秀的框架(如spring, netty等)我就不再继续重复造轮子了，谢谢！  