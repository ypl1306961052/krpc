      Q: 已经有这么多rpc框架了，为什么要重复造轮子？
      A: 是的，已经有很多rpc框架了, 如spring cloud, dubbo(阿里), motan(微博), thrift(Facebook), grpc(谷歌), 
         tars(腾讯) , venus 等等，每一个肯定都有自己的亮点，每一个也都会有自己不足的地方，没有哪一个能满足你所有期望。
         同样的，krpc 框架也不会满足你所有期望，但是，krpc 从最基本的接口设计开始就有很多独到的地方，
         不仅使得框架简洁也使得框架强大，易扩展，所以这不是一个重复的没什么价值的轮子，而是一个能给你带来全新体验的轮子。
      
      Q: rpc 客户端和服务端的接口契约应该是怎样的形式？
      A: 一般会有以下几种风格：
      
           1） spring cloud 是基于spring mvc, 实际上是不需要定义接口的，实践中一般是在实现类上加 RestController 
                 等注解把一个类直接变成服务。而客户端要调用此服务直接通过rest template 调用，或者用feign框架通过定
                 义客户端接口（只需定义无需实现）来调用服务。feign客户端接口和服务端类实现不需要一一对应，可按需定义接口。
                  
                 spring mvc 一般用swagger来生成api文档，作为客户端和服务端接口契约。
      
           2） 手写纯java接口，如dubbo, motan 等；服务端实现该接口，客户端调用该接口（一般会根据接口生成动态代理）。
                 
                 采用这类框架的公司一般还需要自己搞一套api管理系统来维护客户端和服务端的接口契约，最简单的情况是直
                 接把java接口作为接口契约（但让一个做前端的开发直接去看java接口总有点不合适）。 
      
           3）通过 idl 生成java接口或其他语言的接口，如grpc, thrift, tars， 生成后就和手写java接口方式一样了。
                 客户端和服务端的接口契约就是idl文件。
      
           krpc的选择是idl方式，基于google 的 protobuff 文件来定义接口契约。
      
      Q: 我不喜欢通过idl预生成代码！
      A: 好吧，想想看，为什么几个互联网巨头都是用idl方式？跨语言当然是一个因素，但是更重要的是 idl 是一种交流风格，
          建立一种与语言无关的idl契约形式比围绕java接口更好，需要亲自去实践一下才会体验到这种好处。
      
          思维定势转换总是很困难，即使你的应用不需要跨语言，也可以尝试用用 google protobuff。
          
          google protobuff 序列化的大小和性能上网上有各种测评报告。生成的java类在易用性上有几点我觉得
          做的很好：
          1）很容易通过builder模式创建输入输出对象
          2）builder 生成的对象是只读的，但很容易和builder进行转换
          3) 对象的默认值绝对不会是null, 不需要总是判断是否为null
          4) 默认值不做序列化
          5) 提供可选的反射功能（非java自己的反射）可以实现一些高级功能
          
          如果亲自尝试后还是不喜欢，那就放弃吧。
      
      Q: rpc接口应该长什么样子？
      A: 网上充斥着这种 hello world 的rpc例子, 表示rpc是多么简单：
      
             interface HelloService {
                 String hello1(String message);
                 String hello2(String name,String message);
            }
           
            简单的接口隐藏着2个核心问题： 1）怎样定义入参  2）怎样定义响应， 这个决定了rpc框架的最基础设计。
      
            先看入参： 是允许多个入参 还是 只允许一个入参对象 ？
      
             多个入参看上去简单，但实践中负作用更明显：
      
            1） 虽然对应用层是透明的，但必须要知道rpc调用的时候必须要传递方法名，每个参数的类型，序列化和反序列化
                必须要对每个参数各做一次
            2） 参数多的情况方法签名很混乱，哪个参数在前在后不能随便调整重构
            3） 接口增加参数是最常见的情况，但增加参数只能往后面加，不能加在中间
      
            如果只允许一个入参对象则以上的问题都不存在。
            
            再看响应： 响应对象可以是直接的值（String, int, List<>，Map<>这种）还是只允许一个响应对象 ？
      
            返回直接的值看上去简单，但实践中负作用更明显：
             1）出错的情况如何返回？抛异常？ 
             2）如果本来返回string, 现在想多返回一些数据怎么办？ 新加一个接口 ？
       
            如果响应必须是一个对象 则以上的问题都不存在，直接在响应对象里加属性就可以。 
      
            所以，虽然很多rpc框架支持这种看似简单的入参和响应定义方式（从java rmi 延续下来的古老传统？），
            在实践中，一个入参对象一个响应对象才是最佳实践。
      
            我喜欢 google protobuff 里的服务定义方式。这个仅仅是idl定义，google protobuff 并没有约定具体每种语言转换后
            应该是什么形式，可自由发挥：
      
            service SearchService {  
                 rpc Search (SearchRequest) returns (SearchResponse);  
            }  
  
            krpc 只允许一个入参对象一个响应对象, 而其它框架在这方面都更复杂。
            grpc在上述接口基础上扩展出了 stream 特性, 比krpc复杂很多。
      
      Q: 使用异常还是错误码？
      A: 不管rpc接口形式上是否和普通接口一样，有一点不一样的是rpc调用时出错是很正常的，一般的rpc设计方案都是抛出异常。
      
            抛异常的方案不好的地方有： 
            
             1) 可以一抛到底，但万一在业务层想对rpc出错做处理，则必须try catch，业务层本来清晰的代码逻辑会混入不必要的程式
                化代码。 try catch越多业务层代码越丑陋。
             2) 前端js调用后端出错了, 直接给前端看一个500错误？一般各种异常总是需要转换成错误码和错误描述。
      
          即使rpc框架不支持错误码，实践中人们也会通过各种方式约定错误码机制。

          krpc的方案是不抛出异常，强制要求每个响应对象里一定要有一个 retCode（错误码）属性，以及一个可选的retMsg（错误描述）
          krpc系统级的错误和业务层的错误都使用这个retCode属性来表示错误。
          krpc的接口调用不论什么情况都不会抛出异常，包括且不限：超时，网络连接中断，rpc调用阻塞的线程被其它线程中断等
          
          dubbo文档中的推荐做法是使用异常而不是使用错误码, 一般的rpc也都是如此；krpc这一点和其它rpc框架差异很大。
      
      Q: 为什么选择protobuff作为序列化，为什么不支持多种序列化格式?
      A：在业务量没有达到一定程度的情况下，比较序列化方案没有太大意义，只要不是太差，序列化方面的差异对业务影响微乎其微。
           在业务量达到了一定程度后，你总会想用更好的序列化方案。
           
           目前业界接受程度最高的序列化方案是： protobuff 。protobuff只做序列化这一件事情并做的足够好。
           protobuff有两个版本，protobuff 3.x 比protobuff  2.x 有进一步的改进。
      
           krpc的选择是仅支持 protobuff 3.x 一种方式。
      
      Q: protostuff 也能透明地将 java 对象序列化成 protobuff 格式, 为什么不用 protostuff ?
      A: protostuff在带来很多好处的时候也有很多使用上的坑，但不采纳protostuff最主要的考虑是：
      
            希望以proto文件为接口契约进行开发，而不是以java类/java接口为接口契约进行开发。
            纯动态的http网关要在没有java类的基础上做protobuff序列化，使用protostuff无法做到这一点。
      
      Q: 为什么选择netty 4作为nio框架?
      A:  当一个nio框架已快成事实上的行业标准的时候，没有必要再去支持多个nio框架，更没必要自己去写nio框架。
      
            netty 4相比netty 3.x以及其它nio框架在以下方面具有优势：
      
            1) 性能出众，其中之一：pooled buffer的使用可大幅提高性能；
            2) 扩展性强,  常见的编解码方案在netty中都能找到，很容易扩展；
            3) 更易于维护， 主流的nio框架了解的人多；
      
           netty 4只做网络api这一件事情并已做的足够好。
       
           kprc传输层直接使用netty 4，不做额外的封装。因为不需要支持多框架且只支持一种序列化格式，krpc的网络层设计比绝大
           多数rpc框架都简洁高效。
      
      Q: 应该如何定义异步调用的客户端接口？
      A: 一般有以下几种方式：
      
           1) 在rpc框架外做客户端异步封装，比如spring 4.x里的 AsyncRestTemplate， 和rpc本身没太大关系，只是在客户端封装
              了一个线程池来实现异步回调；
           2) 返回Jdk里的Future, 比如dubbo； 此Future功能有限，不能增加listener
           3) 返回自定义的Future, 一般会增加对listener的支持；比如motan，grpc
           4) 在入参中增加回调，如 grpc, tars, 回调接口形式各异
           5) 在入参中传入回调类并且需要服务端配合实现，如dubbo, venus 等,  这种设计方案很糟糕
      
          以上除AsyncRestTemplate外使用起来或多或少都不方便，如：
      
          1) motan 需在接口上加一个异步的注解 @MotanAsync，还需配一个异步接口的maven代码生成插件； 
          motan可返回 ResponseFuture,比Future好很多，但不能和jdk 1.8的CompletableFuture相比
          2) dubbo  需显示申明哪些接口要异步，这时会返回null, 要另外调RpcContext.getContext().getFuture()才能拿到 Future, 
             而返回的这个Future功能还非常有限，做不了什么事情
          3) dubbo 的 callback 功能既难配，也不好用； 为了客户端的异步，需要服务端配合客户端来实现回调，这个设计很糟糕
          4) venus 需显式地在接口上加注解表示异步，或者传入 InvocationListener 表示回调；也有dubbo callback一样问题：
             为了客户端的异步，需要服务端配合客户端来实现回调
          5) tars 可传入回调接口来实现异步回调，但回调接口的设计实在太丑了，正常/超时/异常是3个不同方法让使用者难以处理；
          6) grpc 可通过ClientCalls的futureUnaryCall获取到一个ListenableFuture<T>, 比Future好很多，但不能和jdk 1.8的
             CompletableFuture相比
          7) grpc 也可通过asyncUnaryCall使用回调方式StreamObserver（感觉没太大用，future版本就够了，
             StreamObserver用来处理简单的请求响应过于复杂了）
      
          motan和grpc明显比其它版本的异步方案好些，但还不够好。以上rpc框架上设计的时候jdk 1.8还没出来，
          还没有CompletionStage<T>和CompletableFuture<T>； 即使现在有了jdk 1.8，这些rpc框架要兼容老版本就很难改变接口形态。
      
          krpc的做法如下：
      
          1) 在idl生成代码的同时 基于proto文件里的service申明 生成2个接口：1个同步接口，1个异步接口
          2) 客户端想用哪种就申明哪种形式的referer，也可同时申明同步和异步的referer
          3) 异步接口直接返回 jdk 1.8里的 CompletableFuture<T> 对象，在此对象上可做各种组合和回调
          4) 在预生成接口文件后无需再配置任何注解，加任何插件
          5) 不论客户端用什么形式调用服务，服务端只需实现一次 （可用同步或异步，由服务端自己决定和客户端调用方式没关系）

          krpc的做法相比其他rpc框架更现代，更简洁。   
        
      Q: 服务端如果想异步实现某接口，应该如何做？
      A: 比较一下各个框架的做法：
      
          1) spring mvc, 需servlet 3.0, 通过返回 DefferedResult<ModelAndView> 或 WebAsyncTask 来实现异步
          2) motan  文档中未看到如何异步实现服务
          3) dubbo   只有客户端以callback形式调用的方法 服务端才可以异步实现， 客户端若用同步调用，服务端就只能同步实现
          4) venus  同dubbo callback一样的问题
          5) tars   可以使用AsyncContext实现异步
          6) grpc  GRPC的做法比较另类，服务端接口形式天生就是异步的，所有接口中总是有一个StreamObserver入参用来返回结果，
             没有简单的同步接口形式。grpc 为了异步而把简单的同步搞复杂了。
          
          以上的做法里tars相对较好，但还不够好； 其它框架存在的问题是： 如果要在多线程中传递整个请求上下文，
          需开发者自己提供一个类封装入参和callback（最少也需要一个匿名的Runnable或Callable)，不够方便；基于前面的Q/A, 
          入参如果仅仅是1个简单对象，封装和传递起来会简单很多。
      
          krpc的做法是：
      
          1) 服务端实现的接口的形式上是同步接口 Res method(Req req)，和客户端使用的同步接口完全一样
          2) 如果业务层返回null表明业务层是采用异步实现， 否则就是同步实现
          3) 业务层可以使用ServerContext.closure(req) 获取到一个closure对象,  此对象包含上下文和请求对象，
             可以在队列/线程间任意传递
          4) 业务层可在获取到结果后调用closure.done(res) 返回结果

      Q: krpc为什么要定制protoc工具？
      A: protoc 工具在生成请求对象和响应对象的序列化代码非常好，但是对 service 定义 生成的java 代码非常冗余丑陋，
         grpc框架自己也是做了定制。
          
         对protoc源码 修改的地方主要是生成的服务接口，会根据service定义生成2个接口： 1个同步，1个异步，
         不会生成任何stub代码。生成的接口非常简单，没有一点冗余代码。
      
      Q: krpc为什么要用服务号和消息号？
      A：krpc在protobuff的标准扩展机制上对服务和消息各增加了一个id属性，如下：
           service SearchService {  
             option (krpc.serviceId)=100;
             rpc Search1(SearchRequest) returns (SearchResponse) { option (krpc.msgId)=1; };  
             rpc Search2 (SearchRequest) returns (SearchResponse) { option (krpc.msgId)=2; };  
           }
            
           使用serviceId,msgId的好处：
      
           1) 网络上rpc调用时传递的是serviceId,msgId 而不是服务名消息名，占用字节更短，仅需2个int
           2) 使用serviceId没有二义性，否则必须包名+接口名才能保证不冲突 
           3) 客户端和服务端可以独立地调整服务名，服务所在的包，消息名，只要id不变，一方修改不会影响另外一方; 
              想对接口重构的时候特别方便
      
      Q: krpc为什么没有单向调用（只有请求不要响应）形式？
      A: 1) 客户端可使用异步接口然后丢弃返回的future一样可以达到单向调用的效果
         2) 必须在接口外用一种特殊的配置方式来申明哪些接口是单向定义的以便客户端和服务端做特殊处理，增加了复杂度
         3) 对nio框架的长连接，返回一个成功的响应包几乎不会增加带宽；而没有响应包，对问题的跟踪排查都不方便；
            使用场景并不多；
         4) 纯粹的单向调用在框架内部确实可以少创建一个锁，但因为实际并未在锁上真正发生等待，
            也仅仅是一个对象的创建开销差异而已；
      
      Q: krpc的网络包格式有什么特点？
      A: krpc的网络包除了固定的长度前缀外和protobuff的包体外，还有个扩展包头，扩展包头的格式也是protobuff形式的，
         占用字节数很小，很容易扩展； 实际和百度的brpc框架里的baidu_std协议很类似，但取消了消息嵌套。
      
      Q: krpc里的push设计有什么特点？
      A: socket是双工的，但是除grpc外大部分rpc框架在设计时并没有充分考虑这个，所以后期几乎难以支持服务端
         主动push请求给客户端。
         
         虽然可以通过客户端也起一个服务端口来绕过此问题，但某些场景下并不能这么干，比如app和服务端建立长连接，
         服务端要往app推消息。
      
         krpc从一开始就考虑到此问题，push的配置和调用就和正常的rpc调用一样简洁。
      
      Q: krpc和grpc都是基于protobuff, netty 4，krpc有什么独特的优势?
      A: krpc和grpc适合的场景不同，不应该直接做比较。
      
          grpc的目标不仅仅是single request/single response这样的rpc, 还支持3种其它模式： 
          request stream/single response, single request/resposne stream,  request stream/response stream, 
          后面3种模式在内网rpc调用几乎用不到，grpc特别适合跨越互联网的建立http连接比较昂贵的场景。
          
          grpc底层的通讯协议是标准的http2协议, 协议本身远比krpc网络包协议复杂，内部实现也远比krpc复杂，
          但长连接建立好后收发数据的效率和krpc没太大差异。
          
          grpc框架的强项不在内网通讯。
    
          如果硬要拿grpc来做内网rpc通讯框架和krpc做比较，krpc更具优势，主要体现在几点上：
    
          1) 绝大多数应用的服务端实现是同步方式，grpc生成的接口定义却总是异步的，业务层的开发不可避免必须
             用到 StreamObserver，不够简洁；
          2) 绝大多数java rpc框架是对服务接口实现动态代理，客户端代码只需依赖该接口就可以做rpc调用； 而grpc每次
             rpc调用必须先根据channel创建stub再进行调用，不透明不够简洁； 
          3) krpc特有的一些方面比grpc更好，如： 用错误码而不是异常，返回CompletableFuture，通过服务号消息号来定位服务
          4) grpc 框架不支持单个地址上建立多个连接，需客户端在外围包装，而krpc可以；
             建多个连接的必要性在于： netty4框架里相同连接的所有消息实际上是在服务端固定的单线程中顺序处理的，
             当单线程达到100%负载的时候就算服务端有多核也无法再提高性能了，这时就需要和服务端建立多个连接以便将
             消息分到不同的线程中来处理 
          5) grpc官方对一些外围系统的集成并不提供，如注册与发现，全链路跟踪，和spring boot集成等，
             使用grpc有可能附带地要去用很多第三方的东西或自己开发很多东西
          6) krpc提供一个http通用网关而grpc做不到且没有看到有这方面的计划
      
      Q: krpc提供http功能吗？
      A: 提供。krpc提供简洁而强大的http支持，轻量无需容器，通过routes.xml来配置路由，支持restful风格路由，
         无需配置默认就支持json格式的请求体,  无需在接口上加各种注解，强大的扩展机制。
      
           krpc的http功能虽然强大，但并不建议在内网用http通讯，http的使用场景建议如下：
           1) 作为通用http网关对外提供http接口
           2) 在开发测试阶段开启http端口方便测试
           3) 兼容一些老的http模块
      
      Q: krpc的http通用网关和zuul比有什么差异？
      A: 两者具有相同的目标，但使用的场景不同。
      
         两者的差异点有：
      
          zuul对接的内部系统仍然是走http协议，更通用。而krpc webserver对接的内部系统是krpc协议，仅适用于krpc的微服务。
          zuul需要web容器，是基于servlet api开发的服务。krpc webserver底层基于netty, 无需容器，并不遵循servlet规范。
          zuul支持几种扩展点:PRE,ROUTING,POST,ERROR;  krpc webserver的扩展点更多。
          zuul的扩展形式基本是阻塞模式；krpc webserver的扩展形式既可以是同步的，也可以是异步的。
          zuul是一个需要单独部署的模块；krpc webserver既可以单独启动，也可以和server同时启动, 启动方式更灵活。
      
      Q: krpc的扩展性如何？
      A：krpc提供三种扩展机制：
  	      1) 通过预定义的SPI机制进行扩展, 如注册与发现插件，load balance插件，动态路由插件，http网关插件等
  	      2) 如果在spring容器中运行，任意一个实现了 SPI 接口的bean都能被框架直接识别, 无需使用spi机制
  	      3) 深度定制：框架内部主要对象的创建都集中在bootstrap类中，可通过继承此类创建自己所需的对象
	
      Q: krpc里为什么没看到注解方式进行配置?
      A: 1) 在启动程序的主流方式已变为spring boot的情况下，所需的配置参数都可以统一通过application.properties或 
             application.yaml来进行配置， springboot的自动配置和spring自带的注解就足以完成所有配置，看不出在业务类上
             增加rpc框架级别的注解有任何优势；
	      2) 每个服务的可配置参数通常都会很多，注解看上去简洁，但注解的参数越多就越丑陋，远不如配置文件来的简洁；
	      3) krpc的接口是protoc工具生成的，生成的接口类不适合去做任何改动，否则难以和proto文件保持同步；
	
      Q: 如何评价其它微服务框架？
      A:  1) spring cloud:  通讯是http短连接+json，需要web容器支持，其它没有明显的短板，生态异常丰富
	      2) dubbo:  很强大，虽然已经成为apache顶级项目，但历史负担太重，想要包容的东西太多，要保持向下兼容的同时做
	                      大的提升非常困难； 内部代码层级太复杂；对http支持不够，即使加上dubbox的扩展也还不够，不足以作为
	                      通用http网关
	      3) motan:  目标没有dubbo这么庞大，代码层次更简洁，对外接口不错，但内部实现不够好，长连接连接池的实现没有
	                      完全发挥nio的优势，http功能需容器支持，不足以作为通用http网关
	      4) grpc:  特定的场景下使用很合适，但用在内网开发微服务太重，java api相比其他框架明显不够友好
	      5) tars: 外围配套很齐全， 异步回调接口设计存在明显的不足
	      6) venus:  实测性能不错，设计思想基本还停在5年前
	      7) 这些框架里除了grpc外的其它的框架都不支持java 1.8里的CompletableFuture<T>或CompletionStage<T>，异步编程方
	          面都不够现代。

      Q: krpc框架的http接口为什么不采用servlet规范？
      A: 未来的趋势是微服务，去容器化
	     1) 微服务本来就很轻量，为什么还要起个容器？
	     2) 在netty的基础上开发http服务相当简单，还有多大的必要要通过容器来提供http服务？
	     3) 未来都是前后端分离的开发趋势，还有多少人会用到容器提供的jsp？即使要渲染html, 用netty+模板引擎一样很容易实现
	     4) 看看spring 5里的spring WebFlux
	
      Q: krpc框架的http接口为什么不采用 JAX-RS 等规范？
      A: 对krpc来说，采用JAX-RS规范无法完成以下目标： 
	     1) 现代的http服务默认就应该能自动解析 form形式的参数, json形式的参数, path形式的参数，默认输出就应该是json, 而不应该
	         需要额外配置
	     2) 对 header, cookie 参数通过命名约定比用注解一个个配置更好
	     3) 没法添加额外的路由配置参数
	     4) 路由信息放在每个类里不够简洁, 不如将所有路由集中在一个配置文件里更容易维护和管理
	
      Q: krpc框架如何支持全链路调用链跟踪系统？
      A: 1) krpc框架的目标是不绑定到特定的APM框架下，所以krpc定义了自己的Trace跟踪方式和扩展方式，可通过配置不同的adapter和
	          不同的apm系统进行集成；目前有计划支持的是 zipkin, skywalking, cat
	      2) krpc提供了javaagent探针，可采集第三方组件的数据，无需写任何代码
	      3) 大部分apm系统对异步调用的跟踪支持都不好，krpc框架对异步调用的跟踪做了很好的支持；
	      4) 当需要手工打点的时候，krpc自带的Trace接口比其它APM系统的接口都更简洁； 

      Q: 有些rpc框架支持injvm调用，krpc为什么不支持？
      A: 看不出injvm调用方式有什么必要, 使用场景有限

      Q: 如何评价spring 5里的WebFlux?
      A: TBD 
	  
	  
      
