package beans;

import org.springframework.beans.BeansException;

public interface BeanFactory {

    Object getBean(String beanName) throws BeansException, exception.BeansException;

    void registerBeanDefinition(BeanDefinition beanDefinition);

}
