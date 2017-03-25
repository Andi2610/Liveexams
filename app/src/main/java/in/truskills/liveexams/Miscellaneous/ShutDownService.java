package in.truskills.liveexams.Miscellaneous;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

import in.truskills.liveexams.SqliteDatabases.QuizDatabase;

public class ShutDownService extends BroadcastReceiver {

    public ShutDownService() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        QuizDatabase ob=new QuizDatabase(context);
        SharedPreferences dataPrefs=context.getSharedPreferences("dataPrefs",Context.MODE_PRIVATE);
        SharedPreferences quizPrefs=context.getSharedPreferences("quizPrefs",Context.MODE_PRIVATE);
//        SharedPreferences prefs=context.getSharedPreferences("dataPrefs",Context.MODE_PRIVATE);
        Log.d("switchOff", "onReceive: inSwitchOff");
        String folder_main = ".LiveExams";
        File f = new File(Environment.getExternalStorageDirectory(), folder_main);
        if (f.exists()) {
            ConstantsDefined.deleteDir(f);
        }
        ob.deleteMyTable();
        SharedPreferences.Editor e=dataPrefs.edit();
        e.clear();
        e.apply();
        e=quizPrefs.edit();
        e.clear();
        e.apply();
    }

}
