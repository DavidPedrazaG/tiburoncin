package uni.agustiniana.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import uni.agustiniana.ui.navigation.RouteScreen
import uni.agustiniana.ui.screens.BluetoothDeviceScreen
import uni.agustiniana.ui.screens.DemoScreen

@Composable
fun Navigation(

){
    val navController = rememberNavController()

    var startDestination: RouteScreen = RouteScreen.Home



    //Navhost es una funcion que importamos del compose, esta pide como parametros el controlador y el donde esta y hacia adonde va a ir
    NavHost(
        navController = navController, //Controlador
        startDestination = startDestination //Destino inicial
    ){
        //Se crea un composable donde entre los <> se indica cual es la interfaz inicial, en este caso routescreen.login
        composable<RouteScreen.Home>{
            //Se llama el nombre de el archivo inicial, en este caso LoginScreen
            BluetoothDeviceScreen(
                onDeviceSelected = { device ->
                    // Handle device selection
                },
                navigateToDemo = {
                    navController.navigate(RouteScreen.Demo)
                }
            )
        }
        //OJO es necesario crear el composable de la interfaz a la que queremos ir, sin esto no nos llevara a ningun lado
        composable<RouteScreen.Demo>{
            DemoScreen()
        }
    }
}