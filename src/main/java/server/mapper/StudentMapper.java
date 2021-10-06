package server.mapper;

import org.apache.ibatis.annotations.Param;
import server.sql.Student;

import java.util.List;

public interface StudentMapper {
    public List<Student> getStudentList(@Param("table") String table);
    int resetPerfomance (@Param("table") String table);
    int resetScreenStaticTime (@Param("table") String table);
    Student getStudentById (@Param("table") String table, @Param("studentId") int studentId);
    int changeStudentPerformance (@Param("table") String table, @Param("studentId") int studentId, @Param("performance") String performance);
    int changeStudentStatus (@Param("table") String table, @Param("studentId") int studentId, @Param("status") String status);
    int changeStudentScreenStaticTime (@Param("table") String table, @Param("studentId") int studentId, @Param("SST") int SST);
    int changeStudentPassword (@Param("table") String table, @Param("studentId") int studentId, @Param("password") String password);
    int isTableExist (@Param("table") String table);
    List<String> getTableNameList();
    Integer createNewTable (@Param("name") String name);
    Integer deleteOldTable (@Param("name") String name);
    Integer insertNewStudents(@Param("table") String table, @Param("list") List<Student> list);
    int isColumnExist (@Param("table") String table, @Param("testName") String testName);
    Integer createNewColumn (@Param("table") String table, @Param("testName") String testName);
}
