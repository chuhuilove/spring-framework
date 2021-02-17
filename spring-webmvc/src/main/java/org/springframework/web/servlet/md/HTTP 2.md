## 1.11 HTTP/2

需要Servlet 4容器来支持HTTP/2,并且Spring Framework 5与Servlet API 4兼容.从编程模型的角度来看,应用程序不需要做任何特定的事情.但是,有一些与服务器配置有关的注意事项.更多信息,参阅[HTTP/2 wiki page](https://github.com/spring-projects/spring-framework/wiki/HTTP-2-support).

Servlet API确实公开了一个与HTTP/2相关的构造器.用户可以使用`javax.servlet.http.PushBuilder`主动将资源推送到客户端,并且它作为`@RequestMapping`方法的方法参数而受支持.