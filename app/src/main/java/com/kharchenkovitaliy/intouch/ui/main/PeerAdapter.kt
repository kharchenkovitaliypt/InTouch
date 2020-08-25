//package com.kharchenkovitaliy.intouch.ui.main
//
//import android.view.View
//import com.airbnb.epoxy.EpoxyAttribute
//import com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash
//import com.airbnb.epoxy.EpoxyModelClass
//import com.airbnb.epoxy.EpoxyModelWithHolder
//import com.airbnb.epoxy.TypedEpoxyController
//import com.kharchenkovitaliy.intouch.R
//import com.kharchenkovitaliy.intouch.model.Peer
//import com.kharchenkovitaliy.intouch.shared.SyntheticEpoxyHolder
//import kotlinx.android.synthetic.main.item_peer.*
//
//class PeerAdapter(
//    private val onPeerMenuClick: (Peer, View) -> Unit
//) : TypedEpoxyController<List<Peer>>() {
//
//    override fun buildModels(peers: List<Peer>) {
//        peers.forEach { peer ->
//            PeerModel_()
//                .id(peer.id.value)
//                .peer(peer)
//                .onPeerMenuClick(onPeerMenuClick)
//                .addTo(this)
//        }
//    }
//}
//
//@EpoxyModelClass(layout = R.layout.item_peer)
//abstract class PeerModel : EpoxyModelWithHolder<SyntheticEpoxyHolder>() {
//
//    @EpoxyAttribute(DoNotHash) lateinit var onPeerMenuClick: (Peer, View) -> Unit
//    @EpoxyAttribute lateinit var peer: Peer
//
//    override fun bind(holder: SyntheticEpoxyHolder) {
//        super.bind(holder)
//        holder.name.text = peer.name
//
//        holder.menu.setOnClickListener { view ->
//            onPeerMenuClick(peer, view)
//        }
//    }
//}