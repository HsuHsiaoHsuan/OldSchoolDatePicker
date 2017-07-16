package funnybrain.hsu.idv.testdatepicker.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import funnybrain.hsu.idv.testdatepicker.R;

/**
 * Created by freeman on 7/16/17.
 */

public class CathayOldSchoolDatePicker extends LinearLayout {

    private OnDateChangedListener mListener;
    private NumberPicker mYearPicker;
    private NumberPicker mMonthPicker;
    private NumberPicker mDayPicker;
    private TextView mDescription;

    private int mMinYear;
    private int mMaxYear;
    private int mYearRange;

    private boolean mDisplayDescription;

    public CathayOldSchoolDatePicker(Context context) {
        this(context, null, -1);
    }

    public CathayOldSchoolDatePicker(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public CathayOldSchoolDatePicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.widget_date_picker, this);
        mYearPicker = (NumberPicker) view.findViewById(R.id.np_year);
        mMonthPicker = (NumberPicker) view.findViewById(R.id.np_month);
        mDayPicker = (NumberPicker) view.findViewById(R.id.np_day);
        mDescription = (TextView) view.findViewById(R.id.tv_description);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CathayDatePicker, 0, 0);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date(System.currentTimeMillis());
        SimpleDateFormat dfYear = new SimpleDateFormat("yyyy");
        SimpleDateFormat dfMonth = new SimpleDateFormat("MM");
        SimpleDateFormat dfDay = new SimpleDateFormat("dd");
        int nowYear = Integer.valueOf(dfYear.format(now));
        int nowMonth = Integer.valueOf(dfMonth.format(now));
        int nowDay = Integer.valueOf(dfDay.format(now));


        /* year */
        mYearRange = a.getInteger(R.styleable.CathayDatePicker_yearRange, 100);
        mMinYear = a.getInteger(R.styleable.CathayDatePicker_minYear, 1900);
        mMaxYear = a.getInteger(R.styleable.CathayDatePicker_maxYear, nowYear);
        mYearPicker.setMinValue(mMinYear);
        mYearPicker.setMaxValue(mMaxYear);
        int selectedYear = a.getInt(R.styleable.CathayDatePicker_selectedYear, nowYear);
        if (selectedYear > mMaxYear || selectedYear < mMinYear) {
            throw new IllegalArgumentException(String.format("Select year (%d) must be between minYear(%d) and maxYear(%d)", selectedYear, mMinYear, mMaxYear));
        }
        mYearPicker.setValue(selectedYear);
        mYearPicker.setOnValueChangedListener(dateChangeListener);

        /* month */
        boolean displayMonthName = a.getBoolean(R.styleable.CathayDatePicker_displayMonthNames, false);
        mMonthPicker.setMinValue(1);
        mMonthPicker.setMaxValue(12);
        if (displayMonthName) {
            mMonthPicker.setDisplayedValues(context.getResources().getStringArray(R.array.nameOfMonth));
        }
        int selectedMonth = a.getInteger(R.styleable.CathayDatePicker_selectedMonth, nowMonth);
        if (selectedMonth < 1 || selectedMonth > 12) {
            throw new IllegalArgumentException(String.format("Select month (%d) must be between 1 and 12", selectedMonth));
        }
        mMonthPicker.setValue(selectedMonth);
        mMonthPicker.setOnValueChangedListener(dateChangeListener);

        /* day */
        mDayPicker.setMinValue(1);
        mDayPicker.setMaxValue(31);
        int selectedDay = a.getInteger(R.styleable.CathayDatePicker_selectedDay, nowDay);
        int days = getDaysOfMonth(nowYear, nowMonth);
        if (selectedDay > days || selectedDay < 1) {
            throw new IllegalArgumentException(String.format("Select day (%d) must be between 1 and %d", selectedDay, days));
        }
        mDayPicker.setValue(selectedDay);
        mDayPicker.setOnValueChangedListener(dateChangeListener);

        a.recycle();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);

        ss.datetime = new Date(mYearPicker.getValue(), mMonthPicker.getValue()-1, mDayPicker.getValue()).getTime();
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        mYearPicker.setValue(2017);
        mMonthPicker.setValue(3);
        mDayPicker.setValue(15);
    }

    public void setOnDateChangedListener(OnDateChangedListener onDateChangedListener) {
        mListener = onDateChangedListener;
    }

    public interface OnDateChangedListener {
        void onDateChanged(int year, int month, int day);
    }

    NumberPicker.OnValueChangeListener dateChangeListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker numberPicker, int i, int i1) {
            int year = mYearPicker.getValue();
            boolean isLeapYear = isLeapYear(year);

            int month = mMonthPicker.getValue();
            int day = mDayPicker.getValue();

            mDayPicker.setMinValue(1);
            mDayPicker.setMaxValue(getDaysOfMonth(year, month));

            if (mDisplayDescription) {
                mDescription.setText("HELLO");
            }

            if (mListener != null) {
                mListener.onDateChanged(mYearPicker.getValue(), mMonthPicker.getValue(), mDayPicker.getValue());
            }
        }
    };

    private static boolean isLeapYear(int year) {
        if (year % 4 != 0) {
            return false;
        } else if (year % 400 == 0) {
            return true;
        } else if (year % 100 == 0) {
            return false;
        } else {
            return true;
        }
    }

    private static int getDaysOfMonth(int year, int month) {
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 2:
                if (isLeapYear(year)) {
                    return 30;
                } else {
                    return 29;
                }
            default:
                return 30;
        }
    }

    static class SavedState extends BaseSavedState {
        long datetime;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.datetime = in.readLong();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeLong(this.datetime);
        }

        // required field that makes Parcelables from a Parcel
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
