package com.mydoctor.pressure

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.Alignment.Companion.TopStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.InspectableModifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mydoctor.pressure.ui.theme.PressureTheme
import com.mydoctor.pressure.ui.theme.SelectButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PressureTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainPage(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }


    @Composable
    fun MainPage(modifier: Modifier = Modifier) {
        var text by remember { mutableStateOf("Не нажата") }
        Column() {
            Header()
            PressureBlock()
            SegmentedControl(
                items = listOf("День", "Неделя", "Месяц"),
                onItemSelection = { selectedItemIndex ->

                }
            )
            Notes({})
            Targets({})
            MeasurementLog({})
            Button(
                modifier = Modifier
                    .padding(16.dp),
                onClick = { text = "Нажата" },
            ) {
                Text(text)
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        PressureTheme {
            MainPage()
        }
    }

    @Composable
    fun Header() {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(
                    top = 6.dp,
                )
                .padding(
                    top = 44.dp,
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.icon_logo),
                null
            )
            Text(
                text = "Мой доктор",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(
                        start = 13.dp,
                    )
            )
        }
    }

    @Composable
    fun PressureBlock() {
        val context = LocalContext.current
        Box(
            Modifier
                .fillMaxWidth()
                .padding(
                    top = 16.dp,
                ),
            //verticalAlignment = Alignment.CenterVertically,
            //horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.align(alignment = Center)
                //horizontalAlignment = Alignment.SpaceBetween
                //Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Давление",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
                Text(
                    text = "Июня 2024 г.",
                    fontSize = 14.sp,
                )
            }
            SmallFloatingActionButton(
                {
                    val intent = Intent(context, AddData::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier
                    //.size(32.dp,32.dp)
                    .align(alignment = CenterEnd)
                    .padding(end = 16.dp),
                shape = RoundedCornerShape(10.dp),
                containerColor = Color.White,
            ) {
                Image(
                    imageVector = ImageVector.vectorResource(R.drawable.button_add_black),
                    null,
                )
            }
        }
    }


    @Composable
    fun Notes(onClick: () -> Unit) {
        ElevatedCard(
            shape = RoundedCornerShape(24.dp),
            //shape = CardDefaults.shape,
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            ),
            modifier = Modifier
                .padding(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                ),
            //.padding(top = 20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
            )
        ) {
            Column(
                Modifier
                    //.shadow(3.dp)
                    .padding(
                        top = 8.dp,
                        bottom = 16.dp,
                        start = 16.dp,
                        end = 6.dp,
                    ),
                verticalArrangement = Arrangement.spacedBy(8.dp),

                ) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
/*                Icon(
                    painterResource(R.drawable.icon_notes), null,
                )*/
                    Row() {
                        Image(
                            imageVector = ImageVector.vectorResource(R.drawable.icon_notes),
                            null
                        )
                        Text(
                            text = "Заметки",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    /*                Button({},
                                    modifier = Modifier.size(width = 50.dp, height = 70.dp),
                                    content = {
                                        // Specify the icon using the icon parameter
                                        Image(imageVector = Icons.Filled.Add, contentDescription = null)
                                        //Spacer(modifier = Modifier.width(8.dp)) // Adjust spacing
                                    }
                                )*/
                    IconButton({}) {
                        Icon(
                            painterResource(R.drawable.button_add), null,
                            //modifier = Modifier.clickable {}
                        )
                    }
                }
                HorizontalDivider(
                    thickness = 1.dp,
                    modifier = Modifier
                        .padding(end = 10.dp),
                )
                Text(
                    text = "Опиши свое состояние",
                    fontSize = 14.sp,
                    color = Color(0xFF83A0B9),
                    modifier = Modifier
                        .padding(end = 10.dp),
                )
            }
        }
    }

    @Composable
    fun Targets(onClick: () -> Unit) {
        Card(
            Modifier
                .padding(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                )
                .padding(top = 10.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
            ),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            ),
        ) {
            Column(
                Modifier.padding(
                    top = 8.dp,
                    bottom = 16.dp,
                    start = 16.dp,
                    end = 6.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row() {
                        Image(
                            imageVector = ImageVector.vectorResource(R.drawable.icon_targets),
                            null
                        )
                        Text(
                            text = "Цели",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    IconButton({}) {
                        Image(
                            imageVector = ImageVector.vectorResource(R.drawable.button_add),
                            null
                        )
                    }
                }
                HorizontalDivider(
                    thickness = 1.dp,
                    modifier = Modifier
                        .padding(end = 10.dp),
                )
                Text(
                    text = "Поставьте перед собой цель",
                    fontSize = 14.sp,
                    color = Color(0xFF83A0B9),
                    modifier = Modifier
                        .padding(end = 10.dp),
                )
            }
        }
    }

    @Composable
    fun MeasurementLog(onClick: () -> Unit) {
        Card(
            Modifier
                .padding(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                )
                .padding(top = 10.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
            ),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            ),
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        end = 6.dp
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Image(
                        imageVector = ImageVector.vectorResource(R.drawable.icon_measurement_log),
                        null
                    )
                    Text(
                        text = "Журнал измерений",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                IconButton({}) {
                    Image(
                        imageVector = ImageVector.vectorResource(R.drawable.icon_next),
                        null
                    )
                }
            }
        }
    }

    @Composable
    fun SegmentedControl(
        modifier: Modifier = Modifier,
        items: List<String>,
        defaultSelectedItemIndex: Int = 0,
        cornerRadius: Dp = 24.dp,
        onItemSelection: (selectedItemIndex: Int) -> Unit
    ) {
        val selectedIndex = remember { mutableIntStateOf(defaultSelectedItemIndex) }
        val itemIndex = remember { mutableIntStateOf(defaultSelectedItemIndex) }

        Card(
            modifier = Modifier
                .padding(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                )
                .fillMaxWidth()
                .height(42.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            ),
            shape = RoundedCornerShape(cornerRadius)
        ) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .background(Color.White),
                horizontalArrangement = Arrangement.Center
            ) {
                items.forEachIndexed { index, item ->
                    itemIndex.intValue = index
                    Card(
                        modifier = modifier
                            .weight(1f)
                            .padding(
                                top = 8.dp,
                                bottom = 8.dp,
                                start = 16.dp,
                                end = 16.dp
                            ),
                        shape = RoundedCornerShape(cornerRadius),
                        onClick = {
                            selectedIndex.intValue = index
                            onItemSelection(selectedIndex.intValue)
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedIndex.intValue == index) {
                                SelectButton
                            } else {
                                Color.White
                            }
                        ),
                    ) {
                        Box(
                            modifier = modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item,
                                style = LocalTextStyle.current.copy(
                                    fontSize = 14.sp,
                                    fontWeight = if (selectedIndex.intValue == index)
                                        FontWeight.Bold
                                    else
                                        FontWeight.Normal,
                                ),
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }
    }
}