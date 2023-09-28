package beans.factory;

import beans.BeanDefinitionRegistry;
import entity.*;
import beans.DefaultSingletonBeanRegistry;
import exception.BeansException;
import service.BaseService;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// 这里是用工厂方法，来替换ClassPathXmlApplicationContext类
/**
 * 实现了BeanFactory和BeanDefinitionRegistry，那么它即是一个工厂，同时也是一个仓库，既能生产，也生存储
 */
public class SimpleBeanFactory extends DefaultSingletonBeanRegistry implements BeanFactory, BeanDefinitionRegistry {

//    private final Map<String, BeanDefinition> beanDefinitions = new ConcurrentHashMap<>(256);
    // 存放具体的名称
    private final List<String> beanDefinitionNames = new ArrayList<>();

    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);

    /**
     * 用来存放毛坯实例,作用如下：
     * Spring 的做法是在 BeanFactory 中引入一个结构：earlySingletonObjects，这里面存放的就是早期的毛胚实例。
     * 创建 Bean 实例的时候，不用等到所有步骤完成，而是可以在属性还没有注入之前，就把早期的毛胚实例先保存起来，供属性注入时使用。
     * 是为了解决循环依赖，比如创建A需要依赖B，而创建B需要依赖C，创建C又依赖A，就提前创建毛坯存入供他们创建使用
     */
    private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>(256);

    public SimpleBeanFactory() {

    }

    /**
     * 如何处理 constructor？
     * 首先，获取 XML 配置中的属性值，这个时候它们都是通用的 Object 类型，我们需要根据 type 字段的定义判断不同 Value 所属的类型。
     * 作为一个原始的实现这里我们只提供了 String、Integer 和 int 三种类型的判断。
     * 最终通过反射构造对象，将配置的属性值注入到了 Bean 对象中，实现构造器注入。
     *
     * 如何处理 property？
     * 和处理 constructor 相同，要通过 type 字段确定 Value 的归属类型。
     * 但不同之处在于，判断好归属类型后，我们还要手动构造 setter 方法，通过反射将属性值注入到 setter 方法之中。
     * 通过这种方式来实现对属性的赋值。
     *
     * 该方法是通过传入的BeanDefinition（已经封装好了propertyValues和ArgumentValues，这两个属性是从xml中读取的）
     * 来创建对应的bean
     *
     * 其实代码的核心是通过 Java 的反射机制调用构造器及 setter 方法，在调用过程中根据具体的类型把属性值作为一个参数赋值进去。
     * 这也是所有的框架在实现 IoC 时的思路。反射技术是 IoC 容器赖以工作的基础。
     */
    private Object createBean(BeanDefinition beanDefinition) {
        Class<?> clz = null;

        // 创建毛胚bean实例，这里创建的实例是没有具体的属性的
        Object obj = doCreateBean(beanDefinition);
        // 存放到毛胚实例缓存中
        this.earlySingletonObjects.put(beanDefinition.getId(), obj);

        try {
            clz = Class.forName(beanDefinition.getClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // 处理属性,因为属性中有指向其他对象的ref，所以需要特殊处理一下
        // 这里是创建完毛坯实例后，进行后续属性的补齐
        handleProperties(beanDefinition, clz, obj);
        return obj;
    }

    /**
     * 这里只创建了一个空的实例，并没有对属性进行赋值
     */
    private Object doCreateBean(BeanDefinition beanDefinition) {
        Class<?> clz;
        Object obj = null;
        Constructor<?> con;

        try {
            clz = Class.forName(beanDefinition.getClassName());
            // 处理构造器参数
            ArgumentValues argumentValues = beanDefinition.getConstructorArgumentValues();
            // 如果有参数
            if (!argumentValues.isEmpty()) {
                Class<?>[] paramTypes = new Class<?>[argumentValues.getArgumentCount()];
                Object[] paramValues = new Object[argumentValues.getArgumentCount()];
                // 对每一个参数，分数据类型分别处理
                for (int i = 0; i < argumentValues.getArgumentCount(); i++) {
                    ArgumentValue argumentValue = argumentValues.getIndexedArgumentValue(i);
                    if ("String".equals(argumentValue.getType()) || "java.lang.String".equals(argumentValue.getType())) {
                        paramTypes[i] = String.class;
                        paramValues[i] = argumentValue.getValue();
                    } else if ("Integer".equals(argumentValue.getType()) || "java.lang.Integer".equals(argumentValue.getType())) {
                        paramTypes[i] = Integer.class;
                        paramValues[i] = Integer.valueOf((String)argumentValue.getValue());
                    } else if ("int".equals(argumentValue.getType())) {
                        paramTypes[i] = int.class;
                        paramValues[i] = Integer.valueOf((String) argumentValue.getValue());
                    } else {
                        // 默认为string
                        paramTypes[i] = String.class;
                        paramValues[i] = argumentValue.getValue();
                    }
                }

                try {
                    // 按照特定构造器创建实例
                    con = clz.getConstructor(paramTypes);
                    obj = con.newInstance(paramValues);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // 如果没有参数，直接创建实例
                obj = clz.getDeclaredConstructor().newInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return obj;
    }

    /**
     * 核心是以下两行
     * paramTypes[0] = Class.forName(pType);
     * paramValues[0] = getBean((String)pValue);
     * 对 ref 所指向的另一个 Bean 再次调用 getBean() 方法，这个方法会获取到另一个 Bean 实例，这样就实现了另一个 Bean 的注入。
     * 该方法会对毛坯实例的属性赋值
     */
    private void handleProperties(BeanDefinition bd, Class<?> clz, Object obj) {
        System.out.println("handle properties for bean : " + bd.getId());
        // 处理属性
        PropertyValues propertyValues = bd.getPropertyValues();
        if (!propertyValues.isEmpty()) {
            for (int i = 0; i < propertyValues.size(); i++) {
                PropertyValue propertyValue = propertyValues.getPropertyValueList().get(i);
                String pName = propertyValue.getName();
                String pType = propertyValue.getType();
                Object pValue = propertyValue.getValue();
                boolean isRef = propertyValue.isRef();
                Class[] paramTypes = new Class[1]; Object[] paramValues = new Object[1];

                if (!isRef) {
                    // 如果不是ref，只是普通属性
                    // 对每一个属性，分数据类型分别处理
                    if ("String".equals(pType) || "java.lang.String".equals(pType)) {
                        paramTypes[0] = String.class;
                    } else if ("Integer".equals(pType) || "java.lang.Integer".equals(pType)) {
                        paramTypes[0] = Integer.class;
                    } else if ("int".equals(pType)) {
                        paramTypes[0] = int.class;
                    } else {
                        paramTypes[0] = String.class;
                    }
                    paramValues[0] = pValue;
                } else {
                    // 是ref
                    try {
                        paramTypes[0] = Class.forName(pType);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    try {
                        // 这里，是ref的话就再次调用getBean，然后传入对应的信息
                        paramValues[0] = getBean((String) pValue);
                    } catch (BeansException e) {
                        e.printStackTrace();
                    }
                }

                String methodName = "set" + pName.substring(0,1).toUpperCase() + pName.substring(1);
                Method method = null;
                try {
                    method = clz.getMethod(methodName, paramTypes);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }

                try {
                    method.invoke(obj, paramValues);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 根据名称获取对应的实例，如果存在直接取出返回，如果不存在则注册该实例
    @Override
    public Object getBean(String beanName) throws BeansException {
        // 先尝试直接拿bean实例
        Object singleton = this.getSingleton(beanName);
        // 如果此时还没有这个bean的实例，则获取它的定义来创建实例
        if (singleton == null) {
            // 这里是从毛坯中获取对应的实例
            singleton = this.earlySingletonObjects.get(beanName);
            // 如果毛坯中仍然没有，则创建bean实例并注册
            if (singleton == null) {
                // 获取bean的定义
                BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
                if (beanDefinition == null) {
                    throw new BeansException("No bean.");
                }
                singleton = createBean(beanDefinition);
                // 新注册这个bean实例
                this.registerSingleton(beanName, singleton);
            }
        }

        return singleton;
    }

    // 可以激活整个Ioc容器
    public void refresh() {
        for (String beanName : beanDefinitionNames) {
            try {
                getBean(beanName);
            } catch (BeansException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 该方法是该工厂私有的
     * @param name 根据
     * @param beanDefinition 一个已经封装好的beanDefinition
     */
    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) {
        this.beanDefinitionMap.put(name, beanDefinition);
        this.beanDefinitionNames.add(name);
//        if (!beanDefinition.isLazyInit()) {
//            try {
//                getBean(name);
//            } catch (BeansException e) {
//                e.printStackTrace();
//            }
//        }
    }

    public void removeBeanDefinition(String name) {
        this.beanDefinitionMap.remove(name);
        this.beanDefinitionNames.remove(name);
        this.removeSingleton(name);
    }

    @Override
    public BeanDefinition getBeanDefinition(String name) {
        return this.beanDefinitionMap.get(name);
    }

    @Override
    public boolean containsBeanDefinition(String name) {
        return this.beanDefinitionMap.containsKey(name);
    }

    public boolean containsBean(String name) {
        return containsSingleton(name);
    }

    @Override
    public boolean isSingleton(String name) {
        return this.beanDefinitionMap.get(name).isSingleton();
    }

    @Override
    public boolean isPrototype(String name) {
        return this.beanDefinitionMap.get(name).isPrototype();
    }

    @Override
    public Class<?> getType(String name) {
        return this.beanDefinitionMap.get(name).getClass();
    }

    public void registerBean(String beanName, Object obj) {
        this.registerSingleton(beanName, obj);
    }
}
