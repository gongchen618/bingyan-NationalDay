package server.sql;

import org.json.JSONObject;

public class Student {
    private int studentId;
    private String name;
    private String password;
    private String status;
    private int port;

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

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Student() {}
    public Student(int studentId, String name, String password, String status, int port) {
        this.studentId = studentId;
        this.name = name;
        this.password = password;
        this.status = status;
        this.port = port;
    }

    @Override
    public String toString() {
        return "Student{" +
                "studentId=" + studentId +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", status='" + status + '\'' +
                ", port=" + port +
                '}';
    }
}
