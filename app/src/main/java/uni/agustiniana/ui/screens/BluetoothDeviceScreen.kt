package uni.agustiniana.ui.screens

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.launch

@Composable
fun BluetoothDeviceScreen(onDeviceSelected: (BluetoothDevice) -> Unit, navigateToDemo: () -> Unit) {
    val context = LocalContext.current
    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    val availableDevices = remember { mutableStateListOf<BluetoothDevice>() }
    val coroutineScope = rememberCoroutineScope()
    val isScanning = remember { mutableStateOf(false) }
    // Gestiona los permisos utilizando ActivityResultContracts para pedir permisos en tiempo de ejecución
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val allPermissionsGranted = permissions[Manifest.permission.BLUETOOTH_SCAN] == true &&
                    permissions[Manifest.permission.BLUETOOTH_CONNECT] == true &&
                    permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
            if (allPermissionsGranted) {
                scanDevices(bluetoothAdapter, availableDevices, context)
            }
        }
    )

    // Verifica si los permisos ya están otorgados, de lo contrario los solicita
    fun checkAndRequestPermissions() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            scanDevices(bluetoothAdapter, availableDevices, context) // Si ya tiene permisos, escanea los dispositivos
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
    }

    // Escaneo inicial al montar la pantalla
    LaunchedEffect(Unit) {
        checkAndRequestPermissions()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(30, 30, 30)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                coroutineScope.launch {
                    checkAndRequestPermissions() // Al hacer clic en "Scan", vuelve a comprobar los permisos
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Scan", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        availableDevices.forEach { device ->
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .clickable {
                        onDeviceSelected(device) // Conectar y cambiar de ventana
                    }
                    .background(Color(5, 22, 48))
                    .border(3.dp, Color.White)
                    .padding(8.dp)
            ) {
                Text(text = device.name ?: "Unknown Device", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = navigateToDemo) {
            Text(text = "Demo", color = Color.White)
        }
    }
}

fun scanDevices(bluetoothAdapter: BluetoothAdapter, availableDevices: MutableList<BluetoothDevice>, context: Context) {
    availableDevices.clear() // Limpia la lista antes de escanear

    if (bluetoothAdapter.isEnabled) {
        val bondedDevices = if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            bluetoothAdapter.bondedDevices
        } else {
            bluetoothAdapter.bondedDevices
        }
        bluetoothAdapter.bondedDevices
        bondedDevices.forEach { device ->
            if (device.name.contains("ESP32", ignoreCase = true)) {
                availableDevices.add(device)
            }
        }
    }
}
