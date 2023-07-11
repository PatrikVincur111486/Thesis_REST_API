package sk.stuba.fei.uim.vsa.pr1.bonus;

import java.util.List;

public class MyPage implements Page{
    List<Object> results;

    Pageable pageable;

    public MyPage(Pageable pageable) {
        this.pageable = pageable;
    }

    @Override
    public List getContent() {
        return null;
    }

    @Override
    public Pageable getPageable() {
        return pageable;
    }

    @Override
    public Long getTotalElements() {
        return null;
    }

    @Override
    public void setTotalElements(Long totalElements) {

    }

    @Override
    public int getTotalPages() {
        return 0;
    }
}
