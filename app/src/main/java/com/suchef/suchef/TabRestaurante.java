package com.suchef.suchef;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.HashMap;


public class TabRestaurante extends Fragment{

    HashMap<String, String> filial;

    public TabRestaurante() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            filial = (HashMap<String, String>) getArguments().getSerializable("filial");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.tab_restaurante, container, false);

        TextView endereco = (TextView) v.findViewById(R.id.txtEndereco);
        TextView telefones = (TextView) v.findViewById(R.id.txtTelefones);

        StringBuilder sb = new StringBuilder();
        sb.append(filial.get("logradouro"));
        sb.append(", ");
        sb.append(filial.get("numero"));
        sb.append(" - ");
        sb.append(filial.get("complemento"));
        sb.append("\n");
        sb.append(filial.get("municipio"));
        sb.append(" - ");
        sb.append(filial.get("uf"));

        endereco.setText(sb.toString());

        telefones.setText(filial.get("telefone1") + "\n" + filial.get("telefone2") + "\n" + filial.get("telefone3"));

        return v;
    }

    public static TabRestaurante newInstance(HashMap<String, String> data) {
        TabRestaurante fragment = new TabRestaurante();
        Bundle args = new Bundle();
        args.putSerializable("filial", data);
        fragment.setArguments(args);
        return fragment;
    }

}