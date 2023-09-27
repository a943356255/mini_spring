package beans.factory.xml;

import core.Resource;
import entity.*;
import beans.factory.BeanFactory;
import beans.factory.SimpleBeanFactory;
import org.dom4j.Element;

import java.util.*;

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

            // 处理属性
            List<Element> propertyElements = element.elements("property");
            PropertyValues PVS = new PropertyValues();
            for (Element e : propertyElements) {
                String pType = e.attributeValue("type");
                String pName = e.attributeValue("name");
                String pValue = e.attributeValue("value");
                PVS.addPropertyValue(new PropertyValue(pType, pName, pValue));
            }
            beanDefinition.setPropertyValues(PVS);

            //处理构造器参数
            List<Element> constructorElements = element.elements("constructor-arg");
            ArgumentValues AVS = new ArgumentValues();
            for (Element e : constructorElements) {
                String aType = e.attributeValue("type");
                String aName = e.attributeValue("name");
                String aValue = e.attributeValue("value");
                AVS.addArgumentValue(new ArgumentValue(aType, aName, aValue));
            }
            beanDefinition.setConstructorArgumentValues(AVS);

            // 存储进工厂的list,这里beanId已经是动态获取的
            this.simpleBeanFactory.registerBeanDefinition(beanID, beanDefinition);
            // 下面这种方法导致no bean
//            this.simpleBeanFactory.registerBean("beanName", beanDefinition);
        }
    }
}