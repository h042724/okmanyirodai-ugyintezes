package com.example.shop;

public class BookedCase {
    public String id;
    public String user_id;
    public String case_name;
    public String selectedDate;

    public BookedCase(String user_id, String case_name, String selectedDate) {
        this.user_id = user_id;
        this.case_name = case_name;
        this.selectedDate = selectedDate;
    }

    public BookedCase() {}

    public String getCase_name() {
        return case_name;
    }
    public void setCase_name(String case_name) {
        this.case_name = case_name;
    }
    public String getUser_id() {
        return user_id;
    }
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    public String _getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getSelectedDate() {
        return selectedDate;
    }
    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }
}
