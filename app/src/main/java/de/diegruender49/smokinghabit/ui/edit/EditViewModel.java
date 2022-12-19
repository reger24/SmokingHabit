package de.diegruender49.smokinghabit.ui.edit;

import androidx.lifecycle.ViewModel;

import java.text.DateFormat;

public class EditViewModel extends ViewModel {

    private int mSqlId;
    private String mDesctxt;
    private String mDatetxt;
    private String mTimetxt;
    private long mDateTime;

    public EditViewModel() { }

    public void setEntryData (int sqlId, long aDateTime, String aDesc) {
        mSqlId = sqlId;
        mDateTime = aDateTime;
        mDesctxt = aDesc;
        mDatetxt = DateFormat.getDateInstance().format(aDateTime);
        mTimetxt = DateFormat.getTimeInstance().format(aDateTime);
    }

    public String getDescText() { return mDesctxt; }
    public String getDateText() { return mDatetxt; }
    public String getTimeText() { return mTimetxt; }
    public int getSqlId() { return mSqlId; }
}