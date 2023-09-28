package service;

public class BaseService {

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
}
