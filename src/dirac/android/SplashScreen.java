package dirac.android;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

public class SplashScreen extends Activity {
    protected boolean _active = true;
    protected int _splashTime = 4000;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("here","here0");
        setContentView(R.layout.splash_screen);
      //  ImageView IV = (ImageView)findViewById(R.id.imageView1);
     //   IV.setImageResource(R.drawable.dirac_splash_lowres);
        Log.d("here","here1");
        // thread for displaying the SplashScreen
        Thread splashTread = new Thread() {
			@Override
            public void run() {
                try {
                    int waited = 0;
                    while(_active && (waited < _splashTime)) {
                        sleep(100);
                        if(_active) {
                            waited += 100;
                        }
                    }
                } catch(InterruptedException e) {
                    // do nothing
                } finally {
                    finish();
                    Intent intent = new Intent(SplashScreen.this, DIRACAndroidActivity.class);
                    startActivity(intent);
                }
            }
        };
        splashTread.start();
    }

    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            _active = false;
        }
        return true;
    }
}

