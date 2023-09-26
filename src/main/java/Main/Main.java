package Main;

import exception.BeansException;
import service.AService;
import utils.ClassPathXmlApplicationContext;

public class Main {

    public static void main(String[] args) throws BeansException {
        // 这里默认读取resources下的文件，因为内部实现使用了classloader，只能寻找编译过后的文件
        ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("file/test.xml");
        AService aService = (AService) classPathXmlApplicationContext.getBean("aService");
        aService.sayHello();
    }

}
