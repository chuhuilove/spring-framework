# <img src="src/docs/asciidoc/images/spring-framework.png" width="80" height="80"> Spring Framework

This is the home of the Spring Framework: the foundation for all [Spring projects](https://spring.io/projects). Collectively the Spring Framework and the family of Spring projects is often referred to simply as "Spring". 

Spring provides everything required beyond the Java programming language for creating enterprise applications for a wide range of scenarios and architectures. Please read the [Overview](https://docs.spring.io/spring/docs/current/spring-framework-reference/overview.html#spring-introduction) section as reference for a more complete introduction.

## Code of Conduct

This project is governed by the [Spring Code of Conduct](CODE_OF_CONDUCT.adoc). By participating, you are expected to uphold this code of conduct. Please report unacceptable behavior to spring-code-of-conduct@pivotal.io.

## Access to Binaries

For access to artifacts or a distribution zip, see the [Spring Framework Artifacts](https://github.com/spring-projects/spring-framework/wiki/Spring-Framework-Artifacts) wiki page.

## Documentation

The Spring Framework maintains reference documentation ([published](https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/) and [source](src/docs/asciidoc)), Github [wiki pages](https://github.com/spring-projects/spring-framework/wiki), and an
[API reference](https://docs.spring.io/spring-framework/docs/current/javadoc-api/). There are also [guides and tutorials](https://spring.io/guides) across Spring projects.

## Build from Source

See the [Build from Source](https://github.com/spring-projects/spring-framework/wiki/Build-from-Source) Wikipedia page and the [CONTRIBUTING.md](CONTRIBUTING.md) file.

## Stay in Touch

Follow [@SpringCentral](https://twitter.com/springcentral), [@SpringFramework](https://twitter.com/springframework), and its [team members](https://twitter.com/springframework/lists/team/members) on Twitter. In-depth articles can be found at [The Spring Blog](https://spring.io/blog/), and releases are announced via our [news feed](https://spring.io/blog/category/news).

## License

The Spring Framework is released under version 2.0 of the [Apache License](https://www.apache.org/licenses/LICENSE-2.0).


## spring 小知识点随笔

### Spring IoC提供的注入方式
在spring5.1.10.RELEASE中,Spring 提供了两种注入方式:

1. [基于构造方法的注入方式](https://docs.spring.io/spring/docs/5.1.10.RELEASE/spring-framework-reference/core.html#beans-constructor-injection)
2. [基于Setter的注入方式](https://docs.spring.io/spring/docs/5.1.10.RELEASE/spring-framework-reference/core.html#beans-setter-injection)

### [Spring IoC提供的注入模型](https://docs.spring.io/spring/docs/5.1.10.RELEASE/spring-framework-reference/core.html#beans-factory-autowire)

1. no 
    (默认)没有自动装配.Bean引用必须由ref元素定义.对于较大的部署,建议不要更改默认设置,因为明确指定协作者可以提供更好的控制和清晰度.
    在某种程度上,它记录了系统的结构.
    在很多情况下,在属性上添加了一个`@Autowired`注解,则这个属性的不是自动装配的,
2. byType 
    
    如果容器中恰好存在该属性类型的一个bean,则允许自动获取该属性.

3. byName
 
   通过属性名自动装配.Spring寻找与需要自动装配的属性同名的bean.
   例如,如果一个bean定义被设置为按名称自动装配,并且包含一个`master`属性(即,它具有`setMaster(...)方法),那么Spring将查找一个名为`master`的bean定义,并使用它来设置该属性.
   
4. constructor
    类似于byType,但适用于构造函数参数.如果容器中不存在构造函数参数类型的一个bean,则将引发致命错误.



2019-12-20 16:40:08

今晚若有时间,集中精力,查看bean的实例化过程.






