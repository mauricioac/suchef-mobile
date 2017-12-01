package com.suchef.suchef;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ListView list;
    SharedPreferences sharedPref;
    ProgressDialog progress;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Restaurantes");

        sharedPref = this.getSharedPreferences(this.getString(R.string.app_name), Context.MODE_PRIVATE);
        list = (ListView) findViewById(R.id.listaRestaurantes);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://suchef-web.herokuapp.com/api/restaurantes";

        String token = sharedPref.getString("token_api", "");

        progress = new ProgressDialog(this); // this = YourActivity
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setMessage("Buscando lista de restaurantes....");
        progress.setIndeterminate(true);
        progress.setCanceledOnTouchOutside(false);

        progress.show();

        JsonArrayRequest request = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    List<Map<String,String>> restaurantes = new ArrayList<Map<String,String>>();

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject row = response.getJSONObject(i);

                        HashMap<String, String> restaurante = new HashMap<String, String>();

                        JSONArray filiais = row.getJSONArray("filiais");

                        for (int j = 0; j < filiais.length(); j++) {
                            JSONObject filial = filiais.getJSONObject(j);

                            restaurante.put("id", filial.getString("id"));
                            restaurante.put("nome", row.get("nome") + " - " + filial.getString("nome"));
                            restaurante.put("telefone1", filial.getString("telefone1"));
                            restaurante.put("telefone2", filial.getString("telefone2"));
                            restaurante.put("telefone3", filial.getString("telefone3"));
                            restaurante.put("uf", filial.getString("uf"));
                            restaurante.put("municipio", filial.getString("municipio"));
                            restaurante.put("logradouro", filial.getString("logradouro"));
                            restaurante.put("numero", filial.getString("numero"));
                            restaurante.put("complemento", filial.getString("complemento"));
                        }

                        restaurantes.add(restaurante);
                    }

                    ListAdapter adapter = new SimpleAdapter(MainActivity.this, restaurantes, R.layout.item_restaurante,
                            new String[] { "nome" },
                            new int[] { R.id.nome_restaurante });

                    list.setAdapter(adapter);

                    progress.hide();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getMessage());
                error.printStackTrace();
                progress.hide();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authentication", "token=" + sharedPref.getString("token_api", ""));
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 10, 1.0f));

        list.setClickable(true);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = list.getItemAtPosition(position);
                HashMap<String, String> a = (HashMap<String, String>) o;
                Intent intent = new Intent(getApplicationContext(), Restaurante.class);
                intent.putExtra("filial", a);
                startActivity(intent);
                return;
            }
        });
        queue.add(request);
    }
}
