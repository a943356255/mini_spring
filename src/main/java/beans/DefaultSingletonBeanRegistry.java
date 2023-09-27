package beans;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * beanNames用于存储所有单例 Bean 的别名，
 * singletons 则存储 Bean 名称和实现类的映射关系。
 */
public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {

    // 容器中存放所有bean的名称的列表
    protected List<String> beanNames = new ArrayList<>();
    // 容器存放所有bean的实例
    protected final Map<String, Object> singletons = new ConcurrentHashMap<>(256);

    @Override
    public void registerSingleton(String beanName, Object singletonObject) {
        // 同步的代码快，一次只能有一个线程去创建，后续应该会该。
        synchronized (this.singletons) {
            this.singletons.put(beanName, singletonObject);
            this.beanNames.add(beanName);
        }
    }

    @Override
    public Object getSingleton(String beanName) {
        return this.singletons.get(beanName);
    }

    @Override
    public boolean containsSingleton(String beanName) {
        return this.singletons.containsKey(beanName);
    }

    @Override
    public String[] getSingletonNames() {
        return (String[]) this.beanNames.toArray();
    }

    protected void removeSingleton(String beanName) {
        synchronized (this.singletons) {
            this.beanNames.remove(beanName);
            this.singletons.remove(beanName);
        }
    }
}
