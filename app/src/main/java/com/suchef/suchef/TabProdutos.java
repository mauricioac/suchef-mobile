package com.suchef.suchef;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;


public class TabProdutos extends Fragment {

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

        for (HashMap<String, String> produto : produtos) {
            produto.put("preco_formatado", "R$ " + produto.get("preco"));
        }

        ListAdapter adapter = new ProdutosListAdapter(getActivity().getFragmentManager(), getActivity().getApplicationContext(), produtos, R.layout.item_produto,
                new String[] { "preco_formatado", "nome", "descricao" },
                new int[] { R.id.precoProduto, R.id.nomeProduto, R.id.descricaoProduto });


        listaProdutos.setClickable(false);
        listaProdutos.setAdapter(adapter);
    }

}

class ProdutosListAdapter extends SimpleAdapter {
    FragmentManager fm;

    public ProdutosListAdapter(FragmentManager manager, Context context, java.util.List<? extends java.util.Map<java.lang.String,?>> data, @LayoutRes int layout_id, String []keys, @IdRes int []ids) {
        super(context, data, layout_id, keys, ids);

        this.fm = manager;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);

        final EditText quantidade = (EditText) v.findViewById(R.id.txtQuantidade);
        quantidade.setFocusable(false);

        final NumberPickerDialog np = new NumberPickerDialog();
        np.setValueChangeListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                quantidade.setText(String.valueOf(i));
            }
        });

        quantidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                np.show(fm, "time picker");
            }
        });

        return v;
    }
}