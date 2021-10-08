package server.sql;

public class Student {
    private int studentId;
    private String name;
    private String password;
    private String status;
    private String performance;
    private int SST;

    public String getIdAndName () {
        return name + "(" + studentId + ") ";
    }

    public Student(){}
    public Student(int studentId, String name){
        this.studentId = studentId;
        this.name = name;
    }

    public Student(int studentId, String name, String password, String status, String performance, int SST) {
        this.studentId = studentId;
        this.name = name;
        this.password = password;
        this.status = status;
        this.performance = performance;
        this.SST = SST;
    }

    public int getStudentId() {
        return studentId;
    }
    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getPerformance() {
        return performance;
    }
    public void setPerformance(String performance) {
        this.performance = performance;
    }

    public int getSST() {
        return SST;
    }
    public void setSST(int SST) {
        this.SST = SST;
    }
}
