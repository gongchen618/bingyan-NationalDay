package server.sql;

import org.json.JSONObject;

public class Student {
    private int studentId;
    private String name;
    private String password;
    private String status;
    private int seat;

    public Student(){}

    public Student(int studentId, String name, String password, String status, int seat) {
        this.studentId = studentId;
        this.name = name;
        this.password = password;
        this.status = status;
        this.seat = seat;
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

    public int getSeat() {
        return seat;
    }

    public void setSeat(int seat) {
        this.seat = seat;
    }

    @Override
    public String toString() {
        return "Student{" +
                "studentId=" + studentId +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", status='" + status + '\'' +
                ", seat=" + seat +
                '}';
    }
}
