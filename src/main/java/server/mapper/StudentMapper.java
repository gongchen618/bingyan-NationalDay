package server.mapper;

import org.apache.ibatis.annotations.Param;
import server.sql.Student;

import java.util.List;
import java.util.Map;

public interface StudentMapper {
    //对table的操作
    int isTableExist (@Param("table") String table);
    List<String> getTableNameList();
    Integer createNewTable (@Param("name") String name);
    Integer deleteOldTable (@Param("name") String name);

    //对square的操作
    List<Student> getAllExceptTest(@Param("table") String table);
    //这里应该有个 getAll 但是我懒得写了(列出所有学生的每次成绩)

    //对row的操作
    int isStudentExist (@Param("table") String table, @Param("studentId") int StudentId);
    Student getStudentById (@Param("table") String table, @Param("studentId") int studentId);
    Integer deleteStudent (@Param("table") String table, @Param("studentId") int StudentId);
    Integer insertNewStudents (@Param("table") String table, @Param("list") List<Student> list);

    List<String> getTestNameList (@Param("table") String table);
    Map<String, Object> getStudentTestList (@Param("table") String table, @Param("studentId") int StudentId);

    //对column的操作
    int isColumnExist (@Param("table") String table, @Param("column") String column);
    Integer createNewColumn (@Param("table") String table, @Param("column") String column);
    int resetColumn (@Param("table") String table, @Param("column") String column, @Param("val") String val);
    List<String> getColumn (@Param("table") String table, @Param("column") String column);
    void deleteColumn (@Param("table") String table, @Param("column") String column);
    //这里应该有一个查询一列的 val + name 但是我懒得写了(列出所有学生某次考试的成绩)

    //对cell的操作
    Object getCell (@Param("table") String table, @Param("studentId") int studentId, @Param("column") String column);
    int setCell (@Param("table") String table, @Param("studentId") int studentId, @Param("column") String column, @Param("val") String val);
}
