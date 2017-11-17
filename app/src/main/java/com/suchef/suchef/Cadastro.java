package com.suchef.suchef;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;

public class Cadastro extends AppCompatActivity {

    EditText edtText, edtText2, edtText3, edtText4, edtText5;
    Button btn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        edtText = (EditText)findViewById(R.id.editText);
        edtText2 = (EditText)findViewById(R.id.editText2);
        edtText3 = (EditText)findViewById(R.id.editText3);
        edtText4 = (EditText)findViewById(R.id.editText4);
        edtText5 = (EditText)findViewById(R.id.editText5);

        btn = (Button)findViewById(R.id.button);
        final RequestQueue queue = Volley.newRequestQueue(this);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(edtText.getText().toString().isEmpty()){
                    Toast toast = Toast.makeText(Cadastro.this, "Nome Completo Vazio!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                    return;
                } else if(edtText2.getText().toString().isEmpty()){
                    Toast toast = Toast.makeText(Cadastro.this, "Cpf Vazio!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                    return;
                } else if(edtText3.getText().toString().contains("")){

                }

                String url = "https://suchef-web.herokuapp.com/api/cadastro";
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("nome", edtText.getText().toString());
                params.put("cpf", edtText2.getText().toString());
                params.put("email", edtText3.getText().toString());
                params.put("senha", edtText4.getText().toString());

                JSONObject data = new JSONObject(params);

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, data, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response.toString());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        System.out.println(error.getMessage());
                    }
                });

                queue.add(request);



            }
        });


    }
}
