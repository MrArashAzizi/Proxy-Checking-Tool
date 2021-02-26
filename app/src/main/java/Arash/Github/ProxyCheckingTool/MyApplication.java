package Arash.Github.ProxyCheckingTool;

import android.app.Application;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MyApplication extends Application {

    private static final String TAG = "MyApplicationTAG";

    private static Context context;

    private RequestQueue requestQueue;
    private static Arash.Github.ProxyCheckingTool.MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        MyApplication.context = getApplicationContext();
    }

    public static synchronized Arash.Github.ProxyCheckingTool.MyApplication getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return requestQueue;
    }


    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
