package com.kharchenkovitaliy.intouch.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.kharchenkovitaliy.intouch.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.myService.observe(this, Observer { service ->
            this.name.text = service
        })
        viewModel.otherServices.observe(this, Observer { services ->
            this.services.text = services.joinToString(separator = "\n")
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
