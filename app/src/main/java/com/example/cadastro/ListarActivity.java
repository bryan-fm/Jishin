package com.example.cadastro;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

public class ListarActivity extends AppCompatActivity {



    private ListView lista;
    private AlunoDAO dao;
    private List<Aluno> alunos;
    private List<Aluno> alunosFilt = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar);

        lista = findViewById(R.id.lista_aluno);
        dao = new AlunoDAO(this);
        alunos = dao.obterAll();
        alunosFilt.addAll(alunos);

        ArrayAdapter<Aluno> adapt = new ArrayAdapter<Aluno>(this, android.R.layout.simple_expandable_list_item_1, alunosFilt);
        lista.setAdapter(adapt);

        registerForContextMenu(lista);
    }


    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater i = getMenuInflater();
        i.inflate(R.menu.menuprincipal, menu);

        SearchView sv = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                procura(newText);
                return false;
            }
        });
        return true;
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu,v,menuInfo);
        MenuInflater i = getMenuInflater();
        i.inflate(R.menu.menu_contexto,menu);
    }


    public void procura(String nome){
        alunosFilt.clear();
        for(Aluno a: alunos){
            if(a.getNome().toLowerCase().contains(nome.toLowerCase())){
                alunosFilt.add(a);
            }
        }
        lista.invalidateViews();
    }

    public void excluir (MenuItem item){
        AdapterView.AdapterContextMenuInfo menuInfo =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final Aluno alunoExcluir = alunosFilt.get(menuInfo.position);

        AlertDialog dialogex = new AlertDialog.Builder(this)
                .setTitle("Atenção")
                .setMessage("Realmente deseja excluir?")
                .setNegativeButton("NÃO", null)
                .setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alunosFilt.remove(alunoExcluir);
                        alunos.remove(alunoExcluir);
                        dao.excluir(alunoExcluir);
                        lista.invalidateViews();
                    }
                }).create();
        dialogex.show();
    }

    public void cadastrar (MenuItem item){
        Intent it = new Intent(this, MainActivity.class);
        startActivity(it);

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        alunos = dao.obterAll();
        alunosFilt.clear();
        alunosFilt.addAll(alunos);
        lista.invalidateViews();
    }

    public void atualizar (MenuItem item){
        AdapterView.AdapterContextMenuInfo menuInfo =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final Aluno alunoAtualizar = alunosFilt.get(menuInfo.position);
        Intent it = new Intent(this, MainActivity.class);
        it.putExtra("aluno", alunoAtualizar);
        startActivity(it);

    }
}
