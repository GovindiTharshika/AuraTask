package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.SecondaryPurple
import com.example.ui.theme.ThemeBackgroundDark
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onTimeout: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Animation states
    var startAnimation by remember { mutableStateOf(false) }

    val logoScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.6f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logo_scale"
    )

    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = EaseInOutCubic
        ),
        label = "logo_alpha"
    )

    val textAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1400,
            delayMillis = 300,
            easing = EaseOutQuart
        ),
        label = "text_alpha"
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        // Keep the splash screen visible for 2.5 seconds to build immersion
        delay(2500)
        onTimeout()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ThemeBackgroundDark)
            .testTag("splash_screen_root"),
        contentAlignment = Alignment.Center
    ) {
        // --- Full Background Splash Image (9:16 aspect) ---
        AsyncImage(
            model = R.drawable.img_splash_bg,
            contentDescription = "Cosmic Splash Glow Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Semi-transparent dark overlay to keep layout text high-contrast and readable
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.5f),
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        // Centered glowing container
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // Elegant centered logo container
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(logoScale)
                    .alpha(logoAlpha)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                PrimaryCyan.copy(alpha = 0.25f),
                                Color.Transparent
                            )
                        )
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = R.drawable.ic_aura_logo,
                    contentDescription = "AuraTask Logo Image",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Application Typography Headings
            Text(
                text = "AURA TASK",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.SansSerif,
                color = Color.White,
                letterSpacing = 6.sp,
                modifier = Modifier
                    .alpha(textAlpha)
                    .testTag("splash_app_title")
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Aligning Your Cognitive Flow",
                fontSize = 13.sp,
                fontWeight = FontWeight.Light,
                fontFamily = FontFamily.Monospace,
                color = PrimaryCyan.copy(alpha = 0.85f),
                letterSpacing = 1.sp,
                modifier = Modifier.alpha(textAlpha)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Smooth Circular Progress Indicator matching color accents
            CircularProgressIndicator(
                color = PrimaryCyan,
                trackColor = SecondaryPurple.copy(alpha = 0.2f),
                strokeWidth = 3.dp,
                modifier = Modifier
                    .size(36.dp)
                    .alpha(textAlpha)
            )
        }

        // Subtitle signature at the bottom of the screen
        Text(
            text = "PREMIUM PRODUCTIVITY CENTER",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray.copy(alpha = 0.6f),
            letterSpacing = 2.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .alpha(textAlpha)
        )
    }
}
