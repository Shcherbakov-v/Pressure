package com.mydoctor.pressure.ui.utilities

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mydoctor.pressure.R
import com.mydoctor.pressure.ui.theme.PressureTheme
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * A dialog for displaying a [DatePicker]. Date pickers let people select a date.
 *
 * @param initialSelectedDateMillis - the date that will be displayed first on the dialog
 * @param onDismiss - function that is run when a dialog is canceled
 * @param onDateSelected - function that is run when the selection is confirmed. And transmits the selected date
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedDatePickerDialog(
    initialSelectedDateMillis: Long,
    onDismiss: () -> Unit,
    onDateSelected: (Long) -> Unit,
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDateMillis
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { onDateSelected(it) }
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Preview(showBackground = true)
@Composable
fun AdvancedDatePickerDialogPreview() {
    PressureTheme {
        AdvancedDatePickerDialog(
            initialSelectedDateMillis =
            ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault()).toInstant()
                .toEpochMilli(),
            onDismiss = { },
            onDateSelected = { },
        )
    }
}