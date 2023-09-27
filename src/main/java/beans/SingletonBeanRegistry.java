package beans;

/**
 * 这是一个注册bean的接口，我们可以有很多个实现方式，用不同的方法生成bean
 */
public interface SingletonBeanRegistry {

    void registerSingleton(String beanName, Object singletonObject);

    Object getSingleton(String beanName);

    boolean containsSingleton(String beanName);

    String[] getSingletonNames();
}
