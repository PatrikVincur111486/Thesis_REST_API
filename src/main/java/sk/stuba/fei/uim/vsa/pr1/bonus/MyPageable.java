package sk.stuba.fei.uim.vsa.pr1.bonus;

public class MyPageable implements Pageable{
    int page;

    int size;

    public MyPageable(int page, int size) {
        this.page = page;
        this.size = size;
    }

    @Override
    public Pageable of(int page, int size) {
        return new MyPageable(page,size);
    }

    @Override
    public Pageable first() {
        return new MyPageable(0,size);
    }

    @Override
    public Pageable previous() {
        if(page==0) return null;
        return new MyPageable(page-1,size);
    }

    @Override
    public Pageable next() {
        return new MyPageable(page+1,size);
    }

    @Override
    public Integer getPageNumber() {
        return page;
    }

    @Override
    public Integer getPageSize() {
        return size;
    }
}
