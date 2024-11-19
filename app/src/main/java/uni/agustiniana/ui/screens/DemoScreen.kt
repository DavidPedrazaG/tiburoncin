package uni.agustiniana.ui.screens

import android.app.Activity
import android.content.pm.ActivityInfo
import android.net.Uri
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.media3.ui.PlayerView
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay
import uni.agustiniana.R
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DemoScreen() {
    val context = LocalContext.current
    val isMotorOn = remember { mutableStateOf(false) }
    val motorSpeed = remember { mutableStateOf(50) } // Velocidad al 50%
    val distance = remember { mutableStateOf(6) } // Simulando distancia del sensor
    val showAlert = remember { mutableStateOf(false) }
    val deviceName = remember { mutableStateOf("JuanPa ESP32") }
    val imageState = remember { mutableStateOf(70f) } // Posición inicial en x = 70dp
    val animatedOffset by animateFloatAsState( // Estado para la animación de regreso
        targetValue = imageState.value,
        animationSpec = tween(durationMillis = 300)
    )

    // Reemplaza la URI del archivo por la correcta en Android
    val videoUri = Uri.parse("android.resource://${context.packageName}/${R.raw.tiburoncin}")

    // Inicializar ExoPlayer
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ALL
            playWhenReady = true
        }
    }

    // Configuración del exoPlayer fuera del LaunchedEffect
    LaunchedEffect(Unit) {
        val mediaItem = MediaItem.fromUri(videoUri)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    SideEffect {
        (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(30, 30, 30)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .width(180.dp)
                        .padding(10.dp)
                        .height(40.dp)
                        .background(Color(5, 22, 48))
                        .border(3.dp, Color.White)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = "Distancia: ${distance.value} cm",
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.jt_back),
                        contentDescription = "Background Image",
                        modifier = Modifier.size(215.dp)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.jt_front),
                        contentDescription = "Overlay Image",
                        modifier = Modifier
                            .size(75.dp)
                            .offset(x = animatedOffset.dp, y = 70.dp) // Aplica el valor animado para la posición de arrastre y establece el desplazamiento inicial en y
                            .draggable(
                                orientation = Orientation.Horizontal,
                                state = rememberDraggableState { delta ->
                                    // Limita el movimiento a un máximo de 70.dp a cada lado
                                    val newValue = imageState.value + delta
                                    if (newValue in 0f..140f) { // Limitar a 70.dp en ambos lados
                                        imageState.value = newValue
                                    }
                                },
                                onDragStopped = {
                                    // Devuelve la imagen a la posición inicial usando animación
                                    imageState.value = 70f
                                }
                            )
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .width(240.dp)
                        .padding(10.dp)
                        .height(60.dp)
                        .background(Color(5, 22, 48))
                        .border(3.dp, Color.White)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = """  
    Dispositivo conectado:
    ${deviceName.value} BT
    """,
                        color = Color.White
                    )
                }
                Box(
                    modifier = Modifier
                        .width(250.dp)
                        .height(180.dp)
                ) {
                    VideoPlayer(exoPlayer)
                }
                if (isMotorOn.value) {
                    Image(
                        painter = painterResource(id = R.drawable.on),
                        contentDescription = "Motor Button",
                        modifier = Modifier
                            .size(75.dp)
                            .align(Alignment.CenterHorizontally)
                            .clickable {
                                isMotorOn.value = !isMotorOn.value
                            }
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.off),
                        contentDescription = "Motor Button",
                        modifier = Modifier
                            .size(75.dp)
                            .align(Alignment.CenterHorizontally)
                            .clickable {
                                isMotorOn.value = !isMotorOn.value
                            }
                    )
                }
                Box(
                    modifier = Modifier
                        .width(180.dp)
                        .padding(10.dp)
                        .height(25.dp)
                        .background(Color(5, 22, 48))
                        .border(3.dp, Color.White)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = "Motor: ${if (isMotorOn.value) "ON" else "OFF"}",
                        color = Color.White
                    )
                }
            }
            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .width(180.dp)
                        .padding(10.dp)
                        .height(40.dp)
                        .background(Color(5, 22, 48))
                        .border(3.dp, Color.White)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = "Velocidad: ${motorSpeed.value} cm/s",
                        color = Color.White
                    )
                }
                Box(
                    modifier = Modifier
                        .width(130.dp)
                        .padding(10.dp)
                        .height(250.dp)
                        .background(Color(5, 22, 48))
                        .border(3.dp, Color.White)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Slider(
                        value = motorSpeed.value.toFloat(),
                        onValueChange = { motorSpeed.value = it.toInt() },
                        valueRange = 0f..100f,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .rotate(270f),
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = Color(92, 140, 150),
                            inactiveTrackColor = Color.Gray
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun VideoPlayer(exoPlayer: ExoPlayer) {
    AndroidView(
        factory = { context ->
            PlayerView(context).apply {
                player = exoPlayer
                useController = false
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}
