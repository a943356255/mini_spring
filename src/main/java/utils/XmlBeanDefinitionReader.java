package utils;

import entity.BeanDefinition;
import factory.BeanFactory;
import factory.SimpleBeanFactory;
import org.dom4j.Element;

// 将解析好的xml转换为BeanDefinition
public class XmlBeanDefinitionReader {
    // 工厂
    BeanFactory beanFactory;

    // 简单工厂
    SimpleBeanFactory simpleBeanFactory;

    public XmlBeanDefinitionReader(SimpleBeanFactory simpleBeanFactory) {
        this.simpleBeanFactory = simpleBeanFactory;
    }

    public void loadBeanDefinitions(Resource resource) {
        // resource里存储了所有从xml中解析出来的bean
        while (resource.hasNext()) {
            Element element = (Element) resource.next();
            // 构造BeanDefinition实例
            String beanID = element.attributeValue("id");
            String beanClassName = element.attributeValue("class");
            BeanDefinition beanDefinition = new BeanDefinition(beanID, beanClassName);
            // 存储进工厂的list
            this.simpleBeanFactory.registerBean("beanName", beanDefinition);
        }
    }
}