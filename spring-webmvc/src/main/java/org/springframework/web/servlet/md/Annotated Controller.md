## 1.3 Annotated Controller

Spring MVC提供了一个基于注解的编程模型,其中`@Controller`和`@RestController`组件使用注解来表示请求映射,请求输入,异常处理等.带注解的控制器具有灵活的方法签名,无需扩展基类或实现特定的接口.下面的示例显示了由注解定义的控制器:

```Java
@Controller
public class HelloController {

    @GetMapping("/hello")
    public String handle(Model model) {
        model.addAttribute("message", "Hello World!");
        return "index";
    }
}
```

在上面的示例中,该方法接受`Model`并以`String`的形式返回视图名称,但是还存在许多其他选项,本章稍后将对其进行说明.

### <span id="mvc-ann-controller">1.3.1. Declaration</span>

在Servlet的`WebApplicationContext`中你可以使用标准Spring bean 定义来定义controller bean.

`@Controller` 模板允许自动检测,与与Spring对在类路径中检测`@Component`类以及为它们自动注册bean定义相一致.它还充当带注解的类的模板,指示它作为web组件的角色.

要启用自动检测这样的`@Controller` bean,你可以将组件扫描添加到你的Java配置中,如下面的示例所示:

```java
@Configuration
@ComponentScan("org.example.web")
public class WebConfig {

    // ...
}
```

下面的例子展示了与前面例子等价的XML配置:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:p="http://www.springframework.org/schema/p"
    xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        https://www.springframework.org/schema/context/spring-context.xsd">

    <context:component-scan base-package="org.example.web"/>

    <!-- ... -->

</beans>
```
`@RestController`是一个组合的注解,其本身使用`@Controller`和`@ResponseBody`作为元注解,以指示被其注解的类是一个控制器,以及其每个方法都继承了类型级别的`@ResponseBody`注解,因此它直接写入响应体而不是视图解析,并使用HTML模板进行呈现.


#### AOP代理

在某些情况下,你可能需要在运行时用AOP代理装饰控制器.例如,如果直接在控制器上使用`@Transactional`注解.在这种情况下,特别是对于控制器,我们建议使用基于类的代理.这通常是控制器的默认选择.但是,如果控制器必须实现不属于Spring上下文回调的接口(例如`InitializingBean`或者`*Aware`等),则可能需要显式地配置基于类的代理.例如,使用`<tx:annotation-driven/>`,你可以更改为`<tx:annotation-driven proxy-target-class="true"/>`.

### <span id="mvc-ann-requestmapping">1.3.2. Request Mapping</span>

你可以使用`@RequestMapping`注解将请求映射到控制器方法.它具有各种属性,可以通过URL,HTTP方法,请求参数,请求头和媒体类型进行匹配.你可以在类级别使用它来表示共享的映射,也可以在方法级别使用它来缩小到特定的端点映射.

`@RequestMapping`还有HTTP方法特定的快捷方式:

- `@GetMapping`

- `@PostMapping`

- `@PutMapping`

- `@DeleteMapping`

- `@PatchMapping`


上面的注解是Spring MVC提供的[自定义注解](),因为大多数控制器方法应该映射到特定的HTTP方法,而不是使用`@RequestMapping`,在不指定HTTP请求方法的情况下,`@RequesMapping`注解默认情况下与所有HTTP方法匹配.同时,在类级别仍需要`@RequestMapping`来表示共享映射.上面的自定义注解无法注解在类级别上.

以下示例具有类型和方法级别的映射:

```java

@RestController
@RequestMapping("/persons")
class PersonController {

    @GetMapping("/{id}")
    public Person getPerson(@PathVariable Long id) {
        // ...
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void add(@RequestBody Person person) {
        // ...
    }
}
```

#### <span id="mvc-ann-requestmapping-uri-templates">URI patterns<span>

你可以使用以下全局模式和通配符来映射请求:

- `?`  匹配一个字符
- `*`  匹配路径段中的零个或多个字符
- `**` 匹配零或多个路径段


同时,还可以声明URI变量并使用`@PathVariable`访问其值,如以下示例所示:

```java
@GetMapping("/owners/{ownerId}/pets/{petId}")
public Pet findPet(@PathVariable Long ownerId, @PathVariable Long petId) {
    // ...
}
```

还可以在类和方法级别声明URI变量,如以下示例所示:

```java
@Controller
@RequestMapping("/owners/{ownerId}")
public class OwnerController {

    @GetMapping("/pets/{petId}")
    public Pet findPet(@PathVariable Long ownerId, @PathVariable Long petId) {
        // ...
    }
}
```
URI变量会自动转换为适当的类型,或者引发`TypeMismatchException`.默认情况下支持简单类型(`int`,`long`,`Date`等),当然还可以自己注册对任何其他数据类型的支持. 参看[类型转换]()和[`DataBinder`](#mvc-ann-initbinder).


你可以显式地命名URI变量(例如,`@PathVariable("customId")`),但是如果名称相同,并且你的代码是用调试信息或Java 8上的`-parameters`编译器标志编译的,那么你可以省略这个细节.


语法`{varName:regex}`声明带有正则表达式的URI变量,语法为`{varName:regex}`.

例如,给定URL`"/spring-web-3.0.5 .jar"`,以下方法提取名称,版本和文件扩展名:

```java
@GetMapping("/{name:[a-z-]+}-{version:\\d\\.\\d\\.\\d}{ext:\\.[a-z]+}")
public void handle(@PathVariable String version, @PathVariable String ext) {
    // ...
}

```

URI路径模式也可以嵌入`${…​}`占位符,这些占位符在启动时通过针对本地,系统,环境和其他属性源使用`PropertyPlaceHolderConfigurer`进行解析.例如,可以使用它根据外部配置对基本URL进行参数化.

>Spring MVC使用spring-core中的PathMatcher和AntPathMatcher实现来进行URI路径匹配.


#### <span id="mvc-ann-requestmapping-pattern-comparison">Pattern Comparison</span>

当多个模式与URL匹配时,必须将它们进行比较以找到最佳匹配.这是通过使用`AntPathMatcher.getPatternComparator(String path)`来完成的,它查找更具体的模式.

如果模式的URI变量(计数为1),单通配符(计数为1)和双通配符(计数为2)的数量较少,则模式的含义不太明确.如果给定的scope,则选择较长的模式.给定相同的scope和length,将选择具有比通配符更多URI变量的模式.

默认映射模式`(/**)`不参与评比,并且始终排在最后.此外,前缀模式(如`/public/**`)被认为比其他没有双通配符的模式更不具体.


有关完整的详细信息,请参阅`AntPathMatcher`中的`AntPatternComparator`,并且请记住,你可以自定义`PathMatcher`实现.请参阅配置部分中的[路径匹配]().

#### <span id="mvc-ann-requestmapping-suffix-pattern-match">Suffix Match</span>

默认情况下,Spring MVC执行`.*`后缀模式匹配,以便映射到`/person`的控制器也隐式映射到`/person.*`.然后,文件扩展名用于解释请求的内容类型以用于响应(即,代替Accept头),例如`/person.pdf`,`/person.xml`等.

当浏览器用于发送难以一致解释的`Accept`标头时,以这种方式使用文件扩展名是必要的.目前,这已不再是必须的,使用`Accept`标头应该是首选.


随着时间的推移,文件扩展名的使用在许多方面都被证明是有问题的.当使用URI变量,路径参数和URI编码进行覆盖时,可能会引起歧义.关于基于URL的授权和安全性的推理(请参阅下一部分以了解更多详细信息)也变得更加困难.

若要完全禁用文件扩展名,必须设置以下两项:

- `useSuffixPatternMatching(false)`,参看[PathMatchConfigurer]()

- `favorPathExtension(false)`, 参看[ContentNegotiationConfigurer]()

基于URL的内容交互仍然有用(例如,在浏览器中键入URL时).为此,我们建议使用基于查询参数的策略,以避免文件扩展名附带的大多数问题.或者,如果必须使用文件扩展名,请考虑通过`ContentNegotiationConfigurer`的`mediaTypes`属性将它们限制为已显式注册的扩展名列表.

#### <span id="mvc-ann-requestmapping-rfd">Suffix Match and RFD</span>

反射文件下载(RFD)攻击与XSS相似,它依赖于响应中反映的请求输入(例如,查询参数和URI变量).但是,RFD攻击不是将JavaScript插入HTML,而是依靠浏览器切换来执行下载,并在以后双击时将响应视为可执行脚本.

在Spring MVC中,`@ResponseBody`和`ResponseEntity`方法存在风险，因为它们可以呈现不同的内容类型,客户端可以通过URL路径扩展来请求这些内容类型.禁用后缀模式匹配并使用路径扩展进行内容交互可以降低风险,但不足以防止RFD攻击.


为了防止RFD攻击,Spring MVC在呈现响应体之前添加了`Content-Disposition:inline;filename=f.txt`头,以建议固定且安全的下载文件.仅当URL路径包含既未列入白名单也未明确注册用于内容交互的文件扩展名时,才执行此操作.然而,当url被直接输入到浏览器中时,它可能有潜在的副作用.


默认情况下,许多常见的路径扩展名都被列入白名单.具有自定义`HttpMessageConverter`实现的应用程序可以显式地注册用于内容交互的文件扩展名,以避免为这些扩展添加`Content-Disposition`头.参看[Content Types]().

有关RFD的其他建议,请参见[CVE-2015-5211](https://pivotal.io/security/cve-2015-5211).

#### <span id="mvc-ann-requestmapping-consumes">Consumable Media Types</span>

您可以根据请求的`Content-Type`缩小请求映射,如以下示例所示:

```java
@PostMapping(path = "/pets", consumes = "application/json") // 使用consumes属性可按内容类型缩小映射范围.
public void addPet(@RequestBody Pet pet) {
    // ...
}
```
`consumes`属性还支持否定表达式-例如,`!text/plain`表示除`text/plain`之外的任何内容类型.

你可以在类级别上声明一个共享的`consumes`属性.但是,与大多数其他请求映射属性不同的是,当在类级别使用时,方法级别的属性会覆盖而不是扩展类级别声明.

>`MediaType`为常用的媒体类型提供常量,例如`APPLICATION_JSON_VALUE`和`APPLICATION_XML_VALUE`.

#### <span id="mvc-ann-requestmapping-produces">Producible Media Types</span>

您可以根据接受请求头和控制器方法生成的内容类型列表来缩小请求映射,如以下示例所示:

```java
@GetMapping(path = "/pets/{petId}", produces = "application/json;charset=UTF-8")  // 使用produces属性可以按内容类型缩小映射.
@ResponseBody
public Pet getPet(@PathVariable String petId) {
    // ...
}
```

媒体类型可以指定字符集.支持否定的表达式-例如,`!text/plain`表示除`text/plain`以外的任何内容类型.

#### <span id="mvc-ann-requestmapping-params-and-headers">Parameters, headers</span>

#### <span id="mvc-ann-requestmapping-head-options">HTTP HEAD, OPTIONS</span>

#### <span id="mvc-ann-requestmapping-composed">Custom Annotations</span>

Spring MVC支持将[组合注解]()用于请求映射.这些注解本身使用`@RequestMapping`作为元注解,并且旨在以更狭窄,更具体的用途重新声明`@RequestMapping`属性的子集(或全部).


`@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`,和`@PatchMapping`这些都是组合注解的示例.之所以提供这些方法,是因为大多数控制器方法应该映射到特定的HTTP方法,而不是使用`@RequestMapping`,后者默认情况下与所有HTTP方法匹配.如果需要组合注解的示例,请查看如何声明它们.

Spring MVC还支持自定义请求映射属性和自定义请求匹配逻辑.这是一个更高级的技术,需要继承`RequestMappingHandlerMapping`并重写`getCustomMethodCondition`方法,在该方法中,你可以检查自定义属性并返回您自己的`RequestCondition`.



#### <span id="mvc-ann-requestmapping-registration">Explicit Registrations</span>

您可以以编程方式注册处理程序方法,这些方法可用于动态注册或高级案例,例如同一处理程序在不同URL下的不同实例.下面的示例注册一个处理程序方法:

```java

@Configuration
public class MyConfig {

    @Autowired
    public void setHandlerMapping(RequestMappingHandlerMapping mapping, UserHandler handler)  //1.
            throws NoSuchMethodException {

        RequestMappingInfo info = RequestMappingInfo
                .paths("/user/{id}").methods(RequestMethod.GET).build(); //2.

        Method method = UserHandler.class.getMethod("getUser", Long.class); //3.

        mapping.registerMapping(info, handler, method); //4.
    }
}
/**
 * 1. 注入目标处理程序和控制器的处理程序映射.
 * 2. 准备请求映射元数据
 * 3. 获取处理程序方法.
 * 4. 添加注册.
 */
```

在`RequestMappingHandlerMapping`里面,注册的时候,调用的就是`registerMapping`方法,在组织url的时候,使用的就是`RequestMappingInfo`.

### <span id="mvc-ann-methods">1.3.3. Handler Methods</span>
`@RequestMapping`处理程序方法具有灵活的签名,可以从一系列受支持的控制器方法参数和返回值中进行选择.

#### <span id="mvc-ann-arguments"> Method Arguments</span>

下表描述了受支持的控制器方法参数.任何参数均不支持反应性类型.(什么是Reactive types)?

支持JDK 8的`java.util.Optional`作为方法参数,并与具有`required`属性(例如`@RequestParam`,`@RequestHeader`等)的注解结合在一起,等效于`required = false`.


|Controller method argument|描述|
|:---|:---|
|`WebRequest, NativeWebRequest`|对 request parameters,request和session属性的通用访问,而不直接使用Servlet API.|
|`javax.servlet.ServletRequest, javax.servlet.ServletResponse`|选择任何特定的请求或响应类型,例如`ServletRequest`,`HttpServletRequest`或Spring的`MultipartRequest`,`MultipartHttpServletRequest`.|
|`javax.servlet.http.HttpSession`|强制session的存在.因此,这样的参数永远不会为`null`.注意,session访问不是线程安全的.如果允许多个请求并发访问session,请考虑将`RequestMappingHandlerAdapter`实例的`syncnizeOnSession`标志设置为`true`.|
|`javax.servlet.http.PushBuilder`|Servlet 4.0 push builder API for programmatic HTTP/2 resource pushes. 请注意,根据Servlet规范,如果客户端不支持HTTP/2功能,则注入的`PushBuilder`实例可以为`null`.|
|`java.security.Principal`|当前已认证的用户-可能是特定的`Principal`实现类(如果已知).|
|`HttpMethod`|HTTP请求方法|
|`java.util.Locale`|当前请求区域设置,由特定且可用的`LocaleResolver`(实际上是已配置的`LocaleResolver`或`LocaleContextResolver`)决定.|
|`java.util.TimeZone+java.time.ZoneId`|与当前请求关联的时区,由`LocaleContextResolver`确定.|
|`java.io.InputStream, java.io.Reader`|用于访问Servlet API公开的原始请求体.|
|`java.io.OutputStream,java.io.Writer`|用于访问Servlet API公开的原始请求体.|
|`@PathVariable`|用于访问URI模板变量.请参阅[URI Patterns]("#mvc-ann-requestmapping-uri-templates").|
|`@MatrixVariable`|用于访问URI路径段中的name-value.请参阅[Matrix Variables]().|
|`@RequestParam`|用于访问Servlet请求参数,包括multipart文件.参数值将转换为声明的方法参数类型.参见[`@RequestParam`]()以及[Multipart]().
请注意,对于简单参数值,`@RequestParam`的使用是可选的.请参阅此表末尾的"Any other argument".|
|`@RequestHeader`|用于访问请求头.请求头值将转换为声明的方法参数类型.参见[`@RequestHeader`]().|
|`@CookieValue`|访问cookie,cookie值将转换为声明的方法参数类型.参见[`@CookieValue`]()|
|`@RequestBody`|访问Http请求体正文.正文内容通过使用`HttpMessageConverter`实现转换为声明的方法参数类型.参见[`@RequestBody`]()|
|`HttpEntity<B>`|用于访问请求体和请求体.请求体使用`HttpMessageConverter`进行转换.参见[HttpEntity]()|
|`@RequestPart`|在multipart/from-data请求中访问part,请求体使用`HttpMessageConverter`进行转换.参见[Multipart]()|
|`java.util.Map,org.springframework.ui.Model,org.springframework.ui.ModelMap`|用于访问HTML控制器中使用的模型,并作为视图渲染的一部分公开给模板.|
|`RedirectAttributes`|指定在重定向的情况下使用的属性(即追加到查询字符串中),并指定要临时存储的属性,直到重定向后的请求为止.参见[Redirect Attributes]()和 [Flash Attributes]().|
|`@ModelAttribute`|用于访问模型中的现有属性(如果不存在,则进行实例化),并应用数据绑定和验证.参见[`@ModelAttribute`]()以及[Model]()和[`DataBinder`]().|
|`Errors, BindingResult`||
|`SessionStatus`+类级别`@SessionAttributes`||
|`UriComponentsBuilder`||
|`@SessionAttribute`||
|`@RequestAttribute`||
|Any other argument||






#### <span id="mvc-ann-return-types"> Return Values</span>

下表描述了受支持的控制器方法返回值.所有返回值都支持反应性类型.

|Controller method return value|描述|
|:---|:---|
|`@ResponseBody`|返回值通过`HttpMessageConverter`实现转换并写入响应.See [`@ResponseBody`]("#mvc-ann-responsebody").|
|`HttpEntity<B>, ResponseEntity<B>`||
|`HttpHeaders`||
|`String`||
|`View`||
|`java.util.Map, org.springframework.ui.Model`||
|`@ModelAttribute`||
|`ModelAndView`  object||
|`void`||
|`DeferredResult<V>`||
|`Callable<V>`||
|`ListenableFuture<V>, java.util.concurrent.CompletionStage<V>, java.util.concurrent.CompletableFuture<V>`||
|`ResponseBodyEmitter, SseEmitter`||
|`StreamingResponseBody`||
|Reactive types — Reactor, RxJava, or others through `ReactiveAdapterRegistry`||
|Any other return value||







#### <span id="mvc-ann-typeconversion">Type Conversion</span>

如果参数声明不是`String`类型,则某些表示基于`String`的请求输入的带注解的控制器方法参数(例如`@RequestParam`,`@RequestHeader`,`@PathVariable`,`@ MatrixVariable`和`@CookieValue`)可能需要类型转换.

在这种情况下,将根据配置的转换器自动应用类型转换.默认情况下,支持简单类型(`int`,`long`,`Date`和其他）.你可以通过`WebDataBinder`(请参见[`DataBinder`]()))或通过在`FormattingConversionService`中注册`Formatter`来自定义类型转换.参阅[Spring Field Formatting]().


#### <span id="mvc-ann-matrix-variables"> Matrix Variables</span>

[RFC 3986](https://tools.ietf.org/html/rfc3986#section-3.3) 讨论 name-value pairs in path segments.在Spring MVC中,基于Tim Berners-Lee的"old post",我们将其称为"矩阵变量",但它们也可以称为URI路径参数.

矩阵变量可以出现在任何路径段中,每个变量用分号分隔,多个值用逗号分隔(例如,`/cars;color=red,green;year=2012`).也可以通过重复的变量名称指定多个值(例如,`color=red;color=green;color=blue`).

如果期望URL包含矩阵变量,则控制器方法的请求映射必须使用URI变量来屏蔽该变量内容,并确保能够成功地匹配请求,而不依赖于矩阵变量的顺序和存在性.

下面的示例是使用矩阵变量:

```java
// GET /pets/42;q=11;r=22

@GetMapping("/pets/{petId}")
public void findPet(@PathVariable String petId, @MatrixVariable int q) {

    // petId == 42
    // q == 11
}
/**
 * 根据上面的实例,我们的请求的url是 GET /pets/42;q=11;r=22
 * 则在调用方法的时候,方法中的参数值分别是:
 * petId的值为42,q的值为11
 */
```

考虑到所有的路径段可能包含矩阵变量,你有时可能需要消除矩阵变量所在路径变量的歧义.以下示例显示了如何执行此操作:

```java
// GET /owners/42;q=11/pets/21;q=22

@GetMapping("/owners/{ownerId}/pets/{petId}")
public void findPet(
        @MatrixVariable(name="q", pathVar="ownerId") int q1,
        @MatrixVariable(name="q", pathVar="petId") int q2) {

    // q1 == 11
    // q2 == 22
}
/**
 * 根据上面的实例,请求URL路径为 GET /owners/42;q=11/pets/21;q=22,在ownerId路径段中,出现了矩阵变量q,在petId请求段中,也出现了矩阵变量q.
 * 则,在实际获取的时候,需要区分具体路径段中的变量,以消除歧义.
 * 方法中的参数值分别是:
 * q1的值为11,
 * q2的值为12.
 */
```
可以将矩阵变量定义为可选变量,并指定默认值,如以下示例所示:

```java
// GET /pets/42

@GetMapping("/pets/{petId}")
public void findPet(@MatrixVariable(required=false, defaultValue="1") int q) {

    // q == 1
}
/**
 * 在上面的请求URL是 GET /pets/42
 * 并没有设置矩阵变量,但是我们的请求方法要求的矩阵变量性质是可选的,
 * 这里,q参数的值为1
 */
```

要获取所有矩阵变量,可以使用`MultiValueMap`,如以下示例所示:

```java
// GET /owners/42;q=11;r=12/pets/21;q=22;s=23

@GetMapping("/owners/{ownerId}/pets/{petId}")
public void findPet(
        @MatrixVariable MultiValueMap<String, String> matrixVars,
        @MatrixVariable(pathVar="petId") MultiValueMap<String, String> petMatrixVars) {

    // matrixVars: ["q" : [11,22], "r" : 12, "s" : 23]
    // petMatrixVars: ["q" : 22, "s" : 23]
}
```

请注意,如果你需要启用矩阵变量的使用.在MVC Java配置中,就需要通过[Path Matching]()设置带有`removeSemicolonContent = false`的`UrlPathHelper`.在基于XML的配置中,你可以设置`<mvc:annotation-driven enable-matrix-variables="true"/>`.


#### <span id="mvc-ann-requestparam">`@RequestParam`</span>

你可以使用`@RequestParam`注解将Servlet请求参数(即查询参数或表单数据)绑定到控制器中的方法参数.

下面的实例显示了如何做:

```java

@Controller
@RequestMapping("/pets")
public class EditPetForm {

    // ...

    @GetMapping
    public String setupForm(@RequestParam("petId") int petId, Model model) {  // 使用@RequestParam来绑定petId.
        Pet pet = this.clinic.loadPet(petId);
        model.addAttribute("pet", pet);
        return "petForm";
    }

    // ...

}
```

#### <span id="mvc-ann-requestheader"> `@RequestHeader`</span>
#### <span id="mvc-ann-modelattrib-method-args"> `@ModelAttribute`</span>

您可以在方法参数上使用`@ModelAttribute`注解,以从模型访问属性,或者将其实例化(如果不存在).model属性还覆盖了名称与字段名称匹配的HTTP Servlet请求参数中的值.这称为数据绑定,它使你不必处理解析和转换单个查询参数和表单字段的工作.下面的实例显示了如何这样做:
```java
@PostMapping("/owners/{ownerId}/pets/{petId}/edit")
public String processSubmit(@ModelAttribute Pet pet) { } //绑定一个Pet实例.
```

上面的Pet实例解析如下:


#### <span id="mvc-ann-sessionattributes"> `@SessionAttributes`</span>
#### <span id="mvc-ann-sessionattribute"> `@SessionAttribute`</span>
#### <span id="mvc-ann-requestattrib"> `@RequestAttribute`</span>
#### <span id="mvc-redirecting-passing-data"> Redirect Attributes</span>
#### <span id="mvc-flash-attributes"> Flash Attributes</span>

#### <span id="mvc-multipart-forms">Multipart</span>
#### <span id="mvc-ann-requestbody">`@RequestBody`</span>
#### <span id="mvc-ann-httpentity">HttpEntity</span>
#### <span id="mvc-ann-responsebody">`@ResponseBody`</span>
#### <span id="mvc-ann-responseentity">ResponseEntity</span>
#### <span id="mvc-ann-jackson">Jackson JSON</span>

### <span id="mvc-ann-modelattrib-methods">1.3.4. Model</span>

你可以使用`@ModelAttribute`注解:

- 

### <span id="mvc-ann-initbinder">1.3.5. DataBinder</span>

### <span id="mvc-ann-exceptionhandler">1.3.6. Exceptions</span>
### <span id="mvc-ann-controller-advice">1.3.7. Controller Advice</span>























