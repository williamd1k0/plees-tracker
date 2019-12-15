package hu.vmiklos.plees_tracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * The activity is the primary UI of the app: allows starting and stopping the
 * tracking.
 */
public class MainActivity extends AppCompatActivity
{
    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DataModel dataModel = DataModel.getDataModel();
        dataModel.setContext(getApplicationContext());
        updateView();
    }

    @Override protected void onStart()
    {
        super.onStart();
        Log.d("plees", "onStart");
        Intent intent = new Intent(this, MainService.class);
        stopService(intent);
    }

    @Override protected void onStop()
    {
        super.onStop();
        Log.d("plees", "onStop");
        Intent intent = new Intent(this, MainService.class);
        DataModel dataModel = DataModel.getDataModel();
        if (dataModel.getStart() != null && dataModel.getStop() == null)
        {
            startService(intent);
        }
    }

    public void startStop(View v)
    {
        DataModel dataModel = DataModel.getDataModel();
        if (dataModel.getStart() != null && dataModel.getStop() == null)
        {
            dataModel.setStop(Calendar.getInstance().getTime());
            dataModel.storeSleep();
        }
        else
        {
            dataModel.setStart(Calendar.getInstance().getTime());
            dataModel.setStop(null);
        }
        updateView();
    }

    private void updateView()
    {
        DataModel dataModel = DataModel.getDataModel();
        TextView state = (TextView)findViewById(R.id.state);
        Button startStop = (Button)findViewById(R.id.startStop);
        SimpleDateFormat sdf =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        int sleeps = dataModel.getDatabase().sleepDao().getAll().size();
        String sleepStat = sleeps + " sleeps in total.";
        if (dataModel.getStart() != null && dataModel.getStop() != null)
        {
            long durationMS =
                dataModel.getStop().getTime() - dataModel.getStart().getTime();
            String duration = formatDuration(durationMS / 1000);
            state.setText("Started on " + sdf.format(dataModel.getStart()) +
                          ", stopped on " + sdf.format(dataModel.getStop()) +
                          ", slept for " + duration + ". " + sleepStat);
            startStop.setText("Start again");
        }
        else if (dataModel.getStart() != null)
        {
            state.setText("Started on " + sdf.format(dataModel.getStart()) +
                          ", tracking.");
            startStop.setText("Stop");
        }
        else
        {
            state.setText("Press start to begin tracking. " + sleepStat);
            startStop.setText("Start");
        }
    }

    private static String formatDuration(long seconds)
    {
        return String.format("%d:%02d:%02d", seconds / 3600,
                             (seconds % 3600) / 60, seconds % 60);
    }
}
