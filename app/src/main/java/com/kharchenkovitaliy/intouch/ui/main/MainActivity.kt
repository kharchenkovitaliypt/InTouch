package com.kharchenkovitaliy.intouch.ui.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kharchenkovitaliy.intouch.R
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: MainViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val peerAdapter = PeerAdapter(
            onPeerClick = { peer ->
                Toast.makeText(this, peer.toString(), Toast.LENGTH_SHORT).show()
            }
        )
        peers.setController(peerAdapter)

        viewModel.serverServiceLiveData.observe(this, Observer { service ->
            this.name.text = service
        })
        viewModel.peersLiveData.observe(this, Observer { peers ->
            peerAdapter.setData(peers)
        })

        register.setOnClickListener {
            viewModel.register()
        }
        unregister.setOnClickListener {
            viewModel.unregister()
        }

        startDiscover.setOnClickListener {
            viewModel.startDiscovery()
        }
        stopDiscover.setOnClickListener {
            viewModel.stopDiscovery()
        }
    }
}
