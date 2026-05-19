import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.tourtest.feature.notification.dataaccess.NotificationReceiver
import com.example.tourtest.model.Destination
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun TourizmeDatePicker(
    destination: Destination?,
    onDismiss: () -> Unit,
    onDateSelected: (String, Long) -> Unit
) {
    val context = LocalContext.current
    if (destination == null) return

    val sheetState = rememberModalBottomSheetState()
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val calendar = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"))
                calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
                calendar.set(java.util.Calendar.MINUTE, 0)
                calendar.set(java.util.Calendar.SECOND, 0)
                calendar.set(java.util.Calendar.MILLISECOND, 0)
                val startOfToday = calendar.timeInMillis

                return utcTimeMillis >= startOfToday
            }
        }
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Pilih tanggal rencana",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            DatePicker(state = datePickerState, showModeToggle = false)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text(text = "Batal")
                }
                Button(onClick = {
                    val selectedDate = datePickerState.selectedDateMillis
                    if (selectedDate != null) {
                        val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(selectedDate)
                        )
                        onDateSelected(formattedDate, selectedDate)

                        scheduleNotification(
                            context = context,
                            targetTimeMillis = selectedDate,
                            destinationName = destination!!.name,
                            destinationId = destination!!.id
                        )
                    }
                    onDismiss()
                }) {
                    Text(text = "Pilih")
                }
            }
        }
    }
}

// buat notif
fun scheduleNotification(
    context: Context,
    targetTimeMillis: Long,
    destinationName: String,
    destinationId: String
) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val currentTime = System.currentTimeMillis()

    val ONE_HOUR = 60 * 60 * 100L
    val ONE_DAY = 24 * ONE_HOUR

    val reminders = listOf(
        Pair(7 * ONE_DAY, "H-7: Siapkan perlengkapanmu untuk ke $destinationName!"),
        Pair(3 * ONE_DAY, "H-3: Wah, sebentar lagi kamu berangkat ke $destinationName!"),
        Pair(1 * ONE_DAY, "H-1: Besok waktunya ke $destinationName! Jangan sampai ada yang tertinggal."),
        Pair(12 * ONE_HOUR, "12 Jam Lagi: Siap-siap, perjalananmu ke $destinationName akan segera dimulai!")
    )

    reminders.forEach {
            (minusMillis, message) ->
        val triggerTime = targetTimeMillis - minusMillis
        if (triggerTime > currentTime) {
            val intent = Intent(context, NotificationReceiver::class.java).apply {
                putExtra("NOTIFICATION_MESSAGE", message)
                putExtra("DESTINATION_ID", destinationId)
            }

            // kunique code = hashcode ID destinasi dan nilai minus waktunya
            val uniqueRequestCode = destinationId.hashCode() + minusMillis.hashCode()

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                uniqueRequestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }
}