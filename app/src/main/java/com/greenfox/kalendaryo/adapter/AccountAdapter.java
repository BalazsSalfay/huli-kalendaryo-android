package com.greenfox.kalendaryo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.greenfox.kalendaryo.R;
import com.greenfox.kalendaryo.models.KalAuth;
import com.greenfox.kalendaryo.models.KalPref;

import java.util.List;

/**
 * Created by barba on 2018. 01. 08..
 */

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder> {

    private List<KalAuth> auths;
    private Context context;
    private int lastSelectedPosition = -1;
    KalPref kalPref;

    public AccountAdapter(List<KalAuth> authsIn, Context ctx) {
        kalPref = new KalPref(ctx);
        auths = authsIn;
        context = ctx;
    }


    @Override
    public AccountAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_name_with_button, parent, false);
        AccountAdapter.ViewHolder viewHolder = new AccountAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AccountAdapter.ViewHolder holder, int position) {
        KalAuth auth = auths.get(position);
        holder.accountName.setText(auth.getEmail());
        holder.radioButton.setChecked(lastSelectedPosition == position);
    }

    @Override
    public int getItemCount() {
        return auths.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView accountName;
        public RadioButton radioButton;

        public ViewHolder(View view) {
            super(view);
            accountName = view.findViewById(R.id.accountname);
            radioButton = view.findViewById(R.id.radioButton);
            radioButton.setOnClickListener(v -> {
                lastSelectedPosition = getAdapterPosition();
                notifyDataSetChanged();

                kalPref.saveAccountName(accountName.getText().toString());

                Toast.makeText(AccountAdapter.this.context, accountName.getText(),
                        Toast.LENGTH_LONG).show();
            });
        }
    }
}

