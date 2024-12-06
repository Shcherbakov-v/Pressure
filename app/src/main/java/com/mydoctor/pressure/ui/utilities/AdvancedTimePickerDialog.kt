package com.mydoctor.pressure.ui.utilities

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mydoctor.pressure.R
import com.mydoctor.pressure.ui.theme.PressureTheme
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar

/**
 * Shows a picker that allows the user to select time. Subscribe to updates through
 * [TimePickerState]
 *
 * @param onDismiss - function that is run when a dialog is canceled
 * @param onConfirm - function that is run when the selection is confirmed
 * @param currentTime - the time that will be displayed first on the dialog
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedTimePickerDialog(
    title: String = stringResource(R.string.select_time),
    onDismiss: () -> Unit,
    onConfirm: (TimePickerState) -> Unit,
    currentTime: LocalDateTime,
    showDialStart: Boolean = true,
) {
    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.hour,
        initialMinute = currentTime.minute,
        is24Hour = true,
    )
    var showDial by remember { mutableStateOf(showDialStart) }
    val toggleIcon = if (showDial) {
        ImageVector.vectorResource(R.drawable.icon_keyboard)
    } else {
        Icons.Filled.Edit
    }

    SelectTimeDialog(
        timePickerState = timePickerState,
        title = title,
        onDismiss = onDismiss,
        onConfirm = onConfirm,
        toggle = {
            IconButton(onClick = { showDial = !showDial }) {
                Icon(
                    imageVector = toggleIcon,
                    contentDescription = stringResource(R.string.time_picker_type_toggle_description),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectTimeDialog(
    timePickerState: TimePickerState,
    title: String,
    onDismiss: () -> Unit,
    onConfirm: (TimePickerState) -> Unit,
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
                    TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
                    TextButton(onClick = { onConfirm(timePickerState) }) { Text(stringResource(R.string.ok)) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun AdvancedTimePickerDialogPreview() {
    PressureTheme {
        AdvancedTimePickerDialog(
            currentTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(Calendar.getInstance().timeInMillis),
                ZoneId.systemDefault()
            ),
            onDismiss = { },
            onConfirm = { },
        )
    }
}