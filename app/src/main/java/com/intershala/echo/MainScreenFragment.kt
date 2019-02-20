package com.intershala.echo


import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.intershala.echo.MainScreenFragment.Statified.mediaPlayer
import com.intershala.echo.MainScreenFragment.Statified.playbtn
import com.intershala.echo.MainScreenFragment.Statified.songTitle
import com.intershala.echo.adapter.MainScreenAdapter
import java.util.*


class MainScreenFragment : Fragment() {
    var bottomBar: RelativeLayout? = null

    var getSongsList:ArrayList<Songs>?=null
    var nowPlayingBottomBar: RelativeLayout? = null


    var visibleLayout: RelativeLayout? = null
    var noSongs: RelativeLayout? = null
    var recylerView: RecyclerView? = null
    var trackPosition:Int=0
    var myActivity: Activity? = null


var _mainScreenAdapter:MainScreenAdapter?=null

 object Statified
 { var songTitle: TextView? = null
     var mediaPlayer:MediaPlayer?=null
     var playbtn:ImageButton?=null
 }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
getSongsList=getSongsFromPhone()
        val prefs = activity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)
        val action_sort_ascending = prefs?.getString("action_sort_ascending", "true")
        val action_sort_recent = prefs?.getString("action_sort_recent", "false")



        _mainScreenAdapter= MainScreenAdapter(getSongsList as ArrayList<Songs>, myActivity as Context)
        val mLayoutManager=LinearLayoutManager(myActivity)
        recylerView?.layoutManager=mLayoutManager
        recylerView?.itemAnimator=DefaultItemAnimator()
        recylerView?.adapter=_mainScreenAdapter

        if (getSongsList != null) {
            if (action_sort_ascending!!.equals("true", ignoreCase = true)) {
                Collections.sort(getSongsList, Songs.Statified.nameComparator)
                _mainScreenAdapter?.notifyDataSetChanged()
            } else if (action_sort_recent!!.equals("true", ignoreCase = true)) {
                Collections.sort(getSongsList, Songs.Statified.dateComparator)
                _mainScreenAdapter?.notifyDataSetChanged()
            }
        }
bottonMainSetup()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_main_screen, container, false)
        setHasOptionsMenu(true)

        visibleLayout = view?.findViewById<RelativeLayout>(R.id.visibleLayout)
        noSongs = view?.findViewById<RelativeLayout>(R.id.noSongs)
        nowPlayingBottomBar = view?.findViewById<RelativeLayout>(R.id.hiddenBarMainScreen)
        songTitle = view?.findViewById<TextView>(R.id.songTitleMainScreen1)

        recylerView = view?.findViewById<RecyclerView>(R.id.contentMain)
        bottomBar = view?.findViewById(R.id.hiddenBarMainScreen)
        playbtn=view?.findViewById(R.id.playPauseButton1)

        return view
    }
    override fun onAttach(activity: Activity?) {

        super.onAttach(activity)
        myActivity = activity
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }




    fun getSongsFromPhone(): ArrayList<Songs> {
        var arrayList = ArrayList<Songs>()
        var contentResolver = myActivity?.contentResolver
        var songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var songCursor = contentResolver?.query(songUri, null, null, null, null)
        if (songCursor != null && songCursor.moveToFirst()) {
            val songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val dateIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)

            while (songCursor.moveToNext()) {
                var currentId = songCursor.getLong(songId)
                var currentTitle = songCursor.getString(songTitle)
                var currentArtist = songCursor.getString(songArtist)
                var currentData = songCursor.getString(songData)
                var currentDate = songCursor.getLong(dateIndex)
                arrayList.add(Songs(currentId, currentTitle, currentArtist, currentData, currentDate))
            }
        }
        songCursor?.close()
        return arrayList
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
        inflater?.inflate(R.menu.main, menu)
        return
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

var switch= item?.itemId
        if(switch== R.id.by_name)
        {
            val editor = myActivity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)?.edit()
            editor?.putString("action_sort_ascending", "true")
            editor?.putString("action_sort_recent", "false")
            editor?.apply()
            if (getSongsList != null) {
                Collections.sort(getSongsList, Songs.Statified.nameComparator)



            }
            _mainScreenAdapter?.notifyDataSetChanged()
        }
       else if(switch==R.id.by_recently_added)



                {
                    val editor= myActivity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)?.edit()
                    editor?.putString("action_sort_ascending", "false")
                    editor?.putString("action_sort_recent", "true")
                    editor?.apply()
                    if(getSongsList!=null)
                    {
                        Collections.sort(getSongsList, Songs.Statified.dateComparator)
                    }

                    _mainScreenAdapter?.notifyDataSetChanged()
                    return false
                }





        return super.onOptionsItemSelected(item)
    }

    fun bottonMainSetup() {
        try {
            bottomBarClickHandler()
            songTitle?.setText(SongPlayingFragment.staticate.currentSong.songTitle)
            SongPlayingFragment.staticate.mediaPlayer?.setOnCompletionListener({
                songTitle?.setText(SongPlayingFragment.staticate.currentSong.songTitle)
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
            args.putString("MainBottom", "success")
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


}
