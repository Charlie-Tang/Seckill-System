# Seckill-System
高并发优化的练习 不断学习
这是一个高并发ssm框架的练习
从这个练习中我清楚的认识到了我们ssm框架的配置是由先配置mybatis 即我的spring-dao.xml文件开始的 
顺序是先开始创建我的实体类即Entity类 然后开始对实体类进行数据库的创建  即写相应的sql语句 即ddl
写好之后开始写我们想要的数据操作方法  即dao层接口  由于mybatis是以创建dao层相应的xml文件去进行扫描获取实体类的  所以这极为方便
写好我们的xml和ddl方法之后  我们需要通过junit进行各个方法的测试。
测试后开始写我们的service层代码  由于该层数据是链接我的web层的 一般可以通过传json获取数据相关信息。
以前的我直接是报错就把信息存取进message里面  然后通过ol表达式体现在页面上  感觉这并不是很好的方法
我们现在可以通过创建dto和exception相关的数据  来将我们的报错信息一一对应的体现在我们页面所传递的json或者xml文件信息中
配置seivice层主要是需要使用spring的aop即transaction 来将我们的信息传递出去  以往是在xml写tx表达式
但是如今我们可以通过注解来标识我们的aop对应方法 配置spring-service.xml可以达到我们的目的。
之后我们可以通过配置我们的spring-web.xml来进行各种类型的数据操作
我主要是需要视图解析器和对静态资源的加载  该类还去扫描controller下的各种方法
接下来还需要在我们的xml文件中配置dispatcher来进行控制  一般我还会在我的xml中进行filter的配置来进行登录和数据控制

在本次学习中我了解到了注解究竟是个什么玩意  还有ssm框架是拒绝直接访问WEB-INF路径下的各个jsp的
我们访问jsp只能通过RequestMapping设置的各个路径去访问，提到这个我们还需要了解现在很火的restful设计规范 
通过restful设计规范可以很优雅的实现我们的url设置

除此之外我也学习到了高并发的思想  即我们的mysql由于被数据流量速度的限制 和JAVA语言本身GC的限制  导致了我们每秒执行操作的速度大大变慢
如果机房数据库和登录地址是无法改变的  那我们只能通过优化我们的数据库操作来实现它
首先我们应该知道在mysql数据库中由于rowlock即同级锁使我们的读取数据大大变慢  我们应该如何规避这个问题呢？
首先我们可以优化我们的sql语句  即一条sql语句访问数据库的次数  在书写sql语句的时候需要考虑到访问数据库的次数。
其次就是我们可以用到现在最火的nosql数据库即redis数据库  redis不会将数据库保存到本地  我们可以在我们的jedis服务器来规定它保存的时间

由于redis的优点  我们的dao层创建对象可以通过它来实现  而redis数据库是没有实现内部序列化的操作的
我们可以通过一些特定的接口来实现redis数据序列化  在这里我们所使用的protostuff这个jar包来实现序列化  优化我们的对象读取
我们的mysql数据库也是可以进行并优化的  即我们可以将我们的各种操作数据的动作直接放在我们的mysql数据库中
在这里.sql来实现了   我们可以知道优化高并发是可以通过很多种方法的   阿里巴巴的程序员们则通过重构数据库的底层代码来直接实现优化 忒强了
写到这里  如果还有什么想到的  我再继续写

