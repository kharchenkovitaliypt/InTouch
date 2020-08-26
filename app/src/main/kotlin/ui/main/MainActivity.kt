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
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: MainViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Content(viewModel)
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
//
//        startServer.setOnClickListener {
//            viewModel.startServer()
//        }
//        stopServer.setOnClickListener {
//            viewModel.stopServer()
//        }
//
//        startDiscover.setOnClickListener {
//            viewModel.startDiscovery()
//        }
//        stopDiscover.setOnClickListener {
//            viewModel.stopDiscovery()
//        }
//
//        supportFragmentManager.commit {
//            add(MainFragment(), null)
//        }
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
    viewModel: MainViewModel
) {
    val server = viewModel.serverFlow.collectAsState(initial = "???")

    Column(Modifier.padding(8.dp)) {
        Text(text = server.value)
        Row(Modifier.padding(top = 8.dp)) {
            Button(onClick = viewModel::startServer) {
                Text(text = "Start server")
            }
            Button(
                onClick = viewModel::stopServer,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(text = "Stop server")
            }
        }
    }
}
