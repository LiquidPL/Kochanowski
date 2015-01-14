package com.github.LiquidPL.kochanowski.parse.table;

/**
 * A class for storing a single lesson.
 */
public class Lesson 
{
    private int day = -1;
    private int lesson = -1;
	private String subject = "";
	private String teacherCode = "";
	private String classroom = "";
	private String _class = "";

	private int group = 0;
	
	public Lesson () {}
	
	public Lesson (int day, int lesson, String subject, String code, String room, String _class)
	{
        this.day = day;
        this.lesson = lesson;
	    this.subject = subject;
	    this.teacherCode = code;
	    this.classroom = room;
	    this._class = _class;
	}

	public String getSubject ()
	{
		return subject;
	}

	public String getTeacherCode() 
	{
		return teacherCode;
	}

	public String getClassroom() 
	{
		return classroom;
	}
	
	public int getGroup ()
	{
		return this.group;
	}
	
	public void setGroup (int val)
	{
		this.group = val;
	}

	public String _getClass() 
	{
		return _class;
	}

    public int getDay ()
    {
        return day;
    }

    public int getLesson ()
    {
        return lesson;
    }
}
