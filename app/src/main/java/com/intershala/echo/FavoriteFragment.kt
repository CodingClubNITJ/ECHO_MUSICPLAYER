package com.intershala.echo



import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.intershala.echo.Database.database

import com.intershala.echo.FavoriteFragment.Statified.mediaPlayer
import com.intershala.echo.FavoriteFragment.Statified.playbtn
import com.intershala.echo.adapter.FavSreeenAdapter
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_favorite.*
import java.lang.Exception


class FavoriteFragment : Fragment() {
    var myactivity: Activity? = null

    var recylerView: RecyclerView? = null
    var bottomBar: RelativeLayout? = null
    var nofav: TextView? = null
    var sonTitle: TextView? = null
    var refreshlist:ArrayList<Songs>?=null
    var trackPosition:Int=0
    var favcontnt: database?=null
    var favadapter:FavSreeenAdapter?=null
    var listdb:ArrayList<Songs>?=null
    object Statified
    {
        var mediaPlayer:MediaPlayer?=null
        var playbtn: ImageButton? = null



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         val view=inflater.inflate(R.layout.fragment_favorite, container, false)
        favcontnt= database(myactivity)
        activity?.title="Favorites"
        setHasOptionsMenu(true)
        recylerView = view?.findViewById(R.id.favRecyler)
        bottomBar = view?.findViewById(R.id.hiddenBarFavScreen)
        nofav = view?.findViewById(R.id.noFav)
        sonTitle = view?.findViewById(R.id.songTitleFavScreen)
        playbtn = view?.findViewById(R.id.playPauseButton1)
        try
        {
            if(SongPlayingFragment.staticate.mediaPlayer?.isPlaying as Boolean)
            {
                bottomBar?.visibility=View.VISIBLE
            }
            else
            {
                bottomBar?.visibility=View.INVISIBLE
            }
        }
        catch(e:Exception)
        {
            e.printStackTrace()
        }

        return view
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myactivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {

        super.onAttach(activity)
        myactivity = activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        favadapter?.notifyDataSetChanged()
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

try
{
    if(SongPlayingFragment.staticate.mediaPlayer?.isPlaying as Boolean)
    {
        bottomBar?.visibility=View.VISIBLE
    }
    else
    {
        bottomBar?.visibility=View.INVISIBLE
    }
}
catch(e:Exception)
{
    e.printStackTrace()
}


        bottonFavSetup()



        display_fav_by_searching()






    }



    override fun onResume() {
        super.onResume()
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
    }










    fun getSongsFromPhone(): ArrayList<Songs> {
        val arrayList = ArrayList<Songs>()
        val contentResolver = myactivity?.contentResolver
        val songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val songCursor = contentResolver?.query(songUri, null, null, null, null)
        if (songCursor != null && songCursor.moveToFirst()) {
            val songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val dateIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)

            while (songCursor.moveToNext()) {
                val currentId = songCursor.getLong(songId)
                val currentTitle = songCursor.getString(songTitle)
                val currentArtist = songCursor.getString(songArtist)
                val currentData = songCursor.getString(songData)
                val currentDate = songCursor.getLong(dateIndex)
                arrayList.add(Songs(currentId, currentTitle, currentArtist, currentData, currentDate))
            }
        }

        return arrayList
    }

    fun bottonFavSetup() {
        try {
            bottomBarClickHandler()
            sonTitle?.setText(SongPlayingFragment.staticate.currentSong.songTitle)
            SongPlayingFragment.staticate.mediaPlayer?.setOnCompletionListener({
                sonTitle?.setText(SongPlayingFragment.staticate.currentSong.songTitle)
                SongPlayingFragment.Staticated.onSongCompletion()
            })

            if (SongPlayingFragment.staticate.mediaPlayer?.isPlaying as Boolean) {
                playbtn?.setBackgroundResource(R.drawable.pause_icon)
                bottomBar?.visibility = View.VISIBLE
            } else {
                bottomBar?.visibility = View.INVISIBLE
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }


    }



    fun bottomBarClickHandler()
    {
        bottomBar?.setOnClickListener({
          Statified.mediaPlayer=SongPlayingFragment.staticate.mediaPlayer

            val songPlayingFragment=SongPlayingFragment()
            val args=Bundle()

            args.putString("songArtist", SongPlayingFragment.staticate.currentSong.songArtist)
            args.putString("path", SongPlayingFragment.staticate.currentSong.songPath)
            args.putString("songTitle", SongPlayingFragment.staticate.currentSong.songTitle)
            args.putInt("SongId",SongPlayingFragment.staticate.currentSong.songId.toInt() as Int)
            args.putInt("position", SongPlayingFragment.staticate.currentSong.trackPosition.toInt() as Int)
            args.putParcelableArrayList("songData", SongPlayingFragment.staticate.fetchsongs)
            args.putString("FavBottom", "success")
            songPlayingFragment.arguments=args
            fragmentManager?.beginTransaction()?.replace(R.id.details_fragment, songPlayingFragment)?.addToBackStack("SongPlayingFragment")?.commit()

        })


        playbtn?.setOnClickListener{(
                if(SongPlayingFragment.staticate.mediaPlayer?.isPlaying() as Boolean)
                {   SongPlayingFragment.staticate.mediaPlayer?.pause()
                    playbtn?.setBackgroundResource(R.drawable.play_icon)
                    trackPosition=SongPlayingFragment.staticate.mediaPlayer?.currentPosition as Int

                }
                else
                {
                    playbtn?.setBackgroundResource(R.drawable.pause_icon)
                    SongPlayingFragment.staticate.mediaPlayer?.seekTo(trackPosition)
                    SongPlayingFragment.staticate.mediaPlayer?.start()
                }
                )}
    }


    fun display_fav_by_searching()
    {
        refreshlist=ArrayList<Songs>()
        if((favcontnt?.checkSize() as Int >0))
    {         listdb?.clear()
        listdb=favcontnt?.queryDBlist()
        val fetchlist=getSongsFromPhone()
        if(fetchlist!= null)
        {
                  favadapter?.notifyDataSetChanged()
            for( i in 0..fetchlist.size -1) {


                for(j in 0.. listdb?.size as Int -1 ) {
                    if(listdb?.get(j)?.songId === fetchlist.get(i)?.songId) {
                        refreshlist?.add((listdb as ArrayList<Songs>)[j])
                    }
                }
            }
        }
        else
        {

        }

        if(refreshlist== null)
        {
            recylerView?.visibility=View.INVISIBLE
            nofav?.visibility=View.VISIBLE
        }
        else
        {

             favadapter= FavSreeenAdapter(refreshlist as ArrayList<Songs>, myactivity as Context )

             val layoutManager= LinearLayoutManager(activity)
            recylerView?.layoutManager=layoutManager
            recylerView?.itemAnimator=DefaultItemAnimator()
            recylerView?.adapter=favadapter


            recylerView?.setHasFixedSize(true)

        }
    } else
    {
        recylerView?.visibility=View.INVISIBLE
        nofav?.visibility=View.VISIBLE
    }


}


    }

