package com.example.tourtest.core.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
public fun TourizmeDeleteDialog(
    show: Boolean,
    title: String = "Konfirmasi hapus",
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
){
    if(show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = title) },
            text = { Text(text = message) },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text(text = "Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(text =  "Batal")
                }
            }
        )
    }
}