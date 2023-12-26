package be.odisee.framework;

import be.odisee.domain.Exam;
import be.odisee.domain.Student;
import be.odisee.domain.TimeSlot;

import java.util.List;

public interface Solution extends Cloneable {
    public abstract double getObjectiveValue();

    public abstract void setObjectiveValue(double value);

    public abstract Object clone();

    public List<TimeSlot> getTimeSlots();

    public void setTimeSlots(List<TimeSlot> list);
    public List<Exam> getExams();
    public List<Student> getStudents();
    public void setExams(List<Exam> exams);
    public void setStudents(List<Student> students);
}
