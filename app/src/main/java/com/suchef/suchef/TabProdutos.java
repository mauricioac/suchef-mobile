package com.suchef.suchef;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;


public class TabProdutos extends Fragment{

    Button finalizarPedido;
    ListView listaProdutos;

    public TabProdutos() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.tab_produtos, container, false);

//        finalizarPedido = (Button) v.findViewById(R.id.finalizarPedido);
        listaProdutos = (ListView) v.findViewById(R.id.listaProdutos);

        return v;
    }

    public void atualizaProdutos(List<HashMap<String, String>> produtos) {
        ListAdapter adapter = new SimpleAdapter(getActivity().getApplicationContext(), produtos, R.layout.item_produto,
                new String[] { "preco", "nome", "descricao" },
                new int[] { R.id.precoProduto, R.id.nomeProduto, R.id.descricaoProduto });


        System.out.println("LOLZ");
        System.out.println(produtos);
        listaProdutos.setAdapter(adapter);
    }

}