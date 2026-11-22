package com.lira.tracker.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lira.tracker.ui.components.ListenersChart
import com.lira.tracker.ui.components.MetricCard
import com.lira.tracker.ui.components.SettingsScreen
import com.lira.tracker.ui.theme.LiraTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()
            val appStyle by viewModel.appStyle.collectAsState()

            LiraTheme(isDarkTheme = isDarkTheme, appStyle = appStyle) {
                MainAppScaffold(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScaffold(viewModel: MainViewModel) {
    val recordsAsc by viewModel.recordsAsc.collectAsState()
    val recordsDesc by viewModel.recordsDesc.collectAsState()
    val artistId by viewModel.artistId.collectAsState()
    val syncHour by viewModel.syncHour.collectAsState()
    val syncMinute by viewModel.syncMinute.collectAsState()
    val lastSyncTimestamp by viewModel.lastSyncTimestamp.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val appStyle by viewModel.appStyle.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUserMessage()
        }
    }

    val themeBackground = MaterialTheme.colorScheme.background
    val themeSurface = MaterialTheme.colorScheme.surface
    val themePrimary = MaterialTheme.colorScheme.primary
    val themeOnSurface = MaterialTheme.colorScheme.onSurface
    val themeOnSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    Scaffold(
        containerColor = themeBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "LU",
                                color = Color(0xFF1E1E1E),
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (selectedTab == 0) "LIRA" else "Настройки",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = themeOnSurface,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = themePrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = "Яндекс Музыка • Автозапуск в ${syncHour.toString().padStart(2, '0')}:${syncMinute.toString().padStart(2, '0')}",
                                style = MaterialTheme.typography.labelSmall,
                                color = themeOnSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = themeBackground)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = themeSurface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = "Главная"
                        )
                    },
                    label = { Text("Статистика", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = themePrimary,
                        selectedTextColor = themePrimary,
                        indicatorColor = themePrimary.copy(alpha = 0.15f),
                        unselectedIconColor = themeOnSurfaceVariant,
                        unselectedTextColor = themeOnSurfaceVariant
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Настройки"
                        )
                    },
                    label = { Text("Настройки", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = themePrimary,
                        selectedTextColor = themePrimary,
                        indicatorColor = themePrimary.copy(alpha = 0.15f),
                        unselectedIconColor = themeOnSurfaceVariant,
                        unselectedTextColor = themeOnSurfaceVariant
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (selectedTab == 0) {
                // Вкладка 1: Главная (Статистика + График)
                HomeScreen(
                    latestRecord = recordsDesc.firstOrNull(),
                    artistId = artistId,
                    lastSyncTimestamp = lastSyncTimestamp,
                    recordsAsc = recordsAsc,
                    isLoading = isLoading,
                    onFetchNow = { viewModel.fetchNow() }
                )
            } else {
                // Вкладка 2: Настройки на весь экран (Параметры + Темы + Журнал измерений)
                SettingsScreen(
                    currentArtistId = artistId,
                    currentHour = syncHour,
                    currentMinute = syncMinute,
                    isDarkTheme = isDarkTheme,
                    currentStyle = appStyle,
                    records = recordsDesc,
                    onSaveSettings = { newId, h, m -> viewModel.saveSettings(newId, h, m) },
                    onToggleTheme = { isDark -> viewModel.setDarkTheme(isDark) },
                    onSelectStyle = { style -> viewModel.setAppStyle(style) },
                    onClearHistory = { viewModel.clearHistory() }
                )
            }
        }
    }
}

@Composable
fun HomeScreen(
    latestRecord: com.lira.tracker.data.model.ListenerRecord?,
    artistId: String,
    lastSyncTimestamp: Long,
    recordsAsc: List<com.lira.tracker.data.model.ListenerRecord>,
    isLoading: Boolean,
    onFetchNow: () -> Unit
) {
    val themePrimary = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // 1. Главная карточка показателя
        MetricCard(
            record = latestRecord,
            artistId = artistId,
            lastSyncTimestamp = lastSyncTimestamp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Кнопка «Получить данные сейчас»
        Button(
            onClick = onFetchNow,
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = themePrimary,
                disabledContainerColor = themePrimary.copy(alpha = 0.5f)
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.5.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Загрузка с Яндекс Музыки...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Получить данные сейчас",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 3. График динамики слушателей
        ListenersChart(records = recordsAsc)

        Spacer(modifier = Modifier.height(30.dp))
    }
}
