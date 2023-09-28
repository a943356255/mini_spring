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

    /**
     * 程序解析 标签后，获取了 ref 的参数，同时有针对性地设置了 isRef 的值，把它添加到了 PropertyValues 内，
     * 最后程序调用 setDependsOn 方法，它记录了某一个 Bean 引用的其他 Bean。这样，我们引用 ref 的配置就定义好了。
     * 上述解释是在xml中添加了一个对象引用另一个对象的ref后，如何解析xml文件
     */
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
            List<String> refs = new ArrayList<>();
            for (Element e : propertyElements) {
                String pType = e.attributeValue("type");
                String pName = e.attributeValue("name");
                String pValue = e.attributeValue("value");
                String pRef = e.attributeValue("ref");
                String pV;
                boolean isRef;
                if (pValue != null && !pValue.equals("")) {
                    isRef = false;
                    pV = pValue;
                } else {
                    isRef = true;
                    pV = pRef;
                    refs.add(pRef);
                }
                PVS.addPropertyValue(new PropertyValue(pType, pName, pV, isRef));
            }
            beanDefinition.setPropertyValues(PVS);

            // 处理构造器参数
            List<Element> constructorElements = element.elements("constructor-arg");
            ArgumentValues AVS = new ArgumentValues();
            for (Element e : constructorElements) {
                String aType = e.attributeValue("type");
                String aName = e.attributeValue("name");
                String aValue = e.attributeValue("value");
                AVS.addArgumentValue(new ArgumentValue(aType, aName, aValue));
            }
            beanDefinition.setConstructorArgumentValues(AVS);

            String[] refArray = refs.toArray(new String[0]);
            // 这里是设置了它的依赖
            beanDefinition.setDependsOn(refArray);
            // 存储进工厂的list,这里beanId已经是动态获取的
            this.simpleBeanFactory.registerBeanDefinition(beanID, beanDefinition);
        }
    }
}