package context;

public interface ApplicationEventPublisher {

    void publishEvent(ApplicationEvent event);

}
