package com.kharchenkovitaliy.intouch.ui.main

import com.airbnb.epoxy.*
import com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash
import com.kharchenkovitaliy.intouch.R
import com.kharchenkovitaliy.intouch.model.Peer
import com.kharchenkovitaliy.intouch.shared.SyntheticEpoxyHolder
import com.kharchenkovitaliy.intouch.shared.view
import kotlinx.android.synthetic.main.item_peer.*

class PeerAdapter(
    private val onPeerClick: (Peer) -> Unit
) : TypedEpoxyController<List<Peer>>() {

    override fun buildModels(peers: List<Peer>) {
        peers.forEach { peer ->
            PeerModel_()
                .id(peer.id.value)
                .peer(peer)
                .onPeerClick(onPeerClick)
                .addTo(this)
        }
    }
}

@EpoxyModelClass(layout = R.layout.item_peer)
abstract class PeerModel : EpoxyModelWithHolder<SyntheticEpoxyHolder>() {

    @EpoxyAttribute(DoNotHash) lateinit var onPeerClick: (Peer) -> Unit
    @EpoxyAttribute lateinit var peer: Peer

    override fun bind(holder: SyntheticEpoxyHolder) {
        super.bind(holder)
        holder.name.text = peer.name

        holder.view.setOnClickListener {
            onPeerClick(peer)
        }
    }
}