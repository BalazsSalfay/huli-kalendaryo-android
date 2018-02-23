package com.greenfox.kalendaryo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.greenfox.kalendaryo.LoginActivity;
import com.greenfox.kalendaryo.R;
import com.greenfox.kalendaryo.adapter.AccountsList;
import com.greenfox.kalendaryo.models.KalAuth;
import com.greenfox.kalendaryo.models.KalPref;

import java.util.List;

public class AccountsFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    ListView accountNamesView;
    AccountsList adapter;
    KalPref kalpref;
    FloatingActionButton floatingActionButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.listaccounts, container, false);
        kalpref = new KalPref(getActivity());
        adapter = new AccountsList(view.getContext());
        accountNamesView = view.findViewById(R.id.allaccounts);
        accountNamesView.setAdapter(adapter);
        floatingActionButton = view.findViewById(R.id.addNewAccount);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), LoginActivity.class);
                i.putExtra("ifNewAccChoosen", true);
                startActivity(i);
            }
        });

        List<KalAuth> auths = kalpref.getKalAuths();
        adapter.addAll(auths);
        return view;
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
