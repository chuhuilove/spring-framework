## 1.1 DispatcherServlet

与许多其他web框架一样,Spring MVC也是围绕前端控制器模式设计的,
其中核心`Servlet`的实现是`DispatcherServlet`,提供了一个用于请求处理的共享算法,而实际工作是由可配置的委托组件执行的.
该模型灵活,支持多种工作流程.

与任何`Servlet`一样,`DispatcherServlet`需要使用Java配置或web.xml根据`Servlet`规范声明和映射.然后,`DispatcherServlet`使用Spring配置来发现请求映射,视图解析,异常处理等所需的委托组件.

以下Java配置示例注册并初始化`DispatcherServlet`,该容器由`Servlet`容器自动检测到(请参阅[Servlet Config]()):

```java
public class MyWebApplicationInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletCxt) {

        // Load Spring web application configuration
        AnnotationConfigWebApplicationContext ac = new AnnotationConfigWebApplicationContext();
        ac.register(AppConfig.class);
        ac.refresh();

        // Create and register the DispatcherServlet
        DispatcherServlet servlet = new DispatcherServlet(ac);
        ServletRegistration.Dynamic registration = servletCxt.addServlet("app", servlet);
        registration.setLoadOnStartup(1);
        registration.addMapping("/app/*");
    }
}
```
除了直接使用`ServletContext` API外,还可以扩展`AbstractAnnotationConfigDispatcherServletInitializer`并重写特定方法(请参见[Context Hierarchy](#contextHierarchy)下的示例).

以下`web.xml`配置示例注册并初始化`DispatcherServlet`:

```xml
<web-app>

    <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>

    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>/WEB-INF/app-context.xml</param-value>
    </context-param>

    <servlet>
        <servlet-name>app</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value></param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>

    <servlet-mapping>
        <servlet-name>app</servlet-name>
        <url-pattern>/app/*</url-pattern>
    </servlet-mapping>

</web-app>
```
>Spring Boot遵循不同的初始化顺序.Spring Boot并没有陷入Servlet容器的生命周期,而是使用Spring配置来引导自身和内嵌Servlet容器.在Spring配置中检测到`Filter `和`Servlet`声明,并在Servlet容器中注册.

### <span id="contextHierarchy">1.1.1 Context Hierarchy</span> 

DispatcherServlet期望WebApplicationContext(普通ApplicationContext的扩展)用于自己的配置.


WebApplicationContext有一个指向ServletContext及其关联的Servlet的链接.
它还绑定到ServletContext，以便应用程序可以使用requestcontext上的静态方法来查找WebApplicationContext(如果它们需要访问它)


对于许多应用程序来说，拥有一个单一的WebApplicationContext就足够了.
youdao:也可以有一个上下文层次结构，其中一个根WebApplicationContext在多个DispatcherServlet(或其他Servlet)实例之间共享，每个实例都有自己的子WebApplicationContext配置。

google:也可能具有上下文层次结构，其中一个根WebApplicationContext在多个DispatcherServlet（或其他Servlet）实例之间共享，每个实例都有其自己的子WebApplicationContext配置。

有关上下文层次结构特性的更多信息，请参见ApplicationContext的附加功能。


youdao 根WebApplicationContext通常包含基础设施bean，比如需要在多个Servlet实例之间共享的数据存储库和业务服务。
google 根WebApplicationContext通常包含需要在多个Servlet实例之间共享的基础结构Bean，例如数据存储库和业务服务。

这些bean可以有效地继承，并且可以在特定于Servlet的子WebApplicationContext中重写(即重新声明)，该上下文通常包含给定Servlet的本地bean。

下图显示了这种关系:

![](https://docs.spring.io/spring/docs/5.1.10.RELEASE/spring-framework-reference/images/mvc-context-hierarchy.png)


下面的例子配置了一个`WebApplicationContext`层次结构:

```java
public class MyWebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[] { RootConfig.class };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[] { App1Config.class };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] { "/app1/*" };
    }
}
```
>如果不需要应用程序上下文层次结构，则应用程序可以通过getRootConfigClasses()返回所有配置，并通过getServletConfigClasses()返回null。

下面的示例展示了`web.xml`的等效版本:

```xml
<web-app>

    <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>

    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>/WEB-INF/root-context.xml</param-value>
    </context-param>

    <servlet>
        <servlet-name>app1</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>/WEB-INF/app1-context.xml</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>

    <servlet-mapping>
        <servlet-name>app1</servlet-name>
        <url-pattern>/app1/*</url-pattern>
    </servlet-mapping>

</web-app>
```
>如果不需要应用程序上下文层次结构，那么应用程序可能只配置一个“根”上下文，而将contextConfigLocation Servlet参数设置为空。



### <span id="specialBeanTypes">1.1.2. Special Bean Types</span>

### <span id="webMVCConfig">1.1.3. Web MVC Config</span>

### <span id="servletConfig">1.1.4. Servlet Config</span>

在Servlet 3.0+环境中,你可以选择以编程方式配置Servlet容器,以作为替代方案或与web.xml文件结合使用.下面的示例注册了`DispatcherServlet`:

```java
import org.springframework.web.WebApplicationInitializer;

public class MyWebApplicationInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext container) {
        XmlWebApplicationContext appContext = new XmlWebApplicationContext();
        appContext.setConfigLocation("/WEB-INF/spring/dispatcher-config.xml");

        ServletRegistration.Dynamic registration = container.addServlet("dispatcher", new DispatcherServlet(appContext));
        registration.setLoadOnStartup(1);
        registration.addMapping("/");
    }
}
```

`WebApplicationInitializer`是Spring MVC提供的接口,可确保检测到你的实现并将其自动用于初始化任何Servlet 3容器.

实现`WebApplicationInitializer`接口的一个抽象基类是`AbstractDispatcherServletInitializer`,它能是注册`DispatcherServlet`更加容易,具体注册方式是重写指定servlet映射和`DispatcherServlet`配置路径的方法

对于使用基于Java的Spring配置的应用程序,建议按照如以下示例:

```Java
public class MyWebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return null;
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[] { MyWebConfig.class };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }
}
```

如果使用基于xml的Spring配置,应该直接从AbstractDispatcherServletInitializer继承,如下面的示例所示:

```java
public class MyWebAppInitializer extends AbstractDispatcherServletInitializer {

    @Override
    protected WebApplicationContext createRootApplicationContext() {
        return null;
    }

    @Override
    protected WebApplicationContext createServletApplicationContext() {
        XmlWebApplicationContext cxt = new XmlWebApplicationContext();
        cxt.setConfigLocation("/WEB-INF/spring/dispatcher-config.xml");
        return cxt;
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }
}
```

`AbstractDispatcherServletInitializer`还提供了一种方便快捷的方法来添加`Filter`实例,并将其自动映射到`DispatcherServlet`,如以下示例所示:

```Java
public class MyWebAppInitializer extends AbstractDispatcherServletInitializer {

    // ...

    @Override
    protected Filter[] getServletFilters() {
        return new Filter[] {
            new HiddenHttpMethodFilter(), new CharacterEncodingFilter() };
    }
}
```

每个过滤器都根据其具体类型添加一个默认名称,并自动映射到`DispatcherServlet`.

`AbstractDispatcherServletInitializer`的`isAsyncSupported`保护方法提供了一个单独的配置来在`DispatcherServlet`和映射到它的所有过滤器上启用异步支持.默认情况下,此标志设置为`true`.

最后,如果你需要进一步定制`DispatcherServlet`本身,你可以重写`createDispatcherServlet`方法.


### <span id="mvc-servlet-sequence">1.1.5. Processing</span>

DispatcherServlet处理请求的方式如下:


- 搜索`WebApplicationContext`并将其绑定在Request中,作为控制器和流程中其他元素可以使用的属性. 默认情况下,它绑定在`DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE`上.参见`DispatcherServlet`的`doService`方法.

- locale解析器绑定到Request,以使流程中的元素解析在处理Request(呈现视图,准备数据等)时要使用的locale.如果不需要locale解析,则不需要区域解析器.

- theme解析器绑定到Request,以使诸如视图之类的元素确定要使用的theme.

- 如果指定Multipart文件解析器,则将检查请求中是否有Multipart.如果找到Multipart,则将该请求包装在`MultipartHttpServletRequest`中,以供流程中的其他元素进一步处理.有关Multipart处理的更多信息,请参见[Multipart Resolver]().这一步是处理文件上传.



### <span id="mvc-handlermapping-interceptor">1.1.6. Interception</span>

All HandlerMapping implementations support handler interceptors that are useful when you want to apply specific functionality to certain requests — for example, checking for a principal.
HandlerMapping 

所有`HandlerMapping`实现都支持handler interceptors,当你想将特定的功能应用于某些请求时,这些Interceptors非常有用--例如,checking for a principal.
Interceptors必须实现来自`org.springframework.web.servlet`包下

 
 Interceptors must implement HandlerInterceptor from the org.springframework.web.servlet package with three methods that should provide enough flexibility to do all kinds of pre-processing and post-processing:

- preHandle(..): Before the actual handler is executed

- postHandle(..): After the handler is executed

- afterCompletion(..): After the complete request has finished




### <span id="viewResolution">1.1.8. View Resolution</span>

Spring MVC定义了`ViewResolver`和`View`接口,它们允许你在浏览器中呈现模型,而不必绑定到特定的view技术.`ViewResolver`提供了视图名称和实际视图之间的映射.在转移到特定的视图技术之前,`View`解决了数据准备问题.

下表提供了更多关于`ViewResolver`层次结构的细节:

**Table 3. ViewResolver 实现**

|`ViewResolver`|描述|
|:---|:---|
|AbstractCachingViewResolver|`AbstractCachingViewResolver`的子类缓存它们解析的视图实例.缓存提高了某些视图技术的性能.可以通过将`cache`属性设置为`false`来关闭缓存.此外,如果必须在运行时刷新某个视图(例如,修改`FreeMarker`模板时),可以使用`removeFromCache(String viewName, Locale loc)`方法.|
|XmlViewResolver|`ViewResolver`的实现,它接受使用与Spring的XML bean工厂相同的DTD用XML编写的配置文件.默认的配置文件是`/WEB-INF/views.xml`.|
|ResourceBundleViewResolver||
|UrlBasedViewResolver||
|InternalResourceViewResolver||
|FreeMarkerViewResolver||
|ContentNegotiatingViewResolver||

#### Handling
#### Redirecting
#### Forwarding
#### Content Negotiation


1.1.9. Locale
mvc-localeresolver














