package com.vitaliykharchenko.intouch.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.foundation.Box
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageAsset
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.ui.tooling.preview.Preview
import com.vitaliykharchenko.intouch.R
import com.vitaliykharchenko.intouch.model.Peer
import com.vitaliykharchenko.intouch.model.PeerId
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
                    Content(viewModel.state.collectAsState().value)
                }
            }
        }
    }
}

@Composable
private fun Content(state: MainUiState) {

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
        }

        LazyColumnFor(state.peers) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable(onClick = it.onClick)
            ) {
                Row(verticalGravity = Alignment.CenterVertically) {
                    Text(text = it.name, modifier = Modifier.weight(1f), fontSize = 24.sp)
                    Image(asset = vectorResource(R.drawable.ic_item_menu_24))
                }
                Divider()
            }
        }
    }
}
