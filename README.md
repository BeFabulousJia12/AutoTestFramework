# AutoTestFramework
在这么多web项目里，基本的功能测试其实无非就是需要做： 
1. REST或者TCP的接口测试，并可能从数据库里拿到数据进行检查。
2. 基于E2E的Webdriver测试， 
3. 检验发送给第三方的REST请求是否正确。

而在实际的操作过程里，经常这三个是不同的框架或者脚本去完成，非常零散。当然也有比较好的框架例如RobertFramework，但是针对第三点，如何检测发送给第三方的REST请求是正确的呢？一般操作是，需要Fake一个第三方的HTTP Server，并且它能处理这些请求，一般我的做法是将收到的请求放在数据库里（我选择的是mongodb），然后再从数据库里取真实收到的值跟自己期望的值做对比，而RF这种框架类的东西，是不会帮你完成这些具体的操作，需要你自己封装关键字去完成。说到底，没有一劳永逸的框架，如果有特殊业务需求需要特殊验证，还是自己写代码更为灵活。 

因为首先目前的大部分项目都是Java项目，接触java比较多，再加上TestNG非常强大，可以支持多线程执行test，节约测试时间，也可以简单模拟下多线程多业务一起处理时，各个功能是否会影响彼此，造成bug，也算一个非常不错的手段。 当然进行实际压测的时候，还是使用Jemeter更为优秀（只是我一直对Jmeter不是NIO模式感到有些遗憾，毕竟一个机器启动那么多线程去做测试，还是很浪费资源的）。

所以，在这种情况下，我考虑用TestNG自己写了一个简单的功能测试框架，去满足以上3个基本需求。  

该框架包含的内容： 

1. 运行TestNG时候，
  * 可以直接点击testng.xml配置文件，直接运行，
  * 也可以用gradle build命令直接运行，只需要在build.gradle文件里配置如下： 
  
  ```
  useTestNG{
       suites "src/test/resources/testng.xml"
    }
 ```
 
2. 操作各类关系型数据库的方式，用的是mybatis. 

3. 已经包含了各主流关系型数据库mySQL, PostgreSQL, Oracle的配置文件，其中需要注意的是Oracle需要自己下载驱动文件，在这个代码仓库里，我已经把这个文件放在了根目录下的lib文件夹下了。 

4. Webdriver用了两种方式： 
* 一个是传统的基于有界面的浏览器操作，
* 还有一种是利用Chrome Headless特性，无界面操作。 

很明显无界面操作可以提高运行速度，而且在CICD环境里，可以不用再装类似windows的界面图形的系统去跑webdriver的E2E Case.
注意：4.1. 代码里有基于windows平台上运行的headless，以及linux平台上运行的headless(chromedriver放在了linux系统的"/usr/dev/selenium")
	  4.2. chromedriver在两个平台上有不同的文件，也就是windows上后缀为.exe的文件和64位的linux版本。在不同的平台上，需要对应不同的chromedriver才能执行成功。
	  4.3. chromedriver的版本需要跟chrome浏览器的版本对应起来。
	  4.4. chromedriver最新版本需要翻墙出去下载：https://sites.google.com/a/chromium.org/chromedriver/home
	  4.5. 关于linux下安装chrome浏览器，具体方式有两种：http://blog.csdn.net/sdujava2011/article/details/50880663  
	  个人的方式是，先在官网下载chrome的linux版本(例如：google-chrome-stable_current_amd64.deb)，然后将这个安装包放到linux环境里，再执行64位的安装命令：sudo dpkg -i google-chrome-stable_current_amd64.deb，即可。

5. 在启动整个测试之前，会用一个线程去启动一个http的server，这个http server是基于jetty的容器，这个http server主要功能就是为了完成上面说的第三点需求而生，如果你的项目里不需要验证发送出去的http请求，你也可以不用管它。但是，如果你需要做自己业务逻辑的验证工作，你需要在HTTPTestServer这个文件里，加servlet去处理进来的REST请求。  
e.g.
```
context.addServlet(new ServletHolder(new Notify()), "/notify");
context.addServlet(new ServletHolder(new FeedRate()), "/feedRate");
context.addServlet(new ServletHolder(new Quality()), "/quality");
```

6. 支持用最基本的方式对mongodb的连接和进行读写。
