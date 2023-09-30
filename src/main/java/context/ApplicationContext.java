package context;

import beans.factory.ListableBeanFactory;
import beans.factory.config.ConfigurableBeanFactory;
import beans.factory.config.ConfigurableListableBeanFactory;
import core.env.Environment;
import core.env.EnvironmentCapable;
import exception.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;

public interface ApplicationContext extends EnvironmentCapable, ListableBeanFactory, ConfigurableBeanFactory, ApplicationEventPublisher {

    String getApplicationName();

    long getStartupDate();

    ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;

    void setEnvironment(Environment environment);

    Environment getEnvironment();

    void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor);

    void refresh() throws BeansException, IllegalStateException;

    void close();

    boolean isActive();

}
