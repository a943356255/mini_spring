package beans;

import exception.BeansException;

public interface BeanFactory {

    Object getBean(String beanName) throws BeansException;

    void registerBeanDefinition(BeanDefinition beanDefinition);

}
