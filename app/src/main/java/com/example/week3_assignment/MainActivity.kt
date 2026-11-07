package com.example.week3_assignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.week3_assignment.ui.SmartHomeScreen
import com.example.week3_assignment.ui.theme.Week3_assignmentTheme
import com.example.week3_assignment.viewmodel.SmartHomeViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: SmartHomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Week3_assignmentTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

                Scaffold { innerPadding ->
                    SmartHomeScreen(
                        uiState = uiState,
                        searchQuery = searchQuery,
                        onSearchQueryChange = viewModel::onSearchQueryChange,
                        onRoomSelected = viewModel::onRoomSelected,
                        onToggleDevice = viewModel::onToggleDevice,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}