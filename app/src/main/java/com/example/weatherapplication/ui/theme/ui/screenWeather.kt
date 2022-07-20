package com.example.weatherapplication.ui.theme.ui


import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.service.dto.FullWeather
import com.example.weatherapp.service.dto.currentWeather
import com.example.weatherapplication.R
import kotlin.math.roundToInt

@Composable
fun screenWeather (viewModel:WeatherviewModel) {

    val curremt:currentWeather? by viewModel.current.collectAsState()
    val forecast:FullWeather? by viewModel.forecast.collectAsState(null)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(top = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Card(
            modifier = Modifier
                .width(350.dp)
                .height(440.dp),
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = Color.White,
            elevation = 10.dp,
            shape = RoundedCornerShape(23.dp),

            ) {
            curremt?.let { 
                weatherView(current = it)
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(bottom = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Card(
                modifier = Modifier
                    .width(350.dp)
                    .height(200.dp),
                backgroundColor =MaterialTheme.colors.secondary,
                contentColor = Color.White,
                elevation = 10.dp,
                shape = RoundedCornerShape(23.dp)

            ) {
                forecast?.let {
                    currentDailyForecast(forecast = it)
                }

            }

        }

    }
}



@Composable
fun weatherView(current: currentWeather) {
    Box {
        Row(
            Modifier
                .padding(top = 28.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {

            Image(
                painter = painterResource(id = current.background()),
                contentDescription = "Background",
                modifier = Modifier.size(150.dp),
                alignment = Alignment.Center,
                contentScale = ContentScale.FillWidth,
            )
        }
        Column(
            Modifier
                .padding(top = 48.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = temperature(current.main.temp),
                textAlign = TextAlign.Center,
                color = Color.White,
                fontSize = 38.sp
            )
            Text(
                text = current.sys.country,
                textAlign = TextAlign.Center,
                color = Color.White,
                fontSize = 36.sp
            )
        }
        Row(
            Modifier
                .padding(bottom = 18.dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Text(text = "WindNow", fontSize = 20.sp, color = Color.White)
            Text(text = "Humidity", fontSize = 20.sp, color = Color.White)
            Text(text = "Precipitation", fontSize = 20.sp, color = Color.White)
        }
        Row(
            Modifier
                .padding(top = 18.dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Text(text = WindNow(current.wind.speed), fontSize = 20.sp, color = Color.White)
            Text(text = Humidity(current.main.humidity), fontSize = 20.sp, color = Color.White)
            Text(text = Precipitation(current.main.feelsLike), fontSize = 20.sp, color = Color.White)
        }

    }

}
@Composable
fun Precipitation(feelsLike:Double): String {
    return stringResource(id = R.string.precipitation, feelsLike.roundToInt())

}

@Composable
fun Humidity(humidity: Int): String {
    return stringResource(id = R.string.humidity, humidity)


}

@Composable
fun WindNow(speed: Double): String {
    return stringResource(id =R.string.windNow, speed.roundToInt())

}

@Composable
fun temperature(temp: Double): String {
    return stringResource(id = R.string.temperature_degrees, temp.roundToInt())

}

@DrawableRes
private fun currentWeather.background(): Int {
    val conditions = weather.first().main
    return when {
        conditions.contains("cloud", ignoreCase = true) -> R.drawable.day_partial_cloud
        conditions.contains("rain", ignoreCase = true) -> R.drawable.day_rain
        else -> R.drawable.day_clear
    }
}


@Composable
fun currentDailyForecast(forecast: FullWeather) {
    Box {
        Row(
            Modifier
                .padding(top = 28.dp)
                .size(329.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = "Night", fontSize = 18.sp, color = Color.White)
            Image(
                painter = painterResource(forecast.forecastIcon()),
                contentDescription = "Background",
                modifier = Modifier.size(24.dp),
                alignment = Alignment.Center,
                contentScale = ContentScale.FillWidth,
            )
        }
        Column(
            Modifier
                .padding(top = 18.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = Temperature(forecast.current.temp),
                textAlign = TextAlign.Center,
                color = Color.White,
                fontSize = 28.sp
            )
        }
    }

}

private fun FullWeather.forecastIcon(): Int {
    val conditions = current.weather.first().main
    return when {
        conditions.contains("cloud", ignoreCase = true) -> R.drawable.night_partial_cloud
        conditions.contains("rain", ignoreCase = true) ->R.drawable.night_rain
        else -> R.drawable.night_clear
    }
}


@Composable
fun Temperature(temp: Double): String {
    return stringResource(id =R.string.temperature_degrees, temp.roundToInt())

}


