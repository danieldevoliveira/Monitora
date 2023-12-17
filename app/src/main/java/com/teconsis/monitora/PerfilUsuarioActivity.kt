package com.teconsis.monitora

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson

class PerfilUsuarioActivity : AppCompatActivity() {
    private lateinit var showUsersButton: Button
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_usuario)

        databaseHelper = DatabaseHelper(this)

        val atualizarPerfilButton: Button = findViewById(R.id.atualizarPerfilButton)

        atualizarPerfilButton.setOnClickListener {
            val intent = Intent(this, AtualizacaoUsuarioActivity::class.java)
            startActivity(intent)
        }

        val configButton: Button = findViewById(R.id.configButton)
        configButton.setOnClickListener {
            val intent = Intent(this, ConfiguracoesActivity::class.java)
            startActivity(intent)
        }

        showUsersButton = findViewById(R.id.showUsersButton)

        showUsersButton.setOnClickListener {
            val userList = databaseHelper.getAllUsers()
            val gson = Gson()
            val userListJson = gson.toJson(userList)
            val intent = Intent(this, ListaUsuariosActivity::class.java)
            intent.putExtra("userListJson", userListJson)
            startActivity(intent)
        }
    }
}
