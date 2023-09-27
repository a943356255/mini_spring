package factory;

import beans.BeanDefinitionRegistry;
import entity.BeanDefinition;
import beans.DefaultSingletonBeanRegistry;
import exception.BeansException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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

    public SimpleBeanFactory() {

    }

    // 根据名称获取对应的实例，如果存在直接取出返回，如果不存在则注册该实例
    public Object getBean(String beanName) throws BeansException {
        // 先尝试直接拿bean实例
        Object singleton = this.getSingleton(beanName);
        // 如果此时还没有这个bean的实例，则获取它的定义来创建实例
        if (singleton == null) {
            // 获取bean的定义
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (beanDefinition == null) {
                throw new BeansException("No bean.");
            }
            try {
                singleton = Class.forName(beanDefinition.getClassName()).getDeclaredConstructor().newInstance();
            } catch (IllegalAccessException | InstantiationException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
            // 新注册这个bean实例
            this.registerSingleton(beanName, singleton);
        }

        return singleton;
    }

    /**
     * 该方法是该工厂私有的
     * @param name 根据
     * @param beanDefinition 一个已经封装好的beanDefinition
     */
    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) {
        this.beanDefinitionMap.put(name, beanDefinition);
        this.beanDefinitionNames.add(name);
        if (!beanDefinition.isLazyInit()) {
            try {
                getBean(name);
            } catch (BeansException e) {
                e.printStackTrace();
            }
        }
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

//    // 存储所有从xml中解析的bean的id以及name,组装成BeanDefinition
//    private final List<BeanDefinition> beanDefinitions = new ArrayList<>();
//    // 仅仅存储beanNames，用于快速判断xml中是否有该元素
//    private final List<String> beanNames = new ArrayList<>();
//    // 这里存储已经创建了的实例
//    private final Map<String, Object> singletons = new HashMap<>();
//
//    public SimpleBeanFactory() {
//
//    }
//
//    // getBean，容器的核心方法
//    public Object getBean(String beanName) throws BeansException {
//        // 先尝试直接拿Bean实例
//        Object singleton = singletons.get(beanName);
//        // 如果此时还没有这个Bean的实例，则获取它的定义来创建实例
//        if (singleton == null) {
//            int i = beanNames.indexOf(beanName);
//            // i不存在，说明从xml中加载的内容没有该beanName，直接返回
//            if (i == -1) {
//                throw new BeansException();
//            } else {
//                // 获取Bean的定义
//                BeanDefinition beanDefinition = beanDefinitions.get(i);
//                try {
//                    singleton = Class.forName(beanDefinition.getClassName()).getDeclaredConstructor().newInstance();
//                } catch (IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
//                    e.printStackTrace();
//                }
//                // 注册Bean实例
//                singletons.put(beanDefinition.getId(), singleton);
//            }
//        }
//
//        return singleton;
//    }
//
//    @Override
//    public Boolean containsBean(String name) {
//        return null;
//    }
//
//    @Override
//    public void registerBean(String beanName, Object obj) {
//
//    }
//
//    public void registerBean(String BeanName, BeanDefinition beanDefinition) {
//        this.beanDefinitions.add(beanDefinition);
//        this.beanNames.add(beanDefinition.getId());
//    }
}
