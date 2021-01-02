package com.vitaliykharchenko.intouch.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitaliykharchenko.intouch.R

@Composable
fun MainView(state: MainUi) {

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
            contentAlignment = Alignment.Center
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
    LazyColumn {
        items(peers) {
            Row(
                modifier = Modifier.clickable(onClick = it.onClick)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = it.name, modifier = Modifier.weight(1f), fontSize = 24.sp)
                Image(imageVector = vectorResource(R.drawable.ic_item_menu_24))
            }
        }
    }
}