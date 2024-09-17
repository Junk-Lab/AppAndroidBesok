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
import com.google.android.gms.tasks.OnCompleteListener
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import android.util.Log
import android.content.Intent
import android.net.Uri
import android.content.ActivityNotFoundException
import android.widget.Toast
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable

data class Report(
    val clientName: String,
    val technicianName: String,
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
        requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }
}

@Composable
fun MainContent(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val locationClient = LocationServices.getFusedLocationProviderClient(context)

    val clientNameField = remember { mutableStateOf("") }
    val technicianNameField = remember { mutableStateOf("") }
    val observationField = remember { mutableStateOf("") }

    val errorMessage = remember { mutableStateOf<String?>(null) }
    val successMessage = remember { mutableStateOf<String?>(null) }

    fun getCurrentLocation(onLocationReceived: (latitude: Double, longitude: Double) -> Unit) {
        locationClient.lastLocation.addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful && task.result != null) {
                val location = task.result
                val latitude = location.latitude
                val longitude = location.longitude
                onLocationReceived(latitude, longitude)
            } else {
                // Trate erros aqui se necessário
            }
        })
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
                colors = ButtonDefaults.buttonColors(containerColor = BackgroundColorGlobal, contentColor = androidx.compose.ui.graphics.Color.White),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Text(text = "Suporte")
            }
        }
    }

    @Composable
    fun AppFooter() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(BackgroundColorGlobal),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Copyright © 2024 Vallis. All Rights Reserved",
                color = Color.White,
                fontSize = 10.sp
            )
        }
    }

    fun generateReport() {
        if (clientNameField.value.isBlank() || technicianNameField.value.isBlank() || observationField.value.isBlank()) {
            errorMessage.value = "Por favor, preencha todos os campos."
            successMessage.value = null
            return
        }

        val currentDateTime = LocalDateTime.now()
        val formattedDate = currentDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        val formattedTime = currentDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        getCurrentLocation { latitude, longitude ->
            val report = Report(
                clientName = clientNameField.value,
                technicianName = technicianNameField.value,
                observation = observationField.value,
                date = formattedDate,
                time = formattedTime,
                latitude = latitude,
                longitude = longitude
            )

            Log.d("GenerateReport", "--------------------------------------")
            Log.d("GenerateReport", "Relatório Gerado:")
            Log.d("GenerateReport", "Nome do Cliente: ${report.clientName}")
            Log.d("GenerateReport", "Nome do Técnico: ${report.technicianName}")
            Log.d("GenerateReport", "Detalhes do serviço: ${report.observation}")
            Log.d("GenerateReport", "Data: ${report.date}")
            Log.d("GenerateReport", "Hora: ${report.time}")
            Log.d("GenerateReport", "Latitude: ${report.latitude}")
            Log.d("GenerateReport", "Longitude: ${report.longitude}")
            Log.d("GenerateReport", "--------------------------------------")

            errorMessage.value = null
            successMessage.value = "Relatório enviado com sucesso!"
            clientNameField.value = ""
            technicianNameField.value = ""
            observationField.value = ""
        }
    }

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

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Greeting()
                Spacer(modifier = Modifier.height(14.dp))

                TextField(
                    value = clientNameField.value,
                    onValueChange = { newValue ->
                        if (newValue.length <= 100) {
                            clientNameField.value = newValue
                        }
                    },
                    label = { Text("Nome do Cliente") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, shape = RoundedCornerShape(8.dp)),
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
                    shape = RoundedCornerShape(7.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = technicianNameField.value,
                    onValueChange = { newValue ->
                        if (newValue.length <= 100) {
                            technicianNameField.value = newValue
                        }
                    },
                    label = { Text("Nome do Técnico") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, shape = RoundedCornerShape(8.dp)),
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
                    shape = RoundedCornerShape(7.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = observationField.value,
                    onValueChange = { newValue ->
                        if (newValue.length <= 360) {
                            observationField.value = newValue
                        }
                    },
                    label = { Text("Detalhes do Serviço:") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, shape = RoundedCornerShape(8.dp)),
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
                    shape = RoundedCornerShape(7.dp),
                    maxLines = 10,
                    minLines = 5
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (errorMessage.value != null) {
                    Text(
                        text = errorMessage.value!!,
                        color = Color.Red,
                        modifier = Modifier.padding(bottom = 16.dp),
                        fontSize = 12.sp,
                    )
                }

                if (successMessage.value != null) {
                    Text(
                        text = successMessage.value!!,
                        color = Color.Green,
                        modifier = Modifier.padding(bottom = 16.dp),
                        fontSize = 12.sp,
                    )
                }

                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()

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
                        .width(250.dp)
                ) {
                    Text(text = "Enviar relatório")
                }

            }

            AppFooter()
        }

        SupportButton(onClick = {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:vallissuporte@gmail.com")
            }
            try {
                Log.d("SupportButton", "Starting email intent")

                context.startActivity(Intent.createChooser(emailIntent, "Suporte"))
            } catch (e: ActivityNotFoundException) {
                Log.e("SupportButton", "No email app found", e)
                // Exibe uma mensagem para o usuário, pois não há aplicativos de e-mail disponíveis
                Toast.makeText(context, "Nenhum aplicativo de e-mail encontrado", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

@Preview(showBackground = true)
@Composable
fun MainContentPreview() {
    MainContent()
}

@Composable
fun Greeting() {
    // Adicione o conteúdo do Greeting aqui
}

