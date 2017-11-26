package com.suchef.suchef;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.support.v7.widget.ShareActionProvider;
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
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    HashMap<String, String> filial;
    TabProdutos tabProdutos;
    private ShareActionProvider mShareActionProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurante);

        sharedPref = this.getSharedPreferences(this.getString(R.string.app_name), Context.MODE_PRIVATE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        filial = (HashMap<String, String>) getIntent().getSerializableExtra("filial");

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        setTitle(filial.get("nome"));

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://suchef-web.herokuapp.com/api/filiais/" + filial.get("id") + "/produtos";

        System.out.println(url);

        String token = sharedPref.getString("token_api", "");

        JsonArrayRequest request = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    List<HashMap<String,String>> produtos = new ArrayList<>();

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject row = response.getJSONObject(i);

                        HashMap<String, String> produto = new HashMap<>();
                        produto.put("id", row.getString("id"));
                        produto.put("nome", row.getString("nome"));
                        produto.put("ref", row.getString("ref"));
                        produto.put("descricao", row.getString("descricao"));
                        produto.put("imagem", row.getString("imagem"));
                        produto.put("preco", row.getString("preco"));

                        produtos.add(produto);
                    }

                    System.out.println(produtos);

                    tabProdutos.atualizaProdutos(produtos);
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

        queue.add(request);
    }

    private void setupViewPager(ViewPager viewPager) {
        tabProdutos = new TabProdutos();

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(TabRestaurante.newInstance(filial), "Informações");
        adapter.addFragment(tabProdutos, "Produtos");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_restaurante, menu);

        // Locate MenuItem with ShareActionProvider
        final MenuItem item = menu.findItem(R.id.action_share);
        View v =  MenuItemCompat.getActionView(item);
        v.setClickable(true);
        System.out.println(item);

//        v.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onOptionsItemSelected(item);
//            }
//        });

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);


        return true;
    }

    private void setShareIntent(Intent shareIntent) {
        startActivity(Intent.createChooser(shareIntent, "Enviar para"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_share:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                setShareIntent(intent);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected((item));
    }

}
