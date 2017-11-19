package com.suchef.suchef;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin, btnCadastro;
    EditText edtEmail, edtSenha;
    ProgressDialog progress;
    SharedPreferences sharedPref;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = (Button) findViewById(R.id.loginCta);
        btnCadastro = (Button) findViewById(R.id.loginCadastro);
        edtEmail = (EditText) findViewById(R.id.loginEmail);
        edtSenha = (EditText) findViewById(R.id.loginSenha);

        progress = new ProgressDialog(this); // this = YourActivity
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setMessage("Enviando dados. Aguarde....");
        progress.setIndeterminate(true);
        progress.setCanceledOnTouchOutside(false);

        sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        String token = sharedPref.getString("token_api", "");

        if (!token.isEmpty()) {
            startActivity(new Intent(this, MainActivity.class));
            return;
        }

        final RequestQueue queue = Volley.newRequestQueue(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean valido = true;

                if(edtEmail.getText().toString().isEmpty()){
                    edtEmail.setError("Email vazio");
                    valido = false;
                }

                if(edtSenha.getText().toString().isEmpty()){
                    edtSenha.setError("Senha vazia");
                    valido = false;
                }

                if (!valido) {
                    return;
                }

                btnLogin.setEnabled(false);
                progress.show();

                String url = "https://suchef-web.herokuapp.com/api/login";
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("email", edtEmail.getText().toString());
                params.put("senha", edtSenha.getText().toString());

                JSONObject data = new JSONObject(params);

                JsonObjectRequest request = new JsonObjectRequest(url, data, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    System.out.println(response.toString());
                    try {
                        String nome = response.getString("nome");
                        String token = response.getString("token_api");

                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("nome", nome);
                        editor.putString("token_api", token);
                        editor.commit();

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                        startActivity(intent);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    NetworkResponse networkResponse = error.networkResponse;

                    btnLogin.setEnabled(true);
                    progress.hide();


                    Toast toast = Toast.makeText(getApplicationContext(), "Login e/ou senha incorretos!", Toast.LENGTH_LONG);
                    toast.show();
                    }
                });

                queue.add(request);
            }
        });

        btnCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Cadastro.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
            }
        });
    }
}
