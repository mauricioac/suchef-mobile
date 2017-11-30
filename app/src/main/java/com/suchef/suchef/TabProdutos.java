package com.suchef.suchef;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.app.FragmentManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TabProdutos extends Fragment implements QuantidadeObserver{

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

    public void finalizaPedido() {
        ProgressDialog pr = new ProgressDialog(this.getActivity());
        pr.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pr.setMessage("Enviando pedido...");
        pr.setIndeterminate(true);
        pr.setCanceledOnTouchOutside(false);
        pr.show();

        //TODO: enviar pedido API
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.tab_produtos, container, false);

        final LayoutInflater il = inflater;

        listaProdutos = (ListView) v.findViewById(R.id.listaProdutos);

        pedido = new Pedido();
        produtos = new ArrayList<>();

        fab = (FloatingActionButton) v.findViewById(R.id.cartFAB);
        fab.hide();

        Method _finaliza = null;
        try {

            _finaliza = this.getClass().getMethod("finalizaPedido");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        final Method finaliza = _finaliza;

        final Object self = this;

        fab.setOnClickListener(new View.OnClickListener() {
            private PopupWindow pw;

            @Override
            public void onClick(View v) {
                showPopup();


            }
            private void showPopup() {
                Rect displayRectangle = new Rect();
                Window window = getActivity().getWindow();
                window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

                try {
                    v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View layout = il.inflate(R.layout.popup,
                            (ViewGroup) v.findViewById(R.id.popup_1));
                    pw = new PopupWindow(layout, (int)(displayRectangle.width() * 0.9f), 870, true);
                    pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
                    pw.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                    pw.setElevation(10);

                    View container = (View) pw.getContentView().getParent();
                    Context context = pw.getContentView().getContext();
                    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                    WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
                    p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                    p.dimAmount = 0.3f;
                    wm.updateViewLayout(container, p);


                    float total = pedido.subtotal();
                    TextView txtTotal = (TextView) layout.findViewById(R.id.txtTotal);
                    txtTotal.setText("R$ " + String.format("%.2f", total));

                    Button finalizar = (Button) layout.findViewById(R.id.btnFinalizarPedido);

                    finalizar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            pw.dismiss();

                            try {
                                finaliza.invoke(self);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

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