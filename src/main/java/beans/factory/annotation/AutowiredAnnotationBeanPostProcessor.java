package beans.factory.annotation;

import beans.factory.BeanFactory;
import beans.factory.support.AutowireCapableBeanFactory;
import exception.BeansException;
import utils.Autowired;
import beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

public class AutowiredAnnotationBeanPostProcessor implements BeanPostProcessor {

    private BeanFactory beanFactory;

    /**
     * 该方法是在创建该bean之前，遍历所有的成员属性，看是否加了@Autowired标签，如果加了该标签，找到该bean，并进行注入
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Object result = bean;

        Class<?> clazz = bean.getClass();
        Field[] fields = clazz.getDeclaredFields();

        if (fields != null) {
            // 对每一个属性进行判断，如果带有@Autowired注解则进行处理
            for (Field field : fields) {
                // 核心代码
                boolean isAutowired = field.isAnnotationPresent(Autowired.class);
                if (isAutowired) {
                    // 根据属性名查找同名的bean
                    String fieldName = field.getName();
                    Object autowiredObj = this.getBeanFactory().getBean(fieldName);
                    // 设置属性值，完成注入
                    try {
                        field.setAccessible(true);
                        field.set(bean, autowiredObj);
                        System.out.println("autowire " + fieldName + " for bean " + beanName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return result;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return null;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public void setBeanFactory(AutowireCapableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
