package com.kharchenkovitaliy.intouch.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
