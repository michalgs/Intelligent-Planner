package com.example.io_project.ui.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.atan2

@Composable
fun PieChart(
    modifier: Modifier = Modifier,
    radius:Float = 380f,
    innerRadius:Float = 220f,
    transparentWidth:Float = 70f,
    input:List<PieChartInput>,
    centerText:String = "Aktywności"
) {
    val filteredInput = input.filter { it.value > 0 }

    var circleCenter by remember {
        mutableStateOf(Offset.Zero)
    }

    var inputList by remember {
        mutableStateOf(filteredInput)
    }
    var isCenterTapped by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ){
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(true){
                    detectTapGestures(
                        onTap = { offset->
                            val tapAngleInDegrees = (-atan2(
                                x = circleCenter.y - offset.y,
                                y = circleCenter.x - offset.x
                            ) * (180f / PI).toFloat() - 90f).mod(360f)
                            val centerClicked = if(tapAngleInDegrees<90) {
                                offset.x<circleCenter.x+innerRadius && offset.y<circleCenter.y+innerRadius
                            }else if(tapAngleInDegrees<180){
                                offset.x>circleCenter.x-innerRadius && offset.y<circleCenter.y+innerRadius
                            }else if(tapAngleInDegrees<270){
                                offset.x>circleCenter.x-innerRadius && offset.y>circleCenter.y-innerRadius
                            }else{
                                offset.x<circleCenter.x+innerRadius && offset.y>circleCenter.y-innerRadius
                            }

                            if(centerClicked){
                                inputList = inputList.map {
                                    it.copy(isTapped = !isCenterTapped)
                                }
                                isCenterTapped = !isCenterTapped
                            }else{
                                var anglePerValue = 360f

                                if(filteredInput.isNotEmpty()) {
                                    anglePerValue = 360f/filteredInput.sumOf { it.value }
                                }
                                var currAngle = 0f
                                inputList.forEach { pieChartInput ->

                                    currAngle += pieChartInput.value * anglePerValue
                                    if(tapAngleInDegrees<currAngle){
                                        val description = pieChartInput.description
                                        inputList = inputList.map {
                                            if(description == it.description){
                                                it.copy(isTapped = !it.isTapped)
                                            }else{
                                                it.copy(isTapped = false)
                                            }
                                        }
                                        return@detectTapGestures
                                    }
                                }
                            }
                        }
                    )
                }
        ){
            val width = size.width
            val height = size.height
            circleCenter = Offset(x= width/2f,y= height/2f)

            var totalValue = 0
            var anglePerValue = 360f

            if(filteredInput.isNotEmpty())
            {
                totalValue = filteredInput.sumOf {
                    it.value
                }
                anglePerValue = 360f/totalValue
            }

            var currentStartAngle = 0f

            if(inputList.isNotEmpty())
            {
                inputList.forEach { pieChartInput ->
                    val scale = if (pieChartInput.isTapped) 1.1f else 1.0f
                    val angleToDraw = pieChartInput.value * anglePerValue
                    scale(scale) {
                        drawArc(
                            color = pieChartInput.color,
                            startAngle = currentStartAngle,
                            sweepAngle = angleToDraw,
                            useCenter = true,
                            size = Size(
                                width = radius * 2f,
                                height = radius * 2f
                            ),
                            topLeft = Offset(
                                (width - radius * 2f) / 2f,
                                (height - radius * 2f) / 2f
                            )
                        )
                        currentStartAngle += angleToDraw
                    }
                    var rotateAngle = currentStartAngle - angleToDraw / 2f - 90f
                    var factor = 1f
                    if (rotateAngle > 90f) {
                        rotateAngle = (rotateAngle + 180).mod(360f)
                        factor = -0.92f
                    }

                    val percentage = (pieChartInput.value / totalValue.toFloat() * 100).toInt()

                    drawContext.canvas.nativeCanvas.apply {
                        if (percentage > 3) {
                            rotate(rotateAngle) {
                                drawText(
                                    "$percentage %",
                                    circleCenter.x,
                                    circleCenter.y + (radius - (radius - innerRadius) / 2f) * factor,
                                    Paint().apply {
                                        textSize = 18.sp.toPx()
                                        textAlign = Paint.Align.CENTER
                                        color = Color.White.toArgb()
                                    }
                                )
                            }
                        }
                    }
                    if (pieChartInput.isTapped) {
                        val tabRotation = currentStartAngle - angleToDraw - 90f
                        rotate(tabRotation) {
                            drawRoundRect(
                                topLeft = circleCenter,
                                size = Size(12f, radius * 1.2f),
                                color = Color.White,
                                cornerRadius = CornerRadius(15f, 15f)
                            )
                        }
                        rotate(tabRotation + angleToDraw) {
                            drawRoundRect(
                                topLeft = circleCenter,
                                size = Size(12f, radius * 1.2f),
                                color = Color.White,
                                cornerRadius = CornerRadius(15f, 15f)
                            )
                        }
                        rotate(rotateAngle) {
                            drawContext.canvas.nativeCanvas.apply {
                                drawText(
                                    "${pieChartInput.description}: ${pieChartInput.value}",
                                    circleCenter.x,
                                    circleCenter.y + radius * 1.25f * factor,
                                    Paint().apply {
                                        textSize = 18.sp.toPx()
                                        textAlign = Paint.Align.CENTER
                                        color = Color.Black.toArgb()
                                        isFakeBoldText = true
                                    }
                                )
                            }
                        }
                    }
                }

                if(inputList.first().isTapped){
                    rotate(-90f){
                        drawRoundRect(
                            topLeft = circleCenter,
                            size = Size(12f,radius*1.2f),
                            color = Color.White,
                            cornerRadius = CornerRadius(15f,15f)
                        )
                    }
                }
            }

            drawContext.canvas.nativeCanvas.apply {
                drawCircle(
                    circleCenter.x,
                    circleCenter.y,
                    innerRadius,
                    Paint().apply {
                        color = Color.White.copy(alpha = 0.6f).toArgb()
                        setShadowLayer(10f,0f,0f, Color.LightGray.toArgb())
                    }
                )
            }

            drawCircle(
                color = Color.White.copy(0.2f),
                radius = innerRadius+transparentWidth/2f
            )

        }
        Text(
            centerText,
            modifier = Modifier
                .width(Dp(innerRadius))
                .padding(8.dp),
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )

    }
}

data class PieChartInput(
    val color:Color,
    var value:Int,
    val description:String,
    val isTapped:Boolean = false
)