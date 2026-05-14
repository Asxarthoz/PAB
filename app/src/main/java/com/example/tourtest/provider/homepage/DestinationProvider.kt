package com.example.tourtest.provider.homepage
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.tourtest.model.Destination

class DestinationProvider : PreviewParameterProvider<Destination> {
    override val values = sequenceOf(
        Destination(
            id = "1",
            name = "Keraton Surakarta",
            location = "Solo",
            description = "Tempat bersejarah yang sangat indah.",
            price = "15.000",
            imageUrl = "",
            gmapUrl = "",
        ),
        Destination(
            id = "2",
            name = "Candi Cetho yang Sangat Jauh di Atas Gunung Karanganyar", // Contoh nama panjang
            location = "Karanganyar",
            description = "Pemandangannya luar biasa.",
            price = "10.000",
            imageUrl = "",
            gmapUrl = ""
        )
    )
}