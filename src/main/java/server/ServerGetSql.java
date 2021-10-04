package server;

import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import server.sql.MybatisUtils;
import server.sql.Student;
import server.mapper.StudentMapper;

import java.util.List;

public class ServerGetSql {

    static String classId = "class1";

    public static String getClassId() {
        return classId;
    }

    public static void setClassId(String classId) {
        ServerGetSql.classId = classId;
    }

    @Test
    public void justTest(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        /*enum STATUS{
            Absent, Normal, LeaveEarly, Late;
        }
        STATUS status = STATUS.Absent;
        System.out.println(status);
        */ //看样子好像是失败了

        sqlSession.commit();
        sqlSession.close();
    }

    public static void printStudentList(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
        List<Student> studentList = studentMapper.getStudentList(classId);

        for (Student student : studentList){
            System.out.println(student);
        }

        sqlSession.close();
    }

    public static void printStudentListByOrder(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
        List<Student> studentList = studentMapper.getStudentList(classId);

        System.out.println("当前班级：" + classId);
        int sumAbsent = 0, sumAll = 0, sumNormal = 0;
        for (Student student : studentList){
            System.out.println(student.getStudentId() + " " + student.getName() + " " + student.getStatus());
            ++sumAll;
            if (student.getStatus().equalsIgnoreCase("normal")) ++sumNormal;
            if (student.getStatus().equalsIgnoreCase("absent")) ++sumAbsent;
        }
        System.out.println("应到人数：" + sumAll + "；当前人数：" + (sumAll - sumAbsent)
                + "；缺席人数：" + sumAbsent + "；异常人数：" + (sumAll - sumNormal - sumAbsent));

        sqlSession.close();
    }

    public static void resetStatus(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        int res = studentMapper.resetStatus(classId);
        if (res > 0) {
            System.out.println ("已重置所有学生状态为 Absent");
        } else {
            System.out.println ("重置失败!");
        }

        sqlSession.commit();
        sqlSession.close();
    }

    public static Student getStudentById(int studentId){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        Student student = studentMapper.getStudentById(classId, studentId);
        //System.out.println(student);

        sqlSession.close();
        return student;
    }

    public static void changeStudentStatus(int studentId, String status){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        Student student = getStudentById(studentId);
        if (student != null) {
            int res = studentMapper.changeStudentStatus(classId, studentId, "\"" + status + "\"");
            if (res < 0) {
                System.out.println(student.getName() + " 状态修改失败，当前状态：" + status);
            } else {
                System.out.println(student.getName() + " 状态修改成功，当前状态：" + status);
            }
        } else {
            System.out.println("学生不存在！");
        }

        sqlSession.commit();
        sqlSession.close();
    }

    public static boolean changeStudentPassword(int studentId, String password){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        int res = studentMapper.changeStudentPassword(classId, studentId, "\"" + password + "\"");

        sqlSession.commit();
        sqlSession.close();

        if (res < 0) {
            return false;
        } else {
            return true;
        }
    }
}
