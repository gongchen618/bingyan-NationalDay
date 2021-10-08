package server;

import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import server.sql.MybatisUtils;
import server.sql.Student;
import server.mapper.StudentMapper;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

    public static void printStudentListByOrder(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
        List<Student> studentList = studentMapper.getAllExceptTest(getClassId());

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
    public static Student getStudentById(int studentId){

        SqlSession sqlSession = MybatisUtils.getSqlSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        Student student = studentMapper.getStudentById(getClassId(), studentId);
        //System.out.println(student);

        sqlSession.close();
        return student;
    }

    public static void setCell(int studentId, String columnName, String val){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        Student student = getStudentById(studentId);
        int res = studentMapper.setCell(getClassId(), studentId, "`" + columnName + "`", "\"" + val + "\"");
        if (columnName.equals("status")){
            if (val.equals("Online")) System.out.println(student.getIdAndName() + "已上线");
            else System.out.println(student.getIdAndName() + "已离线");
        } else if (columnName.equals("performance")){
            System.out.println(student.getIdAndName() + "状态更新为：" + val);
        }

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

    public static boolean isTestExist (String test) {
        if (test.equals("studentId")) return false;
        if (test.equals("password")) return false;
        if (test.equals("name")) return false;
        if (test.equals("SST")) return false;
        if (test.equals("status")) return false;
        if (test.equals("performance")) return false;

        SqlSession sqlSession = MybatisUtils.getSqlSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        int res = studentMapper.isColumnExist("\"" + getClassId() + "\"", "\"" + test + "\"");

        sqlSession.commit();
        sqlSession.close();

        if (res > 0) return true;
        else return false;
    }

    public static void createNewTest (String testName){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        Integer res = studentMapper.createNewColumn(getClassId(), "`" + testName + "`");
        System.out.println("测试 " + testName + " 创建成功！");

        sqlSession.commit();
        sqlSession.close();
    }

    public static boolean isStudentExist (int studentId) {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        int res = studentMapper.isStudentExist("`" + getClassId() + "`", studentId);

        sqlSession.commit();
        sqlSession.close();

        if (res > 0) return true;
        else return false;
    }

    public static void deleteStudent (int studentId){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        Integer res = studentMapper.deleteStudent ("`" + getClassId() + "`", studentId);

        sqlSession.commit();
        sqlSession.close();
    }

    public static boolean printTestNameList () {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        List<String> list = studentMapper.getTestNameList("\"" + getClassId() + "\"");
        if (list.size() == 6) {
            System.out.println("当前没有测试！");
            return false;
        }

        System.out.println("当前存在的测试有：");
        for (String test : list) {
            if (test.equals("studentId")) continue;
            if (test.equals("password")) continue;
            if (test.equals("name")) continue;
            if (test.equals("SST")) continue;
            if (test.equals("status")) continue;
            if (test.equals("performance")) continue;

            System.out.print (test + "  ");
        }
        System.out.println("");

        sqlSession.commit();
        sqlSession.close();

        return true;
    }

    public static void printStudentTestList (int studentId){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        Map<String, Object> map = studentMapper.getStudentTestList("`" + getClassId() + "`", studentId);

        System.out.println(getStudentById(studentId).getIdAndName() + "的各次测试成绩如下：");
        for (Entry<String, Object> s : map.entrySet()) {
            if (s.getKey().equals("studentId")) continue;
            if (s.getKey().equals("password")) continue;
            if (s.getKey().equals("name")) continue;
            if (s.getKey().equals("SST")) continue;
            if (s.getKey().equals("status")) continue;
            if (s.getKey().equals("performance")) continue;

            System.out.print("[" + s.getKey() + "]:" + s.getValue() + "  ");
        }
        System.out.println("");

        sqlSession.commit();
        sqlSession.close();
    }

    public static void resetColumn (String columnName) {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        int res = 0;
        if (columnName.equals("SST")) {
            res = studentMapper.resetColumn("`" + getClassId() + "`", "`" + columnName + "`", "\"0\"");
            if (res > 0) System.out.println ("已重置所有学生的屏幕静止时间");
        } else if (columnName.equals("performance")){
            res = studentMapper.resetColumn("`" + getClassId() + "`", "`" + columnName + "`", "\"缺席\"");
            System.out.println ("已重置所有学生状态为 \"缺席\"");
        } else {
            System.out.println("出现了意外的状态重置请求！");
        }
        if (res <= 0) System.out.println("学生状态重置失败！");

        sqlSession.commit();
        sqlSession.close();
    }

    public static Object getCell (String columnName, int studentId){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        Object val = studentMapper.getCell ("`" + getClassId() + "`", studentId, "`" + columnName + "`");

        sqlSession.commit();
        sqlSession.close();

        return val;
    }

    public static void deleteColumn (String columnName) {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        studentMapper.deleteColumn ("`" + getClassId() + "`", "`" + columnName + "`");

        sqlSession.commit();
        sqlSession.close();
    }

    public static List<String> getColumn (String columnName){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        List<String> list = studentMapper.getColumn ("`" + getClassId() + "`", "`" + columnName + "`");

        sqlSession.commit();
        sqlSession.close();

        return list;
    }
}
