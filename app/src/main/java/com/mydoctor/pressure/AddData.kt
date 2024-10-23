package com.mydoctor.pressure

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mydoctor.pressure.ui.theme.PressureTheme
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar

class AddData : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PressureTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AddDataPage(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDataPage(modifier: Modifier = Modifier) {

    //var showDialog by remember { mutableStateOf(false) }


    Box(
        modifier = Modifier.fillMaxHeight()
    ) {
        Column {
            Header(modifier)
            PressureEndPulse()
            DataAndTime()
            Note()
        }
        Button(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .align(alignment = Alignment.BottomCenter)
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 24.dp,
                ),
            colors = ButtonDefaults.buttonColors(Color(0xFF0088FF))
        ) {
            Text("Сохранить")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    PressureTheme {
        AddDataPage()
    }
}

@Composable
fun Header(modifier: Modifier) {
    val context = LocalContext.current
    Box(
        Modifier
            .fillMaxWidth()
            .padding(
                top = 22.dp,
            )
            .padding(
                top = 44.dp,
            ),

        //verticalAlignment = Alignment.CenterVertically,
        //horizontalArrangement = Arrangement.SpaceBetween
    ) {
        SmallFloatingActionButton(
            {
                (context as? Activity)?.finish()
            },
            modifier = Modifier
                .align(alignment = CenterStart)
                .padding(start = 16.dp),
            shape = RoundedCornerShape(10.dp),
            containerColor = Color.White,
        ) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.button_back),
                null,
            )
        }
        Text(
            text = "Добавить данные",
            modifier = Modifier
                .align(alignment = Alignment.Center),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        )
    }
}

@Composable
fun PressureEndPulse() {
    var textSystolic by remember { mutableStateOf("") }
    var textDiastolic by remember { mutableStateOf("") }
    var textPulse by remember { mutableStateOf("") }

    val pattern = remember { Regex("^\\d+\$") }

    Row(
        Modifier
            .fillMaxWidth()
            .padding(
                top = 16.dp,
                start = 16.dp,
                end = 16.dp,
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        //verticalAlignment = Alignment.CenterVertically,
        //horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            //modifier = Modifier.align(alignment = Center)
            //horizontalAlignment = Alignment.SpaceBetween
            //Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Кровяное давление",
                fontSize = 16.sp,
            )
            Row {
                Column(
                    modifier = Modifier.width(width = 104.dp)
                ) {
                    Text(
                        text = "Систолическое",
                        fontSize = 12.sp,
                        color = Color(0x801C1C24),
                    )
                    OutlinedTextField(
                        placeholder = { Text("120") },
                        value = textSystolic,
                        onValueChange = {
                            if (it.isEmpty() || it.matches(pattern)) {
                                textSystolic = it
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedPlaceholderColor = Color(0xFF83A0B9),
                            focusedPlaceholderColor = Color(0xFF83A0B9),
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                }
                Column(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .width(width = 104.dp)
                ) {
                    Text(
                        text = "Диастолическое",
                        fontSize = 12.sp,
                        color = Color(0x801C1C24),
                    )
                    OutlinedTextField(
                        placeholder = { Text("90") },
                        value = textDiastolic,
                        onValueChange = {
                            if (it.isEmpty() || it.matches(pattern)) {
                                textDiastolic = it
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedPlaceholderColor = Color(0xFF83A0B9),
                            focusedPlaceholderColor = Color(0xFF83A0B9),
                        ),
                    )
                }
            }
        }


        Column(
            modifier = Modifier.width(width = 104.dp)
        ) {
            Text(
                text = "Пульс",
                fontSize = 16.sp,
            )
            Text(
                text = "",
                fontSize = 12.sp,
                color = Color(0x801C1C24),
            )
            OutlinedTextField(
                placeholder = { Text("70") },
                value = textPulse,
                onValueChange = {
                    if (it.isEmpty() || it.matches(pattern)) {
                        textPulse = it
                    }
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedPlaceholderColor = Color(0xFF83A0B9),
                    focusedPlaceholderColor = Color(0xFF83A0B9),
                ),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataAndTime() {


    val currentTime = Calendar.getInstance()
    //DateTimeFormatter.ofPattern("MMMM dd, yyyy | hh:mma", Locale.getDefault()).format(formattedDate)

    val formatterDate = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    val formatterTime = DateTimeFormatter.ofPattern("HH:mm");

    val instant = Instant.ofEpochMilli(currentTime.timeInMillis)
    val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

    var startDate = formatterDate.format(date)
    val startTime = formatterTime.format(date)

    var textData by remember { mutableStateOf("") }
    var textTime by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val onDismiss: () -> Unit = { showDatePicker = false }
    val onDateSelected: (Long) -> Unit = { it ->
        val instantLocal = Instant.ofEpochMilli(it)
        val dateLocal = LocalDateTime.ofInstant(instantLocal, ZoneId.systemDefault())
        textData = formatterDate.format(dateLocal)

        Log.d("PressureTag", it.toString())
    }

    val focusManager = LocalFocusManager.current

    Row(
        Modifier
            .fillMaxWidth()
            .padding(
                top = 24.dp,
                start = 16.dp,
                end = 16.dp,
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier
                .padding(end = 12.dp)
                .weight(1f)
        ) {
            Text(
                text = "Дата измерений",
                fontSize = 16.sp,
            )
            OutlinedTextField(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .onFocusChanged {
                        if (it.isFocused) {
                            showDatePicker = true
                            focusManager.clearFocus(true)
                        }
                    },
                placeholder = { Text(if (textData == "") startDate else "") },
                value = textData,
                onValueChange = { textData = it },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedPlaceholderColor = Color(0xFF83A0B9),
                    focusedPlaceholderColor = Color(0xFF83A0B9),
                ),
            )
            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = onDismiss,
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let { onDateSelected(it) }
                            onDismiss()
                        }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = onDismiss) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }
        }
        Column(
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1f)
        ) {
            Text(
                text = "Время измерений",
                fontSize = 16.sp,
            )
            OutlinedTextField(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .onFocusChanged {
                        if (it.isFocused) {
                            showTimePicker = true
                            focusManager.clearFocus(true)
                        }
                    },
                placeholder = { Text(if (textTime == "") startTime else "") },
                value = textTime,
                onValueChange = { textTime = it },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedPlaceholderColor = Color(0xFF83A0B9),
                    focusedPlaceholderColor = Color(0xFF83A0B9),
                ),
            )
            //val calendar = Calendar.getInstance()

            val onConfirmTime: (TimePickerState) -> Unit =
                { it ->
                    showTimePicker = false

                    currentTime[Calendar.HOUR_OF_DAY] = it.hour
                    currentTime[Calendar.MINUTE] = it.minute
                    currentTime[Calendar.SECOND] = 0
                    currentTime[Calendar.MILLISECOND] = 0

                    val millis = currentTime.timeInMillis

                    val instantLocal = Instant.ofEpochMilli(millis)
                    val dateLocal = LocalDateTime.ofInstant(instantLocal, ZoneId.systemDefault())
                    textTime = formatterTime.format(dateLocal)

                    Log.d("PressureTag", millis.toString())
                }
            val onDismissTime: () -> Unit = { showTimePicker = false }

            val timePickerState = rememberTimePickerState(
                initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
                initialMinute = currentTime.get(Calendar.MINUTE),
                is24Hour = true,
            )

            if (showTimePicker) {
                var showDial by remember { mutableStateOf(true) }

                val toggleIcon = if (showDial) {
                    ImageVector.vectorResource(R.drawable.icon_keyboard)
                } else {
                    Icons.Filled.Edit
                }

                AdvancedTimePickerDialog(
                    onDismiss = { onDismissTime() },
                    onConfirm = { onConfirmTime(timePickerState) },
                    toggle = {
                        IconButton(onClick = { showDial = !showDial }) {
                            Icon(
                                imageVector = toggleIcon,
                                contentDescription = "Time picker type toggle",
                            )
                        }
                    },
                ) {
                    if (showDial) {
                        TimePicker(
                            state = timePickerState,
                        )
                    } else {
                        TimeInput(
                            state = timePickerState,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdvancedTimePickerDialog(
    title: String = "Select Time",
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    toggle: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier =
            Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    toggle()
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    TextButton(onClick = onConfirm) { Text("OK") }
                }
            }
        }
    }
}


@Composable
fun Note() {
    var textNote by remember { mutableStateOf("") }
    Text(
        modifier = Modifier.padding(
            top = 24.dp,
            start = 16.dp,
        ),
        text = "Заметка",
        fontSize = 16.sp,
    )
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 16.dp,
                start = 16.dp,
                end = 16.dp,
            ),
        placeholder = { Text(if (textNote == "") "Опиши свое самочуствие" else "") },
        value = textNote,
        onValueChange = { textNote = it },
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedPlaceholderColor = Color(0xFF83A0B9),
            focusedPlaceholderColor = Color(0xFF83A0B9),
        ),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}