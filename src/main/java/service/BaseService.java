package service;

import beans.factory.annotation.Autowired;

public class BaseService {

    @Autowired
    private BaseBaseService bbs;

    public BaseService() {

    }

    public BaseService(BaseBaseService bbs) {
        this.bbs = bbs;
    }

    public BaseBaseService getBbs() {
        return bbs;
    }

    public void setBbs(BaseBaseService bbs) {
        this.bbs = bbs;
    }

    public void sayHello() {
        System.out.println("Base Service says Hello");
        bbs.sayHello();
    }
}
