package com.hisrv.android.logviewer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.airpush.android.Airpush;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

	private TextView mLogText;
	private LogTask mLogTask;
	private Airpush mAirpush;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mAirpush = new Airpush(getApplicationContext());
		mLogText = (TextView) findViewById(R.id.logtext);
		mLogTask = new LogTask();
		mLogTask.execute();
		mAirpush.startAppWall();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mLogTask != null) {
			mLogTask.cancel(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private class LogTask extends AsyncTask<Void, String, Integer> {

		private final static int QUIT = 0;
		private final static int ERROR = 1;

		private boolean canceled = false;

		@Override
		protected void onCancelled(Integer result) {
			canceled = true;
		}

		@Override
		protected Integer doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				Process process = Runtime.getRuntime().exec("logcat");
				while (true) {
					BufferedReader br = new BufferedReader(
							new InputStreamReader(process.getInputStream()));
					String s;
					while ((s = br.readLine()) != null) {
						publishProgress(s + "\n");
					}
					try {
						this.wait(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (canceled) {
						return QUIT;
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return ERROR;
			}
			// return QUIT;
		}

		protected void onProgressUpdate(String... progress) {
			mLogText.append(progress[0]);
		}

		protected void onPostExecute(Integer result) {
		}
	}
}
