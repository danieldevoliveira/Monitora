package com.teconsis.monitora

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CadastroUsuarioActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro_usuario)

        databaseHelper = DatabaseHelper(this)

        val gravarButton: Button = findViewById(R.id.gravarButton)
        val voltarButton: Button = findViewById(R.id.retornarButton)

        voltarButton.setOnClickListener {
            // Lógica para gravar os dados do usuário na tabela de usuários
            // após a gravação, retorne para a tela de login (MainActivity)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Encerra a atividade atual (CadastroUsuarioActivity)
        }

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)

        fun validateCadastro(email: String, password: String): Boolean {
            val emailPattern = Patterns.EMAIL_ADDRESS
            return emailPattern.matcher(email).matches() && password.isNotEmpty()
        }

        gravarButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (validateCadastro(email, password)) {
                try {
                    databaseHelper.insertUser(email, password)
                    Toast.makeText(this, "Usuário adicionado com sucesso", Toast.LENGTH_SHORT)
                        .show()
                } catch (e: IllegalArgumentException) {
                    Toast.makeText(
                        this,
                        "Erro ao adicionar usuário: ${e.message}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            } else {
                Toast.makeText(
                    this,
                    "Por favor, insira um endereço de e-mail válido e uma senha não vazia.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }
}
