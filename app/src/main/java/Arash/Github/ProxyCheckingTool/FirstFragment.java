package Arash.Github.ProxyCheckingTool;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import Arash.Github.ProxyCheckingTool.Helpers.PreferenceHelper;
import Arash.Github.ProxyCheckingTool.Helpers.Statics;


public class FirstFragment extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }


    public static TextView ProxyAddress, ProxyPort, ProxyUsername, ProxyPassword;
    public static RadioButton RB_Http, RB_Sock; // For Proxy Type
    public static AutoCompleteTextView TargetDropDown;

    void initViews(View view) {
        ProxyAddress = view.findViewById(R.id.txtProxy);
        ProxyPort = view.findViewById(R.id.txtPort);
        ProxyUsername = view.findViewById(R.id.txtUsername);
        ProxyPassword = view.findViewById(R.id.txtPassword);
        RB_Http = view.findViewById(R.id.RB_Http);
        RB_Sock = view.findViewById(R.id.RB_Sock);
        TargetDropDown = view.findViewById(R.id.TargetDropDown);
    }

    private void GetTextFromPref() {
        ProxyAddress.setText(PreferenceHelper.getInstance().getString(Statics.ProxyAddress_SharedPreferencesKey, ""));
        ProxyPort.setText(PreferenceHelper.getInstance().getString(Statics.PortNumber_SharedPreferencesKey, ""));
        ProxyUsername.setText(PreferenceHelper.getInstance().getString(Statics.Username_SharedPreferencesKey, ""));
        ProxyPassword.setText(PreferenceHelper.getInstance().getString(Statics.Password_SharedPreferencesKey, ""));
    }

    public static String TargetManager() {
        String Target = null;
        switch (TargetDropDown.getText().toString()) {
            case "Github":
                Target = "https://github.com";
                break;
            case "Gitlab":
                Target = "https://gitlab.com/";
                break;
            case "Wikipedia":
                Target = "https://en.wikipedia.org/wiki/Main_Page";
                break;
        }
        return Target;

    }

    public static String KeywordManager() {
        String Key = null;
        switch (TargetDropDown.getText().toString()) {
            case "Github":
                Key = "github";
                break;
            case "Gitlab":
                Key = "gitlab";
                break;
            case "Wikipedia":
                Key = "wikipedia";
                break;
        }
        return Key;
    }

    public static View MyView;

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context context = view.getContext();


        initViews(view);
        GetTextFromPref();
        MyView = view;

        TextView vpnAlert = view.findViewById(R.id.vpnAlertTV);
        if (!MyApplication.CheckForVPN()) vpnAlert.setVisibility(View.GONE);
        vpnAlert.setOnClickListener(v -> vpnAlert.setVisibility(View.GONE));


        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

        String[] type = new String[]{"Github", "Gitlab", "Wikipedia"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item, type);
        TargetDropDown.setAdapter(adapter);
        TargetDropDown.setText(adapter.getItem(0), false);

        RB_Sock.setChecked(true);

        //For Proxy Types
        RB_Http.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) RB_Sock.setChecked(false);
        });
        RB_Sock.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) RB_Http.setChecked(false);
        });

        //NavHostFragment.findNavController(FirstFragment.this).navigate(R.id.action_FirstFragment_to_SecondFragment);
    }


    @Override
    public void onDestroy() {
        PreferenceHelper.getInstance().setString(Statics.ProxyAddress_SharedPreferencesKey, ProxyAddress.getText().toString());
        PreferenceHelper.getInstance().setString(Statics.PortNumber_SharedPreferencesKey, ProxyPort.getText().toString());

        PreferenceHelper.getInstance().setString(Statics.Username_SharedPreferencesKey, ProxyUsername.getText().toString());
        PreferenceHelper.getInstance().setString(Statics.Password_SharedPreferencesKey, ProxyPassword.getText().toString());

        super.onDestroy();
    }
}