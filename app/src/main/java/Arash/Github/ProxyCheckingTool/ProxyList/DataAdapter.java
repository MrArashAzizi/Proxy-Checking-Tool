package Arash.Github.ProxyCheckingTool.ProxyList;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Arash.Github.ProxyCheckingTool.R;

public class DataAdapter extends RecyclerView.Adapter<MyViewHolder> {

    Context context;
    List<ItemDataModel> IDM_List;

    public DataAdapter(Context context, List<ItemDataModel> IDM_List) {
        this.context = context;
        this.IDM_List = IDM_List;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.style_proxylist, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ItemDataModel dataModel = IDM_List.get(position);
        holder.txtProxyPort.setText(dataModel.getIpPort());
        Locale loc = new Locale("en_",dataModel.getCountry());
        holder.txtLocation.setText(loc.getDisplayCountry());
        holder.txtType.setText("Proxy Type: " + dataModel.getType().toUpperCase());
        holder.txtLevel.setText("Proxy Level: " + dataModel.getProxy_level());
        holder.txtLastCheck.setText("Last Check: " + dataModel.getLast_checked());

        holder.itemView.setAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), android.R.anim.fade_in));

        if (position + 1 == getItemCount()) {
            // set bottom margin to 72dp.
            setBottomMargin(holder.itemView, (int) (72 * Resources.getSystem().getDisplayMetrics().density));
        } else {
            // reset bottom margin back to zero. (your value may be different)
            setBottomMargin(holder.itemView, 0);
        }
    }

    public static void setBottomMargin(View view, int bottomMargin) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, bottomMargin);
            view.requestLayout();
        }
    }

    @Override
    public int getItemCount() {
        return IDM_List.isEmpty() ? 0 : IDM_List.size();
    }

    public void filteredList(ArrayList<ItemDataModel> filteredList){
        IDM_List = filteredList;
        notifyDataSetChanged();
    }
}
