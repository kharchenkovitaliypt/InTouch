package com.vitaliykharchenko.intouch.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.foundation.Box
import androidx.compose.foundation.ContentGravity
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.vitaliykharchenko.intouch.R
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: MainViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DarkTheme {
                Surface(color = MaterialTheme.colors.background) {
                    MainView(viewModel.uiFlow.collectAsState().value)
                }
            }
        }
    }
}

@Composable
private fun MainView(state: MainUi) {

    Column(Modifier.fillMaxSize()) {

        Column(Modifier.padding(16.dp)) {
            Text(text = state.serverName)

            Row(Modifier.padding(top = 8.dp)) {
                Button(onClick = state.onStartServer) {
                    Text(text = "Start server")
                }
                Button(
                    onClick = state.onStopServer,
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Text(text = "Stop server")
                }
            }

            Row(Modifier.padding(top = 16.dp)) {
                Button(onClick = state.onStartDiscovery) {
                    Text(text = "Start discovery")
                }
                Button(
                    onClick = state.onStopDiscovery,
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Text(text = "Stop discovery")
                }
            }
        }

        Box(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth(),
            gravity = ContentGravity.Center
        ) {
            when (state.peersState) {
                is PeersUiState.Idle -> { /* Empty space */
                }
                is PeersUiState.Waiting -> {
                    CircularProgressIndicator(
                        modifier = Modifier.height(48.dp)
                    )
                }
                is PeersUiState.Data -> {
                    PeersView(state.peersState.peers)
                }
                is PeersUiState.Error -> {
                    Text(
                        text = state.peersState.desc,
                        color = MaterialTheme.colors.error
                    )
                }
            }
        }
    }
}

@Composable
private fun PeersView(peers: List<PeerUi>) {
    LazyColumnFor(peers) {
        Row(
            modifier = Modifier.clickable(onClick = it.onClick)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalGravity = Alignment.CenterVertically
        ) {
            Text(text = it.name, modifier = Modifier.weight(1f), fontSize = 24.sp)
            Image(asset = vectorResource(R.drawable.ic_item_menu_24))
        }
    }
}
