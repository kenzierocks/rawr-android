package net.octyl.rawr.android.frag

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import net.octyl.rawr.android.R
import net.octyl.rawr.android.dagger.injectDependencies

class AvailablePlaylistFragment : Fragment() {

    private lateinit var availablePlaylists: RecyclerView

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_playlist, container, false)
    }

    override fun onAttach(context: Context?) {
        injectDependencies()
        super.onAttach(context)
    }
}