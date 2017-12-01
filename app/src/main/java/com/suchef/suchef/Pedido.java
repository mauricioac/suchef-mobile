package com.suchef.suchef;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by mauricio on 27/11/2017.
 */

public class Pedido {
    // id => quantidade
    HashMap<Produto, Integer> pedido;

    public Pedido() {
        this.pedido = new HashMap<>();
    }

    public void atualizaProduto(Produto produto, int quantidade) {
        if (quantidade == 0) {
            if (pedido.containsKey(produto)) {
                pedido.remove(produto);
            }

            return;
        }

        pedido.put(produto, quantidade);
    }

    public float subtotal() {
        float subtotal = 0;

        for (Produto k : pedido.keySet()) {
            subtotal += (k.getPreco() * pedido.get(k));
        }

        return subtotal;
    }

    public boolean vazio() {
        boolean vazio = true;

        for (Produto k : pedido.keySet()) {
            if (pedido.get(k) > 0) {
                vazio = false;
            }
        }

        return vazio;
    }

    public int numeroItens() {
        int cont = 0;

        for (Produto k : pedido.keySet()) {
            cont += pedido.get(k);
        }

        return cont;
    }

    public JSONObject toJson() {
        JSONObject payload = new JSONObject();
        try {
            payload.put("subtotal", subtotal());

            JSONArray produtos = new JSONArray();

            for (Produto k : pedido.keySet()) {
                JSONObject produto = new JSONObject();
                produto.put("id", k.getId());
                produto.put("quantidade", pedido.get(k));

                produtos.put(produto);
            }

            payload.put("produtos", produtos);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return payload;
    }

}
