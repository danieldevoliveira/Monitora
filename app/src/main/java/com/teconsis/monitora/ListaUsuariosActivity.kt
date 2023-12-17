package com.teconsis.monitora

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson

class ListaUsuariosActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_usuarios)

        databaseHelper = DatabaseHelper(this)

        val sharedPreferences = getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE)
        val loggedInUserEmail = sharedPreferences.getString("loggedInUserEmail", "")
        val userListJson = intent.getStringExtra("userListJson")
        val userListView = findViewById<ListView>(R.id.userListView)
        val errorTextView = findViewById<TextView>(R.id.errorTextView)
        val voltarButton: Button = findViewById(R.id.returnButton)
        val deleteButton: Button = findViewById(R.id.deleteButton)
        val loggedInUserRole = loggedInUserEmail?.let { databaseHelper.getRoleUser(it) }
        val userIdEditTextContainer = findViewById<LinearLayout>(R.id.userIdEditTextContainer)

        voltarButton.setOnClickListener {
            val intent = Intent(this, PerfilUsuarioActivity::class.java)
            startActivity(intent)
            finish()
        }

        if (loggedInUserRole == "admin") {
            if (userListJson != null) {
                val gson = Gson()
                val userListType = object : TypeToken<ArrayList<User>>() {}.type
                val userList = gson.fromJson<ArrayList<User>>(userListJson, userListType)

                userListView.visibility = View.VISIBLE
                errorTextView.visibility = View.GONE
                userIdEditTextContainer.visibility = View.VISIBLE
                deleteButton.isEnabled = true
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, userList)
                userListView.adapter = adapter
            } else {
                userListView.visibility = View.GONE
                errorTextView.visibility = View.VISIBLE
                userIdEditTextContainer.visibility = View.GONE
                deleteButton.isEnabled = false
            }
        } else {
            userListView.visibility = View.GONE
            errorTextView.visibility = View.VISIBLE
            userIdEditTextContainer.visibility = View.GONE
            deleteButton.isEnabled = false
        }

        deleteButton.setOnClickListener {
            val userIdEditText = findViewById<EditText>(R.id.userIdEditText)
            val userIdToDelete = userIdEditText.text.toString().toIntOrNull()

            try {
                if (userIdToDelete != null) {
                    // Chama a função para excluir o usuário do DatabaseHelper
                    val rowsDeleted = loggedInUserEmail?.let { it1 ->
                        databaseHelper.deleteUserById(it1, userIdToDelete)
                    }

                    if (rowsDeleted != null) {
                        if (rowsDeleted > 0) {
                            val userList = databaseHelper.getAllUsers()
                            val adapter =
                                ArrayAdapter(this, android.R.layout.simple_list_item_1, userList)
                            userListView.adapter = adapter
                        } else {
                            Toast.makeText(this, "Usuário não existe.", Toast.LENGTH_SHORT).show()
                        }
                    }
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