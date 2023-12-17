package com.teconsis.monitora

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson

class ListaAparelhosActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_aparelhos)

        databaseHelper = DatabaseHelper(this)
        val aparelhoListView = findViewById<ListView>(R.id.aparelhoListView)
        val voltarButton: Button = findViewById(R.id.returnButton)
        val deleteButton: Button = findViewById(R.id.deleteButton)

        voltarButton.setOnClickListener {
            val intent = Intent(this, CadastroAparelhoActivity::class.java)
            startActivity(intent)
            finish()
        }

            var aparelhoList = databaseHelper.getAllAparelhos()
            val gson = Gson()
            val aparelhoListJson = gson.toJson(aparelhoList)
            val aparelhoListType = object : TypeToken<ArrayList<Aparelho>>() {}.type
            aparelhoList = gson.fromJson<ArrayList<Aparelho>>(aparelhoListJson, aparelhoListType)
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, aparelhoList)
            aparelhoListView.adapter = adapter

        deleteButton.setOnClickListener {
            val aparelhoIdEditText = findViewById<EditText>(R.id.aparelhoIdEditText)
            val aparelhoIdToDelete = aparelhoIdEditText.text.toString().toIntOrNull()

            try {
                if (aparelhoIdToDelete != null) {
                            databaseHelper.deletarAparelhoPorId(aparelhoIdToDelete)
                            val aparelhoList = databaseHelper.getAllAparelhos()
                            val adapter =
                                ArrayAdapter(this, android.R.layout.simple_list_item_1, aparelhoList)
                            aparelhoListView.adapter = adapter
                } else {
                    Toast.makeText(this, "ID não pode ser vazio.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: SecurityException) {
                Toast.makeText(
                    this,
                    "Usuário não é administrador: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}