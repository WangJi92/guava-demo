package guava.order;

import lombok.extern.slf4j.Slf4j;

/**
 * descrption: 工作人员信息
 * authohr: wangji
 * date: 2018-02-07 10:19
 */
@Slf4j
public class Worker {
    private Integer workId;
    private String name;

    public Integer getWorkId() {
        return workId;
    }

    public void setWorkId(Integer workId) {
        this.workId = workId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Worker(Integer workId, String name) {
        this.workId = workId;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Worker{" +
                "workId=" + workId +
                ", name='" + name + '\'' +
                '}';
    }
}
