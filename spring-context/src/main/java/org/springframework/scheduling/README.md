#7. 任务执行和定时

Spring framework分别通过`TaskExecutor`和`TaskScheduler`为异步任务执行和定时任务执行提供了抽象.
Spring还提供那些在应用程序服务器环境中支持线程池或委托给CommonJ接口的实现.
最后,在公有接口后面使用这些实现可以抽象化Java SE 5,Java SE 6和Java EE环境之间的差异.

Spring还有功能集成类,以支持使用`Timer`(从1.3开始成为JDK的一部分)和Quartz Scheduler(https://www.quartz-scheduler.org/)进行调度.
你可以使用`FactoryBean`分别设置对`Timer`或`Trigger`实例的可选引用来设置这两个调度程序.
此外,还提供了Quartz Scheduler和`Timer`的便捷类,可用于调用现有目标对象的方法(类似于常规的`MethodInvokingFactoryBean`操作)

## 7.1 Spring `TaskExecutor`

## 7.2 Spring `TaskScheduler `

除了`TaskExecutor`之外,Spring 3.0还引入了`TaskScheduler`,它提供了多种方法来计划任务在将来某个时刻运行.
以下列表显示了`TaskScheduler`接口定义:

```java
public interface TaskScheduler {

    ScheduledFuture schedule(Runnable task, Trigger trigger);

    ScheduledFuture schedule(Runnable task, Instant startTime);

    ScheduledFuture schedule(Runnable task, Date startTime);

    ScheduledFuture scheduleAtFixedRate(Runnable task, Instant startTime, Duration period);

    ScheduledFuture scheduleAtFixedRate(Runnable task, Date startTime, long period);

    ScheduledFuture scheduleAtFixedRate(Runnable task, Duration period);

    ScheduledFuture scheduleAtFixedRate(Runnable task, long period);

    ScheduledFuture scheduleWithFixedDelay(Runnable task, Instant startTime, Duration delay);

    ScheduledFuture scheduleWithFixedDelay(Runnable task, Date startTime, long delay);

    ScheduledFuture scheduleWithFixedDelay(Runnable task, Duration delay);

    ScheduledFuture scheduleWithFixedDelay(Runnable task, long delay);
}
```
名为`schedule`,参数只有`Runnable`和`Date`的方法,是`TaskScheduler`中最简单的调度方法.这将令任务在指定时间后运行一次.
其他所有方法都可以令任务重复运行. `fixed-rate`和`fixed-delay`方法用于简单的定期执行,但是接受`Trigger`作为参数的方法则更加灵活.

### 7.2.1 `Trigger` 接口

`Trigger`接口的灵感主要来自于[JSR-236](https://jcp.org/en/jsr/detail?id=236),该接口在Spring 3.0时还没有正式实现.
`Trigger`的基本思想是:执行时间可以根据过去的执行结果或者其他任意条件来确定.如果这些决定确实考虑了前面执行的结果,那么这些信息在`TriggerContext`中是可用的.
`Trigger`接口自身是相当的简单:

```java
public interface Trigger {

    Date nextExecutionTime(TriggerContext triggerContext);
}
```

`TriggerContext`是最重要的部分.它封装了所有相关的数据,如果需要,将来还可以扩展.`TriggerContext`是一个接口(默认情况下使用`SimpleTriggerContext`实现).
下面的列表显示了`Trigger`实现的可用方法:

```java
public interface TriggerContext {

    Date lastScheduledExecutionTime();

    Date lastActualExecutionTime();

    Date lastCompletionTime();
}
```
### 7.2.2 `Trigger` 实现

Spring为`Trigger`接口提供了两个实现.最有趣的是`CronTrigger`.它启用了基于cron表达式的任务调度.
例如,下面任务计划在每小时的15分钟后运行,但仅在工作日的9到5点的"工作时间"内运行:
```java
scheduler.schedule(task, new CronTrigger("0 15 9-17 * * MON-FRI"));
```

另一个实现是`PeriodicTrigger`,它接受一个固定的周期,一个可选的初始延迟值和一个布尔值,
以指示该周期是应解释为`fixed-rate`还是`fixed-delay`.由于`TaskScheduler`接口已经定义了以`fixed-rate`和`fixed-delay`调度任务的方法,
因此应尽可能直接使用这些方法.`PeriodicTrigger`实现的价值在于:你可以在依赖`Trigger`抽象的组件中使用它.
例如:允许周期性触发器,基于cron的触发器,甚至自定义触发器实现可互换使用,这可能很方便.
这样的组件可以利用依赖项注入,这样你就可以在外部配置这样的`Trigger`,从而可以轻松地修改或扩展它们.

### 7.2.3. `TaskScheduler`实现

与Spring的`TaskExecutor`一样,`TaskScheduler`这样设置的主要好处是应用程序的调度需求与部署环境解耦.
当部署到应用服务器环境时,此抽象概念尤其相关,因为应用程序本身不应该直接创建线程.
对于这样的场景,Spring提供了一个`TimerManagerTaskScheduler`,它委托给WebLogic或WebSphere上的CommonJ `TimerManager`,
以及一个最近更新的`DefaultManagedTaskScheduler`,它委托给Java EE 7+环境中的JSR-236 `ManagedScheduledExecutorService`.两者通常都配置了JNDI查找.


只要不需要外部线程管理,一个更简单的替代方法就是在应用程序中设置一个本地`ScheduledExecutorService`,它可以通过Spring的`ConcurrentTaskScheduler`进行调整.
为了方便起见,Spring还提供了`ThreadPoolTaskScheduler`,它在内部委托给`ScheduledExecutorService`,以提供与`ThreadPoolTaskExecutor`类似的公共bean风格的配置.
这些变体对于宽松的应用程序服务器环境中的本地嵌入式线程池设置非常有效,特别是在Tomcat和Jetty上.

## 7.3 基于注解的定时和异步任务执行的支持

Spring为任务调度和异步方法执行提供注解支持.

### 7.3.1. Enable Scheduling Annotations

要启用对`@Scheduled`和`@Async`注解的支持,可以将`@EnableScheduling`和`@EnableAsync`添加到被`@Configuration`注解的类上,如以下示例所示:
```java
@Configuration
@EnableAsync
@EnableScheduling
public class AppConfig {
}
```

如果你的应用只需要支持`@Scheduled`,那么你就可以只启用`@EnableScheduling`注解.
为了获得更细粒度的控制,还可以另外实现[`SchedulingConfigurer`](https://docs.spring.io/spring-framework/docs/5.1.10.RELEASE/javadoc-api/org/springframework/scheduling/annotation/SchedulingConfigurer.html)接口,[`AsyncConfigurer`](https://docs.spring.io/spring-framework/docs/5.1.10.RELEASE/javadoc-api/org/springframework/scheduling/annotation/AsyncConfigurer.html)接口,或者两者都实现.

如果喜欢xml配置,还可以使用`<task:annotation-driven>`元素:
```xml
<task:annotation-driven executor="myExecutor" scheduler="myScheduler"/>
<task:executor id="myExecutor" pool-size="5"/>
<task:scheduler id="myScheduler" pool-size="10"/>
```

注意,对于前面的XML,提供了一个executor引用来处理那些与带有`@Async`注解的方法对应的任务,并提供了调度程序引用来管理那些带有`@Scheduled`注解的方法.

> 处理`@Async`注解的默认通知模式是`proxy`,它只允许通过代理拦截调用.同一类内的本地调用不能通过这种方式被截获.
> 对于更高级的拦截模式,可以考虑结合编译时或加载时织入切换到`aspectj`模式.

### 7.3.2. The @Scheduled 注解

您可以将`@Scheduled`注解以及触发器元数据添加到方法中.例如,下面的方法每5秒调用一次,延迟固定,这意味着周期是从每次调用的完成时间开始计算的:
```java
@Scheduled(fixedDelay=5000)
public void doSomething() {
    // something that should execute periodically
}
```

如果需要`fixed-rate`执行,可以更改注解中指定的属性名.每五秒钟调用一次以下方法(在每次调用的连续开始时间之间测量):

```java
@Scheduled(fixedRate=5000)
public void doSomething() {
    // something that should execute periodically
}

```
对于`fixed-delay`和`fixed-rate`的任务,可以通过指示在方法第一次执行之前等待的毫秒数来指定初始延迟,如下面的`fixedRate`示例所示:
```java
@Scheduled(initialDelay=1000, fixedRate=5000)
public void doSomething() {
    // something that should execute periodically
}
```

如果简单的周期性调度表达能力不够,可以提供cron表达式.例如,以下代码只在工作日执行:
```java
@Scheduled(cron="*/5 * * * * MON-FRI")
public void doSomething() {
    // something that should execute on weekdays only
}
```
> 还可以使用`zone`属性指定解析cron表达式的时区.


请注意,要调度的方法的返回值类型必须是`void`,并且不能有任何参数.如果方法需要与来自容器的其他对象交互,则通常通过依赖项注入提供这些对象.

>从Spring Framework 4.3开始,任何作用域的bean都支持`@Scheduled`注解的方法.
>
>确保您没有在运行时初始化同一个`@Scheduled`注解类的多个实例,除非业务确实希望对每个这样的实例调度回调的需求.
>与此相关的是,确保不要在加了`@Scheduled`注解的类上再添加`@Configurable`注解.
>否则,你将得到两次初始化(一次通过容器,一次通过@Configurable  织入),结果是每个`@Scheduled`方法被调用两次.

### 7.3.3. The @Async annotation
### 7.3.4. Executor Qualification with @Async
### 7.3.5. Exception Management with @Async
