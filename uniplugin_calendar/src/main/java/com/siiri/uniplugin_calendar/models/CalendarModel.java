package com.siiri.uniplugin_calendar.models;

public class CalendarModel {

    private Long id;
    private String name;
    private String accountName;
    private String accountType;
    private int calendarColor;
    private String ownerAccount;

    private boolean isReadOnly = false;

    private boolean isDefault = false;

    public CalendarModel(Long id, String name, String accountName, String accountType, int calendarColor, String ownerAccount) {
        this.id = id;
        this.name = name;
        this.accountName = accountName;
        this.accountType = accountType;
        this.calendarColor = calendarColor;
        this.ownerAccount = ownerAccount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public int getCalendarColor() {
        return calendarColor;
    }

    public void setCalendarColor(int calendarColor) {
        this.calendarColor = calendarColor;
    }

    public String getOwnerAccount() {
        return ownerAccount;
    }

    public void setOwnerAccount(String ownerAccount) {
        this.ownerAccount = ownerAccount;
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    public void setReadOnly(boolean readOnly) {
        isReadOnly = readOnly;
    }

}
