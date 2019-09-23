package idv.ron.alarmmanagerdemo;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

public class MainFragment extends Fragment implements
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private Activity activity;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private int year, month, day, hour, minute;
    private TextView tvMessage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = getActivity();
        if (activity == null) {
            return;
        }
        //記得先permission監控客戶是否開機 因為關機鬧鐘會被取消 要創一個新的
        alarmManager =
                (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvMessage = view.findViewById(R.id.tvMessage);

        view.findViewById(R.id.btSetAlarm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popDatePicker();
            }
        });

        view.findViewById(R.id.btCancelAlarm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pendingIntent != null) {
                    alarmManager.cancel(pendingIntent);
                    tvMessage.setText(R.string.textAlarmCancelled);
                    pendingIntent = null;
                }
            }
        });
    }

    // 跳出DatePicker選alarm設定日期
    private void popDatePicker() {
        //Calendar 日曆 Instance事例
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(activity, this, year, month, day).show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        // 將挑選的日期存至實體變數
        this.year = year;
        this.month = month;
        this.day = dayOfMonth;
        // 日期選完後選時間
        popTimePicker();
    }

    // 跳出TimePicker選alarm設定時間
    private void popTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        new TimePickerDialog(activity, this, hour, minute, false).show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // 將挑選的時間存至實體變數
        this.hour = hourOfDay;
        this.minute = minute;
        long alarmTime = getTimeFromPicker();
        // 日期時間都挑選完畢後可以設定alarm時間
        pendingIntent = Common.setAlarm(activity, alarmTime, true);
        String text = "Alarm scheduled at: " + Common.getFormatTime(alarmTime);
        tvMessage.setText(text);
    }

    // 將選好的日期時間轉成設定alarm所需的時間格式-毫秒
    private long getTimeFromPicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute);
        return calendar.getTimeInMillis();
    }
}
