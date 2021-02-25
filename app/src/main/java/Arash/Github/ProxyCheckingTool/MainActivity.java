package Arash.Github.ProxyCheckingTool;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;

import java.lang.reflect.Type;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

import Arash.Github.ProxyCheckingTool.Helpers.PreferenceHelper;
import Arash.Github.ProxyCheckingTool.ProxyList.DataAdapter;
import Arash.Github.ProxyCheckingTool.ProxyList.ItemDataModel;
import okhttp3.OkHttpClient;


public class MainActivity extends AppCompatActivity {
    OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Context context = getApplicationContext();

        PreferenceHelper.initialize(getApplicationContext());

        findViewById(R.id.fabGO).setOnClickListener(v -> {

            if (FirstFragment.ProxyAddress.length() == 0) {
                FirstFragment.ProxyAddress.setError("Enter the address");
                return;
            }
            if (FirstFragment.ProxyPort.length() == 0) {
                FirstFragment.ProxyAddress.setError("Enter the Port");
                return;
            }

            ProgressDialog pDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
            pDialog.setCancelable(false);
            pDialog.show();

            long StartTimer = System.currentTimeMillis();

            ConnectivityManager CM = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = CM.getActiveNetworkInfo();
            boolean NetStats = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            if (NetStats) {
                pDialog.setMessage("Trying to find Keyword (" + FirstFragment.KeywordManager() + ") ...");
                int proxyPort = Integer.parseInt(FirstFragment.ProxyPort.getText().toString());
                String proxyHost = FirstFragment.ProxyAddress.getText().toString().trim().toLowerCase();
                String username = FirstFragment.ProxyUsername.getText().toString();
                String password = FirstFragment.ProxyPassword.getText().toString();


                if (FirstFragment.ProxyUsername.length() != 0) {
                    if (FirstFragment.ProxyPassword.length() != 0) {
                        FirstFragment.ProxyPassword.setError(null);
                        Authenticator.setDefault(new Authenticator() {
                            @Override
                            protected PasswordAuthentication getPasswordAuthentication() {
                                if (getRequestingHost().equalsIgnoreCase(proxyHost)) {
                                    if (proxyPort == getRequestingPort())
                                        return new PasswordAuthentication(username, password.toCharArray());
                                }
                                return null;
                            }
                        });
                    } else {
                        FirstFragment.ProxyPassword.setError("Enter the password!");
                        pDialog.dismiss();
                        return;
                    }
                }
                if (FirstFragment.RB_Http.isChecked()) {
                    client = new OkHttpClient.Builder()
                            .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort)))
                            .build();
                }
                if (FirstFragment.RB_Sock.isChecked()) {
                    client = new OkHttpClient.Builder()
                            .proxy(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyHost, proxyPort)))
                            .build();
                }
                String ResponseKeyWord = FirstFragment.KeywordManager().toLowerCase();
                String TargetAddress = FirstFragment.TargetManager();

                AndroidNetworking.get(TargetAddress)
                        .setOkHttpClient(client)
                        .setPriority(Priority.HIGH)
                        .build()
                        .getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {
                                if (response.toLowerCase().contains(ResponseKeyWord)) {
                                    long EndTimer = System.currentTimeMillis();
                                    CustomSnakeBar(FirstFragment.MyView, "KeyWord Founded in " + ((EndTimer - StartTimer) + "ms"), Color.GREEN, Color.BLACK);
                                } else
                                    CustomSnakeBar(FirstFragment.MyView, "KeyWord NotFound!", Color.GRAY, Color.WHITE);
                                pDialog.dismiss();
                            }

                            @Override
                            public void onError(ANError anError) {
                                CustomSnakeBar(FirstFragment.MyView, anError.getMessage(), Color.RED, Color.WHITE);
                                Log.i("AndroidNetworkingError", "Error Detail: " + anError.getErrorDetail());
                                Log.i("AndroidNetworkingError", "Error Body: " + anError.getErrorBody());
                                Log.i("AndroidNetworkingError", "Error Code: " + anError.getErrorCode());
                                Log.i("AndroidNetworkingError", "Error Message: " + anError.getMessage());
                                pDialog.dismiss();
                            }
                        });
            } else {
                pDialog.dismiss();
                CustomSnakeBar(FirstFragment.MyView, "Check Your Internet Connection!", Color.RED, Color.WHITE);
            }
        });
        findViewById(R.id.fabGoToProxyList).setOnClickListener(v -> {
            PassListBottomSheet();
        });
    }

    public void PassListBottomSheet() {

        try {

            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.btm_proxylist, null);

            ProgressBar Pb = view.findViewById(R.id.ProxyProgressBar);

            String API_ADDRESS = "http://pubproxy.com/api/proxy?limit=10&format=json";
            //Data from github : https://github.com/clarketm/proxy-list

            RecyclerView RView = view.findViewById(R.id.RecyclerView);
            List<ItemDataModel> itemDataModels = new ArrayList<>();
            DataAdapter dataAdapter = new DataAdapter(view.getContext(), itemDataModels);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                    API_ADDRESS, null, response -> {
                if (response == null) {
                    Toast.makeText(this, "There is nothing to show!", Toast.LENGTH_LONG).show();
                    return;
                }
                String jsonOutput = null;
                try {
                    jsonOutput = response.get("data").toString();
                    Pb.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Type listType = new TypeToken<List<ItemDataModel>>() {
                }.getType();

                List<ItemDataModel> MyData = new Gson().fromJson(jsonOutput, listType);
                itemDataModels.clear();
                itemDataModels.addAll(MyData);

                dataAdapter.notifyDataSetChanged();
            }, error -> Log.i("Response", error.toString()));

            request.setShouldCache(false);
            MyApplication.getInstance().addToRequestQueue(request);
            RView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            RView.setItemAnimator(new DefaultItemAnimator());
            RView.setAdapter(dataAdapter);
            bottomSheetDialog.setContentView(view);
            bottomSheetDialog.show();

        } catch (Exception ignored) {
        }
    }

    void CustomSnakeBar(View view, String Message, int BackgroundColor, int TextColor) {
        Snackbar snackbar;
        snackbar = Snackbar.make(view, Message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(BackgroundColor);
        TextView textView = snackBarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setTextColor(TextColor);
        textView.setMaxLines(5);
        snackbar.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void Browser(Context context, String URL) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setShowTitle(false);
        builder.setUrlBarHidingEnabled(true);
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(context, Uri.parse(URL));
    }

    public void Action_Go_To_Github(MenuItem item) {
        Browser(this, "https://github.com/MrArashAzizi/Proxy-Checking-Tool");
    }
}