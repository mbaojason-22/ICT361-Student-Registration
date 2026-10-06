package com.example.ict361studentregistration;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LecturerGroupViewModel extends ViewModel {

    public static class StudentItem {
        private String studentId;
        private String name;
        private String groupName;

        public StudentItem(String studentId, String name, String groupName) {
            this.studentId = studentId;
            this.name = name;
            this.groupName = groupName;
        }

        public String getStudentId() {
            return studentId;
        }

        public String getName() {
            return name;
        }

        public String getGroupName() {
            return groupName;
        }
    }

    public static class CourseGroupData {
        private String courseCode;
        private String courseTitle;
        private String adminName;
        private int maxCapacity;
        private List<StudentItem> students;

        public CourseGroupData(String courseCode, String courseTitle, String adminName, int maxCapacity, List<StudentItem> students) {
            this.courseCode = courseCode;
            this.courseTitle = courseTitle;
            this.adminName = adminName;
            this.maxCapacity = maxCapacity;
            this.students = students;
        }

        public String getCourseCode() {
            return courseCode;
        }

        public String getCourseTitle() {
            return courseTitle;
        }

        public String getAdminName() {
            return adminName;
        }

        public int getMaxCapacity() {
            return maxCapacity;
        }

        public List<StudentItem> getStudents() {
            return students;
        }

        public int getStudentCount() {
            return students != null ? students.size() : 0;
        }
    }

    private final MutableLiveData<Map<String, CourseGroupData>> courseGroupsLiveData = new MutableLiveData<>();
    private final Map<String, CourseGroupData> groupsMap = new HashMap<>();

    public LecturerGroupViewModel() {
        initSampleData();
    }

    private void initSampleData() {
        // ICT361 - Student Registration & Mobile Dev
        List<StudentItem> ict361Students = new ArrayList<>();
        ict361Students.add(new StudentItem("221045050", "Sarah Connor", "Group A"));
        ict361Students.add(new StudentItem("221045051", "John Connor", "Group B"));
        groupsMap.put("ICT361", new CourseGroupData("ICT361", "Student Registration & Mobile Dev", "Lecturer (Admin)", 15, ict361Students));

        // ICT341 - Cybersecurity Principles
        List<StudentItem> ict341Students = new ArrayList<>();
        ict341Students.add(new StudentItem("221045010", "Fiona Gallagher", "Group A"));
        ict341Students.add(new StudentItem("221045011", "George Clark", "Group A"));
        ict341Students.add(new StudentItem("221045012", "Hannah Abbott", "Group A"));
        ict341Students.add(new StudentItem("221045013", "Ian Malcolm", "Group B"));
        groupsMap.put("ICT341", new CourseGroupData("ICT341", "Cybersecurity Principles", "Lecturer (Admin)", 15, ict341Students));

        // ICT381 - System Modelling and Design
        List<StudentItem> ict381Students = new ArrayList<>();
        ict381Students.add(new StudentItem("221045030", "Michael Scott", "Group A"));
        ict381Students.add(new StudentItem("221045031", "Nina Williams", "Group B"));
        groupsMap.put("ICT381", new CourseGroupData("ICT381", "System Modelling and Design", "Lecturer (Admin)", 15, ict381Students));

        // ICT371 - Advanced Database
        List<StudentItem> ict371Students = new ArrayList<>();
        ict371Students.add(new StudentItem("221045001", "Alex Johnson", "Group A"));
        ict371Students.add(new StudentItem("221045002", "Beatrice Smith", "Group A"));
        ict371Students.add(new StudentItem("221045003", "Charlie Brown", "Group A"));
        ict371Students.add(new StudentItem("221045004", "Diana Prince", "Group B"));
        ict371Students.add(new StudentItem("221045005", "Ethan Hunt", "Group B"));
        groupsMap.put("ICT371", new CourseGroupData("ICT371", "Advanced Database", "Lecturer (Admin)", 15, ict371Students));

        // ICT351 - Theory of Computation
        List<StudentItem> ict351Students = new ArrayList<>();
        ict351Students.add(new StudentItem("221045020", "Julia Roberts", "Group A"));
        ict351Students.add(new StudentItem("221045021", "Kevin Bacon", "Group A"));
        ict351Students.add(new StudentItem("221045022", "Laura Croft", "Group B"));
        groupsMap.put("ICT351", new CourseGroupData("ICT351", "Theory of Computation", "Lecturer (Admin)", 15, ict351Students));

        courseGroupsLiveData.setValue(groupsMap);
    }

    public LiveData<Map<String, CourseGroupData>> getCourseGroupsLiveData() {
        return courseGroupsLiveData;
    }

    public CourseGroupData getCourseGroup(String courseCode) {
        return groupsMap.get(courseCode);
    }

    public boolean removeStudent(String courseCode, String studentId) {
        CourseGroupData groupData = groupsMap.get(courseCode);
        if (groupData != null && groupData.getStudents() != null) {
            List<StudentItem> list = groupData.getStudents();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getStudentId().equals(studentId)) {
                    list.remove(i);
                    courseGroupsLiveData.setValue(groupsMap);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean addStudent(String courseCode, String studentId, String name, String groupName) {
        CourseGroupData groupData = groupsMap.get(courseCode);
        if (groupData != null && groupData.getStudents() != null) {
            if (groupData.getStudentCount() >= groupData.getMaxCapacity()) {
                return false; // Group capacity limit reached (max 15)
            }
            groupData.getStudents().add(new StudentItem(studentId, name, groupName));
            courseGroupsLiveData.setValue(groupsMap);
            return true;
        }
        return false;
    }
}
