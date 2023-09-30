package beans.factory.config;

import beans.factory.ListableBeanFactory;
import beans.factory.config.ConfigurableBeanFactory;
import beans.factory.support.AutowireCapableBeanFactory;

//
public interface ConfigurableListableBeanFactory extends ListableBeanFactory, AutowireCapableBeanFactory, ConfigurableBeanFactory {

}
