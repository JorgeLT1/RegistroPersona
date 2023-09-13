package com.example.registros

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.registros.domain.ClienteDb
import com.example.registros.data.local.entities.Cliente
import com.example.registros.ui.theme.RegistrosTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    lateinit var ClienteDB: ClienteDb
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RegistrosTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PantallaCliente()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun PantallaCliente(viewModel: ClienteViewModel = hiltViewModel()) {
    val clientes by viewModel.clientes.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        viewModel.isMessageShownFlow.collectLatest {
            if (it) {
                snackbarHostState.showSnackbar(
                    message = "Cliente guardado",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "Personas") },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = "personas detalles", style =
                    MaterialTheme.typography.titleMedium
                )
                OutlinedTextField(
                    value = viewModel.Nombre,
                    onValueChange = { viewModel.Nombre = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Nombre") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = viewModel.Apellido,
                    onValueChange = { viewModel.Apellido = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Apellido") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = viewModel.Edad,
                    onValueChange = { viewModel.Edad = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Edad") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = viewModel.Numero,
                    onValueChange = { viewModel.Numero = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Numero") },
                    singleLine = true
                )
                val keyboardController =
                    LocalSoftwareKeyboardController.current
                OutlinedButton(onClick = {
                    keyboardController?.hide()
                    if (viewModel.validar()) {
                        viewModel.saveCliente()
                        viewModel.setMessageShown()
                    }
                })
                {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Guardar"
                    )
                    Text(text = "Guardar")
                }
            }
            Text(
                text = "Lista de personas", style =
                MaterialTheme.typography.titleMedium
            )
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(clientes) { Cliente ->
                    Text(text = Cliente.nombre)
                    Text(text = Cliente.apellido)
                    Text(text = Cliente.edad)
                    Text(text = Cliente.numero)

                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RegistrosTheme {
        PantallaCliente()
    }
}

@HiltViewModel
class ClienteViewModel @Inject constructor(
    private val clienteDb: ClienteDb,
) : ViewModel() {
    var Nombre by mutableStateOf("")
    var Apellido by mutableStateOf("")
    var Edad by mutableStateOf("")
    var Numero by mutableStateOf("")

    private val _isMessageShown = MutableSharedFlow<Boolean>()
    val isMessageShownFlow = _isMessageShown.asSharedFlow()
    fun setMessageShown() {
        viewModelScope.launch {
            _isMessageShown.emit(true)
        }
    }

    val clientes: StateFlow<List<Cliente>> =
        clienteDb.clienteDao().getAll().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun saveCliente() {
        viewModelScope.launch {
            val cliente = Cliente(

                nombre = Nombre,
                apellido = Apellido,
                edad = Edad,
                numero = Numero
            )
            clienteDb.clienteDao().save(cliente)
            limpiar()
        }
    }

    fun validar(): Boolean {
        return !(Nombre == "" || Apellido == "" || Edad == "" || Numero == "")
    }

    private fun limpiar() {
        Nombre = ""
        Apellido = ""
        Edad = ""
        Numero = ""
    }
}

