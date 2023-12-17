package com.teconsis.monitora

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity

class ConfiguracoesActivity : AppCompatActivity() {
    private lateinit var modoOperacao: ModoOperacao
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracoes)

        databaseHelper = DatabaseHelper(this)
        sharedPreferences = getSharedPreferences("Configuracoes", MODE_PRIVATE)

        val ligaDesligaButton = findViewById<ToggleButton>(R.id.ligaDesligaButton)
        val savedState = sharedPreferences.getBoolean("estadoBotao", false)
        ligaDesligaButton.isChecked = savedState
        val configurarButton: Button = findViewById(R.id.configurarButton)
        val sairButton: Button = findViewById(R.id.sairButton)

        ligaDesligaButton.setOnCheckedChangeListener { buttonView, isChecked ->
            modoOperacao = ModoOperacao.ligarDesligar(1, isChecked)
            databaseHelper.atualizarModoOperacao(modoOperacao)
            val editor = sharedPreferences.edit()
            editor.putBoolean("estadoBotao", isChecked)
            editor.apply()
        }

        configurarButton.setOnClickListener {
            val intent = Intent(this, ConfigurarActivity::class.java)
            startActivity(intent)
            finish()
        }

        sairButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}
