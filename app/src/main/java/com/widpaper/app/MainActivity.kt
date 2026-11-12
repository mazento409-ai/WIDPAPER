package com.widpaper.app

import android.app.WallpaperManager
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color as C
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val ACC = C(0xFF5BC8FF)
val NAVY = C(0xFF0A1A3F)
val LINE = C(0xFF3A5A9A)
val TX = C(0xFFBFE6FF)

class Wp(val name: String, val c1: Int, val c2: Int)

val BRANDS = listOf("iPhone", "Xiaomi", "Samsung", "Oppo")
val BASE = listOf(0f, 20f, 215f, 140f)
val OPPO = listOf(
    "Android 10 · ColorOS 7", "Android 11 · ColorOS 11", "Android 12 · ColorOS 12",
    "Android 13 · ColorOS 13", "Android 14 · ColorOS 14", "Android 15 · ColorOS 15",
    "Android 16 · ColorOS 16", "Android 17 · ColorOS 17"
)
val HEIGHTS = listOf(230, 190, 190, 230)

fun hsv(h: Float, s: Float, v: Float): Int =
    android.graphics.Color.HSVToColor(floatArrayOf(((h % 360f) + 360f) % 360f, s, v))

fun wallpapers(b: Int): List<Wp> =
    if (b == 3) OPPO.flatMapIndexed { v, n ->
        (0..3).map { i ->
            val h = v * 45f + i * 47f
            Wp("$n #${i + 1}", hsv(h, .75f, .95f), hsv(h + 200f, .6f, .25f))
        }
    } else (0..5).map { i ->
        val h = BASE[b] + i * 55f
        Wp("${BRANDS[b]} #${i + 1}", hsv(h, .65f, 1f), hsv(h + 40f, .7f, .3f))
    }

fun setWall(ctx: Context, w: Wp) {
    try {
        val dm = ctx.resources.displayMetrics
        val bmp = Bitmap.createBitmap(dm.widthPixels, dm.heightPixels, Bitmap.Config.ARGB_8888)
        val p = Paint().apply {
            shader = LinearGradient(0f, 0f, dm.widthPixels.toFloat(), dm.heightPixels.toFloat(), w.c1, w.c2, Shader.TileMode.CLAMP)
        }
        Canvas(bmp).drawPaint(p)
        WallpaperManager.getInstance(ctx).setBitmap(bmp)
        Toast.makeText(ctx, "Wallpaper set", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(ctx, "Could not set wallpaper", Toast.LENGTH_SHORT).show()
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { App() }
    }
}

@Composable
fun App() {
    val ctx = LocalContext.current
    var brand by remember { mutableIntStateOf(0) }
    var tab by remember { mutableIntStateOf(0) }
    var pick by remember { mutableStateOf<Wp?>(null) }

    Box(Modifier.fillMaxSize().background(C.Black).statusBarsPadding()) {
        Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            if (tab != 2) {
                Row(Modifier.padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BRANDS.forEachIndexed { i, n ->
                        val on = i == brand
                        val shape = RoundedCornerShape(22.dp)
                        Box(
                            Modifier.weight(1f).height(44.dp).clip(shape)
                                .background(if (on) ACC else C.Transparent)
                                .border(1.dp, if (on) ACC else LINE, shape)
                                .clickable { brand = i },
                            contentAlignment = Alignment.Center
                        ) { Text(n, color = if (on) NAVY else TX, fontSize = 14.sp) }
                    }
                }
            }
            when (tab) {
                0 -> LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 20.dp, bottom = 120.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(wallpapers(brand)) { i, w ->
                        Box(
                            Modifier.fillMaxWidth().height(HEIGHTS[i % 4].dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Brush.linearGradient(listOf(C(w.c1), C(w.c2))))
                                .clickable { pick = w }
                        )
                    }
                }
                1 -> Column(Modifier.padding(top = 24.dp)) {
                    Text("Clock widget", color = TX, fontSize = 15.sp)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = {
                        val m = AppWidgetManager.getInstance(ctx)
                        if (m.isRequestPinAppWidgetSupported) {
                            m.requestPinAppWidget(ComponentName(ctx, ClockWidget::class.java), null, null)
                        } else {
                            Toast.makeText(ctx, "Add it from your home screen widgets list", Toast.LENGTH_LONG).show()
                        }
                    }) { Text("Add to home screen") }
                }
                else -> Column(Modifier.padding(top = 56.dp)) {
                    Text("Widpaper 1.0", color = C.White, fontSize = 20.sp)
                }
            }
        }

        val barShape = RoundedCornerShape(34.dp)
        Row(
            Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(bottom = 28.dp)
                .width(256.dp).height(56.dp).clip(barShape)
                .background(C.White.copy(alpha = 0.14f))
                .border(1.dp, C.White.copy(alpha = 0.3f), barShape),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icons: List<ImageVector> = listOf(Icons.Outlined.Image, Icons.Outlined.Widgets, Icons.Outlined.Settings)
            icons.forEachIndexed { i, ic ->
                Box(
                    Modifier.width(if (i == tab) 86.dp else 52.dp).height(42.dp)
                        .clip(RoundedCornerShape(26.dp))
                        .background(if (i == tab) C.White.copy(alpha = 0.22f) else C.Transparent)
                        .clickable { tab = i },
                    contentAlignment = Alignment.Center
                ) { Icon(ic, contentDescription = listOf("Wallpapers", "Widgets", "Settings")[i], tint = C.White) }
            }
        }
    }

    pick?.let { w ->
        AlertDialog(
            onDismissRequest = { pick = null },
            title = { Text(w.name) },
            confirmButton = { TextButton(onClick = { setWall(ctx, w); pick = null }) { Text("Set as wallpaper") } },
            dismissButton = { TextButton(onClick = { pick = null }) { Text("Cancel") } }
        )
    }
}
