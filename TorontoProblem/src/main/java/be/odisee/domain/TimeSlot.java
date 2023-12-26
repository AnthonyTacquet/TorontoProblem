package be.odisee.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TimeSlot implements Serializable{
	
	private int ID;
	private List<Exam> exams;
	
	public TimeSlot(int ID){
		this.ID  = ID;
		exams = new ArrayList<>();
	}

	public TimeSlot(TimeSlot copyTimeSlot) {
		this.ID = copyTimeSlot.getID();
		this.exams = copyTimeSlot.getExams();
	}

	public int getID() {
		return ID;
	}
	public List<Exam> getExams(){
		return exams;
	}
	public void setID(int id) {
		ID = id;
	}
	public void setExams(List<Exam> exams){
		this.exams = exams;
	}

	public void addExam(Exam exam){
		this.exams.add(exam);
	}

	public List<Integer> getAllSIDInTimeSlot(){
		return exams.stream().map(Exam::getSID).flatMap(List::stream).distinct().toList();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof TimeSlot timeSlot)) return false;
        return getID() == timeSlot.getID() && Objects.equals(getExams(), timeSlot.getExams());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getID(), getExams());
	}

	@Override
	public String toString() {
		return "TimeSlot{" +
				"ID=" + ID +
				", exams=" + exams.stream().map(Exam::getID).map(String::valueOf).collect(Collectors.joining(", ")) +
				'}';
	}
}
