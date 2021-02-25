package Arash.Github.ProxyCheckingTool.ProxyList;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import Arash.Github.ProxyCheckingTool.R;


public class MyViewHolder extends RecyclerView.ViewHolder {

    TextView txtProxyPort, txtLocation, txtLevel, txtType, txtLastCheck;

    public MyViewHolder(View MyView) {
        super(MyView);
        txtProxyPort = MyView.findViewById(R.id.ProxyAddress);
        txtLocation = MyView.findViewById(R.id.ProxyLoc);
        txtLevel = MyView.findViewById(R.id.Proxy_Type);
        txtType = MyView.findViewById(R.id.Proxy_Level);
        txtLastCheck = MyView.findViewById(R.id.Proxy_LastCheck);
    }
}
