package com.suchef.suchef;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

import java.util.HashMap;

public class Cadastro extends AppCompatActivity {

    EditText editNome, editEmail, editSenha, editRepitaSenha, editCpf;
    Button btnCadatrar, btnGotoLogin;
    SharedPreferences sharedPref;

    ConnectivityManager cm;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        setTitle("Cadastro");

        cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        editNome = (EditText)findViewById(R.id.edtNome);
        editCpf = (EditText)findViewById(R.id.edtCpf);
        editEmail = (EditText)findViewById(R.id.edtEmail);
        editSenha = (EditText) findViewById(R.id.edtSenha);
        editRepitaSenha = (EditText)findViewById(R.id.edtRepitaSenha);

        sharedPref = this.getSharedPreferences(this.getString(R.string.app_name), Context.MODE_PRIVATE);

        String token = sharedPref.getString("token_api", "");

        if (!token.isEmpty()) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
            return;
        }


        btnCadatrar = (Button)findViewById(R.id.button);
        btnGotoLogin = (Button) findViewById(R.id.btnGotoLogin);

        final RequestQueue queue = Volley.newRequestQueue(this);

        final ProgressDialog dialog = new ProgressDialog(this); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Enviando dados. Aguarde....");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);


        btnCadatrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean valido = true;

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();

                if (!isConnected) {
                    Toast.makeText(getApplicationContext(), "Você está offline!", Toast.LENGTH_LONG).show();
                    return;
                }


                if(editNome.getText().toString().isEmpty()){
                    editNome.setError("Nome Completo Vazio!");
                    valido = false;
                }

                if(editCpf.getText().toString().isEmpty()){
                    editCpf.setError("Cpf Vazio!");
                    valido = false;
                }

                if(editEmail.getText().toString().isEmpty()) {
                    editEmail.setError("Email Vazio!");
                    valido = false;
                }

                if(editSenha.getText().toString().isEmpty()) {
                    editSenha.setError("Senha Vazia!");
                    valido = false;
                } else if (editSenha.getText().toString().length() < 6) {
                    editSenha.setError("Senha deve conter no mínimo 6 caracteres");
                    return;
                }

                if(!editSenha.getText().toString().trim().equals(editRepitaSenha.getText().toString().trim())){
                    editRepitaSenha.setError("Senhas devem de ser iguais!");
                    valido = false;
                }

                if (!valido) {
                    return;
                }

                String url = "http://suchef-web.herokuapp.com/api/cadastro";
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("nome", editNome.getText().toString());
                params.put("cpf", editCpf.getText().toString());
                params.put("email", editEmail.getText().toString());
                params.put("senha", editSenha.getText().toString());

                JSONObject data = new JSONObject(params);

                btnCadatrar.setEnabled(false);

                dialog.show();

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

                    btnCadatrar.setEnabled(true);
                    dialog.hide();

                    if (networkResponse != null && networkResponse.statusCode == 422) {

                    }
                    }
                });

                queue.add(request);
            }
        });

        btnGotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
            }
        });
    }
}
