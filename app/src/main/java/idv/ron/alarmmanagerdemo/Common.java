package idv.ron.alarmmanagerdemo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import java.text.DateFormat;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class Common {
    private static final String TAG = "TAG_Common";
    public static final String PREFERENCES_NAME = "preferences";
    public static final String KEY_ALARM_TIME = "alarmTime";

    /**
     * 這裡只針對設定一個alarm。
     * 如果要設定多個alarm，設定alarm時的requestCode必須不同；建議自訂alarm類別與相關屬性。
     * 存入偏好設定檔必須將alarm list轉成JSON array - putString("alarms", JSON array string)
     */
    public static PendingIntent setAlarm(Context context, long time, boolean save) {
        if (save) {
            // 將alarm設定時間存入偏好設定檔，方便重開機後取得
            SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
            preferences.edit().putLong(KEY_ALARM_TIME, time).apply();
        }

        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            return null;
        }

        final int REQ_ALARM = 101;
        // 建立的Intent需指定廣播接收器以攔截AlarmManager發出的廣播
        Intent intentMeeting = new Intent(context, MyMeetingReceiver.class);
        Bundle bundle = new Bundle();
        bundle.putString("content", "Meeting starts at: " + getFormatTime(time));
        intentMeeting.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                REQ_ALARM, intentMeeting, PendingIntent.FLAG_CANCEL_CURRENT);
        // 設定單次準時alarm，而且裝置在低電源、休眠時仍可執行
        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                time,
                pendingIntent);

        // 可以設定alarm每分鐘重複一次，不過不準時。要準時必須使用單次準時alarm
//            alarmManager.setRepeating(
//                    AlarmManager.RTC_WAKEUP,
//                    time,
//                    60 * 1000,
//                    pendingIntent);

        String text = "Alarm scheduled at: " + getFormatTime(time);
        Log.d(TAG, text);
        return pendingIntent;
    }

    public static String getFormatTime(long time) {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
        return format.format(new Date(time));
    }
}
