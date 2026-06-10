package com.example.tourtest.feature.detaildestination.presentation

import TourizmeDatePicker
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import coil.compose.SubcomposeAsyncImage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tourtest.core.components.ShimmerPlaceholder
import com.example.tourtest.core.components.TourizmeDeleteDialog
import com.example.tourtest.core.data.UserSession
import com.example.tourtest.feature.detaildestination.viewmodel.DetailViewModel
import com.example.tourtest.model.Destination
import com.example.tourtest.model.Review
import com.example.tourtest.ui.theme.InterFontFamily
import com.example.tourtest.ui.theme.MontserratFontFamily
import com.example.tourtest.ui.theme.TourizmeBgDark
import com.example.tourtest.ui.theme.TourizmeBlueMain
import com.example.tourtest.ui.theme.TourizmeTextPrimary
import kotlinx.coroutines.launch
import scheduleNotification

// ─────────────────────────────────────────────────────────────────────────────
// Composable utama konten layar detail destinasi
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DestinationDetailContent(
    destination: Destination?,
    isFavorite: Boolean,
    isItineraried: Boolean,
    isLoading: Boolean,
    scrollState: ScrollState,
    currentUserId: String,
    // Ulasan milik user aktif: null = belum pernah mengulas
    userReview: Review?,
    onBack: () -> Unit,
    onFavoriteClick: () -> Unit,
    onCalendarClick: () -> Unit,
    onOpenMaps: () -> Unit,
    onSubmitReview: (rating: Float, comment: String) -> Unit,
    onUpdateReview: (newRating: Float, newComment: String) -> Unit,
    onDeleteMyReview: () -> Unit
) {
    // State form baru (hanya dipakai saat userReview == null)
    var inputRating  by remember { mutableStateOf(5f) }
    var inputComment by remember { mutableStateOf("") }

    // State form edit (hanya dipakai saat userReview != null && isEditing)
    var isEditing       by remember { mutableStateOf(false) }
    var editRating      by remember { mutableStateOf(0f) }
    var editComment     by remember { mutableStateOf("") }

    // Reset edit state saat userReview berubah (misal setelah hapus/simpan)
    LaunchedEffect(userReview) {
        if (userReview == null) {
            isEditing = false
            editRating = 0f
            editComment = ""
            inputRating = 5f
            inputComment = ""
        }
    }

    // Tab: 0 = Tentang, 1 = Ulasan
    var selectedTab by remember { mutableIntStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Detail Destinasi",
                        fontFamily = MontserratFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TourizmeBlueMain,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                windowInsets = WindowInsets(0.dp)
            )
        },
        bottomBar = {
            if (destination != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp,
                    color = Color.White
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Total Estimasi",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                fontFamily = MontserratFontFamily,
                                fontWeight = FontWeight.Normal
                            )
                            Text(
                                "Rp ${destination.price}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TourizmeBlueMain,
                                fontFamily = InterFontFamily
                            )
                        }
                        Button(
                            onClick = onCalendarClick,
                            colors = ButtonDefaults.buttonColors(containerColor = TourizmeBlueMain),
                            shape = RoundedCornerShape(24.dp),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Tambah ke Itinerary",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontFamily = InterFontFamily,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = TourizmeBlueMain) }
            }

            destination == null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) { Text("Destinasi tidak ditemukan", fontFamily = MontserratFontFamily) }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(scrollState)
                ) {
                    SubcomposeAsyncImage(
                        model = destination.imageUrl,
                        contentDescription = destination.name,
                        modifier = Modifier.fillMaxWidth().height(220.dp),
                        contentScale = ContentScale.Crop,
                        loading = { ShimmerPlaceholder() },
                        error = {
                            Box(
                                modifier = Modifier.fillMaxSize().background(Color(0xFFF1F4FA)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Gray)
                            }
                        }
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = destination.name,
                                fontSize = 20.sp,
                                fontFamily = MontserratFontFamily,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = onFavoriteClick, modifier = Modifier.size(36.dp)) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = if (isFavorite) Color.Red else Color.Gray,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.clickable { onOpenMaps() }.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Lokasi",
                                    modifier = Modifier.size(16.dp),
                                    tint = TourizmeBlueMain
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = destination.location,
                                    color = Color.Gray,
                                    fontSize = 14.sp,
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = android.R.drawable.ic_menu_recent_history),
                                    contentDescription = "Jam Operasional",
                                    modifier = Modifier.size(15.dp),
                                    tint = Color.Gray
                                )
                                Text(
                                    text = "${destination.openTime} - ${destination.closeTime}",
                                    color = Color.Gray,
                                    fontSize = 14.sp,
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(color = Color(0xFFEBF1F5), shape = RoundedCornerShape(8.dp)) {
                                Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                                    Text("Mulai Dari", fontSize = 14.sp, color = TourizmeTextPrimary, fontFamily = InterFontFamily, fontWeight = FontWeight.Normal)
                                    Text("Rp ${destination.price} /orang", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TourizmeBlueMain, fontFamily = InterFontFamily)
                                }
                            }
                            Surface(color = Color(0xFF4EDEA3).copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp)) {
                                Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = String.format("%.1f", destination.averageRating),
                                        fontWeight = FontWeight.SemiBold,
                                        color = TourizmeBlueMain,
                                        fontSize = 14.sp,
                                        fontFamily = InterFontFamily
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    ) {
                        listOf("Tentang", "Ulasan").forEachIndexed { index, label ->
                            Column(
                                modifier = Modifier
                                    .clickable { selectedTab = index }
                                    .padding(end = if (index == 0) 24.dp else 0.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 14.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) TourizmeBlueMain else Color.Gray,
                                    fontFamily = InterFontFamily
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                if (selectedTab == index) {
                                    Box(modifier = Modifier.height(2.dp).width(56.dp).background(TourizmeBlueMain, RoundedCornerShape(1.dp)))
                                } else {
                                    Spacer(modifier = Modifier.height(2.dp))
                                }
                            }
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(bottom = 12.dp), color = Color.LightGray)

                    when (selectedTab) {
                        0 -> {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("Tentang Destinasi", fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = InterFontFamily, color = TourizmeBgDark)
                                Text(
                                    text = destination.description,
                                    textAlign = TextAlign.Justify,
                                    lineHeight = 22.sp,
                                    color = Color.DarkGray,
                                    fontSize = 14.sp,
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Normal
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }

                        1 -> {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Ulasan Pengunjung", fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = InterFontFamily, color = TourizmeBgDark)
                                    Text("Lihat Semua", fontSize = 4.sp, color = TourizmeBlueMain, fontWeight = FontWeight.SemiBold, fontFamily = MontserratFontFamily)
                                }

                                if (destination.reviews.isEmpty()) {
                                    Text(
                                        "Belum ada ulasan. Jadilah yang pertama memberikan review!",
                                        color = Color.Gray,
                                        fontSize = 14.sp,
                                        fontFamily = MontserratFontFamily,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                } else {
                                    destination.reviews.forEach { review ->
                                        val isOwner = review.userId == currentUserId
                                        ReviewCard(
                                            review = review,
                                            isOwner = isOwner,
                                            showActions = false
                                        )
                                    }
                                }

                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color.LightGray)

                                if (currentUserId.isNotBlank() && currentUserId != "GUEST") {
                                    if (userReview == null) {
                                        ReviewFormNew(
                                            inputRating = inputRating,
                                            inputComment = inputComment,
                                            onRatingChange = { inputRating = it },
                                            onCommentChange = { inputComment = it },
                                            onSubmit = {
                                                onSubmitReview(inputRating, inputComment)
                                                inputComment = ""
                                                inputRating = 5f
                                            }
                                        )
                                    } else if (!isEditing) {
                                        MyReviewDisplay(
                                            review = userReview,
                                            onEditClick = {
                                                editRating  = userReview.ratingGiven
                                                editComment = userReview.comment
                                                isEditing   = true
                                                coroutineScope.launch {
                                                    scrollState.animateScrollTo(scrollState.maxValue)
                                                }
                                            },
                                            onDeleteClick = onDeleteMyReview
                                        )
                                    } else {
                                        ReviewFormEdit(
                                            editRating  = editRating,
                                            editComment = editComment,
                                            onRatingChange  = { editRating = it },
                                            onCommentChange = { editComment = it },
                                            onSave = {
                                                onUpdateReview(editRating, editComment)
                                                isEditing = false
                                            },
                                            onCancel = {
                                                isEditing   = false
                                                editRating  = 0f
                                                editComment = ""
                                            }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun ReviewCard(
    review: Review,
    isOwner: Boolean,
    showActions: Boolean = false,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            1.dp,
            if (isOwner) TourizmeBlueMain.copy(alpha = 0.3f) else Color(0xFFE8EDF2)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (isOwner) TourizmeBlueMain else Color(0xFF006194)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(review.userName.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = InterFontFamily)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(review.userName, fontWeight = FontWeight.Bold, fontSize = 14.sp, fontFamily = MontserratFontFamily)
                            if (isOwner) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(color = TourizmeBlueMain.copy(alpha = 0.12f), shape = RoundedCornerShape(4.dp)) {
                                    Text("Ulasan Anda", fontSize = 10.sp, color = TourizmeBlueMain, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                                }
                            }
                        }
                        Text("Baru-baru ini", color = Color.Gray, fontSize = 11.sp, fontFamily = MontserratFontFamily)
                    }
                }

                if (isOwner && showActions) {
                    Row {
                        IconButton(onClick = onEditClick, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TourizmeBlueMain, modifier = Modifier.size(18.dp))
                        }
                        IconButton(onClick = onDeleteClick, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Red, modifier = Modifier.size(18.dp))
                        }
                    }
                } else if (!isOwner && review.ratingGiven > 0f) {
                    Row {
                        repeat(review.ratingGiven.toInt()) {
                            Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }

            if (isOwner && review.ratingGiven > 0f) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(review.ratingGiven.toInt()) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${review.ratingGiven.toInt()}/5", fontSize = 11.sp, color = Color.Gray, fontFamily = MontserratFontFamily)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(review.comment, fontSize = 13.sp, color = Color.DarkGray, lineHeight = 19.sp, fontFamily = MontserratFontFamily)
        }
    }
}


@Composable
private fun ReviewFormNew(
    inputRating: Float,
    inputComment: String,
    onRatingChange: (Float) -> Unit,
    onCommentChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Text("Beri Ulasan Anda", fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = MontserratFontFamily)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.LightGray),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Rating: ", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, fontFamily = MontserratFontFamily)
                Spacer(modifier = Modifier.width(6.dp))
                for (i in 1..5) {
                    IconButton(onClick = { onRatingChange(i.toFloat()) }, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = null,
                            tint = if (i <= inputRating) Color(0xFFFFC107) else Color.LightGray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
            OutlinedTextField(
                value = inputComment,
                onValueChange = onCommentChange,
                placeholder = { Text("Tulis pendapat Anda tentang destinasi ini...", fontSize = 13.sp) },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 4,
                shape = RoundedCornerShape(12.dp)
            )
            Button(
                onClick = onSubmit,
                enabled = inputComment.isNotBlank(),
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(containerColor = TourizmeBlueMain),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Kirim Ulasan", color = Color.White, fontSize = 13.sp, fontFamily = InterFontFamily, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun MyReviewDisplay(
    review: Review,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Text("Ulasan Anda", fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = MontserratFontFamily)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FBFF)),
        border = BorderStroke(1.dp, TourizmeBlueMain.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (review.ratingGiven > 0f) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(review.ratingGiven.toInt()) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("${review.ratingGiven.toInt()}/5", fontSize = 12.sp, color = Color.Gray, fontFamily = MontserratFontFamily)
                }
            }
            Text(review.comment, fontSize = 13.sp, color = Color.DarkGray, lineHeight = 19.sp, fontFamily = MontserratFontFamily)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                OutlinedButton(
                    onClick = onDeleteClick,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    border = BorderStroke(1.dp, Color.Red),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Hapus", fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onEditClick,
                    colors = ButtonDefaults.buttonColors(containerColor = TourizmeBlueMain),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit Ulasan", color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }
}


@Composable
private fun ReviewFormEdit(
    editRating: Float,
    editComment: String,
    onRatingChange: (Float) -> Unit,
    onCommentChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxWidth(), color = Color(0xFFEBF1F5), shape = RoundedCornerShape(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Mode Edit Ulasan", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TourizmeBlueMain, fontFamily = MontserratFontFamily)
            TextButton(onClick = onCancel, contentPadding = PaddingValues(0.dp)) {
                Text("Batal", fontSize = 12.sp, color = Color.Red)
            }
        }
    }
    Text("Edit Ulasan Anda", fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = MontserratFontFamily)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, TourizmeBlueMain),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Rating: ", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, fontFamily = MontserratFontFamily)
                Spacer(modifier = Modifier.width(6.dp))
                for (i in 1..5) {
                    IconButton(onClick = { onRatingChange(i.toFloat()) }, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = null,
                            tint = if (i <= editRating) Color(0xFFFFC107) else Color.LightGray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
            OutlinedTextField(
                value = editComment,
                onValueChange = onCommentChange,
                placeholder = { Text("Edit komentar Anda...", fontSize = 13.sp) },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 4,
                shape = RoundedCornerShape(12.dp)
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onCancel) { Text("Batal", color = Color.Red, fontSize = 13.sp) }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onSave,
                    enabled = editComment.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = TourizmeBlueMain),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Simpan", color = Color.White, fontSize = 13.sp)
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DestinationDetailScreen(
    viewModel: DetailViewModel,
    userSession: UserSession,
    onBack: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    val destination      by viewModel.destination.collectAsStateWithLifecycle()
    val isFavorite       by viewModel.isFavorite.collectAsStateWithLifecycle()
    val isItineraried    by viewModel.isPlanned.collectAsStateWithLifecycle()
    val isLoading        by viewModel.isLoading.collectAsStateWithLifecycle()
    val userReview       by viewModel.userReview.collectAsStateWithLifecycle()
    val currentUserId    by userSession.userId.collectAsState(initial = "")

    val scrollState = rememberScrollState()

    var showDeleteFavoriteDialog by remember { mutableStateOf(false) }
    var selectedDestinationForItinerary by remember { mutableStateOf<Destination?>(null) }
    var showLoginPromptDialog by remember { mutableStateOf(false) }

    val openMaps: () -> Unit = {
        try {
            val url = destination?.gmapUrl
            if (!url.isNullOrBlank()) {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            } else {
                Toast.makeText(context, "URL lokasi tidak tersedia", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Tidak dapat membuka peta", Toast.LENGTH_SHORT).show()
        }
    }

    // Dialog konfirmasi hapus favorit
    TourizmeDeleteDialog(
        show = showDeleteFavoriteDialog,
        message = "Yakin ingin menghapus destinasi ini dari favorit?",
        onConfirm = {
            viewModel.toggleFavorite()
            showDeleteFavoriteDialog = false
            Toast.makeText(context, "Dihapus dari favorit", Toast.LENGTH_SHORT).show()
        },
        onDismiss = { showDeleteFavoriteDialog = false }
    )

    // Dialog date picker itinerary
    selectedDestinationForItinerary?.let { dest ->
        TourizmeDatePicker(
            destination = dest,
            onDismiss = { selectedDestinationForItinerary = null },
            onDateSelected = { formattedDate, targetTimeMillis ->
                viewModel.addToItinerary(formattedDate)
                scheduleNotification(
                    context = context,
                    targetTimeMillis = targetTimeMillis,
                    destinationName = dest.name,
                    destinationId = dest.id
                )
                Toast.makeText(context, "Berhasil disimpan ke daftar rencana", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Dialog prompt login
    if (showLoginPromptDialog) {
        AlertDialog(
            onDismissRequest = { showLoginPromptDialog = false },
            title = { Text("Fitur Terbatas") },
            text = { Text("Silahkan login terlebih dahulu untuk menggunakan fitur ini.") },
            confirmButton = {
                Button(onClick = { showLoginPromptDialog = false; onNavigateToLogin() }) { Text("Login") }
            },
            dismissButton = {
                TextButton(onClick = { showLoginPromptDialog = false }) { Text("Batal") }
            }
        )
    }

    DestinationDetailContent(
        destination   = destination,
        isFavorite    = isFavorite,
        isItineraried = isItineraried,
        isLoading     = isLoading,
        scrollState   = scrollState,
        currentUserId = currentUserId ?: "",
        userReview    = userReview,
        onBack        = onBack,
        onFavoriteClick = {
            if (currentUserId.isNullOrBlank() || currentUserId == "GUEST") {
                showLoginPromptDialog = true
            } else {
                if (isFavorite) showDeleteFavoriteDialog = true else viewModel.toggleFavorite()
            }
        },
        onCalendarClick = {
            if (currentUserId.isNullOrBlank() || currentUserId == "GUEST") {
                showLoginPromptDialog = true
            } else {
                selectedDestinationForItinerary = destination
            }
        },
        onOpenMaps = openMaps,
        onSubmitReview = { rating, comment ->
            viewModel.submitReview(rating, comment)
        },
        onUpdateReview = { newRating, newComment ->
            viewModel.updateReview(newRating, newComment)
            Toast.makeText(context, "Ulasan berhasil diperbarui", Toast.LENGTH_SHORT).show()
        },
        onDeleteMyReview = {
            viewModel.deleteMyReview()
            Toast.makeText(context, "Ulasan berhasil dihapus", Toast.LENGTH_SHORT).show()
        }
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Preview
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showSystemUi = true)
@Composable
fun DestinationDetailPreview() {
    val sampleReview = Review(userId = "user1", userName = "Amba", ratingGiven = 5f, comment = "Sangat indah dan menakjubkan!")
    MaterialTheme {
        DestinationDetailContent(
            destination = Destination(
                id = "1", name = "Candi Borobudur", location = "Magelang",
                description = "Candi Buddha terbesar di dunia yang dibangun pada abad ke-9.",
                price = "50.000", imageUrl = "", gmapUrl = "",
                averageRating = 4.8f,
                reviews = listOf(
                    sampleReview,
                    Review("user2", "Budi", 4f, "Tempatnya bagus, tapi agak ramai.")
                )
            ),
            isFavorite    = true,
            isItineraried = false,
            isLoading     = false,
            scrollState   = rememberScrollState(),
            currentUserId = "user1",
            userReview    = sampleReview,
            onBack        = {},
            onFavoriteClick  = {},
            onCalendarClick  = {},
            onOpenMaps       = {},
            onSubmitReview   = { _, _ -> },
            onUpdateReview   = { _, _ -> },
            onDeleteMyReview = {}
        )
    }
}
