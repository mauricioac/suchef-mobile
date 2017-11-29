package com.suchef.suchef;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.app.FragmentManager;
import android.support.design.widget.FloatingActionButton;
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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TabProdutos extends Fragment implements QuantidadeObserver {

    FloatingActionButton fab;
    ListView listaProdutos;
    Pedido pedido;
    ArrayList<Produto> produtos;
    TextView fabCounter;

    public TabProdutos() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_produtos, container, false);

        listaProdutos = (ListView) v.findViewById(R.id.listaProdutos);

        pedido = new Pedido();
        produtos = new ArrayList<>();

        fab = (FloatingActionButton) v.findViewById(R.id.cartFAB);
        fab.hide();

        fabCounter = (TextView) v.findViewById(R.id.fabCounter);
        fabCounter.setVisibility(View.INVISIBLE);

        return v;
    }

    public void atualizaProdutos(List<HashMap<String, String>> produtos) {

        for (HashMap<String, String> produto : produtos) {
            produto.put("preco_formatado", "R$ " + produto.get("preco"));

            Produto p = new Produto(
                    Integer.parseInt(produto.get("id")),
                    Float.parseFloat(produto.get("preco")),
                    produto.get("nome"),
                    produto.get("descricao"),
                    Integer.parseInt(produto.get("filiais_id") == null ? "0" : produto.get("filiais_id"))
            );

            this.produtos.add(p);
        }

        ProdutosListAdapter adapter = new ProdutosListAdapter(getActivity().getFragmentManager(), getActivity().getApplicationContext(), produtos, R.layout.item_produto,
                new String[] { "preco_formatado", "nome", "descricao" },
                new int[] { R.id.precoProduto, R.id.nomeProduto, R.id.descricaoProduto });

        adapter.attachObserver(this);


        listaProdutos.setClickable(false);
        listaProdutos.setAdapter(adapter);
    }

    @Override
    public void onUpdate(int position, int quantidade) {
        Produto p = produtos.get(position);

        pedido.atualizaProduto(p, quantidade);

        if (pedido.vazio()) {
            fab.hide();
            fabCounter.setVisibility(View.INVISIBLE);
        } else {
            fab.show();
            fabCounter.setVisibility(View.VISIBLE);
            fabCounter.setText(String.valueOf(pedido.numeroItens()));
        }
    }
}

class ProdutosListAdapter extends SimpleAdapter {
    FragmentManager fm;
    List<QuantidadeObserver> observers;

    public ProdutosListAdapter(FragmentManager manager, Context context, java.util.List<? extends java.util.Map<java.lang.String,?>> data, @LayoutRes int layout_id, String []keys, @IdRes int []ids) {
        super(context, data, layout_id, keys, ids);

        this.fm = manager;
        observers = new ArrayList<>();
    }

    public void attachObserver(QuantidadeObserver observer) {
        this.observers.add(observer);
    }

    public void notifyObservers(Integer position, Integer quantidade) {
        for (QuantidadeObserver o : observers) {
            o.onUpdate(position, quantidade);
        }
    }

        @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);

        final EditText quantidade = (EditText) v.findViewById(R.id.txtQuantidade);
        quantidade.setFocusable(false);

        // ಠ_ಠ
        Method notify = null;
        try {

            notify = this.getClass().getMethod("notifyObservers", Integer.class, Integer.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        final Method notifyP = notify;

        final Object t = this;

        final int pos = position;

        final NumberPickerDialog np = new NumberPickerDialog();
        np.setValueChangeListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                quantidade.setText(String.valueOf(i));

                try {
                    notifyP.invoke(t, pos, i);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    ex.printStackTrace();
                }
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