package com.github.LiquidPL.kochanowski.util;

/**
 * An object for storing lessons pulled from the database,
 * for the purpose of not having to keep the DB open all the time.
 */
public class Lesson
{
    private int dayId;
    private int hourId;
    private int groupId;
    private int subjectId;

    private String startTime;
    private String endTime;
    private String subjectName;
    private String teacherId;
    private String teacherName;
    private String teacherSurname;
    private String classNameShort;
    private String classNameLong;
    private String classroomName;

    public Lesson (int dayId, int hourId, int groupId, int subjectId, String startTime,
                   String endTime, String subjectName, String teacherId, String teacherName,
                   String teacherSurname, String classNameShort, String classNameLong,
                   String classroomName)
    {
        this.dayId = dayId;
        this.hourId = hourId;
        this.groupId = groupId;
        this.subjectId = subjectId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.subjectName = subjectName;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.teacherSurname = teacherSurname;
        this.classNameShort = classNameShort;
        this.classNameLong = classNameLong;
        this.classroomName = classroomName;
    }

    public int getDayId ()
    {
        return dayId;
    }

    public int getHourId ()
    {
        return hourId;
    }

    public int getGroupId ()
    {
        return groupId;
    }

    public int getSubjectId ()
    {
        return subjectId;
    }

    public String getStartTime ()
    {
        return startTime;
    }

    public String getEndTime ()
    {
        return endTime;
    }

    public String getSubjectName ()
    {
        return subjectName;
    }

    public String getTeacherId ()
    {
        return teacherId;
    }

    public String getTeacherName ()
    {
        return teacherName;
    }

    public String getTeacherSurname ()
    {
        return teacherSurname;
    }

    public String getClassNameShort ()
    {
        return classNameShort;
    }

    public String getClassNameLong ()
    {
        return classNameLong;
    }

    public String getClassroomName ()
    {
        return classroomName;
    }
}
