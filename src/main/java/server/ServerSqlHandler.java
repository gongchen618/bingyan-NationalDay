package server;

import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import server.sql.MybatisUtils;
import server.sql.Student;
import server.mapper.StudentMapper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.max;
import static server.ServerCore.getClassId;

public class ServerSqlHandler {

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
        List<Student> studentList = studentMapper.getStudentList(getClassId());

        for (Student student : studentList){
            System.out.println(student);
        }

        sqlSession.close();
    }

    public static void printStudentListByOrder(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
        List<Student> studentList = studentMapper.getStudentList(getClassId());

        int maxNameLen = 0;
        for (Student student : studentList) maxNameLen = max (maxNameLen, student.getIdAndName().length());

            System.out.println("当前班级：" + getClassId());
        int sumTotal = 0, sumOnline = 0, sumLate = 0, sumLeave = 0, sumNormal = 0;

        for (Student student : studentList){
            System.out.print("[" + String.format("%-7s", student.getStatus()) + "] "
                    + String.format(("%-" + String.valueOf(maxNameLen) + "s"), student.getIdAndName())
                    + student.getPerformance());
            if (student.getStatus().equals("Online")) System.out.println(" " + student.getSST());
            else System.out.println("");

            sumTotal += 1;
            if (student.getStatus().equals("Online")) ++sumOnline;
            if (student.getPerformance().indexOf("迟到") != -1) ++sumLate;
            if (student.getPerformance().indexOf("早退") != -1) ++sumLeave;
            if (student.getPerformance().equals("正常")) ++sumNormal;
        }
        System.out.println("[总人数] " + sumTotal + "   [在线] " + sumOnline + "  [正常] " + sumNormal
                + "   [迟到] " + sumLate + "   [早退] " + sumLeave);

        sqlSession.close();
    }

    public static void resetPerfomance(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        int res = studentMapper.resetPerfomance(getClassId());
        if (res > 0) {
            System.out.println ("已重置所有学生状态为 \"缺席\"");
        } else {
            System.out.println ("学生状态重置失败!");
        }

        sqlSession.commit();
        sqlSession.close();
    }

    public static void resetScreenStaticTime(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        int res = studentMapper.resetScreenStaticTime(getClassId());
        if (res > 0) {
            System.out.println ("已重置所有学生的屏幕静止时间");
        } else {
            System.out.println ("学生状态重置失败!");
        }

        sqlSession.commit();
        sqlSession.close();
    }

    public static Student getStudentById(int studentId){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        Student student = studentMapper.getStudentById(getClassId(), studentId);
        //System.out.println(student);

        sqlSession.close();
        return student;
    }

    public static void changeStudentPerformance(int studentId, String performance){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        Student student = studentMapper.getStudentById(getClassId(), studentId);
        int res = studentMapper.changeStudentPerformance(getClassId(), studentId, "\"" + performance + "\"");
        System.out.println(student.getIdAndName() + "状态更新为：" + performance);

        sqlSession.commit();
        sqlSession.close();
    }

    public static void changeStudentStatus (int studentId, String status){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        Student student = studentMapper.getStudentById(getClassId(), studentId);
        int res = studentMapper.changeStudentStatus(getClassId(), studentId, "\"" + status + "\"");
        if (status.equals("Online")) System.out.println(student.getIdAndName() + "已上线");
        else System.out.println(student.getIdAndName() + "已离线");

        sqlSession.commit();
        sqlSession.close();
    }

    public static void changeStudentScreenStaticTime(int studentId, int SST){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        int res = studentMapper.changeStudentScreenStaticTime(getClassId(), studentId, SST);

        sqlSession.commit();
        sqlSession.close();
    }

    public static void changeStudentPassword(int studentId, String password){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        int res = studentMapper.changeStudentPassword(getClassId(), studentId, "\"" + password + "\"");

        sqlSession.commit();
        sqlSession.close();
    }

    public static boolean isTableExist (String table){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        int cnt = studentMapper.isTableExist("\"" + table + "\"");

        sqlSession.commit();
        sqlSession.close();

        if (cnt == 0) return false;
        else return true;
    }

    public static void printTableName () {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        List<String> list = studentMapper.getTableNameList();
        System.out.println("当前班级有：" + list);

        sqlSession.commit();
        sqlSession.close();
    }

    public static void deleteOldClass (String name){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        Integer res = studentMapper.deleteOldTable ("`" + name + "`");

        sqlSession.commit();
        sqlSession.close();
    }

    public static void createNewClass (String name){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        Integer res = studentMapper.deleteOldTable ("`" + name + "`");
        sqlSession.commit();

        res = studentMapper.createNewTable( "`" + name + "`");
        sqlSession.commit();

        sqlSession.close();
    }

    public static void insertNewStudents (String name, List<Student> list){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        try {
            Integer res = studentMapper.insertNewStudents("`" + name + "`", list);
        } catch (Exception x){
            System.out.println("插入数据失败，请检查学号是否重复");
        }

        sqlSession.commit();
        sqlSession.close();
    }

    public static void createNewTest (String testName){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        if (studentMapper.isColumnExist("\'class3\'", "\'" + testName + "\'") > 0) {
            System.out.println("测试 " + testName + " 创建失败！当前班级已存在同名测试");
        } else {
            Integer res = studentMapper.createNewColumn("`class3`", "`" + testName + "`");
            System.out.println("测试 " + testName + " 创建成功！");
        }

        sqlSession.commit();
        sqlSession.close();
    }
}
