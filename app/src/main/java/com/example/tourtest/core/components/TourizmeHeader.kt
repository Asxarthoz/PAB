package com.example.tourtest.core.components


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tourtest.ui.theme.MontserratFontFamily
import com.example.tourtest.ui.theme.TourizmeBlueMain

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TourizmeSimpleHeaderContent(
    title: String,
    searchQuery: String,
    isSearchActive: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onSearchActiveChange: (Boolean) -> Unit,
    onNotificationClick: () -> Unit
) {
    TopAppBar(
        modifier = Modifier.padding(bottom = 6.dp),
        title = {
            if (isSearchActive) {
                Surface(
                    modifier = Modifier.fillMaxWidth().height(44.dp).padding(end = 8.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHighest
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        BasicTextField(
                            value = searchQuery,
                            onValueChange =  onSearchQueryChange,
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                            decorationBox = { innerTextField ->

                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    if (searchQuery.isEmpty()) {
                                        Text(
                                            text = "Cari di $title...",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        )
                    }
                }
            } else {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontFamily = MontserratFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        },
        navigationIcon = {
            if (isSearchActive) {
                IconButton(onClick = {
                    onSearchActiveChange(false)
                    onSearchQueryChange("")
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Tutup", tint = Color.White )
                }
            }
        },
        actions = {
            if (!isSearchActive) {
                IconButton(onClick = onNotificationClick) {
                    Icon(Icons.Default.Notifications, contentDescription = "Notifikasi", tint = Color.White)
                }

                IconButton(onClick = {
                    onSearchActiveChange(true)
                }) {
                    Icon(Icons.Default.Search, contentDescription = "Cari", tint = Color.White)
                }
            } else {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Hapus")
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
//            containerColor = MaterialTheme.colorScheme.primaryContainer
            containerColor = TourizmeBlueMain
        ),
        windowInsets = WindowInsets(0.dp)

    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun TourizmeSimpleHeader(
    title: String,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onNotificationClick: () -> Unit
) {
    var isSearchActive by remember { mutableStateOf(false) }

    TourizmeSimpleHeaderContent(
        title = title,
        searchQuery = searchQuery,
        isSearchActive = isSearchActive,
        onSearchQueryChange = onSearchQueryChange,
        onSearchActiveChange = { isSearchActive = it },
        onNotificationClick = onNotificationClick,
    )
}

@Preview(name = "Normal Mode", showBackground = true)
@Composable
fun TourizmeSimpleHeaderNormalPreview() {
    MaterialTheme {
        TourizmeSimpleHeaderContent(
            title = "Destinasi",
            searchQuery = "",
            isSearchActive = true,
            onSearchQueryChange = {},
            onSearchActiveChange = {},
            onNotificationClick = {}
        )
    }
}