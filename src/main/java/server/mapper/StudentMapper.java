package server.mapper;

import server.sql.Student;

import java.util.List;

public interface StudentMapper {
    public List<Student> getStudentList();
    Student getStudentById (int studentId);
    int changeStudentStatus (Student student);
    int changeStudentPassword (Student student);
}
