## 1.10. MVC Config

MVC  Java configuration 和MVC XML namespace提供适用于大多数应用程序的默认配置以及用于对其进行自定义的配置API.
有关配置API中的更多高级定制,请参阅Advanced Java Config和Advanced XML Config.
不需要了解由MVC JavaJava configuration和MVC namespace创建的基础bean.如果要了解更多信息,请参阅特殊Bean类型和Web MVC Config.


### 1.10.1 Enable MVC Configuration

在Java configuration中,可以使用`@EnableWebMvc`来启用MVC configuration,就像下面的示例:

```java
@Configuration
@EnableWebMvc
public class WebConfig {
}
```
在XML configuration中,可以使用`<mvc:annotation-driven>`元素来启用MVC configuration,就像下面的示例:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:mvc="http://www.springframework.org/schema/mvc"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/mvc
        https://www.springframework.org/schema/mvc/spring-mvc.xsd">

    <mvc:annotation-driven/>

</beans>
```
前面的示例注册了许多Spring MVC的基础Beans,并适应了类路径上可用的依赖项(例如,JSON,XML等的有效转换器).

### 1.10.2. MVC Config API

在Java configuration中,用户可以实现`WebMvcConfigurer`接口,下面的示例:
```Java
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    // Implement configuration methods...
}
```
在XML中,用户可以检查`<mvc:annotation-driven/>`的属性和子元素.用户可以查看Spring MVC XML模式或使用IDE的代码提示功能来发现可用的属性和子元素.

### 1.10.3. Type Conversion
默认情况下,已安装`Number`和`Date`类型的格式化程序,包括对`@NumberFormat`和`@DateTimeFormat`注解的支持.如果类路径中存在Joda-Time,则还将安装对Joda-Time格式库的完全支持.
在Java configuration中,你可以注册自定义的格式化器和转换器,示例如下:
```java
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        // ...
    }
}
```
下面的示例是基于XML configuration:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:mvc="http://www.springframework.org/schema/mvc"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/mvc
        https://www.springframework.org/schema/mvc/spring-mvc.xsd">

    <mvc:annotation-driven conversion-service="conversionService"/>

    <bean id="conversionService"
            class="org.springframework.format.support.FormattingConversionServiceFactoryBean">
        <property name="converters">
            <set>
                <bean class="org.example.MyConverter"/>
            </set>
        </property>
        <property name="formatters">
            <set>
                <bean class="org.example.MyFormatter"/>
                <bean class="org.example.MyAnnotationFormatterFactory"/>
            </set>
        </property>
        <property name="formatterRegistrars">
            <set>
                <bean class="org.example.MyFormatterRegistrar"/>
            </set>
        </property>
    </bean>

</beans>
```

> 有关何时使用`FormatterRegistrar`实现的更多信息,请参阅FormatterRegistrar SPI和`FormattingConversionServiceFactoryBean`.

### 1.10.4. Validation
默认情况下,如果Bean Validation存在于类路径中(例如,Hibernate Validator),则`LocalValidatorFactoryBean`将注册为全局Validator,以与`@Valid`一起使用,并在控制器方法参数上进行`Validated`.
在Java configuration中,用户可以定制全局`Validator`实例,如下面示例:
```java
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public Validator getValidator(); {
        // ...
    }
}
```
以下示例显示了如何在XML中实现相同的配置:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:mvc="http://www.springframework.org/schema/mvc"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/mvc
        https://www.springframework.org/schema/mvc/spring-mvc.xsd">

    <mvc:annotation-driven validator="globalValidator"/>

</beans>
```
请注意,用户还可以在本地注册`Validator`实现,如以下示例所示:
```java
@Controller
public class MyController {

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(new FooValidator());
    }

}
```
>如果需要在某个地方注入`LocalValidatorFactoryBean`,请创建一个bean并用`@Primary`标记它,以避免与MVC配置中声明的那个冲突.
### 1.10.5. Interceptors
在Java configuration中,用户可以注册拦截器以应用于传入的请求,如以下示例所示:
```java
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LocaleChangeInterceptor());
        registry.addInterceptor(new ThemeChangeInterceptor()).addPathPatterns("/**").excludePathPatterns("/admin/**");
        registry.addInterceptor(new SecurityInterceptor()).addPathPatterns("/secure/*");
    }
}
```
以下示例显示了如何在XML中实现相同的配置:
```xml
<mvc:interceptors>
    <bean class="org.springframework.web.servlet.i18n.LocaleChangeInterceptor"/>
    <mvc:interceptor>
        <mvc:mapping path="/**"/>
        <mvc:exclude-mapping path="/admin/**"/>
        <bean class="org.springframework.web.servlet.theme.ThemeChangeInterceptor"/>
    </mvc:interceptor>
    <mvc:interceptor>
        <mvc:mapping path="/secure/*"/>
        <bean class="org.example.SecurityInterceptor"/>
    </mvc:interceptor>
</mvc:interceptors>
```
### 1.10.6. Content Types
用户可以配置Spring MVC如何根据request来确定请求的媒体类型(例如,`Accept` header,URL路径扩展,查询参数等).

默认情况下,首先检查URL路径扩展名-将`json`,`xml`,`rss`和`atom`注册为已知扩展名(取决于类路径依赖项).其次检查`Accept` header.

考虑将这些默认值更改为只`Accept` header,如果必须使用基于url的内容类型解析,则考虑在路径扩展上使用查询参数策略.

在Java configuration中,用户可以自定义请求的内容类型解析,如以下示例所示:
```java
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.mediaType("json", MediaType.APPLICATION_JSON);
        configurer.mediaType("xml", MediaType.APPLICATION_XML);
    }
}
```
以下示例显示了如何在XML中实现相同的配置:
```xml
<mvc:annotation-driven content-negotiation-manager="contentNegotiationManager"/>

<bean id="contentNegotiationManager" class="org.springframework.web.accept.ContentNegotiationManagerFactoryBean">
    <property name="mediaTypes">
        <value>
            json=application/json
            xml=application/xml
        </value>
    </property>
</bean>
```
### 1.10.7. Message Converters

在Java configuration中,通过重写`configureMessageConverters()`(替换由Spring MVC创建的默认转换器)和`extendMessageConverters()`(自定义默认转换器或将其他转换器添加到默认转换器)来定制`HttpMessageConverter`.

以下示例使用自定义的`ObjectMapper`代替默认的添加了XML和Jackson JSON转换器:
```java
@Configuration
@EnableWebMvc
public class WebConfiguration implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder()
                .indentOutput(true)
                .dateFormat(new SimpleDateFormat("yyyy-MM-dd"))
                .modulesToInstall(new ParameterNamesModule());
        converters.add(new MappingJackson2HttpMessageConverter(builder.build()));
        converters.add(new MappingJackson2XmlHttpMessageConverter(builder.createXmlMapper(true).build()));
    }
}
```

在前面的例子中,`Jackson2ObjectMapperBuilder`是用来创建一个通用配置`MappingJackson2HttpMessageConverter`和`MappingJackson2XmlHttpMessageConverter`启用了缩进,一个定制的日期格式,和jackson-module-parameter-names注册,这增加了支持访问参数名称(功能添加到Java 8).

该构建器自定义Jackson的默认属性,如下所示:

- [`DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES`](https://fasterxml.github.io/jackson-databind/javadoc/2.6/com/fasterxml/jackson/databind/DeserializationFeature.html#FAIL_ON_UNKNOWN_PROPERTIES) 被禁用.

- [`MapperFeature.DEFAULT_VIEW_INCLUSION`](https://fasterxml.github.io/jackson-databind/javadoc/2.6/com/fasterxml/jackson/databind/MapperFeature.html#DEFAULT_VIEW_INCLUSION) 被禁用.


如果在类路径中检测到以下模块,它将自动注册以下模块:
- [jackson-datatype-jdk7](https://github.com/FasterXML/jackson-datatype-jdk7): 支持Java 7类型,比如`java.nio.file.Path`.

- [jackson-datatype-joda](https://github.com/FasterXML/jackson-datatype-joda): 支持Joda-Time类型.

- [jackson-datatype-jsr310](https://github.com/FasterXML/jackson-datatype-jsr310): 支持Java 8 Date和Time API 类型.

- [jackson-datatype-jdk8](https://github.com/FasterXML/jackson-datatype-jdk8): 支持其他Java 8类型,比如`Optional`.

> 使用jackson XML支持启用缩进,除了[jackson-dataformat-xml](https://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22org.codehaus.woodstox%22%20AND%20a%3A%22woodstox-core-asl%22) 这一个包之外,还需要依赖[woodstox-core-asl](https://search.maven.org/#search%7Cga%7C1%7Ca%3A%22jackson-dataformat-xml%22).

其他有趣的Jackson模块也可用：

- [jackson-datatype-money](https://github.com/zalando/jackson-datatype-money): 支持`javax.money`类型(非官方模块).

- [jackson-datatype-hibernate](https://github.com/FasterXML/jackson-datatype-hibernate): 支持Hibernate-specific类型和属性(包括延迟加载方面的特性).

以下示例显示了如何在XML中实现相同的配置:
```xml
<mvc:annotation-driven>
    <mvc:message-converters>
        <bean class="org.springframework.http.converter.json.MappingJackson2HttpMessageConverter">
            <property name="objectMapper" ref="objectMapper"/>
        </bean>
        <bean class="org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter">
            <property name="objectMapper" ref="xmlMapper"/>
        </bean>
    </mvc:message-converters>
</mvc:annotation-driven>

<bean id="objectMapper" class="org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean"
      p:indentOutput="true"
      p:simpleDateFormat="yyyy-MM-dd"
      p:modulesToInstall="com.fasterxml.jackson.module.paramnames.ParameterNamesModule"/>

<bean id="xmlMapper" parent="objectMapper" p:createXmlMapper="true"/>
```

### 1.10.8. View Controllers
这是定义`ParameterizableViewController`的快捷方式,该参数可在调用时立即转发到视图.在视图生成response之前没有Java控制器逻辑要执行的静态情况下,可以使用它.
以下Java配置示例将对`/`的请求转发到名为`home`的视图:
```java
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("home");
    }
}
```
以下示例通过使用XML的`<mvc:view-controller>`元素,实现了与上述示例相同的操作:
```xml
<mvc:view-controller path="/" view-name="home"/>
```
### 1.10.9. View Resolvers

MVC configuration简化了view resolver的注册.

以下Java configuration示例通过使用JSP和Jackson作为JSON呈现的默认视图来配置内容协商视图解析:

```java
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.enableContentNegotiation(new MappingJackson2JsonView());
        registry.jsp();
    }
}
```
下面的示例显示的是相同的XML配置:

```xml
<mvc:view-resolvers>
    <mvc:content-negotiation>
        <mvc:default-views>
            <bean class="org.springframework.web.servlet.view.json.MappingJackson2JsonView"/>
        </mvc:default-views>
    </mvc:content-negotiation>
    <mvc:jsp/>
</mvc:view-resolvers>
```

但是请注意,FreeMarker,Tiles,Groovy标记和script templates也需要配置基础视图技术.

MVC namespace提供了专用元素.以下示例适用于FreeMarker:
```xml
<mvc:view-resolvers>
    <mvc:content-negotiation>
        <mvc:default-views>
            <bean class="org.springframework.web.servlet.view.json.MappingJackson2JsonView"/>
        </mvc:default-views>
    </mvc:content-negotiation>
    <mvc:freemarker cache="false"/>
</mvc:view-resolvers>

<mvc:freemarker-configurer>
    <mvc:template-loader-path location="/freemarker"/>
</mvc:freemarker-configurer>
```

在Java configuration中,用户可以添加相应的`Configurer` bean,如以下示例所示:
```java
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.enableContentNegotiation(new MappingJackson2JsonView());
        registry.freeMarker().cache(false);
    }

    @Bean
    public FreeMarkerConfigurer freeMarkerConfigurer() {
        FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
        configurer.setTemplateLoaderPath("/freemarker");
        return configurer;
    }
}
```

### 1.10.10. Static Resources
此选项提供了一种方便的方法来从基于`Resource`的位置列表中提供静态资源.

在下一个示例中,给定以`/resources`开头的请求,将使用相对路径来查找和服务web应用程序根目录下或`/static`下的类路径下相对于`/public`的静态资源.
这些资源的使用期限为一年,以确保最大程度地利用浏览器缓存并减少浏览器发出的HTTP请求.还评估`Last-Modified` header,如果存在,则返回`304`状态码.

以下清单显示了如何使用Java configuration进行操作:
```java
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**")
            .addResourceLocations("/public", "classpath:/static/")
            .setCachePeriod(31556926);
    }
}
```
以下示例显示了如何在XML中实现相同的配置:
```xml
<mvc:resources mapping="/resources/**"
    location="/public, classpath:/static/"
    cache-period="31556926" />
```
资源处理程序还支持`ResourceResolver`实现和`ResourceTransformer`实现的chain,用户可以使用它们来创建用于处理优化资源的toolchain.
基于从内容,固定的应用程序版本或其他计算出来的MD5哈希,用户可以对版本化的资源url使用`VersionResourceResolver`.
`ContentVersionStrategy`(MD5哈希)是一个很好的选择--有一些明显的例外,比如与模块加载器一起使用的JavaScript资源.
以下示例显示如何在Java configuration中使用`VersionResourceResolver`:
```java
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/public/")
                .resourceChain(true)
                .addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"));
    }
}
```
以下示例显示了如何在XML中实现相同的配置:
```xml
<mvc:resources mapping="/resources/**" location="/public/">
    <mvc:resource-chain resource-cache="true">
        <mvc:resolvers>
            <mvc:version-resolver>
                <mvc:content-version-strategy patterns="/**"/>
            </mvc:version-resolver>
        </mvc:resolvers>
    </mvc:resource-chain>
</mvc:resources>
```
然后,可以使用`ResourceUrlProvider`重写url,并应用解析器和转换器的完整链——例如,插入版本.MVC配置提供了`ResourceUrlProvider` bean,以便可以将其注入其他对象.
您还可以使用ResourceUrlEncodingFilter对Thymeleaf，JSP，FreeMarker以及其他依赖于HttpServletResponse＃encodeURL的URL标签的重写透明化。



































### 1.10.11. Default Servlet
Spring MVC允许将`DispatcherServlet`映射到`/`(因此覆盖了容器默认Servlet的映射),同时仍然允许由容器的默认Servlet处理静态资源请求.
它配置`DefaultServletHttpRequestHandler`为`/**`的URL映射和相对于其他URL映射的最低优先级.

这个handler转发所有请求到默认Servlet.因此,它必须按所有其他URL `HandlerMapping`s的顺序保留在最后.
如果使用`<mvc:annotation-driven>`,就是这种情况.另外,如果用户设置自己的自定义`HandlerMapping`实例,请确保将其`order`属性设置为小于`DefaultServletHttpRequestHandler`的值(即`Integer.MAX_VALUE`).

下面的示例演示如何使用默认设置启用功能:
```java
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }
}
```
以下示例显示了如何在XML中实现相同的配置:
```xml
<mvc:default-servlet-handler/>
```
覆盖`/` Servlet映射的注意事项是,默认Servlet的`RequestDispatcher`必须根据名称而不是路径检索.`DefaultServletHttpRequestHandler`尝试使用大多数主要Servlet容器(包括Tomcat,Jetty,GlassFish,JBoss,Resin,WebLogic和WebSphere)的已知名称列表,在启动时自动检测容器的默认Servlet.如果默认Servlet是使用其他名称自定义配置的,或者在默认Servlet名称未知的情况下使用了不同的Servlet容器,那么用户必须明确提供默认Servlet的名称,如以下示例所示:
```java
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable("myCustomDefaultServlet");
    }

}
```
以下示例显示了如何在XML中实现相同的配置:
```xml
<mvc:default-servlet-handler default-servlet-name="myCustomDefaultServlet"/>
```
### 1.10.12. Path Matching
用户可以自定义与路径匹配和URL处理有关的选项.有关各个选项的详细信息,请参见[`PathMatchConfigurer`](https://docs.spring.io/spring-framework/docs/5.1.10.RELEASE/javadoc-api/org/springframework/web/servlet/config/annotation/PathMatchConfigurer.html) javadoc.

以下示例显示了如何在Java configuration中自定义路径匹配:
```java
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer
            .setUseSuffixPatternMatch(true)
            .setUseTrailingSlashMatch(false)
            .setUseRegisteredSuffixPatternMatch(true)
            .setPathMatcher(antPathMatcher())
            .setUrlPathHelper(urlPathHelper())
            .addPathPrefix("/api",
                    HandlerTypePredicate.forAnnotation(RestController.class));
    }

    @Bean
    public UrlPathHelper urlPathHelper() {
        //...
    }

    @Bean
    public PathMatcher antPathMatcher() {
        //...
    }

}
```
以下示例显示了如何在XML中实现相同的配置:
```xml
<mvc:annotation-driven>
    <mvc:path-matching
        suffix-pattern="true"
        trailing-slash="false"
        registered-suffixes-only="true"
        path-helper="pathHelper"
        path-matcher="pathMatcher"/>
</mvc:annotation-driven>

<bean id="pathHelper" class="org.example.app.MyPathHelper"/>
<bean id="pathMatcher" class="org.example.app.MyPathMatcher"/>
```
### 1.10.13. Advanced Java Config

添加`@EnableWebMvc`注解,意味着导入了`DelegatingWebMvcConfiguration`,而导入该类,意味着

- 为Spring MVC应用程序提供默认的Spring配置
- 检测并委托给`WebMvcConfigurer`接口的实现以自定义该配置.

对于高级模式,可以删除`@EnableWebMvc`并直接继承`DelegatingWebMvcConfiguration`而不是实现`WebMvcConfigurer`,如以下示例所示:
```java
@Configuration
public class WebConfig extends DelegatingWebMvcConfiguration {

    // ...

}
```
用户可以将现有方法保留在`WebConfig`中,还可以覆盖基类中的bean声明,并且在类路径上仍然可以具有任意数量的其他`WebMvcConfigurer`实现.


### 1.10.14. Advanced XML Config

MVC namespace没有高级模式.如果用户需要在bean上自定义一个不能更改的属性,则可以使用Spring `ApplicationContext`的`BeanPostProcessor`生命周期挂钩,如以下示例所示:
```java
@Component
public class MyPostProcessor implements BeanPostProcessor {

    public Object postProcessBeforeInitialization(Object bean, String name) throws BeansException {
        // ...
    }
}
```
请注意,用户需要将`MyPostProcessor`声明为bean,或者在XML中显式声明,或者通过`<component-scan/>`声明将其扫描出来.