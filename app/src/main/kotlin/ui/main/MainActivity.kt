package com.vitaliykharchenko.intouch.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.ui.tooling.preview.Preview
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: MainViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val state = viewModel.state.collectAsState()
            Content(state.value)
        }

//        val peerAdapter = PeerAdapter(
//            onPeerMenuClick = { peer, view ->
//                val popup = PopupMenu(this@MainActivity, view, Gravity.END)
//                popup.menuInflater.inflate(R.menu.peer, popup.menu)
//                popup.setOnMenuItemClickListener { true }
//                popup.show()
//            }
//        )
//        peers.setController(peerAdapter)

//        viewModel.serverLiveData.observe(this, Observer { service ->
//            this.name.text = service
//        })
//        viewModel.peersLiveData.observe(this, Observer { peers ->
//            peerAdapter.setData(peers)
//        })
    }

//    private fun showPopup(view: View) {
//        val popup = PopupMenu(this@MainActivity, view, Gravity.END)
//        popup.menuInflater.inflate(R.menu.peer, popup.menu)
//        popup.setOnMenuItemClickListener { true }
//        popup.show()
//    }
}

@Composable
fun Content(
    state: MainUiState
) {
    val n = 4

    Column(Modifier.padding(8.dp)) {
        Text(text = state.serverName)

        Row(Modifier.padding(top = 8.dp)) {
            Button(onClick = state.onStartServer) {
                Text(text = "Start server $n")
            }
            Button(
                onClick = state.onStopServer,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(text = "Stop server")
            }
        }
    }
}
