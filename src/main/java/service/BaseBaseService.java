package service;

import service.impl.AServiceImpl;

public class BaseBaseService {

    private AServiceImpl as;

    public BaseBaseService() {

    }

    public BaseBaseService(AServiceImpl as) {
        this.as = as;
    }

    public AServiceImpl getAs() {
        return as;
    }

    public void setAs(AServiceImpl as) {
        this.as = as;
    }
}
