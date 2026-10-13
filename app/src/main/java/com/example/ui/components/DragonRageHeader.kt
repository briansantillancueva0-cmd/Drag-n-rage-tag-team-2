package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.DragonAmber
import com.example.ui.theme.DragonGold
import com.example.ui.theme.TextSecondary

@Composable
fun DragonRageHeader(
    title: String = "DRAGON RAGE",
    subtitle: String = "Tenkaichi Tag Team · Android",
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Glowing Dragon Ball Icon
        Box(
            modifier = Modifier
                .size(72.dp),
            contentAlignment = Alignment.Center
        ) {
            // Ambient glowing halo
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(DragonGold.copy(alpha = 0.5f), Color.Transparent)
                        )
                    )
                    .blur(8.dp)
            )

            // The Dragon Ball image
            Image(
                painter = painterResource(id = R.drawable.img_launcher_icon_1791904167194),
                contentDescription = "4-Star Dragon Ball",
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = title,
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                letterSpacing = 1.sp,
                color = DragonAmber
            )
            Text(
                text = subtitle,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary
            )
        }
    }
}
