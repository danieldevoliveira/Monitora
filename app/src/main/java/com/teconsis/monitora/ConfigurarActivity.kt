package com.teconsis.monitora

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ConfigurarActivity : AppCompatActivity() {

    private lateinit var aparelhoEditText: EditText
    private lateinit var temporizadorEditText: EditText
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configurar)

        databaseHelper = DatabaseHelper(this)
        val sharedPreferences = getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE)
        val loggedInUserId = sharedPreferences.getInt("loggedInUserId", -1)
        temporizadorEditText = findViewById(R.id.temporizadorEditText)
        val gravarButton: Button = findViewById(R.id.gravarButton)
        val retornarButton: Button = findViewById(R.id.retornarButton)
        val cadastrarButton: Button = findViewById(R.id.inserirButton)

        val aparelhoSpinner: Spinner = findViewById(R.id.aparelhoSpinner)
        val aparelhoList = databaseHelper.getAllAparelhos()
        val aparelhoAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            aparelhoList.map { it.descricao })
        aparelhoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        aparelhoSpinner.adapter = aparelhoAdapter

        gravarButton.setOnClickListener {
            val temporizador = temporizadorEditText.text.toString().toIntOrNull() ?: 0

            temporizadorEditText.text.clear()

            val db = databaseHelper.writableDatabase
            val selectedAparelho = aparelhoList[aparelhoSpinner.selectedItemPosition]
            val configuracao =
                databaseHelper.verificarEAtualizarConfiguracao(
                    db,
                    loggedInUserId,
                    1,
                    1,
                    temporizador
                )

            if (configuracao) {
                databaseHelper.updateDispositivoById(1, selectedAparelho.id)
                val mensagem = "Configuração cadastrada com sucesso"
                Log.e("Configuração", mensagem)
                Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()

            } else {
                val mensagem = "Falha na inserção da configuração"
                Log.e("Configuração", mensagem)
                Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
            }
        }

        retornarButton.setOnClickListener {
            val intent = Intent(this, ConfiguracoesActivity::class.java)
            startActivity(intent)
            finish()
        }

        cadastrarButton.setOnClickListener{
            val intent = Intent(this, CadastroAparelhoActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}


