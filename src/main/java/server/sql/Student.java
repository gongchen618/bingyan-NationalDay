package server.sql;

import org.json.JSONObject;

public class Student {
    private int studentId;
    private String name;
    private String password;
    private String status;

    public String getIdAndName () {
        return name + "(" + studentId + ") ";
    }

    public Student(){}
    public Student(int studentId, String name, String password, String status) {
        this.studentId = studentId;
        this.name = name;
        this.password = password;
        this.status = status;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    /*public String getName() {
        return name;
    }*/

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
}
