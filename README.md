# IoC创建Bean的大致流程

1、解析xml文件，将xml中读取到的内容利用`ClassPathXmlResource`来进行存储。

2、初始化一个工厂，该工厂可以自由选择，比如`SimpleBeanFactory`或者`AutowireCapableBeanFactory`，又或者是`BeanFactory`，取决于场景需要。

3、实例化一个`XmlBeanDefinitionReader`，该类需要传入第二步生成的工厂，它的主要作用就是解析存储在`ClassPathXmlResource`中的属性，将它封装为一个`BeanDefinition`，然后存储在一个map中，map的key为对象的名字，value就是`BeanDefinition`。用于后续创建bean时，根据名称取到`BeanDefinition`，然后`BeanDefinition`中取得属性。

4、到此为止，上面的三步是为了初始化并且存储一些对象的信息，这些信息都来自xml中的配置。之后，调用`refresh()`来进行具体的创建。

5、在单例模式下，`refresh()`会先从存放了所有bean实例的map中根据名字取该元素，如果不为空，则直接返回，如果为空，则从毛坯实例中尝试获取该元素（存储毛坯实例是为了解决循环依赖的问题，创建一个空的对象，所有属性都不赋值，用于注入），如果还是为空，则执行创建。

6、创建的流程就是获取到之前存储的`BeanDefinition`，然后先创建一个毛坯实例，这里是利用Java的反射以及`BeanDefinition`中存储的`getConstructorArgumentValues`信息，来进行创建，只创建空的类，类的所有属性都不赋值。之后将毛坯实例进行存储。

7、这一步是将上一步创建的毛坯实例属性进行赋值，从`BeanDefinition`中获取到`PropertyValues`，然后遍历，调用`setXXX`方法进行赋值。

8、创建完后，将bean存储到第五步刚开始取元素的那个map当中，并返回本次创建的实例。如果没有采用注解，到这里就已经创建结束了。

9、如果创建的元素当中有属性使用了注解，则会调用`AutowiredAnnotationBeanPostProcessor`的`postProcessBeforeInitialization`方法，该方法会遍历传入实例的所有属性，如果发现带有@Autowired注解，那么就去工厂中获取到对应的实例，并进行注入，然后返回该对象。