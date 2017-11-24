package com.suchef.suchef;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Restaurante extends AppCompatActivity {
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurante);

        sharedPref = this.getSharedPreferences(this.getString(R.string.app_name), Context.MODE_PRIVATE);

        FrameLayout fl = (FrameLayout) findViewById(R.id.tabMode);
        fl.addT

        String id = getIntent().getStringExtra("id");

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://suchef-web.herokuapp.com/api/filiais/" + id + "/produtos";

        String token = sharedPref.getString("token_api", "");

        JsonArrayRequest request = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    List<Map<String,String>> produtos = new ArrayList<Map<String,String>>();

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject row = response.getJSONObject(i);

                        HashMap<String, String> produto = new HashMap<String, String>();
                        produto.put("id", row.getString("id"));
                        produto.put("nome", row.getString("nome"));
                        produto.put("ref", row.getString("ref"));
                        produto.put("descricao", row.getString("descricao"));
                        produto.put("imagem", row.getString("imagem"));
                        produto.put("preco", row.getString("preco"));
                    }

//                    ListAdapter adapter = new SimpleAdapter(MainActivity.this, restaurantes, R.layout.item_restaurante,
//                            new String[] { "nome" },
//                            new int[] { R.id.nome_restaurante });
//
//                    list.setAdapter(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getMessage());
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authentication", "token=" + sharedPref.getString("token_api", ""));
                return headers;
            }
        };
    }
}
