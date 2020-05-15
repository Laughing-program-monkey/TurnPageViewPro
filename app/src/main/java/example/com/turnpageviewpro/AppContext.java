package example.com.turnpageviewpro;

import android.app.Application;

public class AppContext extends Application {
    public  static AppContext Instance;
    @Override
    public void onCreate() {
        super.onCreate();
        Instance=this;
    }
}
