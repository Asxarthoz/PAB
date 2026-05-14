package com.example.tourtest.provider.homepage
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.tourtest.model.Destination

class DestinationProvider : PreviewParameterProvider<List<Destination>> {
    override val values = sequenceOf(
        listOf(
            Destination(
                id = "1",
                name = "Keraton Surakarta",
                location = "Solo",
                description = "Istana bersejarah...",
                price = "15.000",
                imageUrl = "",
                gmapUrl = ""
            ),
            Destination(
                id = "2",
                name = "Pasar Gede",
                location = "Solo",
                description = "Pasar tradisional ikonik...",
                price = "Gratis",
                imageUrl = "",
                gmapUrl = ""
            )
        )
    )
}