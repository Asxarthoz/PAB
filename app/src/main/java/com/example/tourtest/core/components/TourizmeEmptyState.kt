package com.example.tourtest.core.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tourtest.ui.theme.MontserratFontFamily
import com.example.tourtest.ui.theme.TourizmeTextPrimary

@Composable
public fun TourizmeEmptyState(
    message: String,
    subMessage: String? = null,
    imageVector : ImageVector = Icons.Default.Search,
    actionButton: @Composable (() -> Unit)? = null
) {
    Box(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Text(
                text = message,
                fontSize = 16.sp,
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.Medium,
                color = TourizmeTextPrimary
            )
            if(subMessage != null) {
                Text(
                    text = subMessage,
                    fontSize = 14.sp,
                    fontFamily = MontserratFontFamily,
                    fontWeight = FontWeight.Normal,
                    color = TourizmeTextPrimary,
                    textAlign = TextAlign.Center
                )
            }

            if (actionButton != null) {
                Spacer(modifier = Modifier.height(8.dp))
                actionButton()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TourizmeEmptyStatePreview() {
    MaterialTheme {
        TourizmeEmptyState(
            message = "Data Kosong",
            subMessage = "Tidak ada data yang ditemukan"
        )
    }
}