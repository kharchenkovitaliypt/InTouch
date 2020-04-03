package com.kharchenkovitaliy.intouch.ui.main

import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
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
            onPeerMenuClick = { peer, view ->
                val popup = PopupMenu(this@MainActivity, view, Gravity.END)
                popup.menuInflater.inflate(R.menu.peer, popup.menu)
                popup.setOnMenuItemClickListener { true }
                popup.show()
            }
        )
        peers.setController(peerAdapter)

        viewModel.serverLiveData.observe(this, Observer { service ->
            this.name.text = service
        })
        viewModel.peersLiveData.observe(this, Observer { peers ->
            peerAdapter.setData(peers)
        })

        startServer.setOnClickListener {
            viewModel.startServer()
        }
        stopServer.setOnClickListener {
            viewModel.stopServer()
        }

        startDiscover.setOnClickListener {
            viewModel.startDiscovery()
        }
        stopDiscover.setOnClickListener {
            viewModel.stopDiscovery()
        }

        supportFragmentManager.commit {
            add(MainFragment(), null)
        }
    }
}

class MainFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Toast.makeText(activity!!, "Hello with activity: ${activity}", Toast.LENGTH_SHORT).show()
    }
}
