package com.app.appburleservice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import com.app.appburleservice.ui.theme.AppBurleServiceTheme
import com.google.android.gms.location.LocationServices
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.activity.OnBackPressedCallback
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon

data class Report(
    val clientName: String,
    val observation: String,
    val date: String,
    val time: String,
    val latitude: Double,
    val longitude: Double
)

val BackgroundColorGlobal = Color(0xFF0D1117)
var colorFieldGlobal = Color(0xF02C2D2E)
val borderColorGlobal = Color.Transparent
val textColorGlobal = Color.White
var labelColorGlobal = Color.White
var cursorColorGlobal = Color.White
var colorButtonGlobal = Color(0xF026A641)
var colorButtonHoverGlobal = Color(0xF039D353)

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            // Permissão concedida, você pode obter a localização aqui
        } else {
            // Permissão negada
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppBurleServiceTheme {
                Scaffold { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(BackgroundColorGlobal)
                            .padding(innerPadding)
                    ) {
                        MainContent()
                    }
                }
            }
        }
        // Solicitar permissão de localização
        requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)

        // Configurar o comportamento do botão de "voltar" na MainActivity
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Navega para a LoginActivity
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)

                // Aplica a animação de transição
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            }
        })
    }
}

@Composable
fun MainContent(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val locationClient = LocationServices.getFusedLocationProviderClient(context)

    val observationField = remember { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val successMessage = remember { mutableStateOf<String?>(null) }

    // Estado para o Dropdown
    val isDropdownExpanded = remember { mutableStateOf(false) }
    val selectedOption = remember { mutableStateOf("Selecione um cliente") }
    val options = listOf("%{Test-client}% 1", "%{Test-client}% 2", "%{Test-client}% 3", "%{Test-client}% 4", "%{Test-client}% 5", "%{Test-client}% 6", "%{Test-client}% 7", "%{Test-client}% 8", "%{Test-client}% 9", "%{Test-client}% 10", "%{Test-client}% 11", "%{Test-client}% 12", "%{Test-client}% 13", "%{Test-client}% 14", "%{Test-client}% 15")

    // Estado para o AlertDialog
    val showDialog = remember { mutableStateOf(false) }
    val reportContent = remember { mutableStateOf("") }

    // Função para obter localização atual
    fun getCurrentLocation(onLocationReceived: (latitude: Double, longitude: Double) -> Unit) {
        locationClient.lastLocation.addOnCompleteListener { task ->
            if (task.isSuccessful && task.result != null) {
                val location = task.result
                val latitude = location.latitude
                val longitude = location.longitude
                onLocationReceived(latitude, longitude)
            } else {
                // Trate erros aqui se necessário
            }
        }
    }

    @Composable
    fun SupportButton(onClick: () -> Unit) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Button(
                onClick = onClick,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BackgroundColorGlobal,
                ),
                modifier = Modifier
                    .align(Alignment.TopEnd)
            ) {
                Text(text = "Suporte", fontSize = 16.sp, color = textColorGlobal)
            }
        }
    }

    fun generateReport() {
        // Validação de campos
        if (observationField.value.isBlank() || selectedOption.value == "Selecione um cliente") {
            errorMessage.value = "Por favor, preencha todos os campos."
            successMessage.value = null
            return
        }

        val currentDateTime = LocalDateTime.now()
        val formattedDate = currentDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        val formattedTime = currentDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        // Obtém o nome do funcionário (usuário)
        val activity = context as? MainActivity
        val employeeName = activity?.intent?.getStringExtra("USERNAME") ?: "Nome do Funcionário"

        getCurrentLocation { latitude, longitude ->
            val report = Report(
                clientName = selectedOption.value,
                observation = observationField.value,
                date = formattedDate,
                time = formattedTime,
                latitude = latitude,
                longitude = longitude
            )

            // Exibir no AlertDialog
            reportContent.value = """
            Nome do Funcionário: $employeeName
            Nome do Cliente: ${report.clientName}
            Detalhes do serviço: ${report.observation}
            Data: ${report.date}
            Hora: ${report.time}
            Latitude: ${report.latitude}
            Longitude: ${report.longitude}
            """.trimIndent()

            showDialog.value = true

            errorMessage.value = null
            successMessage.value = "Relatório enviado com sucesso!"
            observationField.value = ""
            selectedOption.value = "Selecione um cliente" // Resetar seleção
        }
    }

    // Timeout para mensagens de sucesso e erro
    LaunchedEffect(successMessage.value, errorMessage.value) {
        if (successMessage.value != null) {
            delay(5000) // 5 segundos
            successMessage.value = null
        }
        if (errorMessage.value != null) {
            delay(5000) // 5 segundos
            errorMessage.value = null
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Greeting()
                Spacer(modifier = Modifier.height(16.dp))

                // Componente para seleção de cliente
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorFieldGlobal, shape = RoundedCornerShape(8.dp))
                        .height(if (isDropdownExpanded.value) 200.dp else 56.dp)
                        .clickable {
                            isDropdownExpanded.value = !isDropdownExpanded.value
                        }
                ) {
                    Text(
                        text = selectedOption.value,
                        modifier = Modifier.padding(16.dp),
                        color = Color.White,
                        fontSize = 14.sp
                    )

                    if (isDropdownExpanded.value) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(colorFieldGlobal, shape = RoundedCornerShape(8.dp))
                                .height(200.dp)
                        ) {
                            LazyColumn {
                                items(options) { option ->
                                    Text(
                                        text = option,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                selectedOption.value = option
                                                isDropdownExpanded.value = false
                                            }
                                            .padding(16.dp)
                                            .background(colorFieldGlobal),
                                        color = textColorGlobal
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = observationField.value,
                    onValueChange = { newValue ->
                        if (newValue.length <= 360) {
                            observationField.value = newValue
                        }
                    },
                    label = { Text("Observação:", color = textColorGlobal, fontSize = 14.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorFieldGlobal, shape = RoundedCornerShape(8.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = textColorGlobal,
                        unfocusedTextColor = textColorGlobal,
                        focusedContainerColor = colorFieldGlobal,
                        unfocusedContainerColor = colorFieldGlobal,
                        focusedIndicatorColor = borderColorGlobal,
                        unfocusedIndicatorColor = borderColorGlobal,
                        focusedLabelColor = labelColorGlobal,
                        unfocusedLabelColor = labelColorGlobal,
                        cursorColor = cursorColorGlobal
                    ),
                    shape = RoundedCornerShape(8.dp),
                    minLines = 5,
                    maxLines = 10
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (errorMessage.value != null) {
                    Text(
                        text = errorMessage.value!!,
                        color = Color.Red,
                        fontSize = 12.sp,
                    )
                }

                if (successMessage.value != null) {
                    Text(
                        text = successMessage.value!!,
                        color = Color.Green,
                        fontSize = 12.sp,
                    )
                }

                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        generateReport()
                    },
                    interactionSource = interactionSource,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPressed) colorButtonHoverGlobal else colorButtonGlobal,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .height(50.dp)
                        .width(200.dp)
                ) {
                    Text(text = "Enviar relatório", fontSize = 18.sp, color = textColorGlobal)
                }
            }

            AppFooter()
        }

        SupportButton(onClick = {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:vallissuporte@gmail.com")
                putExtra(Intent.EXTRA_SUBJECT, "Suporte necessário")
            }
            context.startActivity(emailIntent)
        })

        // Exibe o AlertDialog se showDialog for verdadeiro
        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = { Text("Relatório Gerado") },
                text = { Text(reportContent.value) },
                confirmButton = {
                    Button(onClick = { showDialog.value = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainContentPreview() {
    MainContent()
}

@Composable
fun Greeting() {
    // Recebe o contexto e o Intent
    val context = LocalContext.current
    val activity = context as? MainActivity
    val userName = activity?.intent?.getStringExtra("USERNAME") ?: "Nome do Usuário" // Pega o nome do usuário do Intent

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = "Avatar",
                modifier = Modifier.size(64.dp)
            )
            Text(userName, color = textColorGlobal, fontSize = 16.sp) // Exibe o nome do usuário

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
