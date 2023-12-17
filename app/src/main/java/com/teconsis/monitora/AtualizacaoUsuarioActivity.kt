package com.teconsis.monitora

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AtualizacaoUsuarioActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var roleSpinner: Spinner

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_atualizacao_usuario)

        databaseHelper = DatabaseHelper(this)

        val sharedPreferences = getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE)
        val loggedInUserId = sharedPreferences.getInt("loggedInUserId", -1)
        val loggedInUserEmail = sharedPreferences.getString("loggedInUserEmail", "")
        val atualizarButton: Button = findViewById(R.id.atualizarButton)

        emailEditText = findViewById(R.id.novoEmailEditText)
        passwordEditText = findViewById(R.id.novaPasswordEditText)
        roleSpinner = findViewById(R.id.roleSpinner)

        // Popule o Spinner com as opções de papel (role)
        val roles = resources.getStringArray(R.array.roles)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        roleSpinner.adapter = adapter

        emailEditText.setText(loggedInUserEmail)

        atualizarButton.setOnClickListener {
            val novoEmail = emailEditText.text.toString()
            val novaPassword = passwordEditText.text.toString()
            val novoRole = roleSpinner.selectedItem.toString()

            if (validateInput(loggedInUserId, novoEmail, novaPassword, novoRole)) {
                try {
                    databaseHelper.updateUser(loggedInUserId, novoEmail, novaPassword, novoRole)
                    Toast.makeText(this, "Usuário atualizado com sucesso", Toast.LENGTH_SHORT)
                        .show()
                    voltarParaPerfil()
                } catch (e: IllegalArgumentException) {
                    Toast.makeText(this, "Erro ao atualizar usuário", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Dados inválidos!", Toast.LENGTH_SHORT).show()
            }
        }

        val voltarButton: Button = findViewById(R.id.configButton)
        voltarButton.setOnClickListener {
            val intent = Intent(this, ConfiguracoesActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun validateInput(loggedInUserId: Int, email: String, password: String, role: String): Boolean {
        return loggedInUserId >= 0 && isEmailValid(email) && isPasswordValid(password) && isRoleValid(role)
    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.isNotEmpty()
    }

    private fun isRoleValid(role: String): Boolean {
        // Verifique se o papel (role) é uma das opções válidas (você pode personalizar essa validação)
        val validRoles = resources.getStringArray(R.array.roles)
        return validRoles.contains(role)
    }

    private fun voltarParaPerfil() {
        val intent = Intent(this, ConfiguracoesActivity::class.java)
        startActivity(intent)
        finish()
    }
}
