package com.wgc.crudfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wgc.crudfirebase.modelo.Pessoa;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText editNome, editEmail;
    private ListView listVPessoas;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private List<Pessoa> pessoaList = new ArrayList<Pessoa>();
    private ArrayAdapter<Pessoa> pessoaArrayAdapter;

    private final String CHILD = "Pessoa";
    Pessoa pessoaEscolhida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        componentesDeTela();
        configuracaoFirebase();
        eventoDataBase();
        eventoList();
    }

    private void eventoList() {
        listVPessoas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                pessoaEscolhida = (Pessoa) adapterView.getItemAtPosition(i);
                editNome.setText(pessoaEscolhida.getNome());
                editEmail.setText(pessoaEscolhida.getEmail());
            }
        });
    }

    private void eventoDataBase() {
        databaseReference.child(CHILD).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pessoaList.clear();
                for (DataSnapshot objetoSnapshot : dataSnapshot.getChildren()){
                    Pessoa p = objetoSnapshot.getValue(Pessoa.class);
                    pessoaList.add(p);
                }
                pessoaArrayAdapter = new ArrayAdapter<Pessoa>(MainActivity.this,
                        android.R.layout.simple_list_item_1,pessoaList);
                listVPessoas.setAdapter(pessoaArrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void configuracaoFirebase() {
        FirebaseApp.initializeApp(MainActivity.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }

    private void componentesDeTela() {
        editNome = (EditText)findViewById(R.id.editNome);
        editEmail = (EditText)findViewById(R.id.editEmail);
        listVPessoas = (ListView) findViewById(R.id.listVPessoas);
    }

    private void alert(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int idEscolhido = item.getItemId();

        switch (idEscolhido){
            case R.id.menu_new:
                newPessoa();
                break;
            case R.id.menu_update:
                updatePessoa();
                break;
            case R.id.menu_delete:
                deletePessoa();
                break;
            default:
                alert("Opção não identificada");
                break;
        }

        return true;
    }

    private void deletePessoa() {
        Pessoa p = new Pessoa();
        p.setUid(pessoaEscolhida.getUid());
        databaseReference.child(CHILD).child(p.getUid()).removeValue();
        alert("objeto deletado com sucesso");
        limparCampos();
    }

    private void updatePessoa() {
        Pessoa p = new Pessoa();
        p.setUid(pessoaEscolhida.getUid());
        p.setNome(editNome.getText().toString().trim());
        p.setEmail(editEmail.getText().toString().trim());
        databaseReference.child(CHILD).child(p.getUid()).setValue(p);
        alert(p.getNome() + " foi alterado com sucesso");
        limparCampos();
    }

    private void newPessoa() {
        Pessoa p = new Pessoa();
        p.setNome(editNome.getText().toString().trim());
        p.setEmail(editEmail.getText().toString().trim());
        databaseReference.child(CHILD).child(p.getUid()).setValue(p);
        alert(p.getNome() + " foi salvo com sucesso");
        limparCampos();
    }

    private void limparCampos() {
        editEmail.setText("");
        editNome.setText("");
    }
}
