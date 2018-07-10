package guava.cache;

import com.google.common.base.MoreObjects;

/**
 * descrption: 工人
 * authohr: wangji
 * date: 2018-02-05 14:26
 */
public class Employee {
    private final String name;
    private final String dept;
    private final String empID;


    /**
     * @desction:
     *
     * @author: wangji
     * @date: 2018/3/5 16:12
     *@param name
     * @param dept
     * @param empID
     */
    public Employee(String name, String dept, String empID) {

        this.name = name;
        this.dept = dept;
        this.empID = empID;
    }

    public String getName() {
        return name;
    }

    public String getDept() {
        return dept;
    }

    public String getEmpID() {
        return empID;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("Name", this.getName()).add("Department", getDept())
                .add("EmployeeID", this.getEmpID()).toString();
    }
}
