###1.15.2 标准和自定义事件

`ApplicationContext`中的事件处理是通过`ApplicationEvent`类和`ApplicationListener`接口提供的.如果将实现`ApplicationListener`接口的bean部署到上下文中,
每当将`ApplicationEvent`发布到`ApplicationContext`时,都会通知该bean.本质上,这是标准的观察者设计模式.

>从Spring 4.2开始,事件基础结构得到了显着改进,并提供了基于注解的模型以及发布任意事件(即不一定从`ApplicationEvent`扩展的对象)的功能.

**下表描述了Spring提供的标准事件:**


|事件|说明|
|:---:|:---:|
|ContextRefreshedEvent|在初始化或刷新`ApplicationContext`时发布(例如,通过使用`ConfigurableApplicationContext`接口上的`refresh()`方法).在这里,"已初始化"是指所有Bean均已加载,检测到并激活了post-processor处理器Bean,已预先实例化单例,并且可以使用`ApplicationContext`对象.只要容器没有关闭,就可以多次触发刷新,前提是所选的`ApplicationContext`实际上支持这样的"热"更新.例如,`XmlWebApplicationContext`支持"热"更新,但是`GenericApplicationContext`不支持.|
|ContextStartedEvent|在`ConfigurableApplicationContext`接口上使用`start()`方法启动`ApplicationContext`时发布.在这里,"started"表示所有`Lifecycle` bean都收到一个明确的启动信号.|
|ContextStoppedEvent||
|ContextClosedEvent|通过使用`ConfigurableApplicationContext`接口上的`close()`方法关闭`ApplicationContext`时发布.|
|RequestHandledEvent||


你也可以创建和定制自己的事件.下面的例子展示了一个简单的类,它扩展了Spring的`ApplicationEvent`基类:

```java
public class BlackListEvent extends ApplicationEvent {

    private final String address;
    private final String content;

    public BlackListEvent(Object source, String address, String content) {
        super(source);
        this.address = address;
        this.content = content;
    }

    // accessor and other methods...
}
```

要发布自定义`ApplicationEvent`,请调用`ApplicationEventPublisher`上的`publishEvent()`方法.通常,这是通过创建实现`ApplicationEventPublisherAware`的类并将其注册为Spring bean来实现的.
下面的例子展示了这样一个类:
```java
public class EmailService implements ApplicationEventPublisherAware {

    private List<String> blackList;
    private ApplicationEventPublisher publisher;

    public void setBlackList(List<String> blackList) {
        this.blackList = blackList;
    }

    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void sendEmail(String address, String content) {
        if (blackList.contains(address)) {
            publisher.publishEvent(new BlackListEvent(this, address, content));
            return;
        }
        // send email...
    }
}
```

在配置时,Spring容器检测到`EmailService`实现了`ApplicationEventPublisherAware`并自动调用`setApplicationEventPublisher()`.
实际上,传入的参数是Spring容器本身.您正在通过其`ApplicationEventPublisher`接口与应用程序上下文进行交互.

要接收自定义`ApplicationEvent`,您可以创建一个实现`ApplicationListener`的类,并将其注册为一个Spring bean.下面的示例展示了这样的一个类:

```java
public class BlackListNotifier implements ApplicationListener<BlackListEvent> {

    private String notificationAddress;

    public void setNotificationAddress(String notificationAddress) {
        this.notificationAddress = notificationAddress;
    }

    public void onApplicationEvent(BlackListEvent event) {
        // notify appropriate parties via notificationAddress...
    }
}
```

注意,`ApplicationListener`通常是用定制事件的类型参数化的(前一个示例中的`BlackListEvent`).这意味着`onApplicationEvent()`方法可以保证类型安全,避免任何向下转换的操作.
你可以注册任意数量的事件监听器,但是请注意,默认情况下,事件监听器同步接收事件.这意味着`publishEvent()`方法将阻塞,直到所有侦听器都完成对事件的处理.
这种同步和单线程方法的一个优点是,当侦听器接收到事件时,如果事务上下文可用,它将在发布者的事务上下文中进行操作.
如果需要另一种事件发布策略,请参阅javadoc以获得Spring的[ApplicationEventMulticaster](https://docs.spring.io/spring-framework/docs/5.1.10.RELEASE/javadoc-api/org/springframework/context/event/ApplicationEventMulticaster.html)接口的详细文档.


下面的例子展示了用于注册和配置上述每个类的bean定义:

```xml
<bean id="emailService" class="example.EmailService">
    <property name="blackList">
        <list>
            <value>known.spammer@example.org</value>
            <value>known.hacker@example.org</value>
            <value>john.doe@example.org</value>
        </list>
    </property>
</bean>

<bean id="blackListNotifier" class="example.BlackListNotifier">
    <property name="notificationAddress" value="blacklist@example.org"/>
</bean>
```
总之,当调用`emailService` bean的`sendEmail()`方法时,如果有任何应该列入黑名单的电子邮件消息,则会发布`BlackListEvent`类型的自定义事件.
`blackListNotifier` bean注册为`ApplicationListener`并接收`BlackListEvent`,此时它可以通知相关方.

>Spring的事件机制是为同一应用程序上下文中Spring bean之间的简单通信而设计的.
>然而,对于更复杂的企业集成需求,单独维护的[Spring integration](https://spring.io/projects/spring-integration)项目提供了对构建轻量级,[面向模式](https://www.enterpriseintegrationpatterns.com/),事件驱动的体系结构的完整支持,这些体系结构构建于著名的Spring编程模型之上.


#### 基于注解的事件监听器

从Spring 4.2开始,你可以使用`EventListener`注解在托管bean的任何公共方法上注册事件监听器.`BlackListNotifier`可以重写如下:

```java
public class BlackListNotifier {

    private String notificationAddress;

    public void setNotificationAddress(String notificationAddress) {
        this.notificationAddress = notificationAddress;
    }

    @EventListener
    public void processBlackListEvent(BlackListEvent event) {
        // notify appropriate parties via notificationAddress...
    }
}
```
方法签名再次声明它侦听的事件类型(这里为`BlackListEvent`类型),但这次使用灵活的名称,并且没有实现特定的监听器接口(之前实现的是`ApplicationListener`接口).
只要实际的事件类型解决了其实现层次结构中的泛型参数,就可以通过泛型缩小事件类型.

如果你的方法想要监听多个事件,或者如果你不使用任何参数定义监听方法,也可以在注解本身上指定事件类型.下面的例子演示了如何做到这一点:

```java
@EventListener({ContextStartedEvent.class, ContextRefreshedEvent.class})
public void handleContextStart() {
    ...
}
```

也可以通过使用定义[SpEL表达式](https://docs.spring.io/spring/docs/5.1.10.RELEASE/spring-framework-reference/core.html#expressions)注解中的`condition`属性来添加额外的运行时过滤,该注解属性应该与实际调用特定事件的方法相匹配.

下面的例子展示了如何重写我们的通知程序,只有在事件的内容属性等于`my-event`时才能被调用:

```java
@EventListener(condition = "#blEvent.content == 'my-event'")
public void processBlackListEvent(BlackListEvent blEvent) {
    // notify appropriate parties via notificationAddress...
}
```

#### 异步监听

如果希望特定的侦听器异步处理事件,可以使用[常规的`@Async`](https://docs.spring.io/spring/docs/5.1.10.RELEASE/spring-framework-reference/integration.html#scheduling-annotation-support-async)支持.下面的例子演示了如何做到这一点:
```java
@EventListener
@Async
public void processBlackListEvent(BlackListEvent event) {
    // BlackListEvent is processed in a separate thread
}
```
在使用异步事件时要注意以下限制:

- 如果事件侦听器抛出异常,则不会将其传播到调用者,请参阅`AsyncUncaughtExceptionHandler`了解更多细节.
- 此类事件侦听器无法发送应答.如果你需要发送另一个事件作为处理的结果,请注入[`ApplicationEventPublisher`](https://docs.spring.io/spring-framework/docs/5.1.10.RELEASE/javadoc-api/org/springframework/aop/interceptor/AsyncUncaughtExceptionHandler.html)以手动发送事件.

#### 监听器顺序

如果需要在调用另一个侦听器之前调用一个侦听器,可以在方法声明中添加`@Order`注解,如下面的示例所示:
```java
@EventListener
@Order(42)
public void processBlackListEvent(BlackListEvent event) {
    // notify appropriate parties via notificationAddress...
}
```


#### 通用事件