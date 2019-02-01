package net.octyl.rawr.android

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import net.octyl.rawr.android.dagger.injectDependencies

class RawrMainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        injectDependencies()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        drawerLayout = findViewById(R.id.root)

        supportActionBar!!.configureRawrApp()

        switchView(SubView.TRACKS)
    }

    private fun ActionBar.configureRawrApp() {
        // who needs a title here? not us
        title = ""

        setDisplayHomeAsUpEnabled(true)
        val menuIcon = getDrawable(R.drawable.ic_menu)!!
        menuIcon.setTint(getColor(R.color.menu))
        setHomeAsUpIndicator(menuIcon)
    }

    private var subView: SubView? = null
    private fun switchView(subView: SubView) {
        if (this.subView == subView) {
            return
        }

        mainFrag(subView.newFragment())
        this.subView = subView
    }

    private fun mainFrag(frag: Fragment) {
        val fragTransaction = supportFragmentManager.beginTransaction()
        fragTransaction.add(R.id.main_frag_container, frag)
        fragTransaction.commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
            }
            R.id.tracks -> switchView(SubView.TRACKS)
            R.id.playlists -> switchView(SubView.AVAILABLE_PLAYLISTS)
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

}
