package com.vitaliykharchenko.intouch.ui.main

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val Red200 = Color(0xfff297a2)
private val Red300 = Color(0xffea6d7e)
private val Red700 = Color(0xffdd0d3c)
private val Red800 = Color(0xffd00036)
private val Red900 = Color(0xffc20029)

private val DarkThemeColors = darkColors(
    primary = Red300,
    primaryVariant = Red700,
    onPrimary = Color.Black,
    secondary = Red300,
    onSecondary = Color.White,
    error = Red200
)

private val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(4.dp),
    large = RoundedCornerShape(8.dp)
)

@Composable
fun DarkTheme(
    content: @Composable() () -> Unit
) {
    MaterialTheme(
        colors = DarkThemeColors,
        shapes = Shapes,
        content = content
    )
}