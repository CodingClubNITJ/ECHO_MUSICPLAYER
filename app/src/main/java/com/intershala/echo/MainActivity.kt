package com.intershala.echo

import android.app.Notification
import android.app.NotificationManager
import android.support.v4.app.NotificationCompat;
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.RemoteViews
import com.intershala.echo.MainActivity.statified.notificationManager
import com.intershala.echo.adapter.NavigationDrawerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import java.lang.Exception
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {
    var navigationDrawerIconList: ArrayList<String> = arrayListOf()
    var images_ = intArrayOf(
        R.drawable.navigation_allsongs,
        R.drawable.navigation_favorites,
        R.drawable.navigation_settings,
        R.drawable.navigation_aboutus
    )
    object statified
    {
    var drawerLayout: DrawerLayout? = null
        var notificationManager:NotificationManager?=null
}
    var notificationBuilder:Notification?=null
    override fun onCreate(savedInstanceState: Bundle?) {
         navigationDrawerIconList.add("All Songs")
        navigationDrawerIconList.add("Favorites")
        navigationDrawerIconList.add("Setting")
        navigationDrawerIconList.add("About Us")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar=findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
       MainActivity.statified.drawerLayout=findViewById(R.id.drawer_layout)
        var toggle=ActionBarDrawerToggle(this@MainActivity, MainActivity.statified.drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        MainActivity.statified.drawerLayout?.setDrawerListener(toggle)
        toggle.syncState()
val mainsreenFragment=MainScreenFragment()
        this.supportFragmentManager.beginTransaction().add(R.id.details_fragment, mainsreenFragment, "MainScreenFragment").commit()
var _navigationAdapter=NavigationDrawerAdapter(navigationDrawerIconList, images_, this)
        _navigationAdapter.notifyDataSetChanged()
var navigation_recyler_view=findViewById<RecyclerView>(R.id.navigation_recyler_view)
        navigation_recyler_view.layoutManager=LinearLayoutManager(this)
        navigation_recyler_view.itemAnimator=DefaultItemAnimator()
        navigation_recyler_view.adapter=_navigationAdapter
        navigation_recyler_view.setHasFixedSize(true)
val intent= Intent(this, MainActivity::class.java)
        val pendingIntent=PendingIntent.getActivity(this, System.currentTimeMillis().toInt(), intent,
            0)
        val notificationLayoutExpanded = RemoteViews(packageName, R.layout.notification_panel)

        notificationBuilder=NotificationCompat.Builder(this).setContentTitle("A Track is Playing in Background")
            .setSmallIcon(R.drawable.echo_logo).setContentIntent(pendingIntent).setOngoing(true)
            .setAutoCancel(true).build()

          notificationManager=getSystemService(Context.NOTIFICATION_SERVICE )as NotificationManager




    }

    override fun onStart() {
        super.onStart()
        try {  notificationManager?.cancel(1987)

        }catch (e:Exception)
        {
            e.printStackTrace()
        }
    }

    override fun onStop() {
        super.onStop()
try{
  if(SongPlayingFragment.staticate.mediaPlayer?.isPlaying as Boolean)
  {
      notificationManager?.notify(1987, notificationBuilder)
  }
    }catch (e:Exception)
{ e.printStackTrace()
}
    }

    override fun onResume() {
        super.onResume()
        try {  notificationManager?.cancel(1987)

        }catch (e:Exception)
        {
            e.printStackTrace()
        }
    }
}
