package com.teconsis.monitora

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Properties
import java.util.Random
import javax.mail.AuthenticationFailedException
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.SendFailedException
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class MainActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        emailEditText = findViewById(R.id.emailEditText)
        emailEditText.requestFocus()
        passwordEditText = findViewById(R.id.passwordEditText)
        val loginButton: Button = findViewById(R.id.loginButton)
        val novoButton: Button = findViewById(R.id.novoButton)
        val forgotPasswordLink: Button = findViewById(R.id.forgotPasswordLink)

        databaseHelper = DatabaseHelper(this)
        databaseHelper.createAdminUser()
        databaseHelper.createModoOperacaoPadrao()
        databaseHelper.createDispositivoPadrao()
        databaseHelper.createAparelhoPadrao()

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (validateLogin(email)) {
                if (databaseHelper.isEmailExists(email)) {
                    val loggedInUserId = databaseHelper.authenticateUser(email, password)

                    if (loggedInUserId != null) {
                        val sharedPreferences =
                            getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putInt("loggedInUserId", loggedInUserId)
                        editor.putString("loggedInUserEmail", email)
                        editor.apply()
                        val intent = Intent(this, PerfilUsuarioActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this,
                            "Email ou senha inválida.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(this, "Usuário não encontrado.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(
                    this,
                    "Email ou senha inválida.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        novoButton.setOnClickListener {
            val intent = Intent(this, CadastroUsuarioActivity::class.java)
            startActivity(intent)
        }

        fun generateRandomPassword(): String {
            val characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
            val random = Random()
            val password = StringBuilder(6)

            for (i in 0 until 6) {
                val index = random.nextInt(characters.length)
                password.append(characters[index])
            }

            return password.toString()
        }

        forgotPasswordLink.setOnClickListener {
            val userEmail = emailEditText.text.toString()

            if (validateLogin(userEmail)) {
                if (databaseHelper.isEmailExists(userEmail)) {
                    val newPassword = generateRandomPassword()

                    val properties = Properties()
                    properties["mail.smtp.auth"] = "true"
                    properties["mail.smtp.starttls.enable"] = "true"
                    properties["mail.smtp.host"] = "smtp.gmail.com"
                    properties["mail.smtp.port"] = "587"

                    val session = Session.getInstance(properties, object : Authenticator() {
                        override fun getPasswordAuthentication(): PasswordAuthentication {
                            return PasswordAuthentication(
                                getString(R.string.email),
                                getString(R.string.senha)
                            )
                        }
                    })

                    GlobalScope.launch(Dispatchers.IO) {
                        try {
                            val message = MimeMessage(session)
                            message.setFrom(InternetAddress(userEmail))
                            message.setRecipients(
                                Message.RecipientType.TO,
                                InternetAddress.parse(userEmail)
                            )
                            message.subject = "Redefinição de senha"
                            message.setText("Sua nova senha é: $newPassword")

                            Transport.send(message)

                            runOnUiThread {
                                Toast.makeText(
                                    applicationContext,
                                    "Email de redefinição enviado com sucesso",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            databaseHelper.updateUserByEmail(userEmail, newPassword)

                        } catch (e: MessagingException) {
                            val mensagem: String = when (e) {
                                is SendFailedException -> "Falha ao enviar o e-mail. Verifique os destinatários."
                                is AuthenticationFailedException -> "Falha na autenticação do servidor de e-mail."
                                is MessagingException -> "Erro de mensagens: ${e.message}"
                                else -> "Erro ao enviar o e-mail de redefinição de senha: ${e.message}"
                            }

                            runOnUiThread {
                                Toast.makeText(applicationContext, mensagem, Toast.LENGTH_SHORT)
                                    .show()
                            }


                        }
                    }
                } else {
                    Toast.makeText(this, "Usuário não encontrado.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Email inválido.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun validateLogin(email: String): Boolean {
        val emailPattern = Patterns.EMAIL_ADDRESS
        return emailPattern.matcher(email).matches()
    }
}
