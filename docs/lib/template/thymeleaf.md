# Thymeleaf Cheat Sheet

## 简介

### 什么是 Thymeleaf

Thymeleaf 是一个用于 web 和独立环境的现代 server-side Java 模板引擎，能够处理 HTML，XML，JavaScript，CSS 甚至纯文本。

Thymeleaf 的主要目标是提供一种优雅的 highly-maintainable 方式的 creating 模板。为了实现这一目标，它以自然模板的概念为基础，将其逻辑注入模板 files，其方式不会影响模板被用作设计原型。这改善了设计沟通，缩小了设计和开发团队之间的差距。

Thymeleaf 也从一开始就设计了 Web 标准 - 特别是**HTML5** - 允许您创建完全验证的模板，如果这是您的需要。

### Thymeleaf process 可以使用哪种模板

Out-of-the-box，Thymeleaf 允许您处理六种模板，每种模板称为**模板模式**：

- HTML
- XML
- 文本
- JAVASCRIPT
- CSS
- 生

有两种标记模板模式(`HTML`和`XML`)，三种文本模板模式(`TEXT`，`JAVASCRIPT`和`CSS`)和 no-op 模板模式(`RAW`)。

**HTML**模板模式将允许任何类型的 HTML 输入，包括 HTML5，HTML 4 和 XHTML。不会执行验证或 well-formedness 检查，并且模板 code/structure 将在输出中尽可能地受到尊重。

**XML**模板模式将允许 XML 输入。在这种情况下，code 应该是 well-formed - 没有未关闭的标签，没有不带引号的属性等 - 如果找到 well-formedness 违规，解析器将抛出 exceptions。请注意，不会执行任何验证(针对 DTD 或 XML Schema)。

**TEXT**模板模式允许对 non-markup 性质的模板使用特殊语法。此类模板的示例可能是文本电子邮件或模板文档。请注意，HTML 或 XML 模板也可以作为`TEXT`处理，在这种情况下，它们不会被解析为标记，并且每个标记，DOCTYPE，comment 等都将被视为纯文本。

**JAVASCRIPT**模板模式将允许在 Thymeleaf application 中处理 JavaScript files。这意味着能够在 JavaScript files 中使用 model 数据，就像在 HTML files 中一样，但是使用 JavaScript-specific 集成，例如专门的转义或自然脚本。 `JAVASCRIPT`模板模式被视为文本模式，因此使用与`TEXT`模板模式相同的特殊语法。

**CSS**模板模式将允许处理 Thymeleaf application 中涉及的 CSS files。与`JAVASCRIPT`模式类似，`CSS`模板模式也是文本模式，并使用`TEXT`模板模式中的特殊处理语法。

**RAW**模板模式根本不会处理模板。它用于插入未经处理的资源(files，URL 响应，etc.)到正在处理的模板中。例如，HTML 格式的外部不受控制的资源可以包含在 application 模板中，安全地知道任何 Thymeleaf code 这些资源可能会包含将不会被执行。

### 方言：标准方言

Thymeleaf 是一个极其可扩展的模板引擎(实际上它可以称为模板引擎 framework)，它允许您定义和自定义模板处理细节级别的方式。

将一些逻辑应用于标记 artifact(标记，某些文本，comment 或仅仅是占位符，如果模板不是标记)的 object 称为处理器，这些处理器的集合 - 加上可能还有一些额外的 artifacts - 是什么**方言**通常由...组成。开箱即用，Thymeleaf 的核心 library 提供了一种名为**标准方言**的方言，对大多数用户来说应该足够了。

> 请注意，方言实际上可以没有处理器，并且完全由其他类型的 artifacts 组成，但处理器绝对是最常见的用例。

本教程涵盖标准方言。您将在以下页面中了解的每个属性和语法 feature 都由此方言定义，即使未明确提及。

当然，如果用户希望在利用 library 的高级 features 的同时定义自己的处理逻辑，则可以创建自己的方言(甚至扩展标准方言)。 Thymeleaf 也可以配置为在 time 使用多种方言。

> 官方的 thymeleaf-spring3 和 thymeleaf-spring4 integration 软件包都定义了一种名为“SpringStandard Dialect”的方言，它与标准方言大致相同，但是在 Spring Framework 中更好地利用了一些 features(例如，使用 Spring)表达式语言或 SpringEL 而不是 OGNL)。所以如果你是一个 Spring MVC 用户，你不会浪费你的 time，因为你在这里学到的几乎所有内容都将在你的 Spring 应用程序中使用。

标准方言的大多数处理器都是属性处理器。这使得浏览器甚至可以在处理之前正确显示 HTML 模板 files，因为它们只会忽略其他属性。例如，虽然使用标记 libraries 的 JSP 可能包含 code 的片段，但不能由浏览器直接显示，如：

```xml
<form:inputText name="userName" value="${user.name}" />
```

...... Thymeleaf Standard Dialect 将允许我们实现相同的功能：

```xml
<input type="text" name="userName" value="James Carrot" th:value="${user.name}" />
```

这不仅可以被浏览器正确显示，而且还允许我们(可选地)在其中指定 value 属性(在这种情况下为“James Carrot”)，当在浏览器中静态打开原型时将显示该属性，并且这将由在处理模板期间`${user.name}`的 evaluation 产生的 value 代替。

这有助于您的设计人员和开发人员处理相同的模板文件，并减少将静态原型转换为工作模板文件所需的工作量。这样做的能力是一种称为自然模板的特征。

## Good Thymes 虚拟杂货店

本指南的本章和后续章节中显示的示例的 source code 可以在[Good Thymes Virtual Grocery GitHub repository](https://github.com/thymeleaf/thymeleafexamples-gtvg)中找到。

### 杂货店的网站

为了更好地解释使用 Thymeleaf 处理模板所涉及的概念，本教程将使用 demo application，您可以从项目的 web 站点下载该应用程序。

这个 application 是假想的虚拟杂货的 web 网站，它将为我们提供许多场景来展示 Thymeleaf 的许多 features。

首先，我们需要一个简单的 model 实体用于我们的 application：`Products`，它们通过 creating `Orders`卖给`Customers`。我们还将管理`Comments`关于那些`Products`：

![Example application model](https://www.docs4dev.com/images/thymeleaf/3.0/gtvg-model.jpg)
Example application model
我们的 application 也有一个非常简单的服务层，由`Service` objects 组成，包含如下方法：

```java
public class ProductService {

    ...

    public List<Product> findAll() {
        return ProductRepository.getInstance().findAll();
    }

    public Product findById(Integer id) {
        return ProductRepository.getInstance().findById(id);
    }

}
```

在 web 层，我们的 application 将有一个过滤器，它将执行委托给 Thymeleaf-enabled 命令，具体取决于请求 URL：

```java
private boolean process(HttpServletRequest request, HttpServletResponse response)
        throws ServletException {

    try {

        // This prevents triggering engine executions for resource URLs
        if (request.getRequestURI().startsWith("/css") ||
                request.getRequestURI().startsWith("/images") ||
                request.getRequestURI().startsWith("/favicon")) {
            return false;
        }


        /*
         * Query controller/URL mapping and obtain the controller
         * that will process the request. If no controller is available,
         * return false and let other filters/servlets process the request.
         */
        IGTVGController controller = this.application.resolveControllerForRequest(request);
        if (controller == null) {
            return false;
        }

        /*
         * Obtain the TemplateEngine instance.
         */
        ITemplateEngine templateEngine = this.application.getTemplateEngine();

        /*
         * Write the response headers
         */
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        /*
         * Execute the controller and process view template,
         * writing the results to the response writer.
         */
        controller.process(
                request, response, this.servletContext, templateEngine);

        return true;

    } catch (Exception e) {
        try {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (final IOException ignored) {
            // Just ignore this
        }
        throw new ServletException(e);
    }

}
```

这是我们的`IGTVGController`界面：

```java
public interface IGTVGController {

    public void process(
            HttpServletRequest request, HttpServletResponse response,
            ServletContext servletContext, ITemplateEngine templateEngine);

}
```

我们现在要做的就是创建`IGTVGController`接口的 implementations，使用`ITemplateEngine` object 从服务和处理模板中检索数据。

最后，它看起来像这样：

![Example application 主页](https://www.docs4dev.com/images/thymeleaf/3.0/gtvg-view.jpg)
Example application 主页
但首先让我们看看该模板引擎是如何初始化的。

### 创建和配置模板引擎

我们的过滤器中的 process(...)方法包含此 line：

```java
ITemplateEngine templateEngine = this.application.getTemplateEngine();
```

这意味着 GTVGApplication class 负责创建和配置 Thymeleaf application 中最重要的 objects 之一：`TemplateEngine`实例(`ITemplateEngine`接口的 implementation)。

我们的`org.thymeleaf.TemplateEngine` object 初始化如下：

```java
public class GTVGApplication {


    ...
    private final TemplateEngine templateEngine;
    ...


    public GTVGApplication(final ServletContext servletContext) {

        super();

        ServletContextTemplateResolver templateResolver =
                new ServletContextTemplateResolver(servletContext);

        // HTML is the default mode, but we set it anyway for better understanding of code
        templateResolver.setTemplateMode(TemplateMode.HTML);
        // This will convert "home" to "/WEB-INF/templates/home.html"
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        // Template cache TTL=1h. If not set, entries would be cached until expelled
        templateResolver.setCacheTTLMs(Long.valueOf(3600000L));

        // Cache is set to true by default. Set to false if you want templates to
        // be automatically updated when modified.
        templateResolver.setCacheable(true);

        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);

        ...

    }

}
```

有很多方法可以配置`TemplateEngine` object，但是现在这几个 code 的 lines 将教会我们足够的所需步骤。

#### 模板解析器

让我们从模板解析器开始：

```java
ServletContextTemplateResolver templateResolver =
        new ServletContextTemplateResolver(servletContext);
```

模板解析器是 objects，它们实现了名为`org.thymeleaf.templateresolver.ITemplateResolver`的 Thymeleaf API 的接口：

```java
public interface ITemplateResolver {

    ...

    /*
     * Templates are resolved by their name (or content) and also (optionally) their
     * owner template in case we are trying to resolve a fragment for another template.
     * Will return null if template cannot be handled by this template resolver.
     */
    public TemplateResolution resolveTemplate(
            final IEngineConfiguration configuration,
            final String ownerTemplate, final String template,
            final Map<String, Object> templateResolutionAttributes);
}
```

这些 objects 负责确定如何访问我们的模板，在这个 GTVG application 中，`org.thymeleaf.templateresolver.ServletContextTemplateResolver`意味着我们将从 Servlet Context 中检索我们的模板 files 作为资源：每个 Java web application 中都存在 application-wide `javax.servlet.ServletContext` object ，并从 web application 根解析资源。

但这并不是我们可以说的关于模板解析器的全部内容，因为我们可以在其上设置一些 configuration 参数。一，模板模式：

```java
templateResolver.setTemplateMode(TemplateMode.HTML);
```

HTML 是`ServletContextTemplateResolver`的默认模板模式，但最好还是建立它，以便我们的 code 文档清楚地显示正在发生的事情。

```java
templateResolver.setPrefix("/WEB-INF/templates/");
templateResolver.setSuffix(".html");
```

前缀和后缀修改我们将传递给引擎的模板名称，以获取要使用的实际资源名称。

使用此 configuration，模板 name“product/list”将对应于：

```java
servletContext.getResourceAsStream("/WEB-INF/templates/product/list.html")
```

(可选)通过 cacheTTLMs property 在模板解析器中配置解析模板可以在缓存中存在的 time 的数量：

```java
templateResolver.setCacheTTLMs(3600000L);
```

如果达到最大高速缓存大小并且它是当前高速缓存的最旧条目，则在达到该 TTL 之前，模板仍然可以从高速缓存中排除。

> 用户可以通过实现`ICacheManager`接口或通过修改`StandardCacheManager` object 来管理默认缓存来定义缓存行为和大小。

关于模板解析器还有很多东西需要学习，但是现在让我们来看看 Template Engine object 的创建。

#### 模板引擎

模板引擎 objects 是`org.thymeleaf.ITemplateEngine`接口的 implementations。其中一个 implementation 由 Thymeleaf 核心提供：`org.thymeleaf.TemplateEngine`，我们在这里创建一个实例：

```java
templateEngine = new TemplateEngine();
templateEngine.setTemplateResolver(templateResolver);
```

相当简单，不是吗？我们所需要的只是创建一个实例并将模板解析器设置为它。

模板解析器是`TemplateEngine`需要的唯一必需参数，尽管稍后将介绍许多其他参数(消息解析器，缓存大小等)。现在，这就是我们所需要的。

我们的模板引擎现已准备就绪，我们可以使用 Thymeleaf 开始创建我们的页面。

## 使用文本

### A multi-language 欢迎

我们的第一个任务是为我们的杂货网站创建一个主页。

这个页面的第一个 version 非常简单：只是标题和欢迎信息。这是我们的`/WEB-INF/templates/home.html`文件：

```xml
<!DOCTYPE html>

<html xmlns:th="http://www.thymeleaf.org">

  <head>
    <title>Good Thymes Virtual Grocery</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <link rel="stylesheet" type="text/css" media="all"
          href="../../css/gtvg.css" th:href="@{/css/gtvg.css}" />
  </head>

  <body>

    <p th:text="#{home.welcome}">Welcome to our grocery store!</p>

  </body>

</html>
```

你会注意到的第一件事是这个文件是 HTML5，可以被任何浏览器正确显示，因为它不包含任何 non-HTML 标签(浏览器会忽略他们不理解的所有属性，如`th:text`)。

但您可能还注意到此模板实际上不是有效的 HTML5 文档，因为 HTML5 规范不允许我们在`th:*`表单中使用这些 non-standard 属性。实际上，我们甚至在``标签中添加`xmlns:th`属性，绝对是 non-HTML5-ish：

```xml
<html xmlns:th="http://www.thymeleaf.org">
```

...在模板处理中根本没有任何影响，但作为一个咒语，阻止我们的 IDE 抱怨缺少所有这些`th:*`属性的命名空间定义。

那么如果我们想制作这个模板**HTML5-valid**呢？简单：切换到 Thymeleaf 的数据属性语法，使用`data-`前缀作为属性名称和连字符(`-`)分隔符而不是 semi-colons(`:`)：

```xml
<!DOCTYPE html>

<html>

  <head>
    <title>Good Thymes Virtual Grocery</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <link rel="stylesheet" type="text/css" media="all"
          href="../../css/gtvg.css" data-th-href="@{/css/gtvg.css}" />
  </head>

  <body>

    <p data-th-text="#{home.welcome}">Welcome to our grocery store!</p>

  </body>

</html>
```

HTML5 规范允许自定义`data-`前缀属性，因此，使用上面的 code，我们的模板将是一个有效的 HTML5 文档。

> 两种表示法都是完全等效且可互换的，但为了简化和 code samples 的紧凑性，本教程将使用名称空间表示法(`th:*`)。此外，`th:*`表示法更通用，并且在每个 Thymeleaf 模板模式(`XML`，`TEXT` ...)中都允许使用`th:*`表示法，而`data-`表示法仅允许在`HTML`模式中使用。

#### 使用 th:text 和外化文本

外化文本是从模板 files 中提取模板 code 的片段，以便它们可以保存在单独的 files(通常为`.properties` files)中，并且可以使用其他语言编写的等效文本(称为国际化的过程或简称 i18n)轻松替换它们。外化的文本片段通常称为“消息”。

消息始终具有标识它们的 key，并且 Thymeleaf 允许您指定文本应与具有`#{...}`语法的特定消息对应：

```xml
<p th:text="#{home.welcome}">Welcome to our grocery store!</p>
```

我们在这里看到的实际上是 Thymeleaf 标准方言的两个不同的特点：

- `th:text`属性，它评估其 value 表达式并将结果设置为 host 标记的主体，有效地替换了“欢迎来到我们的杂货店 store！”我们在 code 中看到的文字。
- 标准表达式语法中指定的`#{home.welcome}`表达式，指示`th:text`属性要使用的文本应该是`home.welcome` key 对应于我们正在处理模板的 locale 的消息。

现在，这个外化文本在哪里？

Thymeleaf 中外化文本的位置是完全可配置的，它取决于所使用的特定`org.thymeleaf.messageresolver.IMessageResolver` implementation。通常，将使用基于`.properties` files 的 implementation，但是如果我们想要的话，我们可以创建自己的 implementations，例如，从数据库获取消息。

但是，我们在初始化期间没有为模板引擎指定消息解析器，这意味着我们的 application 正在使用由`org.thymeleaf.messageresolver.StandardMessageResolver`实现的标准消息解析器。

标准消息解析程序希望在 properties files 中找到`/WEB-INF/templates/home.html`的消息，并在同一文件夹中使用与模板相同的 name，如：

- `/WEB-INF/templates/home_en.properties`用于英文文本。
- `/WEB-INF/templates/home_es.properties`用于西班牙语文本。
- `/WEB-INF/templates/home_pt_BR.properties`代表葡萄牙语(巴西)语言文本。
- `/WEB-INF/templates/home.properties`表示默认文本(如果 locale 不匹配)。

我们来看看我们的`home_es.properties`文件：

```java
home.welcome=¡Bienvenido a nuestra tienda de comestibles!
```

这就是我们将 Thymeleaf process 作为模板所需的全部内容。让我们创建我们的 Home 控制器。

#### 上下文

在 process to process 我们的模板中，我们将创建一个实现我们之前看到的`IGTVGController`接口的`HomeController` class：

```java
public class HomeController implements IGTVGController {

    public void process(
            final HttpServletRequest request, final HttpServletResponse response,
            final ServletContext servletContext, final ITemplateEngine templateEngine)
            throws Exception {

        WebContext ctx =
                new WebContext(request, response, servletContext, request.getLocale());

        templateEngine.process("home", ctx, response.getWriter());

    }

}
```

我们看到的第一件事是创建 context。 Thymeleaf context 是一个实现`org.thymeleaf.context.IContext`接口的 object。上下文应包含在变量 map 中执行模板引擎所需的所有数据，并且还引用必须用于外部化消息的 locale。

```java
public interface IContext {

    public Locale getLocale();
    public boolean containsVariable(final String name);
    public Set<String> getVariableNames();
    public Object getVariable(final String name);

}
```

这个接口有一个专门的扩展，`org.thymeleaf.context.IWebContext`，意味着在 ServletAPI-based web applications(如 SpringMVC)中使用。

```java
public interface IWebContext extends IContext {

    public HttpServletRequest getRequest();
    public HttpServletResponse getResponse();
    public HttpSession getSession();
    public ServletContext getServletContext();

}
```

Thymeleaf 核心 library 提供了每个接口的 implementation：

- `org.thymeleaf.context.Context`实现`IContext`
- `org.thymeleaf.context.WebContext`实现`IWebContext`

正如您在控制器 code 中看到的那样，`WebContext`是我们使用的那个。实际上我们必须这样做，因为使用`ServletContextTemplateResolver`要求我们使用 context 实现`IWebContext`。

```java
WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
```

这四个构造函数 arguments 中只有三个是必需的，因为如果指定了 none，将使用系统的默认 locale(尽管你不应该在真正的 applications 中发生这种情况)。

我们可以使用一些专门的表达式来获取请求参数以及模板中`WebContext`的 request，session 和 application 属性。例如：

- `${x}`将\_return 存储在 Thymeleaf context 中的变量`x`或作为请求属性。
- `${param.x}`将\_return 一个名为`x`的请求参数(可能是多值的)。
- `${session.x}`将 return 一个名为`x`的 session 属性。
- `${application.x}`将 return \_selet context 属性，名为`x`。

#### 执行模板引擎

在我们的 context object 准备就绪之后，现在我们可以告诉模板引擎使用 context 处理模板(通过它的 name)，并传递响应 writer，以便可以将响应写入它：

```java
templateEngine.process("home", ctx, response.getWriter());
```

让我们看一下使用西班牙语 locale 的结果：

```xml
<!DOCTYPE html>

<html>

  <head>
    <title>Good Thymes Virtual Grocery</title>
    <meta content="text/html; charset=UTF-8" http-equiv="Content-Type"/>
    <link rel="stylesheet" type="text/css" media="all" href="/gtvg/css/gtvg.css" />
  </head>

  <body>

    <p>¡Bienvenido a nuestra tienda de comestibles!</p>

  </body>

</html>
```

### 有关文本和变量的更多信息

#### 未转义的文字

我们主页上最简单的 version 现在似乎已经准备就绪了，但是我们还没有想到......如果我们有这样的消息怎么办？

```xml
home.welcome=Welcome to our <b>fantastic</b> grocery store!
```

如果我们像以前一样执行此模板，我们将获得：

```xml
<p>Welcome to our &lt;b&gt;fantastic&lt;/b&gt; grocery store!</p>
```

这不完全符合我们的预期，因为我们的``标签已被转义，因此它将显示在浏览器中。

这是`th:text`属性的默认行为。如果我们希望 Thymeleaf 尊重我们的 HTML 标签而不是逃避它们，我们将不得不使用不同的属性：`th:utext`(对于“非转义文本”)：

```xml
<p th:utext="#{home.welcome}">Welcome to our grocery store!</p>
```

这将输出我们的消息，就像我们想要的那样：

```xml
<p>Welcome to our <b>fantastic</b> grocery store!</p>
```

#### 使用和显示变量

现在让我们在主页上添加更多内容。对于 example，我们可能希望在欢迎消息下方显示 date，如下所示：

```java
Welcome to our fantastic grocery store!

Today is: 12 july 2010
```

首先，我们必须修改控制器，以便将 date 添加为 context 变量：

```java
public void process(
            final HttpServletRequest request, final HttpServletResponse response,
            final ServletContext servletContext, final ITemplateEngine templateEngine)
            throws Exception {

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
    Calendar cal = Calendar.getInstance();

    WebContext ctx =
            new WebContext(request, response, servletContext, request.getLocale());
    ctx.setVariable("today", dateFormat.format(cal.getTime()));

    templateEngine.process("home", ctx, response.getWriter());

}
```

我们在 context 中添加了一个名为`today`的`String`变量，现在我们可以在模板中显示它：

```xml
<body>

  <p th:utext="#{home.welcome}">Welcome to our grocery store!</p>

  <p>Today is: <span th:text="${today}">13 February 2011</span></p>

</body>
```

正如您所看到的，我们仍然使用\_j 属性 job(这是正确的，因为我们想要替换标签的主体)，但是这个 time 的语法有点不同而不是`#{...}`表达式 value，我们是使用`${...}`一个。这是一个**变量表达式**，它包含一个名为 OGNL(Object-Graph Navigation Language)的语言表达式，它将在我们之前讨论的 context 变量 map 上执行。

`${today}`表达式只是意味着“获取今天调用的变量”，但这些表达式可能更复杂(如`${user.name}`表示“获取名为 user 的变量，并调用其`getName()`方法”)。

属性值有很多可能性：消息，变量表达式......还有很多。下一章将向我们展示所有这些可能性。

## 标准表达式语法

我们将在开发杂货虚拟商店时采取一个小小的 break 来了解 Thymeleaf 标准方言中最重要的部分之一：Thymeleaf 标准表达式语法。

我们已经看到在这种语法中表达的两种类型的有效属性值：消息和变量表达式：

```xml
<p th:utext="#{home.welcome}">Welcome to our grocery store!</p>

<p>Today is: <span th:text="${today}">13 february 2011</span></p>
```

但是有更多类型的表达式，以及更多有趣的细节来了解我们已经知道的那些。首先，让我们看一下标准表达式 features 的快速摘要：

- 简单表达：
- 变量表达式：`${...}`
- 选择变量表达式：`*{...}`
- 消息表达式：`#{...}`
- 链接网址表达式：`@{...}`
- 片段表达式：`~{...}`
- Literals
- 文字 literals：`'one text'`，`'Another one!'`，...
- 数字 literals：`0`，`34`，`3.0`，`12.3`，...
- Boolean literals：`true`，`false`
- 空文字：`null`
- 文字代币：`one`，`sometext`，`main`，......
- 文字操作：
- String 连接：`+`
- 字面替换：`|The name is ${name}|`
- 算术运算：
- 二进制 operators：`+`，`-`，`*`，`/`，`%`
- 减号(一元运算符)：`-`
- Boolean 操作：
- 二进制操作符：`and`，`or`
- Boolean negation(一元 operator)：`!`，`not`
- 比较和平等：
- 比较器：`>`，`<`，`>=`，`<=`(`gt`，`lt`，`ge`，`le`)
- Equality operators：`==`，`!=`(`eq`，`ne`)
- 有条件的 operators：
- If-then：`(if) ? (then)`
- If-then-else：`(if) ? (then) : (else)`
- 默认值：`(value) ?: (defaultvalue)`
- 特殊代币：
- No-Operation：`_`

所有这些 features 都可以组合和嵌套：

```java
'User is of type ' + (${user.isAdmin()} ? 'Administrator' : (${user.type} ?: 'Unknown'))
```

### 消息

正如我们已经知道的那样，`#{...}`消息表达式允许我们链接：

```xml
<p th:utext="#{home.welcome}">Welcome to our grocery store!</p>
```

......对此：

```java
home.welcome=¡Bienvenido a nuestra tienda de comestibles!
```

但是有一个我们仍然没有想到的方面：如果消息文本不是完全静态会发生什么？如果，例如，我们的 application 知道谁是在任何 moment 访问该网站的用户，我们想通过 name 来问候他们呢？

```xml
<p>¡Bienvenido a nuestra tienda de comestibles, John Apricot!</p>
```

这意味着我们需要在消息中添加一个参数。像这样：

```java
home.welcome=¡Bienvenido a nuestra tienda de comestibles, {0}!
```

参数是根据[java.text.MessageFormat](https://docs.oracle.com/javase/10/docs/api/java/text/MessageFormat.html)标准语法指定的，这意味着您可以格式化包中 classes 的 API docs 中指定的 numbers 和 date。

在 order 中为我们的参数指定一个 value，并给定一个名为`user`的 HTTP session 属性，我们可以：

```xml
<p th:utext="#{home.welcome(${session.user.name})}">
  Welcome to our grocery store, Sebastian Pepper!
</p>
```

> 请注意，此处使用`th:utext`表示格式化消息不会被转义。此 example 假定`user.name`已经转义。

可以指定几个参数，以逗号分隔。

消息 key 本身可以来自变量：

```xml
<p th:utext="#{${welcomeMsgKey}(${session.user.name})}">
  Welcome to our grocery store, Sebastian Pepper!
</p>
```

### 变量

我们已经提到`${...}`表达式实际上是在 context 中包含的变量的 map 上执行的 OGNL(Object-Graph Navigation Language)表达式。

> 有关 OGNL 语法和 features 的详细信息，您应该阅读[OGNL 语言指南](http://commons.apache.org/ognl/)

在 Spring MVC-enabled applications 中，OGNL 将被替换为**SpringEL**，但其语法与 OGNL 的语法非常相似(实际上，对于大多数 common 案例来说完全相同)。

从 OGNL 的语法，我们知道表达式：

```xml
<p>Today is: <span th:text="${today}">13 february 2011</span>.</p>
```

......实际上相当于这个：

```java
ctx.getVariable("today");
```

但是 OGNL 允许我们创建更强大的表达式，这就是：

```xml
<p th:utext="#{home.welcome(${session.user.name})}">
  Welcome to our grocery store, Sebastian Pepper!
</p>
```

...通过执行以下命令获取用户 name：

```java
((User) ctx.getVariable("session").get("user")).getName();
```

但是 getter 方法导航只是 OGNL 的 features 之一。让我们看看更多：

```java
/*
 * Access to properties using the point (.). Equivalent to calling property getters.
 */
${person.father.name}

/*
 * Access to properties can also be made by using brackets ([]) and writing
 * the name of the property as a variable or between single quotes.
 */
${person['father']['name']}

/*
 * If the object is a map, both dot and bracket syntax will be equivalent to
 * executing a call on its get(...) method.
 */
${countriesByCode.ES}
${personsByName['Stephen Zucchini'].age}

/*
 * Indexed access to arrays or collections is also performed with brackets,
 * writing the index without quotes.
 */
${personsArray[0].name}

/*
 * Methods can be called, even with arguments.
 */
${person.createCompleteName()}
${person.createCompleteNameWithSeparator('-')}
```

#### Expression Basic Objects

在 context 变量上评估 OGNL 表达式时，某些 object 可用于表达式以获得更高的灵活性。从`#`符号开始，将引用这些 objects(按照 OGNL 标准)：

- `#ctx`：context object。
- `#vars:` context 变量。
- `#locale`：context locale。
- `#request` :(仅在 Web 上下文中)`HttpServletRequest` object。
- `#response` :(仅在 Web 上下文中)`HttpServletResponse` object。
- `#session` :(仅在 Web 上下文中)`HttpSession` object。
- `#servletContext` :(仅在 Web 上下文中)`ServletContext` object。

所以我们可以这样做：

```xml
Established locale country: <span th:text="${#locale.country}">US</span>.
```

您可以在[附录 A.](https://www.docs4dev.com/docs/zh/thymeleaf/3.0/reference/using_thymeleaf.html#appendix-a-expression-basic-objects)中读取这些 objects 的完整 reference。

#### 表达式实用程序 Objects

除了这些基本的 objects 之外，Thymeleaf 将为我们提供一组实用工具 objects，它们将帮助我们在表达式中执行 common 任务。

- `#execInfo`：有关正在处理的模板的信息。
- `#messages`：在变量表达式中获取外部化消息的方法，与使用#{}语法获取的方法相同。
- `#uris`：转义 URLs/URIs 部分的方法
- `#conversions`：执行已配置的转换服务的方法(如果有)。
- `#dates`：`java.util.Date` objects 的方法：格式化，组件提取等。
- `#calendars`：类似于`#dates`，但是对于`java.util.Calendar` objects。
- `#numbers`：格式化数字 objects 的方法。
- `#strings`：`String` objects 的方法：contains，startsWith，prepending/appending 等。
- `#objects`：一般的 objects 方法。
- `#bools`：boolean evaluation 的方法。
- `#arrays`：数组的方法。
- `#lists`：lists 的方法。
- `#sets`：sets 的方法。
- `#maps`：maps 的方法。
- `#aggregates`：用于在数组或集合上创建聚合的方法。
- `#ids`：处理可能重复的 id 属性的方法(对于 example，作为迭代的结果)。

您可以检查[附录 B.](https://www.docs4dev.com/docs/zh/thymeleaf/3.0/reference/using_thymeleaf.html#appendix-b-expression-utility-objects)中每个实用程序 objects 提供的功能。

#### 在我们的主页中重新格式化日期

现在我们知道这些实用程序 objects，我们可以使用它们来改变我们在主页中显示 date 的方式。而不是在我们的`HomeController`中这样做：

```java
SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
Calendar cal = Calendar.getInstance();

WebContext ctx = new WebContext(request, servletContext, request.getLocale());
ctx.setVariable("today", dateFormat.format(cal.getTime()));

templateEngine.process("home", ctx, response.getWriter());
```

......我们可以做到这一点：

```java
WebContext ctx =
    new WebContext(request, response, servletContext, request.getLocale());
ctx.setVariable("today", Calendar.getInstance());

templateEngine.process("home", ctx, response.getWriter());
```

...然后在视图层本身中执行 date 格式：

```xml
<p>
  Today is: <span th:text="${#calendars.format(today,'dd MMMM yyyy')}">13 May 2011</span>
</p>
```

### 关于选择的表达式(星号语法)

变量表达式不仅可以写为`${...}`，还可以写为`*{...}`。

但是有一个重要的区别：星号语法评估所选 objects 上的表达式而不是整个 context 上的表达式。也就是说，由于没有选择的 object，因此美元和星号语法完全相同。

什么是选定的 object？使用`th:object`属性的表达式的结果。让我们在用户 profile(`userprofile.html`)页面中使用一个：

```xml
<div th:object="${session.user}">
    <p>Name: <span th:text="*{firstName}">Sebastian</span>.</p>
    <p>Surname: <span th:text="*{lastName}">Pepper</span>.</p>
    <p>Nationality: <span th:text="*{nationality}">Saturn</span>.</p>
  </div>
```

这完全等同于：

```xml
<div>
  <p>Name: <span th:text="${session.user.firstName}">Sebastian</span>.</p>
  <p>Surname: <span th:text="${session.user.lastName}">Pepper</span>.</p>
  <p>Nationality: <span th:text="${session.user.nationality}">Saturn</span>.</p>
</div>
```

当然，美元和星号语法可以混合使用：

```xml
<div th:object="${session.user}">
  <p>Name: <span th:text="*{firstName}">Sebastian</span>.</p>
  <p>Surname: <span th:text="${session.user.lastName}">Pepper</span>.</p>
  <p>Nationality: <span th:text="*{nationality}">Saturn</span>.</p>
</div>
```

当 object 选择到位时，所选的 object 也可用作美元表达式作为`#object`表达式变量：

```xml
<div th:object="${session.user}">
  <p>Name: <span th:text="${#object.firstName}">Sebastian</span>.</p>
  <p>Surname: <span th:text="${session.user.lastName}">Pepper</span>.</p>
  <p>Nationality: <span th:text="*{nationality}">Saturn</span>.</p>
</div>
```

如上所述，如果没有执行 object 选择，则美元和星号语法是等效的。

```xml
<div>
  <p>Name: <span th:text="*{session.user.name}">Sebastian</span>.</p>
  <p>Surname: <span th:text="*{session.user.surname}">Pepper</span>.</p>
  <p>Nationality: <span th:text="*{session.user.nationality}">Saturn</span>.</p>
</div>
```

### 链接网址

由于它们的重要性，URL 在 web application 模板中是 first-class 公民，而 Thymeleaf 标准方言有一个特殊的语法，`@`语法：`@{...}`

有不同类型的网址：

- 绝对网址：`http://www.thymeleaf.org`
- 相对 URL，可以是：
- Page-relative：`user/login.html`
- Context-relative：`/itemdetails?id=3`(服务器中的 context name 将自动添加)
- Server-relative：`~/billing/processInvoice`(允许在同一服务器中的另一个 context(= application)中调用 URL。
- Protocol-relative 网址：`//code.jquery.com/jquery-2.0.3.min.js`

这些表达式的实际处理及其转换为将要输出的 URL 是通过`org.thymeleaf.linkbuilder.ILinkBuilder`接口的 implementations 来完成的，这些接口被注册到正在使用的`ITemplateEngine` object 中。

默认情况下，此接口的单个 implementation 已注册 class `org.thymeleaf.linkbuilder.StandardLinkBuilder`，这对于基于 Servlet API 的脱机(non-web)和 web 方案都足够了。其他方案(如 gb 框架的 integration)可能需要链接构建器接口的特定 implementation。

让我们使用这种新语法。符合`th:href`属性：

```xml
<!-- Will produce 'http://localhost:8080/gtvg/order/details?orderId=3' (plus rewriting) -->
<a href="details.html"
   th:href="@{http://localhost:8080/gtvg/order/details(orderId=${o.id})}">view</a>

<!-- Will produce '/gtvg/order/details?orderId=3' (plus rewriting) -->
<a href="details.html" th:href="@{/order/details(orderId=${o.id})}">view</a>

<!-- Will produce '/gtvg/order/3/details' (plus rewriting) -->
<a href="details.html" th:href="@{/order/{orderId}/details(orderId=${o.id})}">view</a>
```

有些事情需要注意：

- `th:href`是修饰符属性：处理后，它将计算要使用的链接 URL，并将 value 设置为``标记的`href`属性。
- 我们被允许对 URL 参数使用表达式(如`orderId=${o.id}`中所示)。所需的 URL-parameter-encoding 操作也将自动执行。
- 如果需要多个参数，这些参数将用逗号分隔：`@{/order/process(execId=${execId},execType='FAST')}`
- URL paths 中也允许使用变量模板：`@{/order/{orderId}/details(orderId=${orderId})}`
- 以`/`开头的相对 URL(例如：`/order/details`)将自动以 application context name 为前缀。
- 如果 cookies 未启用或尚未知道，则可能会在相对 URL 中添加`";jsessionid=..."`后缀，以便保留 session。这称为 URL 重写，Thymeleaf 允许您使用 Servlet API 中的`response.encodeURL(...)`机制为每个 URL 插入自己的重写过滤器。
- `th:href`属性允许我们(可选)在我们的模板中具有工作静态`href`属性，以便我们的模板链接在直接打开以进行原型设计时仍可由浏览器导航。

与消息语法(`#{...}`)的情况一样，URL 基数也可以是评估另一个表达式的结果：

```xml
<a th:href="@{${url}(orderId=${o.id})}">view</a>
<a th:href="@{'/details/'+${user.login}(orderId=${o.id})}">view</a>
```

#### 我们主页的菜单

既然我们知道如何创建链接 URL，那么在我们的主页中为网站中的其他一些页面添加一个小菜单呢？

```xml
<p>Please select an option</p>
<ol>
  <li><a href="product/list.html" th:href="@{/product/list}">Product List</a></li>
  <li><a href="order/list.html" th:href="@{/order/list}">Order List</a></li>
  <li><a href="subscribe.html" th:href="@{/subscribe}">Subscribe to our Newsletter</a></li>
  <li><a href="userprofile.html" th:href="@{/userprofile}">See User Profile</a></li>
</ol>
```

#### 服务器根目录相对 URL

可以使用其他语法在 order 中创建 server-root-relative(而不是 context-root-relative)URL，以链接到同一服务器中的不同上下文。这些网址将被指定为`@{~/path/to/something}`

### 片段

片段表达式是表示标记片段并在模板周围移动它们的简单方法。这允许我们复制它们，将它们作为 arguments 传递给其他模板，依此类推。

最常见的用法是使用`th:insert`或`th:replace`进行片段插入(在后面的部分中将详细介绍)：

```xml
<div th:insert="~{commons :: main}">...</div>
```

但它们可以在任何地方使用，就像任何其他变量一样：

```xml
<div th:with="frag=~{footer :: #main/text()}">
  <p th:insert="${frag}">
</div>
```

本教程后面有一整节专门介绍模板布局，包括对片段表达式的更深入解释。

### Literals

#### 文字 literals

文本 literals 只是在单引号之间指定的字符 strings。它们可以包含任何字符，但您应该使用`\'`来转义其中的任何单引号。

```xml
<p>
  Now you are looking at a <span th:text="'working web application'">template file</span>.
</p>
```

#### 数字 literals

数字 literals 就是：numbers。

```xml
<p>The year is <span th:text="2013">1492</span>.</p>
<p>In two years, it will be <span th:text="2013 + 2">1494</span>.</p>
```

#### Boolean literals

boolean literals 是`true`和`false`。例如：

```xml
<div th:if="${user.isAdmin()} == false"> ...
```

在这个例子中，`== false`被写在大括号之外，因此 Thymeleaf 会处理它。如果它写在大括号内，那将是 OGNL/SpringEL 引擎的责任：

```xml
<div th:if="${user.isAdmin() == false}"> ...
```

#### null 文字

也可以使用`null` literal：

```xml
<div th:if="${variable.something} == null"> ...
```

#### 文字代币

实际上，数字，boolean 和 null literals 是文字标记的特例。

这些令牌允许在标准表达式中进行一些简化。它们与 text literals(`'...'`)完全相同，但它们只允许字母(`A-Z`和`a-z`)， numbers(`0-9`)，括号(`[`和`]`)，点(`.`)，连字符(`-`)和下划线(`_`)。所以没有空格，没有逗号等。

好的部分？令牌不需要任何围绕它们的引号。所以我们可以这样做：

```xml
<div th:class="content">...</div>
```

代替：

```xml
<div th:class="'content'">...</div>
```

### 附加文本

文本，无论是\_literal 还是评估变量或消息表达式的结果，都可以使用`+` operator 轻松追加：

```xml
<span th:text="'The name of the user is ' + ${user.name}">
```

### 字面替换

文字替换允许轻松格式化包含变量值的 strings，而无需使用`'...' + '...'`附加 literals。

这些替换必须用竖线(`|`)包围，如：

```xml
<span th:text="|Welcome to our application, ${user.name}!|">
```

这相当于：

```xml
<span th:text="'Welcome to our application, ' + ${user.name} + '!'">
```

文字替换可以与其他类型的表达相结合：

```xml
<span th:text="${onevar} + ' ' + |${twovar}, ${threevar}|">
```

> 在`|...|`字面替换中只允许 variable/message 个表达式(`${...}`，`*{...}`，`#{...}`)。没有其他 literals(`'...'`)，boolean/numeric 令牌，条件表达式等。

### 算术运算

一些算术运算也可用：`+`，`-`，`*`，`/`和`%`。

```xml
<div th:with="isEven=(${prodStat.count} % 2 == 0)">
```

请注意，这些 operators 也可以在 OGNL 变量表达式本身中应用(在这种情况下，将由 OGNL 而不是 Thymeleaf 标准表达式引擎执行)：

```xml
<div th:with="isEven=${prodStat.count % 2 == 0}">
```

请注意，其中一些 operators 存在文本别名：`div`(`/`)，`mod`(`%`)。

### 比较和平等

表达式中的值可以与`>`，`<`，`>=`和`<=`符号进行比较，`==`和`!=` operators 可以用于检查是否相等(或缺少它)。请注意，XML 确定不应在属性值中使用`<`和`>`符号，因此应将它们替换为`<`和`>`。

```xml
<div th:if="${prodStat.count} &gt; 1">
<span th:text="'Execution mode is ' + ( (${execMode} == 'dev')? 'Development' : 'Production')">
```

一个更简单的替代方法可能是使用为某些这些 operators 存在的文本别名：`gt`(`>`)，`lt`(`<`)，`ge`(`>=`)，`le`(`<=`)，`not`(`!`)。也`eq`(`==`)，`neq`/`ne`(`!=`)。

### 条件表达式

条件表达式仅用于评估两个表达式中的一个，具体取决于评估条件的结果(它本身是另一个表达式)。

让我们看一下 example 片段(引入另一个属性修饰符，`th:class`)：

```xml
<tr th:class="${row.even}? 'even' : 'odd'">
  ...
</tr>
```

条件表达式(`condition`，`then`和`else`)的所有三个部分本身都是表达式，这意味着它们可以是变量(`${...}`，`*{...}`)，消息(`#{...}`)，URL(`@{...}`)或 literals(`'...'`)。

条件表达式也可以使用括号嵌套：

```xml
<tr th:class="${row.even}? (${row.first}? 'first' : 'even') : 'odd'">
  ...
</tr>
```

其他表达式也可以省略，在这种情况下，如果条件为 false，则返回 null value：

```xml
<tr th:class="${row.even}? 'alt'">
  ...
</tr>
```

### 默认表达式(Elvis operator)

默认表达式是一种特殊的条件 value，没有 then 部分。它等同于某些语言(例如 Groovy)中的 Elvis operator，允许您指定两个表达式：如果它没有计算为 null，则使用第一个表达式，但如果它没有，则使用第二个表达式。

让我们在用户 profile 页面中看到它的运行情况：

```xml
<div th:object="${session.user}">
  ...
  <p>Age: <span th:text="*{age}?: '(no age specified)'">27</span>.</p>
</div>
```

如您所见，operator 是`?:`，只有在评估`*{age}`的结果为 null 时，我们才在此处使用它来为 name(在本例中为文字 value)指定默认 value。因此，这相当于：

```xml
<p>Age: <span th:text="*{age != null}? *{age} : '(no age specified)'">27</span>.</p>
```

与条件值一样，它们可以包含括号之间的嵌套表达式：

```xml
<p>
  Name:
  <span th:text="*{firstName}?: (*{admin}? 'Admin' : #{default.username})">Sebastian</span>
</p>
```

### No-Operation 标记

No-Operation 标记由下划线符号(`_`)表示。

此标记后面的 idea 是指定表达式的所需结果是什么都不做，i.e。就像可处理的属性(e.g. `th:text`)根本不存在一样。

除了其他可能性之外，这允许开发人员使用原型文本作为默认值。对于 example，而不是：

```xml
<span th:text="${user.name} ?: 'no user authenticated'">...</span>
```

...我们可以直接使用“没有用户身份验证”作为原型文本，从设计的角度来看，code 既简洁又通用：

```xml
<span th:text="${user.name} ?: _">no user authenticated</span>
```

### 数据转换/格式化

Thymeleaf 为变量(`${...}`)和选择(`*{...}`)表达式定义 double-brace 语法，允许我们通过配置的转换服务应用数据转换。

它基本上是这样的：

```xml
<td th:text="${{user.lastAccessDate}}">...</td>
```

注意到那里的 double 括号？：`${{...}}`。这指示 Thymeleaf 将`user.lastAccessDate`表达式的结果传递给转换服务，并要求它在写入结果之前执行**格式化操作**(转换为`String`)。

假设`user.lastAccessDate`的类型为`java.util.Calendar`，如果已经注册了转换服务(`IStandardConversionService`的实现)并且包含`Calendar -> String`的有效转换，则将应用它。

`IStandardConversionService`(`StandardConversionService` class)的默认 implementation 只是在转换为`String`的任何 object 上执行`.toString()`。有关如何注册自定义转换服务 implementation 的更多信息，请查看[有关 Configuration 的更多信息](https://www.docs4dev.com/docs/zh/thymeleaf/3.0/reference/using_thymeleaf.html#more-on-configuration)部分。

> 官方的 thymeleaf-spring3 和 thymeleaf-spring4 integration 包透明地将 Thymeleaf 的转换服务机制与 Spring 自己的转换服务基础结构相集成，以便 Spring configuration 中声明的转换服务和格式化程序将自动可用于`${{...}}`和`*{{...}}`表达式。

### 预处理

除了用于表达式处理的所有这些 features 之外，Thymeleaf 还具有预处理表达式的 feature。

预处理是在正常表达式之前完成的表达式的执行，它允许修改最终将被执行的表达式。

预处理表达式与普通表达式完全相同，但显示为 double 下划线符号(如`__${expression}__`)。

让我们假设我们有一个 i18n `Messages_fr.properties`条目，其中包含一个调用 language-specific 静态方法的 OGNL 表达式，如：

```java
article.text=@myapp.translator.Translator@translateToFrench({0})
```

......和`Messages_es.properties equivalent`：

```java
article.text=@myapp.translator.Translator@translateToSpanish({0})
```

我们可以创建一个标记片段，根据 locale 计算一个表达式或另一个表达式。为此，我们将首先选择表达式(通过预处理)，然后让 Thymeleaf 执行它：

```xml
<p th:text="${__#{article.text('textVar')}__}">Some text here...</p>
```

请注意，法语 locale 的预处理 step 将创建以下等效项：

```xml
<p th:text="${@myapp.translator.Translator@translateToFrench(textVar)}">Some text here...</p>
```

可以使用`\_\_`在属性中转义预处理 String `__`。

## 设置属性值

本章将解释我们在标记中设置(或修改)属性值的方式。

### 设置任何属性的 value

假设我们的网站发布了一个时事通讯，我们希望我们的用户能够订阅它，所以我们创建一个带有表单的`/WEB-INF/templates/subscribe.html`模板：

```xml
<form action="subscribe.html">
  <fieldset>
    <input type="text" name="email" />
    <input type="submit" value="Subscribe!" />
  </fieldset>
</form>
```

与 Thymeleaf 一样，此模板更像是静态原型，而不是 web application 的模板。首先，我们表单中的`action`属性静态链接到模板文件本身，因此没有地方可以进行有用的 URL 重写。其次，提交按钮中的`value`属性使其显示英文文本，但我们希望它能够国际化。

然后输入`th:attr`属性，以及更改其设置的标记属性的 value 的能力：

```xml
<form action="subscribe.html" th:attr="action=@{/subscribe}">
  <fieldset>
    <input type="text" name="email" />
    <input type="submit" value="Subscribe!" th:attr="value=#{subscribe.submit}"/>
  </fieldset>
</form>
```

这个概念非常简单：`th:attr`只需要一个为属性赋予 value 的表达式。创建了相应的控制器和消息 files 后，处理该文件的结果将是：

```xml
<form action="/gtvg/subscribe">
  <fieldset>
    <input type="text" name="email" />
    <input type="submit" value="¡Suscríbe!"/>
  </fieldset>
</form>
```

除了新的属性值之外，您还可以看到 applicacion context name 已自动添加到`/gtvg/subscribe`中的 URL 基础作为前缀，如上一章所述。

但是如果我们想在 time 设置多个属性呢？ XML 规则不允许您在标记中设置两次属性，因此`th:attr`将采用 comma-separated 分配列表，例如：

```xml
<img src="../../images/gtvglogo.png"
     th:attr="src=@{/images/gtvglogo.png},title=#{logo},alt=#{logo}" />
```

给定所需的消息 files，这将输出：

```xml
<img src="/gtgv/images/gtvglogo.png" title="Logo de Good Thymes" alt="Logo de Good Thymes" />
```

### 将 value 设置为特定属性

到现在为止，您可能会想到以下内容：

```xml
<input type="submit" value="Subscribe!" th:attr="value=#{subscribe.submit}"/>
```

......是一个非常丑陋的标记。在属性的 value 中指定赋值可能非常实用，但如果你必须在 time 中完成，那么它不是创建模板的最优雅方式。

Thymeleaf 同意你的意见，这就是为什么在模板中几乎不使用`th:attr`的原因。通常，您将使用其任务设置特定标记属性的其他`th:*`属性(而不仅仅是`th:attr`之类的任何属性)。

对于 example，要设置`value`属性，请使用`th:value`：

```xml
<input type="submit" value="Subscribe!" th:value="#{subscribe.submit}"/>
```

这看起来好多了！让我们尝试对`form`标签中的`action`属性执行相同的操作：

```xml
<form action="subscribe.html" th:action="@{/subscribe}">
```

你还记得我们之前在`home.html`中放过的那些吗？它们正是同样的属性：

```xml
<li><a href="product/list.html" th:href="@{/product/list}">Product List</a></li>
```

有很多这样的属性，每个属性都针对特定的 HTML5 属性：

|                         |                       |                     |
| :---------------------- | :-------------------- | :------------------ |
| `th:abbr`               | `th:accept`           | `th:accept-charset` |
| `th:accesskey`          | `th:action`           | `th:align`          |
| `th:alt`                | `th:archive`          | `th:audio`          |
| `th:autocomplete`       | `th:axis`             | `th:background`     |
| `th:bgcolor`            | `th:border`           | `th:cellpadding`    |
| `th:cellspacing`        | `th:challenge`        | `th:charset`        |
| `th:cite`               | `th:class`            | `th:classid`        |
| `th:codebase`           | `th:codetype`         | `th:cols`           |
| `th:colspan`            | `th:compact`          | `th:content`        |
| `th:contenteditable`    | `th:contextmenu`      | `th:data`           |
| `th:datetime`           | `th:dir`              | `th:draggable`      |
| `th:dropzone`           | `th:enctype`          | `th:for`            |
| `th:form`               | `th:formaction`       | `th:formenctype`    |
| `th:formmethod`         | `th:formtarget`       | `th:fragment`       |
| `th:frame`              | `th:frameborder`      | `th:headers`        |
| `th:height`             | `th:high`             | `th:href`           |
| `th:hreflang`           | `th:hspace`           | `th:http-equiv`     |
| `th:icon`               | `th:id`               | `th:inline`         |
| `th:keytype`            | `th:kind`             | `th:label`          |
| `th:lang`               | `th:list`             | `th:longdesc`       |
| `th:low`                | `th:manifest`         | `th:marginheight`   |
| `th:marginwidth`        | `th:max`              | `th:maxlength`      |
| `th:media`              | `th:method`           | `th:min`            |
| `th:name`               | `th:onabort`          | `th:onafterprint`   |
| `th:onbeforeprint`      | `th:onbeforeunload`   | `th:onblur`         |
| `th:oncanplay`          | `th:oncanplaythrough` | `th:onchange`       |
| `th:onclick`            | `th:oncontextmenu`    | `th:ondblclick`     |
| `th:ondrag`             | `th:ondragend`        | `th:ondragenter`    |
| `th:ondragleave`        | `th:ondragover`       | `th:ondragstart`    |
| `th:ondrop`             | `th:ondurationchange` | `th:onemptied`      |
| `th:onended`            | `th:onerror`          | `th:onfocus`        |
| `th:onformchange`       | `th:onforminput`      | `th:onhashchange`   |
| `th:oninput`            | `th:oninvalid`        | `th:onkeydown`      |
| `th:onkeypress`         | `th:onkeyup`          | `th:onload`         |
| `th:onloadeddata`       | `th:onloadedmetadata` | `th:onloadstart`    |
| `th:onmessage`          | `th:onmousedown`      | `th:onmousemove`    |
| `th:onmouseout`         | `th:onmouseover`      | `th:onmouseup`      |
| `th:onmousewheel`       | `th:onoffline`        | `th:ononline`       |
| `th:onpause`            | `th:onplay`           | `th:onplaying`      |
| `th:onpopstate`         | `th:onprogress`       | `th:onratechange`   |
| `th:onreadystatechange` | `th:onredo`           | `th:onreset`        |
| `th:onresize`           | `th:onscroll`         | `th:onseeked`       |
| `th:onseeking`          | `th:onselect`         | `th:onshow`         |
| `th:onstalled`          | `th:onstorage`        | `th:onsubmit`       |
| `th:onsuspend`          | `th:ontimeupdate`     | `th:onundo`         |
| `th:onunload`           | `th:onvolumechange`   | `th:onwaiting`      |
| `th:optimum`            | `th:pattern`          | `th:placeholder`    |
| `th:poster`             | `th:preload`          | `th:radiogroup`     |
| `th:rel`                | `th:rev`              | `th:rows`           |
| `th:rowspan`            | `th:rules`            | `th:sandbox`        |
| `th:scheme`             | `th:scope`            | `th:scrolling`      |
| `th:size`               | `th:sizes`            | `th:span`           |
| `th:spellcheck`         | `th:src`              | `th:srclang`        |
| `th:standby`            | `th:start`            | `th:step`           |
| `th:style`              | `th:summary`          | `th:tabindex`       |
| `th:target`             | `th:title`            | `th:type`           |
| `th:usemap`             | `th:value`            | `th:valuetype`      |
| `th:vspace`             | `th:width`            | `th:wrap`           |
| `th:xmlbase`            | `th:xmllang`          | `th:xmlspace`       |

### 在 time 设置多个 value

有两个相当特殊的属性叫做`th:alt-title`和`th:lang-xmllang`，它们可以用于在同一个 time 中将两个属性设置为同一个 value。特别：

- `th:alt-title`将设置`alt`和`title`。
- `th:lang-xmllang`将设置`lang`和`xml:lang`。

对于我们的 GTVG 主页，这将允许我们替换：

```xml
<img src="../../images/gtvglogo.png"
     th:attr="src=@{/images/gtvglogo.png},title=#{logo},alt=#{logo}" />
```

......或者这个，相当于：

```xml
<img src="../../images/gtvglogo.png"
     th:src="@{/images/gtvglogo.png}" th:title="#{logo}" th:alt="#{logo}" />
```

…有了这个：

```xml
<img src="../../images/gtvglogo.png"
     th:src="@{/images/gtvglogo.png}" th:alt-title="#{logo}" />
```

### 追加和预先

Thymeleaf 还提供`th:attrappend`和`th:attrprepend`属性，它们将 evaluation 的结果附加(后缀)或前置(前缀)到现有属性值。

对于 example，您可能希望 storeclass 的 name 被添加(未设置，只是添加)到 context 变量中的一个按钮，因为要使用的特定 CSS class 将取决于用户所做的事情。之前：

```xml
<input type="button" value="Do it!" class="btn" th:attrappend="class=${' ' + cssStyle}" />
```

如果您使用`cssStyle`变量设置为`"warning"`来处理此模板，您将获得：

```xml
<input type="button" value="Do it!" class="btn warning" />
```

标准方言中还有两个特定的附加属性：`th:classappend`和`th:styleappend`属性，用于向元素添加 CSS class 或样式片段而不覆盖现有元素：

```xml
<tr th:each="prod : ${prods}" class="row" th:classappend="${prodStat.odd}? 'odd'">
```

(不要担心`th:each`属性.这是一个迭代属性，我们将讨论它 later.)

### Fixed-value boolean 属性

HTML 具有 boolean 属性的概念，没有 value 的属性和 1 的 precence 意味着 value 是“true”。在 XHTML 中，这些属性只占用 1 value，这本身也是如此。

对于 example，`checked`：

```xml
<input type="checkbox" name="option2" checked /> <!-- HTML -->
<input type="checkbox" name="option1" checked="checked" /> <!-- XHTML -->
```

标准方言包含允许您通过评估条件来设置这些属性的属性，因此如果计算为 true，则属性将设置为其固定 value，如果计算为 false，则不会设置该属性：

```xml
<input type="checkbox" name="active" th:checked="${user.active}" />
```

标准方言中存在以下 fixed-value boolean 属性：

|                     |                |                 |
| :------------------ | :------------- | :-------------- |
| `th:async`          | `th:autofocus` | `th:autoplay`   |
| `th:checked`        | `th:controls`  | `th:declare`    |
| `th:default`        | `th:defer`     | `th:disabled`   |
| `th:formnovalidate` | `th:hidden`    | `th:ismap`      |
| `th:loop`           | `th:multiple`  | `th:novalidate` |
| `th:nowrap`         | `th:open`      | `th:pubdate`    |
| `th:readonly`       | `th:required`  | `th:reversed`   |
| `th:scoped`         | `th:seamless`  | `th:selected`   |

### 设置任何属性的 value(默认属性处理器)

Thymeleaf 提供了一个默认属性处理器，允许我们设置任何属性的 value，即使在标准方言中没有为它定义特定的`th:*`处理器。

所以类似于：

```xml
<span th:whatever="${user.name}">...</span>
```

将导致：

```xml
<span whatever="John Apricot">...</span>
```

### 支持 HTML5-friendly 属性和元素名称

也可以使用完全不同的语法以更加 HTML5-friendly 的方式将处理器应用于模板。

```xml
<table>
    <tr data-th-each="user : ${users}">
        <td data-th-text="${user.login}">...</td>
        <td data-th-text="${user.name}">...</td>
    </tr>
</table>
```

`data-{prefix}-{name}`语法是在 HTML5 中编写自定义属性的标准方法，无需开发人员使用任何名称空间的名称，如`th:*`。 Thymeleaf 使所有方言(不仅是标准方言)自动使用此语法。

还有一种语法来指定自定义标记：`{prefix}-{name}`，它遵循 W3C 自定义元素规范(较大的 W3C Web 组件规范的一部分)。例如，这可用于`th:block`元素(或`th-block`)，这将在后面的部分中解释。

**重要说明：**此语法是命名空间`th:*`的一个补充，它不会替换它。完全没有意图在将来弃用命名空间语法。

## 迭代

到目前为止，我们已经创建了一个主页，一个用户 profile 页面以及一个允许用户订阅我们的新闻通讯的页面......但是我们的产品呢？为此，我们需要一种方法来迭代集合中的项目以构建我们的产品页面。

### 迭代基础知识

要在`/WEB-INF/templates/product/list.html`页面中显示产品，我们将使用 table。我们的每个产品都会连续显示(一个元素)，因此对于我们的模板，我们需要创建一个模板行 - 一个可以说明我们希望如何显示每个产品的模板行 - 然后指示 Thymeleaf 重复它，每个产品一次。

标准方言为我们提供了一个属性：`th:each`。

#### 使用 th:each

对于我们的产品列表页面，我们需要一个控制器方法，从服务层检索产品列表并将其添加到模板 context：

```java
public void process(
        final HttpServletRequest request, final HttpServletResponse response,
        final ServletContext servletContext, final ITemplateEngine templateEngine)
        throws Exception {

    ProductService productService = new ProductService();
    List<Product> allProducts = productService.findAll();

    WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
    ctx.setVariable("prods", allProducts);

    templateEngine.process("product/list", ctx, response.getWriter());

}
```

然后我们将在模板中使用`th:each`迭代产品列表：

```xml
<!DOCTYPE html>

<html xmlns:th="http://www.thymeleaf.org">

  <head>
    <title>Good Thymes Virtual Grocery</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <link rel="stylesheet" type="text/css" media="all"
          href="../../../css/gtvg.css" th:href="@{/css/gtvg.css}" />
  </head>

  <body>

    <h1>Product list</h1>

    <table>
      <tr>
        <th>NAME</th>
        <th>PRICE</th>
        <th>IN STOCK</th>
      </tr>
      <tr th:each="prod : ${prods}">
        <td th:text="${prod.name}">Onions</td>
        <td th:text="${prod.price}">2.41</td>
        <td th:text="${prod.inStock}? #{true} : #{false}">yes</td>
      </tr>
    </table>

    <p>
      <a href="../home.html" th:href="@{/}">Return to home</a>
    </p>

  </body>

</html>
```

您在上面看到的`prod : ${prods}`属性 value 意味着“对于评估`${prods}`的结果中的每个元素，使用名为 prod 的变量中的当前元素重复此模板片段”。让我们给出一个 name 每个我们看到的东西：

- 我们将`${prods}`称为迭代表达式或迭代变量。
- 我们将`prod`称为迭代变量或简称为变量。

请注意，`prod` iter 变量的作用域为元素，这意味着它可用于内部标记，如`。

#### 可重复的值

`java.util.List` class 不是可用于 Thymeleaf 迭代的唯一值。有一组非常完整的 objects 被认为是`th:each`属性可迭代的：

- 任何 object 实现`java.util.Iterable`
- 任何 object 实现`java.util.Enumeration`。
- 任何实现`java.util.Iterator`的 object，其值将在迭代器返回时使用，而不需要缓存 memory 中的所有值。
- 任何 object 实现`java.util.Map`。迭代 maps 时，iter 变量将是 class `java.util.Map.Entry`。
- 任何 array。
- 任何其他 object 都将被视为包含 object 本身的 single-valued 列表。

### 保持迭代状态

当使用`th:each`时，Thymeleaf 提供了一种用于跟踪迭代状态的机制：状态变量。

状态变量在`th:each`属性中定义，并包含以下数据：

- 当前迭代索引，从 0 开始。这是`index` property。
- 当前迭代索引，从 1 开始。这是`count` property。
- 迭代变量中元素的总量。这是`size` property。
- 每次迭代的 iter 变量。这是`current` property。
- 当前迭代是偶数还是奇数。这些是`even/odd` boolean properties。
- 当前迭代是否是第一个。这是`first` boolean property。
- 当前迭代是否是最后一次。这是`last` boolean property。

让我们看看我们如何将它与前面的例子一起使用：

```xml
<table>
  <tr>
    <th>NAME</th>
    <th>PRICE</th>
    <th>IN STOCK</th>
  </tr>
  <tr th:each="prod,iterStat : ${prods}" th:class="${iterStat.odd}? 'odd'">
    <td th:text="${prod.name}">Onions</td>
    <td th:text="${prod.price}">2.41</td>
    <td th:text="${prod.inStock}? #{true} : #{false}">yes</td>
  </tr>
</table>
```

状态变量(此 example 中的`iterStat`)在`th:each`属性中定义，方法是在 iter 变量本身之后写入 name，用逗号分隔。就像 iter 变量一样，status 变量的范围也是由持有`th:each`属性的标记定义的 code 的片段。

我们来看看处理模板的结果：

```xml
<!DOCTYPE html>

<html>

  <head>
    <title>Good Thymes Virtual Grocery</title>
    <meta content="text/html; charset=UTF-8" http-equiv="Content-Type"/>
    <link rel="stylesheet" type="text/css" media="all" href="/gtvg/css/gtvg.css" />
  </head>

  <body>

    <h1>Product list</h1>

    <table>
      <tr>
        <th>NAME</th>
        <th>PRICE</th>
        <th>IN STOCK</th>
      </tr>
      <tr class="odd">
        <td>Fresh Sweet Basil</td>
        <td>4.99</td>
        <td>yes</td>
      </tr>
      <tr>
        <td>Italian Tomato</td>
        <td>1.25</td>
        <td>no</td>
      </tr>
      <tr class="odd">
        <td>Yellow Bell Pepper</td>
        <td>2.50</td>
        <td>yes</td>
      </tr>
      <tr>
        <td>Old Cheddar</td>
        <td>18.75</td>
        <td>yes</td>
      </tr>
    </table>

    <p>
      <a href="/gtvg/" shape="rect">Return to home</a>
    </p>

  </body>

</html>
```

请注意，我们的迭代状态变量已经完美地工作，只将`odd` CSS class 设置为奇数行。

如果您没有显式设置状态变量，Thymeleaf 将始终通过将`Stat`后缀为迭代变量的 name 来为您创建一个：

```xml
<table>
  <tr>
    <th>NAME</th>
    <th>PRICE</th>
    <th>IN STOCK</th>
  </tr>
  <tr th:each="prod : ${prods}" th:class="${prodStat.odd}? 'odd'">
    <td th:text="${prod.name}">Onions</td>
    <td th:text="${prod.price}">2.41</td>
    <td th:text="${prod.inStock}? #{true} : #{false}">yes</td>
  </tr>
</table>
```

### 通过延迟检索数据进行优化

有时我们可能希望优化数据集合的检索(来自数据库的 e.g. )，这样只有在真正使用它们时才会检索这些集合。

> 实际上，这可以应用于任何数据，但考虑到 in-memory 集合可能具有的大小，检索要迭代的集合是此方案最常见的情况。

为了支持这一点，Thymeleaf 提供了一种延迟加载 context 变量的机制。实现`ILazyContextVariable`接口的 Context 变量 - 最有可能通过扩展其`LazyContextVariable` default implementation - 将在正在执行的 moment 中解析。例如：

```java
context.setVariable(
     "users",
     new LazyContextVariable<List<User>>() {
         @Override
         protected List<User> loadValue() {
             return databaseRepository.findAllUsers();
         }
     });
```

这个变量可以在不知道其惰性的情况下使用，在 code 中如下：

```xml
<ul>
  <li th:each="u : ${users}" th:text="${u.name}">user name</li>
</ul>
```

但是在同一 time，如果`condition`在 code 中评估为`false`，则永远不会被初始化(它的`loadValue()`方法永远不会被调用)，例如：

```xml
<ul th:if="${condition}">
  <li th:each="u : ${users}" th:text="${u.name}">user name</li>
</ul>
```

## 有条件的 Evaluation

### 简单条件：“if”和“除非”

有时，如果满足某个条件，您将需要模板的片段才会出现在结果中。

例如，假设我们希望在我们的产品 table 中显示一个列，其中包含每个产品存在的 comments 数量，如果有任何 comments，则指向该产品的 comment 详细信息页面的链接。

在 order 中，我们将使用`th:if`属性：

```xml
<table>
  <tr>
    <th>NAME</th>
    <th>PRICE</th>
    <th>IN STOCK</th>
    <th>COMMENTS</th>
  </tr>
  <tr th:each="prod : ${prods}" th:class="${prodStat.odd}? 'odd'">
    <td th:text="${prod.name}">Onions</td>
    <td th:text="${prod.price}">2.41</td>
    <td th:text="${prod.inStock}? #{true} : #{false}">yes</td>
    <td>
      <span th:text="${#lists.size(prod.comments)}">2</span> comment/s
      <a href="comments.html"
         th:href="@{/product/comments(prodId=${prod.id})}"
         th:if="${not #lists.isEmpty(prod.comments)}">view</a>
    </td>
  </tr>
</table>
```

这里有很多东西要看，所以让我们关注重要的 line：

```xml
<a href="comments.html"
   th:href="@{/product/comments(prodId=${prod.id})}"
   th:if="${not #lists.isEmpty(prod.comments)}">view</a>
```

这将创建指向 comments 页面(带有 URL `/product/comments`)的链接，其中`prodId`参数设置为产品的`id`，但前提是该产品具有任何 comments。

让我们看一下结果标记：

```xml
<table>
  <tr>
    <th>NAME</th>
    <th>PRICE</th>
    <th>IN STOCK</th>
    <th>COMMENTS</th>
  </tr>
  <tr>
    <td>Fresh Sweet Basil</td>
    <td>4.99</td>
    <td>yes</td>
    <td>
      <span>0</span> comment/s
    </td>
  </tr>
  <tr class="odd">
    <td>Italian Tomato</td>
    <td>1.25</td>
    <td>no</td>
    <td>
      <span>2</span> comment/s
      <a href="/gtvg/product/comments?prodId=2">view</a>
    </td>
  </tr>
  <tr>
    <td>Yellow Bell Pepper</td>
    <td>2.50</td>
    <td>yes</td>
    <td>
      <span>0</span> comment/s
    </td>
  </tr>
  <tr class="odd">
    <td>Old Cheddar</td>
    <td>18.75</td>
    <td>yes</td>
    <td>
      <span>1</span> comment/s
      <a href="/gtvg/product/comments?prodId=4">view</a>
    </td>
  </tr>
</table>
```

完善！这正是我们想要的。

请注意，`th:if`属性不仅会评估 boolean 条件。它的功能稍微超出了它，它会根据这些规则将指定的表达式评估为`true`：

- 如果 value 不为 null：
- 如果 value 是 boolean 并且是`true`。
- 如果 value 是一个数字并且是 non-zero
- 如果 value 是一个字符并且是 non-zero
- 如果 value 是 String 并且不是“false”，“off”或“no”
- 如果 value 不是 boolean，数字，字符或 String。
- (如果 value 为 null，则 th:if 将计算为 false)。

此外，`th:if`有一个逆属性`th:unless`，我们可以在之前的 example 中使用它，而不是在 OGNL 表达式中使用`not`：

```xml
<a href="comments.html"
   th:href="@{/comments(prodId=${prod.id})}"
   th:unless="${#lists.isEmpty(prod.comments)}">view</a>
```

### 切换语句

还有一种方法可以使用 Java 中的等效开关结构有条件地显示内容：`th:switch`/`th:case`属性集。

```xml
<div th:switch="${user.role}">
  <p th:case="'admin'">User is an administrator</p>
  <p th:case="#{roles.manager}">User is a manager</p>
</div>
```

请注意，只要将一个`th:case`属性计算为`true`，同一个 switch context 中的每个其他`th:case`属性都将被计算为`false`。

默认选项指定为`th:case="*"`：

```xml
<div th:switch="${user.role}">
  <p th:case="'admin'">User is an administrator</p>
  <p th:case="#{roles.manager}">User is a manager</p>
  <p th:case="*">User is some other thing</p>
</div>
```

## 模板布局

### 包括模板片段

#### 定义和引用片段

在我们的模板中，我们通常希望包含来自其他模板的部分，例如页脚，页眉，菜单......

为了做到这一点，Thymeleaf 需要我们定义这些部分，“片段”，以便包含，这可以使用`th:fragment`属性来完成。

假设我们要为所有杂货页面添加标准版权页脚，因此我们创建一个包含此 code 的`/WEB-INF/templates/footer.html`文件：

```xml
<!DOCTYPE html>

<html xmlns:th="http://www.thymeleaf.org">

  <body>

    <div th:fragment="copy">
      &copy; 2011 The Good Thymes Virtual Grocery
    </div>

  </body>

</html>
```

上面的 code 定义了一个名为`copy`的片段，我们可以使用`th:insert`或`th:replace`属性中的一个轻松地在我们的主页中包含它(以及`th:include`，但是自 Thymeleaf 3.0 以来不再推荐使用它)：

```xml
<body>

  ...

  <div th:insert="~{footer :: copy}"></div>

</body>
```

请注意，`th:insert`需要一个片段表达式(`~{...}`)，它是一个导致片段的表达式。在上面的 example 中，这是一个 non-complex 片段表达式，(`~{`，`}`)封闭是完全可选的，所以上面的 code 等同于：

```xml
<body>

  ...

  <div th:insert="footer :: copy"></div>

</body>
```

#### 片段规范语法

片段表达式的语法非常简单。有三种不同的格式：

- `"~{templatename::selector}"`包括在名为`templatename`的模板上应用指定标记选择器而产生的片段。请注意`selector`可以只是片段 name，因此您可以像上面的`~{footer :: copy}`一样指定像`~{templatename::fragmentname}`这样简单的东西。

> 标记选择器语法由底层 AttoParser 解析 library 定义，类似于 XPath 表达式或 CSS selectors。有关详细信息，请参阅[附录 C.](https://www.docs4dev.com/docs/zh/thymeleaf/3.0/reference/using_thymeleaf.html#appendix-c-markup-selector-syntax)。

- `"~{templatename}"`包含名为`templatename`的完整模板。

> 请注意，您在`th:insert`/`th:replace`标记中使用的模板 name 必须由模板引擎当前使用的模板解析器解析。

- `~{::selector}"`或`"~{this::selector}"`从同一模板插入一个与`selector`匹配的片段。如果在表达式出现的模板上找不到，则模板 calls(插入)的堆栈将遍历最初处理的模板(根)，直到`selector`匹配某个 level。

上面例子中的`templatename`和`selector`都可以是 fully-featured 表达式(偶数条件！)，如：

```xml
<div th:insert="footer :: (${user.isAdmin}? #{footer.admin} : #{footer.normaluser})"></div>
```

再次注意`th:insert`/`th:replace`中周围的`~{...}`包络是如何可选的。

片段可以包含任何`th:*`属性。一旦将片段包含在目标模板(具有`th:insert`/`th:replace`属性的模板)中，就会评估这些属性，并且它们将能够引用此目标模板中定义的任何 context 变量。

> 这种片段方法的一大优点是，您可以在浏览器完美显示的页面中编写片段，具有完整且有效的标记结构，同时仍保留使 Thymeleaf 将其包含在其他模板中的能力。

#### 在没有 th:fragment 的情况下引用片段

由于 Markup Selector 的强大功能，我们可以包含不使用任何`th:fragment`属性的片段。它甚至可以是来自不同的 application 的标记 code，完全不了解 Thymeleaf：

```xml
...
<div id="copy-section">
  &copy; 2011 The Good Thymes Virtual Grocery
</div>
...
```

我们可以使用上面的片段简单地通过其`id`属性引用它，方式与 CSS 选择器类似：

```xml
<body>

  ...

  <div th:insert="~{footer :: #copy-section}"></div>

</body>
```

#### th:insert 和 th:replace(和 th:include)之间的区别

`th:insert`和`th:replace`之间有什么区别(和`th:include`，自 3.0 以来不推荐)？

- `th:insert`是最简单的：它只是将指定的片段作为其 host 标记的主体插入。
- `th:replace`实际上用指定的片段替换了它的 host 标记。
- `th:include`类似于`th:insert`，但它不是插入片段，而是仅插入此片段的内容。

所以像这样的 HTML 片段：

```xml
<footer th:fragment="copy">
  &copy; 2011 The Good Thymes Virtual Grocery
</footer>
```

...在 host ``标签中包含三次，如下所示：

```xml
<body>

  ...

  <div th:insert="footer :: copy"></div>

  <div th:replace="footer :: copy"></div>

  <div th:include="footer :: copy"></div>

</body>
```

......将导致：

```xml
<body>

  ...

  <div>
    <footer>
      &copy; 2011 The Good Thymes Virtual Grocery
    </footer>
  </div>

  <footer>
    &copy; 2011 The Good Thymes Virtual Grocery
  </footer>

  <div>
    &copy; 2011 The Good Thymes Virtual Grocery
  </div>

</body>
```

### 可参数化的片段签名

在 order 中为模板片段创建更多 function-like 机制，使用`th:fragment`定义的片段可以指定一组参数：

```xml
<div th:fragment="frag (onevar,twovar)">
    <p th:text="${onevar} + ' - ' + ${twovar}">...</p>
</div>
```

这需要使用这两种语法之一来从`th:insert`或`th:replace`调用片段：

```xml
<div th:replace="::frag (${value1},${value2})">...</div>
<div th:replace="::frag (onevar=${value1},twovar=${value2})">...</div>
```

请注意，order 在最后一个选项中并不重要：

```xml
<div th:replace="::frag (twovar=${value2},onevar=${value1})">...</div>
```

#### 片段局部变量没有片段 arguments

即使片段没有像这样的 arguments 定义：

```xml
<div th:fragment="frag">
    ...
</div>
```

我们可以使用上面指定的第二种语法来调用它们(只有第二种语法)：

```xml
<div th:replace="::frag (onevar=${value1},twovar=${value2})">
```

这相当于`th:replace`和`th:with`的组合：

```xml
<div th:replace="::frag" th:with="onevar=${value1},twovar=${value2}">
```

**注意**片段的局部变量规范 - 无论是否有参数签名 - 都不会导致 context 在执行之前被清空。片段仍然可以像访问目前那样访问调用模板中使用的每个 context 变量。

#### th:assert 表示 in-template 断言

`th:assert`属性可以指定应该被计算的 comma-separated 表达式列表，并为每个 evaluation 生成 true，否则会引发 exception。

```xml
<div th:assert="${onevar},(${twovar} != 43)">...</div>
```

这对于验证片段签名的参数非常方便：

```xml
<header th:fragment="contentheader(title)" th:assert="${!#strings.isEmpty(title)}">...</header>
```

### 灵活的布局：仅仅是片段插入

由于片段表达式，我们可以为不是 text，numbers，bean objects 的片段指定参数，而是指定标记片段。

这允许我们以一种方式创建我们的片段，使得它们可以通过来自调用模板的标记来丰富，从而产生非常灵活的**模板布局机制**。

注意在下面的片段中使用`title`和`links`变量：

```xml
<head th:fragment="common_header(title,links)">

  <title th:replace="${title}">The awesome application</title>

  <!-- Common styles and scripts -->
  <link rel="stylesheet" type="text/css" media="all" th:href="@{/css/awesomeapp.css}">
  <link rel="shortcut icon" th:href="@{/images/favicon.ico}">
  <script type="text/javascript" th:src="@{/sh/scripts/codebase.js}"></script>

  <!--/* Per-page placeholder for additional links */-->
  <th:block th:replace="${links}" />

</head>
```

我们现在可以将这个片段称为：

```xml
...
<head th:replace="base :: common_header(~{::title},~{::link})">

  <title>Awesome - Main</title>

  <link rel="stylesheet" th:href="@{/css/bootstrap.min.css}">
  <link rel="stylesheet" th:href="@{/themes/smoothness/jquery-ui.css}">

</head>
...
```

...结果将使用调用模板中的实际`和`标记作为`title`和`links`变量的值，从而导致我们的片段在插入期间自定义：

```xml
...
<head>

  <title>Awesome - Main</title>

  <!-- Common styles and scripts -->
  <link rel="stylesheet" type="text/css" media="all" href="/awe/css/awesomeapp.css">
  <link rel="shortcut icon" href="/awe/images/favicon.ico">
  <script type="text/javascript" src="/awe/sh/scripts/codebase.js"></script>

  <link rel="stylesheet" href="/awe/css/bootstrap.min.css">
  <link rel="stylesheet" href="/awe/themes/smoothness/jquery-ui.css">

</head>
...
```

#### 使用空片段

特殊片段表达式，即空片段(`~{}`)，可用于指定无标记。使用前面的 example：

```xml
<head th:replace="base :: common_header(~{::title},~{})">

  <title>Awesome - Main</title>

</head>
...
```

注意片段(`links`)的第二个参数如何设置为空片段，因此没有为``块写入任何内容：

```xml
...
<head>

  <title>Awesome - Main</title>

  <!-- Common styles and scripts -->
  <link rel="stylesheet" type="text/css" media="all" href="/awe/css/awesomeapp.css">
  <link rel="shortcut icon" href="/awe/images/favicon.ico">
  <script type="text/javascript" src="/awe/sh/scripts/codebase.js"></script>

</head>
...
```

#### 使用 no-operation 标记

如果我们只想让我们的片段使用其当前标记作为默认 value，no-op 也可以用作片段的参数。再次，使用`common_header` example：

```xml
...
<head th:replace="base :: common_header(_,~{::link})">

  <title>Awesome - Main</title>

  <link rel="stylesheet" th:href="@{/css/bootstrap.min.css}">
  <link rel="stylesheet" th:href="@{/themes/smoothness/jquery-ui.css}">

</head>
...
```

看看`title`参数(`common_header`片段的第一个参数)是如何设置为 no-op(`_`)的，这导致片段的这一部分根本不被执行(`title` = no-operation)：

```xml
<title th:replace="${title}">The awesome application</title>
```

结果是：

```xml
...
<head>

  <title>The awesome application</title>

  <!-- Common styles and scripts -->
  <link rel="stylesheet" type="text/css" media="all" href="/awe/css/awesomeapp.css">
  <link rel="shortcut icon" href="/awe/images/favicon.ico">
  <script type="text/javascript" src="/awe/sh/scripts/codebase.js"></script>

  <link rel="stylesheet" href="/awe/css/bootstrap.min.css">
  <link rel="stylesheet" href="/awe/themes/smoothness/jquery-ui.css">

</head>
...
```

#### 高级条件插入片段

emtpy 片段和 no-operation 令牌的可用性允许我们以非常简单和优雅的方式执行片段的条件插入。

对于 example，我们可以在 order 中执行此操作，只有在用户是管理员时 insert 我们的`common :: adminhead`片段，如果不是，则 insert 什么(emtpy 片段)：

```xml
...
<div th:insert="${user.isAdmin()} ? ~{common :: adminhead} : ~{}">...</div>
...
```

此外，我们可以使用 order 中的 no-operation 标记仅在满足指定条件时插入片段，但如果不满足条件则保留标记而不进行修改：

```xml
...
<div th:insert="${user.isAdmin()} ? ~{common :: adminhead} : _">
    Welcome [[${user.name}]], click <a th:href="@{/support}">here</a> for help-desk support.
</div>
...
```

另外，如果我们已经配置了模板解析器以检查模板资源是否存在 - 通过它们的`checkExistence` flag - 我们可以使用片段本身的存在作为默认操作中的条件：

```xml
...
<!-- The body of the <div> will be used if the "common :: salutation" fragment  -->
<!-- does not exist (or is empty).                                              -->
<div th:insert="~{common :: salutation} ?: _">
    Welcome [[${user.name}]], click <a th:href="@{/support}">here</a> for help-desk support.
</div>
...
```

### 删除模板片段

回到 example application，让我们重新审视产品列表模板的最后一个 version：

```xml
<table>
  <tr>
    <th>NAME</th>
    <th>PRICE</th>
    <th>IN STOCK</th>
    <th>COMMENTS</th>
  </tr>
  <tr th:each="prod : ${prods}" th:class="${prodStat.odd}? 'odd'">
    <td th:text="${prod.name}">Onions</td>
    <td th:text="${prod.price}">2.41</td>
    <td th:text="${prod.inStock}? #{true} : #{false}">yes</td>
    <td>
      <span th:text="${#lists.size(prod.comments)}">2</span> comment/s
      <a href="comments.html"
         th:href="@{/product/comments(prodId=${prod.id})}"
         th:unless="${#lists.isEmpty(prod.comments)}">view</a>
    </td>
  </tr>
</table>
```

这个 code 作为一个模板很好，但作为一个静态页面(当浏览器直接打开而没有 Thymeleaf 处理它时)它不会成为一个好的原型。

为什么？因为虽然浏览器可以完全显示，但 table 只有一行，而且这行包含 mock 数据。作为原型，它看起来不够逼真......我们应该有多个产品，我们需要更多的行。

所以让我们添加一些：

```xml
<table>
  <tr>
    <th>NAME</th>
    <th>PRICE</th>
    <th>IN STOCK</th>
    <th>COMMENTS</th>
  </tr>
  <tr th:each="prod : ${prods}" th:class="${prodStat.odd}? 'odd'">
    <td th:text="${prod.name}">Onions</td>
    <td th:text="${prod.price}">2.41</td>
    <td th:text="${prod.inStock}? #{true} : #{false}">yes</td>
    <td>
      <span th:text="${#lists.size(prod.comments)}">2</span> comment/s
      <a href="comments.html"
         th:href="@{/product/comments(prodId=${prod.id})}"
         th:unless="${#lists.isEmpty(prod.comments)}">view</a>
    </td>
  </tr>
  <tr class="odd">
    <td>Blue Lettuce</td>
    <td>9.55</td>
    <td>no</td>
    <td>
      <span>0</span> comment/s
    </td>
  </tr>
  <tr>
    <td>Mild Cinnamon</td>
    <td>1.99</td>
    <td>yes</td>
    <td>
      <span>3</span> comment/s
      <a href="comments.html">view</a>
    </td>
  </tr>
</table>
```

好的，现在我们有三个，对原型来说肯定更好。但是......当我们用 Thymeleaf 处理它时会发生什么？：

```xml
<table>
  <tr>
    <th>NAME</th>
    <th>PRICE</th>
    <th>IN STOCK</th>
    <th>COMMENTS</th>
  </tr>
  <tr>
    <td>Fresh Sweet Basil</td>
    <td>4.99</td>
    <td>yes</td>
    <td>
      <span>0</span> comment/s
    </td>
  </tr>
  <tr class="odd">
    <td>Italian Tomato</td>
    <td>1.25</td>
    <td>no</td>
    <td>
      <span>2</span> comment/s
      <a href="/gtvg/product/comments?prodId=2">view</a>
    </td>
  </tr>
  <tr>
    <td>Yellow Bell Pepper</td>
    <td>2.50</td>
    <td>yes</td>
    <td>
      <span>0</span> comment/s
    </td>
  </tr>
  <tr class="odd">
    <td>Old Cheddar</td>
    <td>18.75</td>
    <td>yes</td>
    <td>
      <span>1</span> comment/s
      <a href="/gtvg/product/comments?prodId=4">view</a>
    </td>
  </tr>
  <tr class="odd">
    <td>Blue Lettuce</td>
    <td>9.55</td>
    <td>no</td>
    <td>
      <span>0</span> comment/s
    </td>
  </tr>
  <tr>
    <td>Mild Cinnamon</td>
    <td>1.99</td>
    <td>yes</td>
    <td>
      <span>3</span> comment/s
      <a href="comments.html">view</a>
    </td>
  </tr>
</table>
```

最后两行是 mock 行！嗯，当然它们是：迭代只适用于第一行，所以没有理由为什么 Thymeleaf 应该删除其他两个。

我们需要一种在模板处理过程中删除这两行的方法。让我们在第二个和第三个标签上使用`th:remove`属性：

```xml
<table>
  <tr>
    <th>NAME</th>
    <th>PRICE</th>
    <th>IN STOCK</th>
    <th>COMMENTS</th>
  </tr>
  <tr th:each="prod : ${prods}" th:class="${prodStat.odd}? 'odd'">
    <td th:text="${prod.name}">Onions</td>
    <td th:text="${prod.price}">2.41</td>
    <td th:text="${prod.inStock}? #{true} : #{false}">yes</td>
    <td>
      <span th:text="${#lists.size(prod.comments)}">2</span> comment/s
      <a href="comments.html"
         th:href="@{/product/comments(prodId=${prod.id})}"
         th:unless="${#lists.isEmpty(prod.comments)}">view</a>
    </td>
  </tr>
  <tr class="odd" th:remove="all">
    <td>Blue Lettuce</td>
    <td>9.55</td>
    <td>no</td>
    <td>
      <span>0</span> comment/s
    </td>
  </tr>
  <tr th:remove="all">
    <td>Mild Cinnamon</td>
    <td>1.99</td>
    <td>yes</td>
    <td>
      <span>3</span> comment/s
      <a href="comments.html">view</a>
    </td>
  </tr>
</table>
```

处理完毕后，所有内容都会再次显示：

```xml
<table>
  <tr>
    <th>NAME</th>
    <th>PRICE</th>
    <th>IN STOCK</th>
    <th>COMMENTS</th>
  </tr>
  <tr>
    <td>Fresh Sweet Basil</td>
    <td>4.99</td>
    <td>yes</td>
    <td>
      <span>0</span> comment/s
    </td>
  </tr>
  <tr class="odd">
    <td>Italian Tomato</td>
    <td>1.25</td>
    <td>no</td>
    <td>
      <span>2</span> comment/s
      <a href="/gtvg/product/comments?prodId=2">view</a>
    </td>
  </tr>
  <tr>
    <td>Yellow Bell Pepper</td>
    <td>2.50</td>
    <td>yes</td>
    <td>
      <span>0</span> comment/s
    </td>
  </tr>
  <tr class="odd">
    <td>Old Cheddar</td>
    <td>18.75</td>
    <td>yes</td>
    <td>
      <span>1</span> comment/s
      <a href="/gtvg/product/comments?prodId=4">view</a>
    </td>
  </tr>
</table>
```

那个属性中的`all` value 是什么意思？ `th:remove`可以以五种不同的方式运行，具体取决于其 value：

- `all`：删除包含标记及其所有 children。
- `body`：不要删除包含标记，但删除所有 children。
- `tag`：删除包含标记，但不删除其 children。
- `all-but-first`：删除包含第一个标记的所有 children。
- `none`：什么都不做。此 value 对动态 evaluation 很有用。

`all-but-first` value 有什么用？在原型设计时，它会让我们保存一些`th:remove="all"`：

```xml
<table>
  <thead>
    <tr>
      <th>NAME</th>
      <th>PRICE</th>
      <th>IN STOCK</th>
      <th>COMMENTS</th>
    </tr>
  </thead>
  <tbody th:remove="all-but-first">
    <tr th:each="prod : ${prods}" th:class="${prodStat.odd}? 'odd'">
      <td th:text="${prod.name}">Onions</td>
      <td th:text="${prod.price}">2.41</td>
      <td th:text="${prod.inStock}? #{true} : #{false}">yes</td>
      <td>
        <span th:text="${#lists.size(prod.comments)}">2</span> comment/s
        <a href="comments.html"
           th:href="@{/product/comments(prodId=${prod.id})}"
           th:unless="${#lists.isEmpty(prod.comments)}">view</a>
      </td>
    </tr>
    <tr class="odd">
      <td>Blue Lettuce</td>
      <td>9.55</td>
      <td>no</td>
      <td>
        <span>0</span> comment/s
      </td>
    </tr>
    <tr>
      <td>Mild Cinnamon</td>
      <td>1.99</td>
      <td>yes</td>
      <td>
        <span>3</span> comment/s
        <a href="comments.html">view</a>
      </td>
    </tr>
  </tbody>
</table>
```

`th:remove`属性可以采用任何 Thymeleaf 标准表达式，因为它返回一个允许的 String 值(`all`，`tag`，`body`，`all-but-first`或`none`)。

这意味着删除可能是有条件的，例如：

```xml
<a href="/something" th:remove="${condition}? tag : none">Link text not to be removed</a>
```

另请注意，`th:remove`认为`null`是`none`的同义词，因此以下工作方式与上面的 example 相同：

```xml
<a href="/something" th:remove="${condition}? tag">Link text not to be removed</a>
```

在这种情况下，如果`${condition}`是 false，将返回`null`，因此不会执行删除操作。

### 布局继承

为了能够将单个文件作为布局，可以使用片段。使用`th:fragment`和`th:replace`的`title`和`content`的简单布局的示例：

```xml
<!DOCTYPE html>
<html th:fragment="layout (title, content)" xmlns:th="http://www.thymeleaf.org">
<head>
    <title th:replace="${title}">Layout Title</title>
</head>
<body>
    <h1>Layout H1</h1>
    <div th:replace="${content}">
        <p>Layout content</p>
    </div>
    <footer>
        Layout footer
    </footer>
</body>
</html>
```

此 example 声明了一个名为**layout**的片段，其标题和内容作为参数。两者都将在页面上替换，并在下面的 example 中通过提供的片段表达式继承它。

```xml
<!DOCTYPE html>
<html th:replace="~{layoutFile :: layout(~{::title}, ~{::section})}">
<head>
    <title>Page Title</title>
</head>
<body>
<section>
    <p>Page content</p>
    <div>Included on page</div>
</section>
</body>
</html>
```

在此文件中，`html`标记将替换为布局，但在布局中`title`和`content`将分别替换为`title`和`section`块。

如果需要，布局可以由多个片段组成页眉和页脚。

## 局部变量

Thymeleaf calls 局部变量是为模板的特定片段定义的变量，并且仅可用于该片段内的 evaluation。

我们已经看到的示例是产品列表页面中的`prod` iter 变量：

```xml
<tr th:each="prod : ${prods}">
    ...
</tr>
```

该`prod`变量仅在标记的范围内可用。特别：

- 它将可用于在该标记中执行的任何其他`th:*`属性，其优先级低于`th:each`(这意味着它们将在`th:each`之后执行)。
- 它将可用于标记的任何 child 元素，例如任何`元素。

Thymeleaf 为您提供了一种使用`th:with`属性声明局部变量而无需迭代的方法，其语法类似于属性 value 赋值：

```xml
<div th:with="firstPer=${persons[0]}">
  <p>
    The name of the first person is <span th:text="${firstPer.name}">Julius Caesar</span>.
  </p>
</div>
```

处理`th:with`时，该`firstPer`变量被创建为局部变量并添加到来自 context 的变量 map 中，因此它可用于 evaluation 以及 context 中声明的任何其他变量，但仅限于包含的范围内``标签。

您可以使用通常的多重赋值语法在同一 time 定义多个变量：

```xml
<div th:with="firstPer=${persons[0]},secondPer=${persons[1]}">
  <p>
    The name of the first person is <span th:text="${firstPer.name}">Julius Caesar</span>.
  </p>
  <p>
    But the name of the second person is
    <span th:text="${secondPer.name}">Marcus Antonius</span>.
  </p>
</div>
```

`th:with`属性允许重用在同一属性中定义的变量：

```xml
<div th:with="company=${user.company + ' Co.'},account=${accounts[company]}">...</div>
```

让我们在 Grocery 的主页上使用它！还记得我们为输出格式化的 date 而编写的 code 吗？

```xml
<p>
  Today is:
  <span th:text="${#calendars.format(today,'dd MMMM yyyy')}">13 february 2011</span>
</p>
```

那么，如果我们希望`"dd MMMM yyyy"`实际上依赖于 locale 呢？对于 example，我们可能希望将以下消息添加到`home_en.properties`：

```java
date.format=MMMM dd'','' yyyy
```

......和我们`home_es.properties`的等价物：

```java
date.format=dd ''de'' MMMM'','' yyyy
```

现在，让我们使用`th:with`将本地化 date 格式转换为变量，然后在`th:text`表达式中使用它：

```xml
<p th:with="df=#{date.format}">
  Today is: <span th:text="${#calendars.format(today,df)}">13 February 2011</span>
</p>
```

那简洁干净。事实上，鉴于`th:with`的`precedence`比`th:text`高，我们可以在`span`标签中解决这个问题：

```xml
<p>
  Today is:
  <span th:with="df=#{date.format}"
        th:text="${#calendars.format(today,df)}">13 February 2011</span>
</p>
```

你可能会想：优先顺序？我们还没有谈过这个！好吧，不要担心，因为这正是下一章的内容。

## 属性优先

在同一个标签中写入多个`th:*`属性会发生什么？例如：

```xml
<ul>
  <li th:each="item : ${items}" th:text="${item.description}">Item description here...</li>
</ul>
```

我们希望在`th:text`之前执行`th:each`属性，以便我们得到我们想要的结果，但是考虑到 HTML/XML 标准没有给写入标签中的属性的 order 赋予任何意义，优先级必须在 order 中的属性本身中建立机制，以确保它将按预期工作。

因此，所有 Thymeleaf 属性都定义了一个数字优先级，它建立了在标记中执行它们的 order。这个 order 是：

| 订购 | 特征                | 属性                                       |
| :--- | :------------------ | :----------------------------------------- |
| 1    | 片段包含            | `th:insert` `th:replace`                   |
| 2    | 片段迭代            | `th:each`                                  |
| 3    | 有条件的 evaluation | `th:if` `th:unless` `th:switch` `th:case`  |
| 4    | 局部变量定义        | `th:object` `th:with`                      |
| 5    | 一般属性修改        | `th:attr` `th:attrprepend` `th:attrappend` |
| 6    | 具体属性修改        | `th:value` `th:href` `th:src` `...`        |
| 7    | 文字(标签正文修改)  | `th:text` `th:utext`                       |
| 8    | 片段规范            | `th:fragment`                              |
| 9    | 片段删除            | `th:remove`                                |

这个优先级机制意味着如果属性位置被反转，上面的迭代片段将给出完全相同的结果(虽然它的可读性稍差)：

```xml
<ul>
  <li th:text="${item.description}" th:each="item : ${items}">Item description here...</li>
</ul>
```

## Comments 和 Blocks

### 标准 HTML/XML comments

标准 HTML/XML comments ``可以在 Thymeleaf 模板中的任何位置使用。这些 comments 中的任何内容都不会由 Thymeleaf 处理，并将逐字复制到结果中：

```xml
<!-- User info follows -->
<div th:text="${...}">
  ...
</div>
```

### Thymeleaf parser-level comment 阻止

Parser-level comment 块是 code，当 Thymeleaf 解析时，它将被简单地从模板中删除。它们看起来像这样：

```java
<!--/* This code will be removed at Thymeleaf parsing time! */-->
```

Thymeleaf 将删除`和`\*/-->`之间的所有内容，因此当模板静态打开时，这些 comment 块也可用于显示 code，知道在 Thymeleaf 处理它时它将被删除：

```xml
<!--/*-->
  <div>
     you can see me only before Thymeleaf processes me!
  </div>
<!--*/-->
```

对于具有大量的表进行原型设计，这可能非常方便，例如：

```xml
<table>
   <tr th:each="x : ${xs}">
     ...
   </tr>
   <!--/*-->
   <tr>
     ...
   </tr>
   <tr>
     ...
   </tr>
   <!--*/-->
</table>
```

### Thymeleaf prototype-only comment 阻止

当模板静态打开(i.e.作为原型)时，Thymeleaf 允许定义标记为 comments 的特殊 comment 块，但在执行模板时 Thymeleaf 认为是正常标记。

```xml
<span>hello!</span>
<!--/*/
  <div th:text="${...}">
    ...
  </div>
/*/-->
<span>goodbye!</span>
```

Thymeleaf 的解析系统将简单地删除`和`/\*/-->`标记，但不删除其内容，因此将取消注释。因此，在执行模板时，Thymeleaf 实际上会看到：

```xml
<span>hello!</span>

  <div th:text="${...}">
    ...
  </div>

<span>goodbye!</span>
```

与 parser-level comment 块一样，此 feature 为 dialect-independent。

### 合成 th:block 标签

Thymeleaf 唯一包含在标准方言中的元素处理器(不是属性)是`th:block`。

`th:block`是一个纯粹的属性容器，允许模板开发人员指定他们想要的任何属性。 Thymeleaf 将执行这些属性，然后简单地使块，但不是它的内容，消失。

所以它可能是有用的，例如，当 creating 每个元素需要多个的迭代表时：

```xml
<table>
  <th:block th:each="user : ${users}">
    <tr>
        <td th:text="${user.login}">...</td>
        <td th:text="${user.name}">...</td>
    </tr>
    <tr>
        <td colspan="2" th:text="${user.address}">...</td>
    </tr>
  </th:block>
</table>
```

当与 prototype-only comment 块结合使用时尤其有用：

```xml
<table>
    <!--/*/ <th:block th:each="user : ${users}"> /*/-->
    <tr>
        <td th:text="${user.login}">...</td>
        <td th:text="${user.name}">...</td>
    </tr>
    <tr>
        <td colspan="2" th:text="${user.address}">...</td>
    </tr>
    <!--/*/ </th:block> /*/-->
</table>
```

注意这个解决方案如何允许模板是有效的 HTML(不需要在`中添加禁止的`块)，并且在浏览器中静态打开时仍然可以正常工作！

## 内联

### 表达内联

虽然标准方言允许我们使用标记属性来完成几乎所有操作，但在某些情况下我们可能更喜欢将表达式直接编写到 HTML 文本中。例如，我们更喜欢写这个：

```xml
<p>Hello, [[${session.user.name}]]!</p>
```

......而不是这个：

```xml
<p>Hello, <span th:text="${session.user.name}">Sebastian</span>!</p>
```

`[[...]]`或`[(...)]`之间的表达式被认为是 Thymeleaf 中的**内联表达式**，在其中我们可以使用任何在`th:text`或`th:utext`属性中也有效的表达式。

请注意，虽然`[[...]]`对应于`th:text`(i.e.结果将是 HTML-escaped)，但`[(...)]`对应于`th:utext`并且不会执行任何 HTML-escaping。所以对于一个变量如`msg = 'This is great!'`，给定这个片段：

```xml
<p>The message is "[(${msg})]"</p>
```

结果将使那些``标签未转义，因此：

```xml
<p>The message is "This is <b>great!</b>"</p>
```

而如果像以下一样逃脱：

```xml
<p>The message is "[[${msg}]]"</p>
```

结果将是 HTML-escaped：

```xml
<p>The message is "This is &lt;b&gt;great!&lt;/b&gt;"</p>
```

请注意，**文本内联在我们的标记中的每个标记的主体中默认为 active** - 而不是标记本身 - 因此我们无需执行任何操作。

#### 内联 vs 自然模板

如果你来自其他模板引擎，其中这种输出文本的方式是常态，你可能会问：为什么我们从一开始就不这样做？它比所有`th:text`属性更少 code！

好吧，在那里要小心，因为尽管你可能会发现内联非常有趣，但是你应该永远记住，当你静态打开它们时，内联表达式将逐字显示在 HTML files 中，因此你可能无法将它们用作设计原型了！

浏览器静态显示 code 片段而不使用内联的区别...

```java
Hello, Sebastian!
```

......并使用它......

```java
Hello, [[${session.user.name}]]!
```

......在设计实用性方面非常清楚。

#### 禁用内联

但是可以禁用此机制，因为实际上可能存在我们想要输出`[[...]]`或`[(...)]` sequences 而不将其内容作为表达式处理的情况。为此，我们将使用`th:inline="none"`：

```xml
<p th:inline="none">A double array looks like this: [[1, 2, 3], [4, 5]]!</p>
```

这将导致：

```xml
<p>A double array looks like this: [[1, 2, 3], [4, 5]]!</p>
```

### 文字内联

文本内联与我们刚刚看到的表达内联功能非常相似，但它实际上增加了更多功能。必须使用`th:inline="text"`显式启用它。

文本内联不仅允许我们使用我们刚刚看到的相同内联表达式，而且实际上处理标签主体就好像它们是在`TEXT`模板模式下处理的模板一样，这允许我们执行 text-based 模板逻辑(不仅仅是输出表达式)。

我们将在下一章中看到有关文本模板模式的更多信息。

### JavaScript 内联

JavaScript 内联允许在`HTML`模板模式下处理的模板中更好地整合 JavaScript ``块。

与文本内联一样，这实际上相当于处理脚本内容，就好像它们是`JAVASCRIPT`模板模式中的模板一样，因此文本模板模式的所有功能(见下一章)都将在眼前。但是，在本节中，我们将重点介绍如何使用它将 Thymeleaf 表达式的输出添加到 JavaScript 块中。

必须使用`th:inline="javascript"`显式启用此模式：

```xml
<script th:inline="javascript">
    ...
    var username = [[${session.user.name}]];
    ...
</script>
```

这将导致：

```xml
<script th:inline="javascript">
    ...
    var username = "Sebastian \"Fruity\" Applejuice";
    ...
</script>
```

上面的 code 中需要注意的两件重要事项：

首先，JavaScript 内联不仅会输出所需的文本，还会使用引号和 JavaScript-escape 其内容将其括起来，以便表达式结果输出为**well-formed JavaScript 文字**。

其次，发生这种情况是因为我们输出`${session.user.name}`表达式为**转义**，i.e。使用 double-bracket 表达式：`[[${session.user.name}]]`。如果相反，我们使用非转义，如：

```xml
<script th:inline="javascript">
    ...
    var username = [(${session.user.name})];
    ...
</script>
```

结果如下：

```xml
<script th:inline="javascript">
    ...
    var username = Sebastian "Fruity" Applejuice;
    ...
</script>
```

...这是一个格式错误的 JavaScript code。但是，如果我们通过附加内联表达式来构建脚本的一部分，那么输出未转义的内容可能就是我们所需要的，因此最好有这个工具。

#### JavaScript 自然模板

所提到的 JavaScript 内联机制的智能远不止仅仅应用 JavaScript-specific 转义并将表达式结果输出为有效\_lite。

对于 example，我们可以在 JavaScript comments 中包装我们的(转义的)内联表达式，如：

```xml
<script th:inline="javascript">
    ...
    var username = /*[[${session.user.name}]]*/ "Gertrud Kiwifruit";
    ...
</script>
```

并且 Thymeleaf 将忽略我们在 comment 之后和分号之前(在这种情况下为`'Gertrud Kiwifruit'`)编写的所有内容，因此执行此操作的结果将与我们不使用包装 comments 时完全相同：

```xml
<script th:inline="javascript">
    ...
    var username = "Sebastian \"Fruity\" Applejuice";
    ...
</script>
```

但要仔细看看原始模板 code：

```xml
<script th:inline="javascript">
    ...
    var username = /*[[${session.user.name}]]*/ "Gertrud Kiwifruit";
    ...
</script>
```

请注意这是**有效的 JavaScript** code。当您以静态方式打开模板文件时(无需在服务器上执行)，它将完美执行。

所以我们这里有一个方法来做**JavaScript 自然模板**！

#### 高级内联 evaluation 和 JavaScript 序列化

关于 JavaScript 内联的一个重要注意事项是，这个表达式 evaluation 是智能的，不仅限于 Strings。 Thymeleaf 将使用以下类型的 objects 正确编写 JavaScript 语法：

- Strings
- Numbers
- 布尔
- 数组
- 集合
- Maps
- Beans(带有 getter 和 setter 方法的 objects)

例如，如果我们有以下 code：

```xml
<script th:inline="javascript">
    ...
    var user = /*[[${session.user}]]*/ null;
    ...
</script>
```

`${session.user}`表达式将评估为`User` object，而 Thymeleaf 将正确地将其转换为 Javascript 语法：

```xml
<script th:inline="javascript">
    ...
    var user = {"age":null,"firstName":"John","lastName":"Apricot",
                "name":"John Apricot","nationality":"Antarctica"};
    ...
</script>
```

完成此 JavaScript 序列化的方法是通过`org.thymeleaf.standard.serializer.IStandardJavaScriptSerializer`接口的 implementation 实现，该接口可以在模板引擎上使用的`StandardDialect`实例上进行配置。

此 JS 序列化机制的默认 implementation 将在 classpath 中查找[Jackson library](https://github.com/FasterXML/jackson)，如果存在，将使用它。如果没有，它将应用 built-in 序列化机制，涵盖大多数场景的需要并产生类似的结果(但不太灵活)。

### CSS 内联

Thymeleaf 还允许在 CSS ``标签中使用内联，例如：

```xml
<style th:inline="css">
  ...
</style>
```

对于 example，假设我们将两个变量设置为两个不同的`String`值：

```java
classname = 'main elems'
align = 'center'
```

我们可以像以下一样使用它们：

```xml
<style th:inline="css">
    .[[${classname}]] {
      text-align: [[${align}]];
    }
</style>
```

结果将是：

```xml
<style th:inline="css">
    .main\ elems {
      text-align: center;
    }
</style>
```

请注意 CSS 内联如何具有一些智能，就像 JavaScript 一样。具体来说，通过像`[[${classname}]]`这样的转义表达式输出的表达式将作为**CSS 标识符**进行转义。这就是为什么我们的`classname = 'main elems'`在上面的 code 片段中变成`main\ elems`的原因。

#### 高级 features：CSS 自然模板等

与之前针对 JavaScript 解释的内容相同，CSS 内联还允许我们的``标记静态和动态地工作，i.e。作为**CSS 自然模板**通过在 comments 中包装内联表达式。看到：

```xml
<style th:inline="css">
    .main\ elems {
      text-align: /*[[${align}]]*/ left;
    }
</style>
```

## 文本模板模式

### 文本语法

三个 Thymeleaf 模板模式被认为是**文本**：`TEXT`，`JAVASCRIPT`和`CSS`。这使它们与标记模板模式区分开来：`HTML`和`XML`。

文本模板模式和标记模式之间的 key 区别在于，在文本模板中没有标签可以以属性的形式插入逻辑，因此我们必须依赖其他机制。

这些机制的第一个也是最基本的是**内联**，我们已经在前一章中详细介绍过了。内联语法是在文本模板模式下输出表达式结果的最简单方法，因此这是文本电子邮件的完美有效模板。

```java
Dear [(${name})],

  Please find attached the results of the report you requested
  with name "[(${report.name})]".

  Sincerely,
    The Reporter.
```

即使没有标签，上面的 example 也是一个完整有效的 Thymeleaf 模板，可以在`TEXT`模板模式下执行。

但是为了包含比仅仅输出表达式更复杂的逻辑，我们需要一个新的 non-tag-based 语法：

```java
[## th:each="item : ${items}"]
  - [(${item})]
[/]
```

这实际上是更详细的浓缩 version：

```java
[#th:block th:each="item : ${items}"]
  - [#th:block th:utext="${item}" /]
[/th:block]
```

请注意这个新语法是如何基于声明为`[#element ...]`而不是``的元素(i.e.可处理标记)。元素像`[#element ...]`一样打开并像`[/element]`一样关闭，并且可以通过使用`/`以几乎等同于 XML 标记的方式最小化 open 元素来声明独立标记：`[#element ... /]`。

标准方言只包含其中一个元素的处理器：already-known `th:block`，虽然我们可以在我们的方言中扩展它，并以通常的方式创建新元素。此外，允许将`th:block`元素(`[#th:block ...] ... [/th:block]`)缩写为空 string(`[## ...] ... [/]`)，因此上面的块实际上等效于：

```java
[## th:each="item : ${items}"]
  - [## th:utext="${item}" /]
[/]
```

并且假定`[## th:utext="${item}" /]`等同于内联非转义表达式，我们可以在 order 中使用它来减少 code。因此，我们最终得到了上面看到的 code 的第一个片段：

```java
[## th:each="item : ${items}"]
  - [(${item})]
[/]
```

请注意，文本语法需要完整的元素平衡(没有未关闭的标记)和引用的属性 - 它比 HTML-style 更多 XML-style。

让我们看一个更完整的`TEXT`模板示例，一个纯文本电子邮件模板：

```java
Dear [(${customer.name})],

This is the list of our products:

[## th:each="prod : ${products}"]
   - [(${prod.name})]. Price: [(${prod.price})] EUR/kg
[/]

Thanks,
  The Thymeleaf Shop
```

执行后，结果可能是这样的：

```java
Dear Mary Ann Blueberry,

This is the list of our products:

   - Apricots. Price: 1.12 EUR/kg
   - Bananas. Price: 1.78 EUR/kg
   - Apples. Price: 0.85 EUR/kg
   - Watermelon. Price: 1.91 EUR/kg

Thanks,
  The Thymeleaf Shop
```

另一个例子是`JAVASCRIPT`模板模式，一个`greeter.js`文件，我们 process 作为文本模板，我们从 HTML 页面调用结果。请注意，这不是 HTML 模板中的``块，而是将`.js`文件作为模板自行处理：

```java
var greeter = function() {

    var username = [[${session.user.name}]];

    [## th:each="salut : ${salutations}"]
      alert([[${salut}]] + " " + username);
    [/]

};
```

执行后，结果可能是这样的：

```java
var greeter = function() {

    var username = "Bertrand \"Crunchy\" Pear";

      alert("Hello" + " " + username);
      alert("Ol\u00E1" + " " + username);
      alert("Hola" + " " + username);

};
```

#### 转义元素属性

在 order 中，为了避免与可能在其他模式中处理的模板部分(e.g. `text` -mode 内联`HTML`模板)进行交互，Thymeleaf 3.0 允许对其文本语法中元素的属性进行转义。所以：

- `TEXT`模板模式中的属性将为 HTML-unescaped。
- `JAVASCRIPT`模板模式中的属性将为 JavaScript-unescaped。
- `CSS`模板模式中的属性将为 CSS-unescaped。

所以这在`TEXT` -mode 模板中完全没问题(注意`>`)：

```java
[## th:if="${120&lt;user.age}"]
     Congratulations!
  [/]
```

当然`<`在真正的文本模板中没有任何意义，但如果我们正在处理一个包含上面的 code 的`th:inline="text"`块的 HTML 模板，那么它是一个很好的 idea，我们想确保我们的浏览器不会使用`静态打开文件作为原型时打开标记的 name。

### 可扩展性

这种语法的一个优点是它和标记一样可扩展。开发人员仍然可以使用自定义元素和属性定义自己的方言，为它们应用前缀(可选)，然后在文本模板模式中使用它们：

```java
[#myorg:dosomething myorg:importantattr="211"]some text[/myorg:dosomething]
```

### Textual prototype-only comment blocks：添加 code

`JAVASCRIPT`和`CSS`模板模式(不适用于`TEXT`)允许在特殊的 comment 语法`/*[+...+]*/`之间包含 code，以便 Thymeleaf 在处理模板时自动取消注释这样的 code：

```java
var x = 23;

/*[+

var msg  = "This is a working application";

+]*/

var f = function() {
    ...
```

将被执行为：

```java
var x = 23;

var msg  = "This is a working application";

var f = function() {
...
```

您可以在这些 comments 中包含表达式，它们将被评估：

```java
var x = 23;

/*[+

var msg  = "Hello, " + [[${session.user.name}]];

+]*/

var f = function() {
...
```

### Textual parser-level comment blocks：删除 code

以类似于 prototype-only comment 块的方式，所有三种文本模板模式(`TEXT`，`JAVASCRIPT`和`CSS`)都可以指示 Thymeleaf 删除特殊`/*[- */`和`/* -]*/`标记之间的 code，如下所示：

```java
var x = 23;

/*[- */

var msg  = "This is shown only when executed statically!";

/* -]*/

var f = function() {
...
```

或者，在`TEXT`模式下：

```java
...
/*[- Note the user is obtained from the session, which must exist -]*/
Welcome [(${session.user.name})]!
...
```

### 自然 JavaScript 和 CSS 模板

如上一章所示，JavaScript 和 CSS 内联提供了在 JavaScript/CSS comments 中包含内联表达式的可能性，如：

```java
...
var username = /*[[${session.user.name}]]*/ "Sebastian Lychee";
...
```

...这是有效的 JavaScript，一旦执行可能看起来像：

```java
...
var username = "John Apricot";
...
```

在 comments 中包含内联表达式的相同技巧实际上可以用于整个文本模式语法：

```java
/*[## th:if="${user.admin}"]*/
     alert('Welcome admin');
  /*[/]*/
```

当模板静态打开时，将显示上述 code 中的警报 - 因为它是 100％有效的 JavaScript - 以及如果用户是管理员，则模板为 run 时也是如此。它相当于：

```java
[## th:if="${user.admin}"]
     alert('Welcome admin');
  [/]
```

...实际上是在模板解析期间转换初始 version 的 code。

但请注意，comments 中的包装元素不会像内联输出表达式那样清除它们所在的 lines(向右找到`;`)。该行为仅为内联输出表达式保留。

因此，Thymeleaf 3.0 允许以自然模板**的形式开发**复杂的 JavaScript 脚本和 CSS 样式表，既可用作原型，也可用作工作模板。

## 我们杂货店的更多页面

现在我们对使用 Thymeleaf 了解很多，我们可以在我们的网站上添加一些新的页面来进行 order management。

请注意，我们将重点关注 HTML code，但如果您想查看相应的控制器，可以查看捆绑的 source code。

### 订单列表

让我们从创建 order 列表页面`/WEB-INF/templates/order/list.html`开始：

```xml
<!DOCTYPE html>

<html xmlns:th="http://www.thymeleaf.org">

  <head>

    <title>Good Thymes Virtual Grocery</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <link rel="stylesheet" type="text/css" media="all"
          href="../../../css/gtvg.css" th:href="@{/css/gtvg.css}" />
  </head>

  <body>

    <h1>Order list</h1>

    <table>
      <tr>
        <th>DATE</th>
        <th>CUSTOMER</th>
        <th>TOTAL</th>
        <th></th>
      </tr>
      <tr th:each="o : ${orders}" th:class="${oStat.odd}? 'odd'">
        <td th:text="${#calendars.format(o.date,'dd/MMM/yyyy')}">13 jan 2011</td>
        <td th:text="${o.customer.name}">Frederic Tomato</td>
        <td th:text="${#aggregates.sum(o.orderLines.{purchasePrice * amount})}">23.32</td>
        <td>
          <a href="details.html" th:href="@{/order/details(orderId=${o.id})}">view</a>
        </td>
      </tr>
    </table>

    <p>
      <a href="../home.html" th:href="@{/}">Return to home</a>
    </p>

  </body>

</html>
```

这里没有什么可以让我们感到惊讶，除了这一点 OGNL 魔法：

```xml
<td th:text="${#aggregates.sum(o.orderLines.{purchasePrice * amount})}">23.32</td>
```

这样做，对于 order 中的每个 order line(`OrderLine` object)，将其`purchasePrice`和`amount`properties(通过调用相应的`getPurchasePrice()`和`getAmount()`方法)相乘，并将结果返回到 numbers 列表中，稍后由`#aggregates.sum(...)` function 汇总。 order 获取 order 总价。

你必须喜欢 OGNL 的力量。

### 订单详情

现在，对于 order 详细信息页面，我们将在其中大量使用星号语法：

```xml
<!DOCTYPE html>

<html xmlns:th="http://www.thymeleaf.org">

  <head>
    <title>Good Thymes Virtual Grocery</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <link rel="stylesheet" type="text/css" media="all"
          href="../../../css/gtvg.css" th:href="@{/css/gtvg.css}" />
  </head>

  <body th:object="${order}">

    <h1>Order details</h1>

    <div>
      <p><b>Code:</b> <span th:text="*{id}">99</span></p>
      <p>
        <b>Date:</b>
        <span th:text="*{#calendars.format(date,'dd MMM yyyy')}">13 jan 2011</span>
      </p>
    </div>

    <h2>Customer</h2>

    <div th:object="*{customer}">
      <p><b>Name:</b> <span th:text="*{name}">Frederic Tomato</span></p>
      <p>
        <b>Since:</b>
        <span th:text="*{#calendars.format(customerSince,'dd MMM yyyy')}">1 jan 2011</span>
      </p>
    </div>

    <h2>Products</h2>

    <table>
      <tr>
        <th>PRODUCT</th>
        <th>AMOUNT</th>
        <th>PURCHASE PRICE</th>
      </tr>
      <tr th:each="ol,row : *{orderLines}" th:class="${row.odd}? 'odd'">
        <td th:text="${ol.product.name}">Strawberries</td>
        <td th:text="${ol.amount}" class="number">3</td>
        <td th:text="${ol.purchasePrice}" class="number">23.32</td>
      </tr>
    </table>

    <div>
      <b>TOTAL:</b>
      <span th:text="*{#aggregates.sum(orderLines.{purchasePrice * amount})}">35.23</span>
    </div>

    <p>
      <a href="list.html" th:href="@{/order/list}">Return to order list</a>
    </p>

  </body>

</html>
```

除了这个嵌套的 object 选择之外，这里没什么新东西：

```xml
<body th:object="${order}">

  ...

  <div th:object="*{customer}">
    <p><b>Name:</b> <span th:text="*{name}">Frederic Tomato</span></p>
    ...
  </div>

  ...
</body>
```

...这使`*{name}`相当于：

```xml
<p><b>Name:</b> <span th:text="${order.customer.name}">Frederic Tomato</span></p>
```

## 更多关于 Configuration

### 模板解析器

对于 Good Thymes Virtual Grocery，我们选择了一个名为`ServletContextTemplateResolver`的`ITemplateResolver` implementation，它允许我们从 Servlet Context 获取模板作为资源。

除了让我们能够通过实现`ITemplateResolver,` Thymeleaf 创建我们自己的模板解析器，包括四个开箱即用的\_implement：

- `org.thymeleaf.templateresolver.ClassLoaderTemplateResolver`，将模板解析为类加载器资源，如：

```java
return Thread.currentThread().getContextClassLoader().getResourceAsStream(template);
```

- `org.thymeleaf.templateresolver.FileTemplateResolver`，它将模板解析为文件系统中的 files，如：

```java
return new FileInputStream(new File(template));
```

- `org.thymeleaf.templateresolver.UrlTemplateResolver`，将模板解析为 URL(甚至是 non-local 个)，如：

```java
return (new URL(template)).openStream();
```

- `org.thymeleaf.templateresolver.StringTemplateResolver`，它直接解析模板，因为`String`被指定为`template`(或模板 name，在这种情况下显然远远超过仅仅 name)：

```java
return new StringReader(templateName);
```

`ITemplateResolver`的所有 pre-bundled implementation 都允许使用相同的 configuration 参数集，其中包括：

- 前缀和后缀(如前所见)：

```java
templateResolver.setPrefix("/WEB-INF/templates/");
templateResolver.setSuffix(".html");
```

- 允许使用与文件名不直接对应的模板名称的模板别名。如果同时存在 suffix/prefix 和别名，则将在 prefix/suffix 之前应用别名：

```java
templateResolver.addTemplateAlias("adminHome","profiles/admin/home");
templateResolver.setTemplateAliases(aliasesMap);
```

- 读取模板时要应用的编码：

```java
templateResolver.setEncoding("UTF-8");
```

- 要使用的模板模式：

```java
// Default is HTML
templateResolver.setTemplateMode("XML");
```

- 模板缓存的默认模式，以及用于定义特定模板是否可缓存的模式：

```java
// Default is true
templateResolver.setCacheable(false);
templateResolver.getCacheablePatternSpec().addPattern("/users/*");
```

- 解析模板缓存条目的 TTL(以毫秒为单位)源自此模板解析程序。如果未设置，从缓存中删除条目的唯一方法是超过缓存最大大小(将删除最旧的条目)。

```java
// Default is no TTL (only cache size exceeded would remove entries)
templateResolver.setCacheTTLMs(60000L);
```

> Thymeleaf Spring integration 包提供`SpringResourceTemplateResolver` implementation，它使用所有 Spring 基础结构来访问和读取 applications 中的资源，这是 Spring-enabled applications 中推荐的 implementation。

#### 链接模板解析器

此外，模板引擎可以指定多个模板解析器，在这种情况下，可以在它们之间建立 order 以进行模板解析，这样，如果第一个无法解析模板，则会询问第二个，依此类推：

```java
ClassLoaderTemplateResolver classLoaderTemplateResolver = new ClassLoaderTemplateResolver();
classLoaderTemplateResolver.setOrder(Integer.valueOf(1));

ServletContextTemplateResolver servletContextTemplateResolver =
        new ServletContextTemplateResolver(servletContext);
servletContextTemplateResolver.setOrder(Integer.valueOf(2));

templateEngine.addTemplateResolver(classLoaderTemplateResolver);
templateEngine.addTemplateResolver(servletContextTemplateResolver);
```

当应用多个模板解析器时，建议为每个模板解析器指定模式，以便 Thymeleaf 可以快速丢弃那些不打算解析模板的模板解析器，从而增强 performance。这样做不是必要条件，而是建议：

```java
ClassLoaderTemplateResolver classLoaderTemplateResolver = new ClassLoaderTemplateResolver();
classLoaderTemplateResolver.setOrder(Integer.valueOf(1));
// This classloader will not be even asked for any templates not matching these patterns
classLoaderTemplateResolver.getResolvablePatternSpec().addPattern("/layout/*.html");
classLoaderTemplateResolver.getResolvablePatternSpec().addPattern("/menu/*.html");

ServletContextTemplateResolver servletContextTemplateResolver =
        new ServletContextTemplateResolver(servletContext);
servletContextTemplateResolver.setOrder(Integer.valueOf(2));
```

如果未指定这些可解析的模式，我们将依赖于我们正在使用的每个`ITemplateResolver` \_implement 的特定功能。请注意，并非所有 implementations 都可以在解析之前确定模板的存在，因此可以始终将模板视为可解析和 break 解析链(不允许其他解析器检查相同的模板)，但随后无法阅读真实资源。

核心 Thymeleaf 中包含的所有`ITemplateResolver` \_implement 包括一种机制，它允许我们在解析可解析之前让解析器真正检查资源是否存在。它是`checkExistence` flag，其工作方式如下：

```java
ClassLoaderTemplateResolver classLoaderTemplateResolver = new ClassLoaderTemplateResolver();
classLoaderTemplateResolver.setOrder(Integer.valueOf(1));
classLoaderTempalteResolver.setCheckExistence(true);
```

此`checkExistence` flag 强制解析器在解析阶段执行资源存在的实际检查(如果存在检查返回 false，则调用链中的后续解析器)。虽然这在每种情况下听起来都不错，但在大多数情况下，这意味着 Double 访问资源本身(一次用于检查存在，另一次用于读取它)，并且在某些情况下可能是 performance 问题，e.g. remote URL-based 模板资源 - 一个潜在的 performance 问题，无论如何都可以通过使用模板缓存大大减轻(在这种情况下，模板只会在访问它们的第一个 time 时解析)。

### 消息解析器

我们没有为 Grocery application 明确指定 Message Resolver implementation，正如前面所解释的那样，这意味着所使用的 implementation 是一个`org.thymeleaf.messageresolver.StandardMessageResolver` object。

`StandardMessageResolver`是`IMessageResolver`接口的标准 implementation，但是如果我们想要的话，我们可以自己创建，以适应我们的 application 的特定需求。

> Thymeleaf Spring integration 包默认提供`IMessageResolver` implementation，它使用标准 Spring 方式检索外化消息，使用 Spring Application Context 声明的`MessageSource` beans。

#### 标准消息解析器

那么`StandardMessageResolver`如何查找特定模板中请求的消息？

如果模板 name 是`home`并且它位于`/WEB-INF/templates/home.html`，并且请求的 locale 是`gl_ES`，那么此解析器将在以下 files 中查找消息，在此 order 中：

- `/WEB-INF/templates/home_gl_ES.properties`
- `/WEB-INF/templates/home_gl.properties`
- `/WEB-INF/templates/home.properties`

有关完整消息解析机制如何工作的更多详细信息，请参阅`StandardMessageResolver` class 的 JavaDoc 文档。

#### 配置消息解析器

如果我们想要向模板引擎添加消息解析器(或更多)，该怎么办？简单：

```java
// For setting only one
templateEngine.setMessageResolver(messageResolver);

// For setting more than one
templateEngine.addMessageResolver(messageResolver);
```

为什么我们想拥有多个消息解析器？出于与模板解析器相同的原因：订购消息解析器，如果第一个消息解析器无法解析特定消息，则会询问第二个，然后是第三个，等等。

### 转换服务

允许我们通过 double-brace 语法(`${{...}}`)执行数据转换和格式化操作的转换服务实际上是标准方言的 feature，而不是 Thymeleaf 模板引擎本身。

因此，配置它的方法是将`IStandardConversionService`接口的自定义 implementation 直接设置到正在配置到模板引擎中的`StandardDialect`实例中。喜欢：

```java
IStandardConversionService customConversionService = ...

StandardDialect dialect = new StandardDialect();
dialect.setConversionService(customConversionService);

templateEngine.setDialect(dialect);
```

> 请注意，thymeleaf-spring3 和 thymeleaf-spring4 包中包含`SpringStandardDialect`，并且此方言已经 pre-configured，实现了`IStandardConversionService`，它将 Spring 自己的转换服务基础结构集成到 Thymeleaf 中。

### Logging

Thymeleaf 非常关注 logging，并且总是试图通过其 logging 界面提供最大量的有用信息。

使用的 logging library 是`slf4j,`，它实际上充当了我们可能想要在 application(example，`log4j`)中使用的 logging implementation 的 bridge。

Thymeleaf classes 将 log `TRACE`，`DEBUG`和`INFO` -level 信息，取决于我们想要的 level 细节，除了一般 logging 之外，它将使用与 TemplateEngine class 相关联的三个特殊 logger，我们可以为不同的目的单独配置：

- `org.thymeleaf.TemplateEngine.CONFIG`将在初始化期间输出 library 的详细 configuration。
- `org.thymeleaf.TemplateEngine.TIMER`将输出有关 time 每个模板所用 time 的数量的信息(对基准测试很有用！)
- `org.thymeleaf.TemplateEngine.cache`是一组 loggers 的前缀，用于输出有关缓存的特定信息。虽然缓存 loggers 的名称可由用户配置，因此可能会更改，但默认情况下它们是：
- `org.thymeleaf.TemplateEngine.cache.TEMPLATE_CACHE`
- `org.thymeleaf.TemplateEngine.cache.EXPRESSION_CACHE`

使用`log4j`的 Thymeleaf 的 logging 基础结构的 example configuration 可以是：

```java
log4j.logger.org.thymeleaf=DEBUG
log4j.logger.org.thymeleaf.TemplateEngine.CONFIG=TRACE
log4j.logger.org.thymeleaf.TemplateEngine.TIMER=TRACE
log4j.logger.org.thymeleaf.TemplateEngine.cache.TEMPLATE_CACHE=TRACE
```

## 模板缓存

Thymeleaf 的工作得益于一组解析器 - 用于标记和文本 - 将模板解析为 events 的 sequences(开放标记，文本，关闭标记，comment，etc.)和一系列处理器 - 每种类型的行为都需要应用一个 - 修改 order 中解析的 event 序列模板，通过将原始模板与我们的数据相结合来创建我们期望的结果。

它还包括 - 默认情况下 - 缓存 stores 解析模板;在处理模板 files 之前读取和解析的 events 序列。这在使用 web application 时特别有用，并基于以下概念构建：

- Input/Output 几乎总是任何 application 中最慢的部分。相比之下，In-memory 处理非常快。
- 克隆现有的 in-memory event 序列总是比读取模板文件，解析它并为其创建新的 event 序列要快得多。
- Web applications 通常只有几十个模板。
- 模板 files 是 small-to-medium 大小，并且在 application 为 running 时不会修改它们。

这一切都导致 idea 缓存 web application 中最常用的模板是可行的而不浪费大量的 memory，并且它将节省大量的 time，这些 time 将花费在一小组 files 上的 input/output 操作上，事实上，永远不会改变。

我们如何控制这个缓存？首先，我们之前已经了解到，我们可以在模板解析器中启用或禁用它，甚至只对特定模板执行操作：

```java
// Default is true
templateResolver.setCacheable(false);
templateResolver.getCacheablePatternSpec().addPattern("/users/*");
```

此外，我们可以通过建立我们自己的 Cache Manager object 来修改其 configuration，它可以是默认`StandardCacheManager` implementation 的实例：

```java
// Default is 200
StandardCacheManager cacheManager = new StandardCacheManager();
cacheManager.setTemplateCacheMaxSize(100);
...
templateEngine.setCacheManager(cacheManager);
```

有关配置缓存的更多信息，请参阅`org.thymeleaf.cache.StandardCacheManager`的 javadoc API。

可以从模板缓存中手动删除条目：

```java
// Clear the cache completely
templateEngine.clearTemplateCache();

// Clear a specific template from the cache
templateEngine.clearTemplateCacheFor("/users/userList");
```

## 解耦模板逻辑

### 解耦逻辑：概念

到目前为止，我们已经为我们的 Grocery Store 工作，模板以通常的方式完成，逻辑以属性的形式插入到我们的模板中。

但是 Thymeleaf 还允许我们将模板标记与其逻辑完全分离，允许在`HTML`和`XML`模板模式中创建**完全 logic-less 标记模板**。

主要的 idea 是模板逻辑将在一个单独的逻辑文件中定义(更确切地说是一个逻辑资源，因为它不需要是一个文件)。默认情况下，该逻辑资源将是与模板文件位于同一位置(e.g. 文件夹)的附加文件，具有相同的 name 但扩展名为`.th.xml`：

```java
/templates
+->/home.html
+->/home.th.xml
```

所以`home.html`文件可以完全 logic-less。它可能看起来像这样：

```xml
<!DOCTYPE html>
<html>
  <body>
    <table id="usersTable">
      <tr>
        <td class="username">Jeremy Grapefruit</td>
        <td class="usertype">Normal User</td>
      </tr>
      <tr>
        <td class="username">Alice Watermelon</td>
        <td class="usertype">Administrator</td>
      </tr>
    </table>
  </body>
</html>
```

那里绝对没有 Thymeleaf code。这是一个模板文件，没有 Thymeleaf 或模板知识的设计师可以创建，编辑 and/or 理解。或者由某些外部系统提供的 HTML 片段，根本没有 Thymeleaf 挂钩。

现在让我们通过 creating 我们的附加`home.th.xml`文件将`home.html`模板转换为 Thymeleaf 模板：

```xml
<?xml version="1.0"?>
<thlogic>
  <attr sel="#usersTable" th:remove="all-but-first">
    <attr sel="/tr[0]" th:each="user : ${users}">
      <attr sel="td.username" th:text="${user.name}" />
      <attr sel="td.usertype" th:text="#{|user.type.${user.type}|}" />
    </attr>
  </attr>
</thlogic>
```

在这里我们可以在`thlogic`块中看到很多`标签。这些`标签通过其`sel`属性选择的原始模板的节点上执行属性注入，这些属性包含 Thymeleaf 标记选择器(实际上是 AttoParser 标记选择器)。

另请注意，可以嵌套``标记，以便附加其 selectors。对于 example，上面的`sel="/tr[0]"`将被处理为`sel="#usersTable/tr[0]"`。并且用户 name `的选择器将被处理为`sel="#usersTable/tr[0]//td.username"`。

所以一旦合并，上面看到的两个 files 将与：

```xml
<!DOCTYPE html>
<html>
  <body>
    <table id="usersTable" th:remove="all-but-first">
      <tr th:each="user : ${users}">
        <td class="username" th:text="${user.name}">Jeremy Grapefruit</td>
        <td class="usertype" th:text="#{|user.type.${user.type}|}">Normal User</td>
      </tr>
      <tr>
        <td class="username">Alice Watermelon</td>
        <td class="usertype">Administrator</td>
      </tr>
    </table>
  </body>
</html>
```

这看起来更熟悉，并且确实比创建两个单独的 files 更简洁。但是，解耦模板的优势在于我们可以为我们的模板提供完全独立于 Thymeleaf 的独立性，因此从设计角度来看，它具有更好的可维护性。

当然，仍然需要设计师或开发人员之间的一些 contracts - e.g. 用户``需要`id="usersTable"` - 这一事实，但在许多情况下，pure-HTML 模板将是设计和开发团队之间更好的通信工件。

### 配置解耦模板

#### 启用解耦模板

默认情况下，每个模板都不会出现解耦逻辑。相反，配置的模板解析器(`ITemplateResolver`的实现)将需要使用解耦逻辑专门标记它们解析的模板。

除了`StringTemplateResolver`(不允许解耦逻辑)之外，`ITemplateResolver`的所有其他 out-of-the-box \_\_mplement 将提供一个名为`useDecoupledLogic`的 flag，它将标记由该解析器解析的所有模板，因为它可能使其全部或部分逻辑存在于单独的资源中：

```java
final ServletContextTemplateResolver templateResolver =
        new ServletContextTemplateResolver(servletContext);
...
templateResolver.setUseDecoupledLogic(true);
```

#### 混合耦合和解耦逻辑

启用时，解耦模板逻辑不是必需的。启用后，这意味着引擎将查找包含解耦逻辑的资源，解析并将其与原始模板(如果存在)合并。如果解耦的逻辑资源不存在，则不会引发错误。

此外，在同一模板中，我们可以混合耦合和解耦逻辑，例如通过在原始模板文件中添加一些 Thymeleaf 属性，但将其他属性留给单独的解耦逻辑文件。最常见的情况是使用 new(in v3.0)`th:ref`属性。

### th:ref 属性

`th:ref`只是一个标记属性。它从处理的角度来看没有做任何事情，只是在处理模板时就消失了，但它的用处在于它充当了标记参考 i.e。它可以通过 name 从标记选择器中解析，就像标记 name 或片段(`th:fragment`)一样。

所以，如果我们有一个选择器，如：

```xml
<attr sel="whatever" .../>
```

这将 match：

- 任何``标签。
- 任何带有`th:fragment="whatever"`属性的标签。
- 任何带有`th:ref="whatever"`属性的标签。

`th:ref`对例如使用 pure-HTML `id`属性有什么优势？仅仅是因为我们可能不希望在标签中添加如此多的`id`和`class`属性来充当逻辑锚，这可能最终会污染我们的输出。

从同样的意义上说，`th:ref`的缺点是什么？好吧，显然我们要在模板中添加一些 Thymeleaf 逻辑(“逻辑”)。

请注意`th:ref`属性**的这种适用性不仅适用于解耦逻辑模板 files**：它在其他类型的场景中也是如此，例如片段表达式(`~{...}`)。

### 分离模板的性能影响

影响非常小。当一个已解析的模板被标记为使用解耦逻辑并且它没有被缓存时，模板逻辑资源将首先被解析，解析并处理成一系列指令 in-memory：基本上是要注入每个标记选择器的属性列表。

但这是唯一需要的额外 step，因为在此之后，真正的模板将被解析，并且在解析时，这些属性将由解析器本身注入 on-the-fly，这要归功于 AttoParser 中节点选择的高级功能。因此，解析后的节点将从解析器中出来，就好像它们将注入的属性写入原始模板文件中一样。

这个的最大优点是什么？将模板配置为高速缓存时，它将被缓存，其中包含已注入的属性。因此，一旦缓存模板使用解耦模板进行缓存，其开销绝对为零。

### 解耦逻辑的分辨率

Thymeleaf 解析对应于每个模板的解耦逻辑资源的方式可由用户配置。它由一个扩展点`org.thymeleaf.templateparser.markup.decoupled.IDecoupledTemplateLogicResolver`确定，为其提供了默认的 implementation：`StandardDecoupledTemplateLogicResolver`。

这个标准 Implementation 做了什么？

- 首先，它将`prefix`和`suffix`应用于模板资源的基本 name(通过其`ITemplateResource#getBaseName()`方法获得)。前缀和后缀都可以配置，默认情况下，前缀为空，后缀为`.th.xml`。
- 其次，它要求模板资源通过其`ITemplateResource#relative(String relativeLocation)`方法解析具有计算 name 的相对资源。

要使用的`IDecoupledTemplateLogicResolver`的具体\_impleration 可以在`TemplateEngine`轻松配置：

```java
final StandardDecoupledTemplateLogicResolver decoupledresolver =
        new StandardDecoupledTemplateLogicResolver();
decoupledResolver.setPrefix("../viewlogic/");
...
templateEngine.setDecoupledTemplateLogicResolver(decoupledResolver);
```

## 附录 A：Expression Basic Objects

一些 objects 和变量 maps 始终可以调用。我们来看看他们：

#### 基础 objects

- **#ctx**：context object。 `org.thymeleaf.context.IContext`或`org.thymeleaf.context.IWebContext`的 implementation 实现取决于我们的环境(独立或 web)。

注意`#vars`和`#root`是同一 object 的同义词，但建议使用`#ctx`。

```java
/*
 * ======================================================================
 * See javadoc API for class org.thymeleaf.context.IContext
 * ======================================================================
 */

${#ctx.locale}
${#ctx.variableNames}

/*
 * ======================================================================
 * See javadoc API for class org.thymeleaf.context.IWebContext
 * ======================================================================
 */

${#ctx.request}
${#ctx.response}
${#ctx.session}
${#ctx.servletContext}
```

- **## locale**：直接访问与当前请求关联的`java.util.Locale`。

```java
${#locale}
```

#### Web context 命名空间用于 request/session 属性等。

在 web 环境中使用 Thymeleaf 时，我们可以使用一系列快捷方式来访问请求参数，session 属性和 application 属性：

> 请注意，这些不是 context objects，但 maps 作为变量添加到 context，因此我们在没有`#`的情况下访问它们。在某种程度上，它们充当命名空间。

- **param**：用于检索请求参数。 `${param.foo}`是带有`foo`请求参数值的`String[]`，因此`${param.foo[0]}`通常用于获取第一个 value。

```java
/*
 * ============================================================================
 * See javadoc API for class org.thymeleaf.context.WebRequestParamsVariablesMap
 * ============================================================================
 */

${param.foo}              // Retrieves a String[] with the values of request parameter 'foo'
${param.size()}
${param.isEmpty()}
${param.containsKey('foo')}
...
```

- **session**：用于检索 session 属性。

```java
/*
 * ======================================================================
 * See javadoc API for class org.thymeleaf.context.WebSessionVariablesMap
 * ======================================================================
 */

${session.foo}                 // Retrieves the session atttribute 'foo'
${session.size()}
${session.isEmpty()}
${session.containsKey('foo')}
...
```

- **application**：用于检索 application/servlet context 属性。

```java
/*
 * =============================================================================
 * See javadoc API for class org.thymeleaf.context.WebServletContextVariablesMap
 * =============================================================================
 */

${application.foo}              // Retrieves the ServletContext atttribute 'foo'
${application.size()}
${application.isEmpty()}
${application.containsKey('foo')}
...
```

注意**不需要为访问请求属性**(而不是请求参数)指定名称空间，因为所有请求属性都会自动添加到 context 中作为 context 根目录中的变量：

```java
${myRequestAttribute}
```

#### Web context objects

在 web 环境中，还可以直接访问以下 objects(注意这些是 objects，而不是 maps/namespaces)：

- **#request**：直接访问与当前请求关联的`javax.servlet.http.HttpServletRequest` object。

```java
${#request.getAttribute('foo')}
${#request.getParameter('foo')}
${#request.getContextPath()}
${#request.getRequestName()}
...
```

- **## session**：直接访问与当前请求关联的`javax.servlet.http.HttpSession` object。

```java
${#session.getAttribute('foo')}
${#session.id}
${#session.lastAccessedTime}
...
```

- **#servletContext**：直接访问与当前请求关联的`javax.servlet.ServletContext` object。

```java
${#servletContext.getAttribute('foo')}
${#servletContext.contextPath}
...
```

## 附录 B：表达式实用程序 Objects

#### 执行信息

- **#execInfo**：expression object 提供有关在 Thymeleaf 标准表达式中处理的模板的有用信息。

```java
/*
 * ======================================================================
 * See javadoc API for class org.thymeleaf.expression.ExecutionInfo
 * ======================================================================
 */

/*
 * Return the name and mode of the 'leaf' template. This means the template
 * from where the events being processed were parsed. So if this piece of
 * code is not in the root template "A" but on a fragment being inserted
 * into "A" from another template called "B", this will return "B" as a
 * name, and B's mode as template mode.
 */
${#execInfo.templateName}
${#execInfo.templateMode}

/*
 * Return the name and mode of the 'root' template. This means the template
 * that the template engine was originally asked to process. So if this
 * piece of code is not in the root template "A" but on a fragment being
 * inserted into "A" from another template called "B", this will still
 * return "A" and A's template mode.
 */
${#execInfo.processedTemplateName}
${#execInfo.processedTemplateMode}

/*
 * Return the stacks (actually, List<String> or List<TemplateMode>) of
 * templates being processed. The first element will be the
 * 'processedTemplate' (the root one), the last one will be the 'leaf'
 * template, and in the middle all the fragments inserted in nested
 * manner to reach the leaf from the root will appear.
 */
${#execInfo.templateNames}
${#execInfo.templateModes}

/*
 * Return the stack of templates being processed similarly (and in the
 * same order) to 'templateNames' and 'templateModes', but returning
 * a List<TemplateData> with the full template metadata.
 */
${#execInfo.templateStack}
```

#### 消息

- **#messages**：用于在变量表达式中获取外部化消息的实用程序方法，与使用`#{...}`语法获取它们的方式相同。

```java
/*
 * ======================================================================
 * See javadoc API for class org.thymeleaf.expression.Messages
 * ======================================================================
 */

/*
 * Obtain externalized messages. Can receive a single key, a key plus arguments,
 * or an array/list/set of keys (in which case it will return an array/list/set of
 * externalized messages).
 * If a message is not found, a default message (like '??msgKey??') is returned.
 */
${#messages.msg('msgKey')}
${#messages.msg('msgKey', param1)}
${#messages.msg('msgKey', param1, param2)}
${#messages.msg('msgKey', param1, param2, param3)}
${#messages.msgWithParams('msgKey', new Object[] {param1, param2, param3, param4})}
${#messages.arrayMsg(messageKeyArray)}
${#messages.listMsg(messageKeyList)}
${#messages.setMsg(messageKeySet)}

/*
 * Obtain externalized messages or null. Null is returned instead of a default
 * message if a message for the specified key is not found.
 */
${#messages.msgOrNull('msgKey')}
${#messages.msgOrNull('msgKey', param1)}
${#messages.msgOrNull('msgKey', param1, param2)}
${#messages.msgOrNull('msgKey', param1, param2, param3)}
${#messages.msgOrNullWithParams('msgKey', new Object[] {param1, param2, param3, param4})}
${#messages.arrayMsgOrNull(messageKeyArray)}
${#messages.listMsgOrNull(messageKeyList)}
${#messages.setMsgOrNull(messageKeySet)}
```

#### URIs/URLs

- **#uris**：实用程序 object 用于在 Thymeleaf 标准表达式中执行 URI/URL 操作(尤其是 escaping/unescaping)。

```java
/*
 * ======================================================================
 * See javadoc API for class org.thymeleaf.expression.Uris
 * ======================================================================
 */

/*
 * Escape/Unescape as a URI/URL path
 */
${#uris.escapePath(uri)}
${#uris.escapePath(uri, encoding)}
${#uris.unescapePath(uri)}
${#uris.unescapePath(uri, encoding)}

/*
 * Escape/Unescape as a URI/URL path segment (between '/' symbols)
 */
${#uris.escapePathSegment(uri)}
${#uris.escapePathSegment(uri, encoding)}
${#uris.unescapePathSegment(uri)}
${#uris.unescapePathSegment(uri, encoding)}

/*
 * Escape/Unescape as a Fragment Identifier (#frag)
 */
${#uris.escapeFragmentId(uri)}
${#uris.escapeFragmentId(uri, encoding)}
${#uris.unescapeFragmentId(uri)}
${#uris.unescapeFragmentId(uri, encoding)}

/*
 * Escape/Unescape as a Query Parameter (?var=value)
 */
${#uris.escapeQueryParam(uri)}
${#uris.escapeQueryParam(uri, encoding)}
${#uris.unescapeQueryParam(uri)}
${#uris.unescapeQueryParam(uri, encoding)}
```

#### 转换

- **#reversions**：实用程序 object，允许在模板的任何位置执行转换服务：

```java
/*
 * ======================================================================
 * See javadoc API for class org.thymeleaf.expression.Conversions
 * ======================================================================
 */

/*
 * Execute the desired conversion of the 'object' value into the
 * specified class.
 */
${#conversions.convert(object, 'java.util.TimeZone')}
${#conversions.convert(object, targetClass)}
```

#### 日期

- **#dates**：`java.util.Date` objects 的实用方法：

```java
/*
 * ======================================================================
 * See javadoc API for class org.thymeleaf.expression.Dates
 * ======================================================================
 */

/*
 * Format date with the standard locale format
 * Also works with arrays, lists or sets
 */
${#dates.format(date)}
${#dates.arrayFormat(datesArray)}
${#dates.listFormat(datesList)}
${#dates.setFormat(datesSet)}

/*
 * Format date with the ISO8601 format
 * Also works with arrays, lists or sets
 */
${#dates.formatISO(date)}
${#dates.arrayFormatISO(datesArray)}
${#dates.listFormatISO(datesList)}
${#dates.setFormatISO(datesSet)}

/*
 * Format date with the specified pattern
 * Also works with arrays, lists or sets
 */
${#dates.format(date, 'dd/MMM/yyyy HH:mm')}
${#dates.arrayFormat(datesArray, 'dd/MMM/yyyy HH:mm')}
${#dates.listFormat(datesList, 'dd/MMM/yyyy HH:mm')}
${#dates.setFormat(datesSet, 'dd/MMM/yyyy HH:mm')}

/*
 * Obtain date properties
 * Also works with arrays, lists or sets
 */
${#dates.day(date)}                    // also arrayDay(...), listDay(...), etc.
${#dates.month(date)}                  // also arrayMonth(...), listMonth(...), etc.
${#dates.monthName(date)}              // also arrayMonthName(...), listMonthName(...), etc.
${#dates.monthNameShort(date)}         // also arrayMonthNameShort(...), listMonthNameShort(...), etc.
${#dates.year(date)}                   // also arrayYear(...), listYear(...), etc.
${#dates.dayOfWeek(date)}              // also arrayDayOfWeek(...), listDayOfWeek(...), etc.
${#dates.dayOfWeekName(date)}          // also arrayDayOfWeekName(...), listDayOfWeekName(...), etc.
${#dates.dayOfWeekNameShort(date)}     // also arrayDayOfWeekNameShort(...), listDayOfWeekNameShort(...), etc.
${#dates.hour(date)}                   // also arrayHour(...), listHour(...), etc.
${#dates.minute(date)}                 // also arrayMinute(...), listMinute(...), etc.
${#dates.second(date)}                 // also arraySecond(...), listSecond(...), etc.
${#dates.millisecond(date)}            // also arrayMillisecond(...), listMillisecond(...), etc.

/*
 * Create date (java.util.Date) objects from its components
 */
${#dates.create(year,month,day)}
${#dates.create(year,month,day,hour,minute)}
${#dates.create(year,month,day,hour,minute,second)}
${#dates.create(year,month,day,hour,minute,second,millisecond)}

/*
 * Create a date (java.util.Date) object for the current date and time
 */
${#dates.createNow()}

${#dates.createNowForTimeZone()}

/*
 * Create a date (java.util.Date) object for the current date (time set to 00:00)
 */
${#dates.createToday()}

${#dates.createTodayForTimeZone()}
```

#### 日历

- **#calendars**：类似于`#dates`，但是对于`java.util.Calendar` objects：

```java
/*
 * ======================================================================
 * See javadoc API for class org.thymeleaf.expression.Calendars
 * ======================================================================
 */

/*
 * Format calendar with the standard locale format
 * Also works with arrays, lists or sets
 */
${#calendars.format(cal)}
${#calendars.arrayFormat(calArray)}
${#calendars.listFormat(calList)}
${#calendars.setFormat(calSet)}

/*
 * Format calendar with the ISO8601 format
 * Also works with arrays, lists or sets
 */
${#calendars.formatISO(cal)}
${#calendars.arrayFormatISO(calArray)}
${#calendars.listFormatISO(calList)}
${#calendars.setFormatISO(calSet)}

/*
 * Format calendar with the specified pattern
 * Also works with arrays, lists or sets
 */
${#calendars.format(cal, 'dd/MMM/yyyy HH:mm')}
${#calendars.arrayFormat(calArray, 'dd/MMM/yyyy HH:mm')}
${#calendars.listFormat(calList, 'dd/MMM/yyyy HH:mm')}
${#calendars.setFormat(calSet, 'dd/MMM/yyyy HH:mm')}

/*
 * Obtain calendar properties
 * Also works with arrays, lists or sets
 */
${#calendars.day(date)}                // also arrayDay(...), listDay(...), etc.
${#calendars.month(date)}              // also arrayMonth(...), listMonth(...), etc.
${#calendars.monthName(date)}          // also arrayMonthName(...), listMonthName(...), etc.
${#calendars.monthNameShort(date)}     // also arrayMonthNameShort(...), listMonthNameShort(...), etc.
${#calendars.year(date)}               // also arrayYear(...), listYear(...), etc.
${#calendars.dayOfWeek(date)}          // also arrayDayOfWeek(...), listDayOfWeek(...), etc.
${#calendars.dayOfWeekName(date)}      // also arrayDayOfWeekName(...), listDayOfWeekName(...), etc.
${#calendars.dayOfWeekNameShort(date)} // also arrayDayOfWeekNameShort(...), listDayOfWeekNameShort(...), etc.
${#calendars.hour(date)}               // also arrayHour(...), listHour(...), etc.
${#calendars.minute(date)}             // also arrayMinute(...), listMinute(...), etc.
${#calendars.second(date)}             // also arraySecond(...), listSecond(...), etc.
${#calendars.millisecond(date)}        // also arrayMillisecond(...), listMillisecond(...), etc.

/*
 * Create calendar (java.util.Calendar) objects from its components
 */
${#calendars.create(year,month,day)}
${#calendars.create(year,month,day,hour,minute)}
${#calendars.create(year,month,day,hour,minute,second)}
${#calendars.create(year,month,day,hour,minute,second,millisecond)}

${#calendars.createForTimeZone(year,month,day,timeZone)}
${#calendars.createForTimeZone(year,month,day,hour,minute,timeZone)}
${#calendars.createForTimeZone(year,month,day,hour,minute,second,timeZone)}
${#calendars.createForTimeZone(year,month,day,hour,minute,second,millisecond,timeZone)}

/*
 * Create a calendar (java.util.Calendar) object for the current date and time
 */
${#calendars.createNow()}

${#calendars.createNowForTimeZone()}

/*
 * Create a calendar (java.util.Calendar) object for the current date (time set to 00:00)
 */
${#calendars.createToday()}

${#calendars.createTodayForTimeZone()}
```

#### Numbers

- **## numbers**：数字 objects 的实用程序方法：

```java
/*
 * ======================================================================
 * See javadoc API for class org.thymeleaf.expression.Numbers
 * ======================================================================
 */

/*
 * ==========================
 * Formatting integer numbers
 * ==========================
 */

/*
 * Set minimum integer digits.
 * Also works with arrays, lists or sets
 */
${#numbers.formatInteger(num,3)}
${#numbers.arrayFormatInteger(numArray,3)}
${#numbers.listFormatInteger(numList,3)}
${#numbers.setFormatInteger(numSet,3)}

/*
 * Set minimum integer digits and thousands separator:
 * 'POINT', 'COMMA', 'WHITESPACE', 'NONE' or 'DEFAULT' (by locale).
 * Also works with arrays, lists or sets
 */
${#numbers.formatInteger(num,3,'POINT')}
${#numbers.arrayFormatInteger(numArray,3,'POINT')}
${#numbers.listFormatInteger(numList,3,'POINT')}
${#numbers.setFormatInteger(numSet,3,'POINT')}

/*
 * ==========================
 * Formatting decimal numbers
 * ==========================
 */

/*
 * Set minimum integer digits and (exact) decimal digits.
 * Also works with arrays, lists or sets
 */
${#numbers.formatDecimal(num,3,2)}
${#numbers.arrayFormatDecimal(numArray,3,2)}
${#numbers.listFormatDecimal(numList,3,2)}
${#numbers.setFormatDecimal(numSet,3,2)}

/*
 * Set minimum integer digits and (exact) decimal digits, and also decimal separator.
 * Also works with arrays, lists or sets
 */
${#numbers.formatDecimal(num,3,2,'COMMA')}
${#numbers.arrayFormatDecimal(numArray,3,2,'COMMA')}
${#numbers.listFormatDecimal(numList,3,2,'COMMA')}
${#numbers.setFormatDecimal(numSet,3,2,'COMMA')}

/*
 * Set minimum integer digits and (exact) decimal digits, and also thousands and
 * decimal separator.
 * Also works with arrays, lists or sets
 */
${#numbers.formatDecimal(num,3,'POINT',2,'COMMA')}
${#numbers.arrayFormatDecimal(numArray,3,'POINT',2,'COMMA')}
${#numbers.listFormatDecimal(numList,3,'POINT',2,'COMMA')}
${#numbers.setFormatDecimal(numSet,3,'POINT',2,'COMMA')}

/*
 * =====================
 * Formatting currencies
 * =====================
 */

${#numbers.formatCurrency(num)}
${#numbers.arrayFormatCurrency(numArray)}
${#numbers.listFormatCurrency(numList)}
${#numbers.setFormatCurrency(numSet)}

/*
 * ======================
 * Formatting percentages
 * ======================
 */

${#numbers.formatPercent(num)}
${#numbers.arrayFormatPercent(numArray)}
${#numbers.listFormatPercent(numList)}
${#numbers.setFormatPercent(numSet)}

/*
 * Set minimum integer digits and (exact) decimal digits.
 */
${#numbers.formatPercent(num, 3, 2)}
${#numbers.arrayFormatPercent(numArray, 3, 2)}
${#numbers.listFormatPercent(numList, 3, 2)}
${#numbers.setFormatPercent(numSet, 3, 2)}

/*
 * ===============
 * Utility methods
 * ===============
 */

/*
 * Create a sequence (array) of integer numbers going
 * from x to y
 */
${#numbers.sequence(from,to)}
${#numbers.sequence(from,to,step)}
```

#### Strings

- **## strings**：`String` objects 的实用程序方法：

```java
/*
 * ======================================================================
 * See javadoc API for class org.thymeleaf.expression.Strings
 * ======================================================================
 */

/*
 * Null-safe toString()
 */
${#strings.toString(obj)}                           // also array*, list* and set*

/*
 * Check whether a String is empty (or null). Performs a trim() operation before check
 * Also works with arrays, lists or sets
 */
${#strings.isEmpty(name)}
${#strings.arrayIsEmpty(nameArr)}
${#strings.listIsEmpty(nameList)}
${#strings.setIsEmpty(nameSet)}

/*
 * Perform an 'isEmpty()' check on a string and return it if false, defaulting to
 * another specified string if true.
 * Also works with arrays, lists or sets
 */
${#strings.defaultString(text,default)}
${#strings.arrayDefaultString(textArr,default)}
${#strings.listDefaultString(textList,default)}
${#strings.setDefaultString(textSet,default)}

/*
 * Check whether a fragment is contained in a String
 * Also works with arrays, lists or sets
 */
${#strings.contains(name,'ez')}                     // also array*, list* and set*
${#strings.containsIgnoreCase(name,'ez')}           // also array*, list* and set*

/*
 * Check whether a String starts or ends with a fragment
 * Also works with arrays, lists or sets
 */
${#strings.startsWith(name,'Don')}                  // also array*, list* and set*
${#strings.endsWith(name,endingFragment)}           // also array*, list* and set*

/*
 * Substring-related operations
 * Also works with arrays, lists or sets
 */
${#strings.indexOf(name,frag)}                      // also array*, list* and set*
${#strings.substring(name,3,5)}                     // also array*, list* and set*
${#strings.substringAfter(name,prefix)}             // also array*, list* and set*
${#strings.substringBefore(name,suffix)}            // also array*, list* and set*
${#strings.replace(name,'las','ler')}               // also array*, list* and set*

/*
 * Append and prepend
 * Also works with arrays, lists or sets
 */
${#strings.prepend(str,prefix)}                     // also array*, list* and set*
${#strings.append(str,suffix)}                      // also array*, list* and set*

/*
 * Change case
 * Also works with arrays, lists or sets
 */
${#strings.toUpperCase(name)}                       // also array*, list* and set*
${#strings.toLowerCase(name)}                       // also array*, list* and set*

/*
 * Split and join
 */
${#strings.arrayJoin(namesArray,',')}
${#strings.listJoin(namesList,',')}
${#strings.setJoin(namesSet,',')}
${#strings.arraySplit(namesStr,',')}                // returns String[]
${#strings.listSplit(namesStr,',')}                 // returns List<String>
${#strings.setSplit(namesStr,',')}                  // returns Set<String>

/*
 * Trim
 * Also works with arrays, lists or sets
 */
${#strings.trim(str)}                               // also array*, list* and set*

/*
 * Compute length
 * Also works with arrays, lists or sets
 */
${#strings.length(str)}                             // also array*, list* and set*

/*
 * Abbreviate text making it have a maximum size of n. If text is bigger, it
 * will be clipped and finished in "..."
 * Also works with arrays, lists or sets
 */
${#strings.abbreviate(str,10)}                      // also array*, list* and set*

/*
 * Convert the first character to upper-case (and vice-versa)
 */
${#strings.capitalize(str)}                         // also array*, list* and set*
${#strings.unCapitalize(str)}                       // also array*, list* and set*

/*
 * Convert the first character of every word to upper-case
 */
${#strings.capitalizeWords(str)}                    // also array*, list* and set*
${#strings.capitalizeWords(str,delimiters)}         // also array*, list* and set*

/*
 * Escape the string
 */
${#strings.escapeXml(str)}                          // also array*, list* and set*
${#strings.escapeJava(str)}                         // also array*, list* and set*
${#strings.escapeJavaScript(str)}                   // also array*, list* and set*
${#strings.unescapeJava(str)}                       // also array*, list* and set*
${#strings.unescapeJavaScript(str)}                 // also array*, list* and set*

/*
 * Null-safe comparison and concatenation
 */
${#strings.equals(first, second)}
${#strings.equalsIgnoreCase(first, second)}
${#strings.concat(values...)}
${#strings.concatReplaceNulls(nullValue, values...)}

/*
 * Random
 */
${#strings.randomAlphanumeric(count)}
```

#### Objects

- **## objects**：一般用于 objects 的实用方法

```java
/*
 * ======================================================================
 * See javadoc API for class org.thymeleaf.expression.Objects
 * ======================================================================
 */

/*
 * Return obj if it is not null, and default otherwise
 * Also works with arrays, lists or sets
 */
${#objects.nullSafe(obj,default)}
${#objects.arrayNullSafe(objArray,default)}
${#objects.listNullSafe(objList,default)}
${#objects.setNullSafe(objSet,default)}
```

#### 布尔

- **#bools**：boolean evaluation 的实用程序方法

```java
/*
 * ======================================================================
 * See javadoc API for class org.thymeleaf.expression.Bools
 * ======================================================================
 */

/*
 * Evaluate a condition in the same way that it would be evaluated in a th:if tag
 * (see conditional evaluation chapter afterwards).
 * Also works with arrays, lists or sets
 */
${#bools.isTrue(obj)}
${#bools.arrayIsTrue(objArray)}
${#bools.listIsTrue(objList)}
${#bools.setIsTrue(objSet)}

/*
 * Evaluate with negation
 * Also works with arrays, lists or sets
 */
${#bools.isFalse(cond)}
${#bools.arrayIsFalse(condArray)}
${#bools.listIsFalse(condList)}
${#bools.setIsFalse(condSet)}

/*
 * Evaluate and apply AND operator
 * Receive an array, a list or a set as parameter
 */
${#bools.arrayAnd(condArray)}
${#bools.listAnd(condList)}
${#bools.setAnd(condSet)}

/*
 * Evaluate and apply OR operator
 * Receive an array, a list or a set as parameter
 */
${#bools.arrayOr(condArray)}
${#bools.listOr(condList)}
${#bools.setOr(condSet)}
```

#### 数组

- **#arrays**：数组的实用方法

```java
/*
 * ======================================================================
 * See javadoc API for class org.thymeleaf.expression.Arrays
 * ======================================================================
 */

/*
 * Converts to array, trying to infer array component class.
 * Note that if resulting array is empty, or if the elements
 * of the target object are not all of the same class,
 * this method will return Object[].
 */
${#arrays.toArray(object)}

/*
 * Convert to arrays of the specified component class.
 */
${#arrays.toStringArray(object)}
${#arrays.toIntegerArray(object)}
${#arrays.toLongArray(object)}
${#arrays.toDoubleArray(object)}
${#arrays.toFloatArray(object)}
${#arrays.toBooleanArray(object)}

/*
 * Compute length
 */
${#arrays.length(array)}

/*
 * Check whether array is empty
 */
${#arrays.isEmpty(array)}

/*
 * Check if element or elements are contained in array
 */
${#arrays.contains(array, element)}
${#arrays.containsAll(array, elements)}
```

#### Lists

- **#列表**：lists 的实用程序方法

```java
/*
 * ======================================================================
 * See javadoc API for class org.thymeleaf.expression.Lists
 * ======================================================================
 */

/*
 * Converts to list
 */
${#lists.toList(object)}

/*
 * Compute size
 */
${#lists.size(list)}

/*
 * Check whether list is empty
 */
${#lists.isEmpty(list)}

/*
 * Check if element or elements are contained in list
 */
${#lists.contains(list, element)}
${#lists.containsAll(list, elements)}

/*
 * Sort a copy of the given list. The members of the list must implement
 * comparable or you must define a comparator.
 */
${#lists.sort(list)}
${#lists.sort(list, comparator)}
```

#### Sets

- **## sets**：sets 的实用程序方法

```java
/*
 * ======================================================================
 * See javadoc API for class org.thymeleaf.expression.Sets
 * ======================================================================
 */

/*
 * Converts to set
 */
${#sets.toSet(object)}

/*
 * Compute size
 */
${#sets.size(set)}

/*
 * Check whether set is empty
 */
${#sets.isEmpty(set)}

/*
 * Check if element or elements are contained in set
 */
${#sets.contains(set, element)}
${#sets.containsAll(set, elements)}
```

#### Maps

- **## maps**：maps 的实用程序方法

```java
/*
 * ======================================================================
 * See javadoc API for class org.thymeleaf.expression.Maps
 * ======================================================================
 */

/*
 * Compute size
 */
${#maps.size(map)}

/*
 * Check whether map is empty
 */
${#maps.isEmpty(map)}

/*
 * Check if key/s or value/s are contained in maps
 */
${#maps.containsKey(map, key)}
${#maps.containsAllKeys(map, keys)}
${#maps.containsValue(map, value)}
${#maps.containsAllValues(map, value)}
```

#### 骨料

- **#aggregates**：用于在数组或集合上创建聚合的实用方法

```java
/*
 * ======================================================================
 * See javadoc API for class org.thymeleaf.expression.Aggregates
 * ======================================================================
 */

/*
 * Compute sum. Returns null if array or collection is empty
 */
${#aggregates.sum(array)}
${#aggregates.sum(collection)}

/*
 * Compute average. Returns null if array or collection is empty
 */
${#aggregates.avg(array)}
${#aggregates.avg(collection)}
```

#### 标识

- **#ids**：用于处理可能重复的`id`属性的实用方法(对于 example，作为迭代的结果)。

```xml
/*
 * ======================================================================
 * See javadoc API for class org.thymeleaf.expression.Ids
 * ======================================================================
 */

/*
 * Normally used in th:id attributes, for appending a counter to the id attribute value
 * so that it remains unique even when involved in an iteration process.
 */
${#ids.seq('someId')}

/*
 * Normally used in th:for attributes in <label> tags, so that these labels can refer to Ids
 * generated by means if the #ids.seq(...) function.
 *
 * Depending on whether the <label> goes before or after the element with the #ids.seq(...)
 * function, the "next" (label goes before "seq") or the "prev" function (label goes after
 * "seq") function should be called.
 */
${#ids.next('someId')}
${#ids.prev('someId')}
```

## 附录 C：标记选择器语法

Thymeleaf 的 Markup Selectors 直接借用了 Thymeleaf 的解析 library：[AttoParser](http://attoparser.org/)。

这个 selectors 的语法与 XPath，CSS 和 jQuery 中的 selectors 的语法有很大的相似之处，这使得它们易于用于大多数用户。您可以查看[AttoParser 文档](http://www.attoparser.org/apidocs/attoparser/2.0.4.RELEASE/org/attoparser/select/package-summary.html)处的完整语法 reference。

对于 example，以下选择器将在标记内的每个位置\_选择每个带有 class `content`的``(注意这不是尽可能简洁，请继续阅读以了解原因)：

```xml
<div th:insert="mytemplate :: //div[@class='content']">...</div>
```

基本语法包括：

- `/x`表示使用 name x 的当前节点的直接 children。
- `//x`表示具有 name x 的当前节点的 children，在任何深度。
- `x[@z="v"]`表示带有 name x 的元素和带有 value“v”的名为 z 的属性。
- `x[@z1="v1" and @z2="v2"]`分别表示具有 name x 和属性 z1 和 z2 的元素，其值分别为“v1”和“v2”。
- `x[i]`表示 name x 在其兄弟姐妹中位于数字 i 中的元素。
- `x[@z="v"][i]`表示带有 name x 的元素，带有 value“v”的属性 z，并且在其兄弟姐妹中的数字 i 中也定位匹配此条件。

但也可以使用更简洁的语法：

- `x`完全等同于`//x`(在任何深度 level 搜索 name 或 reference `x`的元素，reference 是`th:ref`或`th:fragment`属性)。
- Selectors 也允许没有元素 name/reference，因为它们包含 arguments 的规范 long。所以`[@class='oneclass']`是一个有效的选择器，它使用 value `"oneclass"`查找带有 class 属性的任何元素(标签)。

高级属性选择 features：

- 除`=`(等于)外，其他比较 operators 也有效：`!=`(不等于)，`^=`(以...开头)和`$=`(ends with)。对于 example：`x[@class^='section']`表示带有 name `x`的元素和带有`section`的属性`class`的 value。
- 可以从`@`(XPath-style)开始和不使用(jQuery-style)来指定属性。所以`x[z='v']`相当于`x[@z='v']`。
- Multiple-attribute 修饰符既可以与`and`(XPath-style)连接，也可以通过链接多个修饰符(jQuery-style)来连接。所以`x[@z1='v1' and @z2='v2']`实际上相当于`x[@z1='v1'][@z2='v2']`(也是`x[z1='v1'][z2='v2']`)。

直接 jQuery-like 选择器：

- `x.oneclass`相当于`x[class='oneclass']`。
- `.oneclass`相当于`[class='oneclass']`。
- `x#oneid`相当于`x[id='oneid']`。
- `#oneid`相当于`[id='oneid']`。
- `x%oneref`表示具有`th:ref="oneref"`或`th:fragment="oneref"`属性的``标记。
- `%oneref`表示具有`th:ref="oneref"`或`th:fragment="oneref"`属性的任何标记。请注意，这实际上只相当于`oneref`，因为可以使用 references 而不是元素名称。
- 可以混合使用直接 selectors 和属性 selectors：`a.external[@href^='https']`。

所以上面的 Markup Selector 表达式：

```xml
<div th:insert="mytemplate :: //div[@class='content']">...</div>
```

可以写成：

```xml
<div th:insert="mytemplate :: div.content">...</div>
```

检查一个不同的 example，这个：

```xml
<div th:replace="mytemplate :: myfrag">...</div>
```

将寻找`th:fragment="myfrag"`片段签名(或`th:ref` references)。但是如果它们存在的话，它们也会查找带有 name `myfrag`的标签(在 HTML 中它们没有)。注意区别：

```xml
<div th:replace="mytemplate :: .myfrag">...</div>
```

...实际上会查找`class="myfrag"`的任何元素，而不关心`th:fragment`签名(或`th:ref` references)。

#### 多值 class 匹配

标记 Selectors 将 class 属性理解为**多值**，因此即使元素具有多个 class 值，也允许 selectors 的 application 在此属性上。

对于 example，`div.two`将 match ``

更新于：5 个月前

### 参考资料

- [Thymeleaf 官网](https://www.thymeleaf.org/)
- [Thymeleaf Github](https://github.com/thymeleaf/thymeleaf/)
- [Thymeleaf 教程](https://fanlychie.github.io/post/thymeleaf.html)
