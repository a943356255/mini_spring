package context;

import core.ClassPathXmlResource;
import core.Resource;
import entity.BeanDefinition;
import factory.BeanFactory;
import factory.SimpleBeanFactory;
import exception.BeansException;
import listener.ApplicationEvent;
import listener.ApplicationEventPublisher;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import factory.xml.XmlBeanDefinitionReader;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;

public class ClassPathXmlApplicationContext implements BeanFactory, ApplicationEventPublisher {

    // 这里是用一个BeanDefinition，来存储xml中的id以及bean的路径
    private final List<BeanDefinition> beanDefinitions = new ArrayList<>();

    private final Map<String, Object> singletons = new HashMap<>();

    BeanFactory beanFactory;

    // context负责整合容器的启动过程，读外部配置，解析Bean定义，创建BeanFactory
    public ClassPathXmlApplicationContext(String fileName) {
        // Resource是一个接口，实例化一个ClassPathXmlResource
        Resource resource = new ClassPathXmlResource(fileName);
        // 实例化一个工厂
        SimpleBeanFactory beanFactory = new SimpleBeanFactory();
        // 为了将从xml中读取到的内容转化为BeanDefinition
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        reader.loadBeanDefinitions(resource);

        // 将生成的beanFactory赋给当前对象,后续调用获取对象以及注册都是通过这个beanFactory
        this.beanFactory = beanFactory;
    }

    // context再对外提供一个getBean，底下就是调用的BeanFactory对应的方法
    public Object getBean(String beanName) throws BeansException {
        return this.beanFactory.getBean(beanName);
    }

    // 是否存在bean
    public boolean containsBean(String name) {
        return this.beanFactory.containsBean(name);
    }

    // 注册bean
    public void registerBean(BeanDefinition beanDefinition) {
        this.beanFactory.registerBean("beanName", beanDefinition);
    }

    // 该方法只是从xml中读取所有定义的bean，获取到id以及他们的路径，并且存储
    private void readXml(String fileName) {
        SAXReader saxReader = new SAXReader();
        try {
            // 这种方法是从编译后的classes目录来加载资源
            URL xmlPath = this.getClass().getClassLoader().getResource(fileName);
            Document document = saxReader.read(xmlPath);
            Element rootElement = document.getRootElement();
            // 对配置文件中的每一个<bean>，进行处理
            for (Element element : (List<Element>) rootElement.elements()) {
                // 获取Bean的基本信息
                String beanID = element.attributeValue("id");
                String beanClassName = element.attributeValue("class");
                BeanDefinition beanDefinition = new BeanDefinition(beanID, beanClassName);
                // 将Bean的定义存放到beanDefinitions
                beanDefinitions.add(beanDefinition);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    // 利用反射创建Bean实例，并存储在singletons中
    private void instanceBeans() {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            try {
                singletons.put(beanDefinition.getId(), Class.forName(beanDefinition.getClassName()).getDeclaredConstructor().newInstance());
            } catch (IllegalAccessException | InstantiationException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void registerBean(String beanName, Object obj) {

    }

    @Override
    public boolean isSingleton(String name) {
        return false;
    }

    @Override
    public boolean isPrototype(String name) {
        return false;
    }

    @Override
    public Class<?> getType(String name) {
        return null;
    }

    @Override
    public void publishEvent(ApplicationEvent event) {

    }
}