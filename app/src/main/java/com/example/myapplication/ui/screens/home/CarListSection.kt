package com.example.myapplication.ui.screens.home

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CarListSection1(
    cars: List<com.example.myapplication.data.model.Car>,
    onCarClick: (String) -> Unit = {},
    onBookNowClick: (String) -> Unit = { carId -> onCarClick(carId) }
) {
    cars.forEach { car ->
        CarItem(
            car = car,
            onClick = { onCarClick(car.id.toString()) },
            onBookNowClick = { onBookNowClick(car.id.toString()) }
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}
