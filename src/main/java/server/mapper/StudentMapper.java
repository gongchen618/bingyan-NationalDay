package server.mapper;

import org.apache.ibatis.annotations.Param;
import server.sql.Student;

import java.util.List;

public interface StudentMapper {
    public List<Student> getStudentList(@Param("table") String table);
    int resetStatus(@Param("table") String table);
    Student getStudentById (@Param("table") String table, @Param("studentId") int studentId);
    int changeStudentStatus (@Param("table") String table, @Param("studentId") int studentId, @Param("status") String status);
    int changeStudentPassword (@Param("table") String table, @Param("studentId") int studentId, @Param("password") String password);
    //int setStudentPort (@Param("table") String table, @Param("studentId") int studentId, @Param("port") int port);
    //白写了...
    int isTableExist (@Param("table") String table);
}
