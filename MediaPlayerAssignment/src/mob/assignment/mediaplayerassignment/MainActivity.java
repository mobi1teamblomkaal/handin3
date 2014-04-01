package mob.assignment.mediaplayerassignment;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

public class MainActivity extends Activity {

	public static String path;
	private static int seekBarMax = 0;
	private static boolean newFileLoaded = false;
	public static final boolean DEBUG = true;
	public static boolean isStarted = false;
	private Button btnPlay;
	private Button btnPause;
	private Button btnStop;
	private Button btnBrowse;
	private static SeekBar sbProgress;
	private static MediaPlayer mp;
	private static Runnable r;
	private static Thread t;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btnPlay = (Button) findViewById(R.id.playb);
		btnPause = (Button) findViewById(R.id.pauseb);
		btnStop = (Button) findViewById(R.id.stopb);
		btnBrowse = (Button) findViewById(R.id.browseb);
		sbProgress = (SeekBar) findViewById(R.id.sb);

		// Runnable responsible for updating the UI with playing progress.
		r = new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						sbProgress.setProgress((mp == null) ? 0 : mp
								.getCurrentPosition());
						Thread.sleep(250);
					} catch (InterruptedException e) {
						Log.e("MedaiPlayerThread", "ProgressFAIL", e);
					}
				}
			}
		};
		t = new Thread(r);
		t.start();

		btnPlay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mp == null) {
					return;
				}
				mp.start();
			}
		});
		btnPause.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mp == null) {
					return;
				}
				if (!mp.isPlaying()) {
					mp.start();

				} else {
					mp.pause();
				}
			}
		});
		btnStop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mp == null) {
					return;
				}
				if (mp.isPlaying()) {
					mp.pause();
				}
				mp.seekTo(0);
			}
		});

		btnBrowse.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				newFileLoaded = true;
				Intent i = new Intent(getBaseContext(),
						FileBrowserActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(i);
			}
		});

		sbProgress
				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
					@Override
					public void onProgressChanged(SeekBar seekBar, int position,
							boolean byUser) {
						if (byUser && mp != null) {
							mp.seekTo(position);
						}
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// Should do nothing
					}

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// Should do nothing
					}
				});
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (newFileLoaded) {
			try {
				if (mp == null) {
					isStarted = true;
					mp = MediaPlayer.create(getBaseContext(), Uri.parse(path));
				} else {
					mp.reset();
					mp = MediaPlayer.create(getBaseContext(), Uri.parse(path));
				}

				mp.start();
				mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						mp.seekTo(0);
					}
				});
				sbProgress.setProgress(0);
				seekBarMax = mp.getDuration();
				newFileLoaded = false;
			} catch (Exception e) {
				if (DEBUG) {
					Toast.makeText(getBaseContext(), e.getMessage(),
							Toast.LENGTH_SHORT).show();
				}
			}
		}
		sbProgress.setMax(seekBarMax);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
