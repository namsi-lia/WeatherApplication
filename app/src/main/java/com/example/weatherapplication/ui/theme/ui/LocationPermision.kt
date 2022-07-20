package com.example.weatherapplication.ui.theme.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LocationDetails(onContinueClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = rememberVectorPainter(image = Icons.Filled.LocationOn),
            contentDescription = stringResource(com.example.weatherapplication.R.string.location_permission_content_description),
            modifier = Modifier.size(68.dp),
        )
        Text(
            text = stringResource(com.example.weatherapplication.R.string.location_permission_description),
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            color = Color.White
        )
        Button(onClick = onContinueClick) {
            Text(text = stringResource(com.example.weatherapplication.R.string.location_permission_continue))
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun LocationNotAvailable(onContinueClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = rememberVectorPainter(image = Icons.Filled.LocationOn),
            contentDescription = stringResource(com.example.weatherapplication.R.string.location_permission_content_description),
            modifier = Modifier.size(68.dp),
        )
        Text(
            text = stringResource(com.example.weatherapplication.R.string.location_not_available),
            textAlign = TextAlign.Center, fontSize = 18.sp, color = Color.White
        )
        Button(onClick = onContinueClick) {
            Text(text = stringResource(com.example.weatherapplication.R.string.location_continue))

        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LocationDetails {}
}