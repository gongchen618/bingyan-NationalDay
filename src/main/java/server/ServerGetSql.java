package server;

import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import server.sql.MybatisUtils;
import server.sql.Student;
import server.mapper.StudentMapper;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;

public class ServerGetSql {

    @Test
    public void justTest(){
    }

    public static void getStudentList(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
        List<Student> studentList = studentMapper.getStudentList();

        for (Student student : studentList){
            System.out.println(student);
        }

        sqlSession.close();
    }

    public static Student getStudentById(int studentId){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        Student student = studentMapper.getStudentById(studentId);
        //System.out.println(student);

        sqlSession.close();
        return student;
    }

    public static void changeStudentStatus(int studentId, String status){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        Student student = new Student();
        student.setStudentId(studentId);//
        student.setStatus(status);//
        int res = studentMapper.changeStudentStatus(student);
        if (res < 0) {
            System.out.println("学生状态修改失败");
        } else {
            System.out.println("Change Successfully");
        }

        sqlSession.commit();
        sqlSession.close();
    }

    public static boolean changeStudentPassword(int studentId, String password){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        Student student = new Student();
        student.setStudentId(studentId);//
        student.setPassword(password);//
        int res = studentMapper.changeStudentPassword(student);

        sqlSession.commit();
        sqlSession.close();

        if (res < 0) {
            return false;
        } else {
            return true;
        }
    }
}
