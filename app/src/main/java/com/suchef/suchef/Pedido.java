package com.suchef.suchef;

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

}
