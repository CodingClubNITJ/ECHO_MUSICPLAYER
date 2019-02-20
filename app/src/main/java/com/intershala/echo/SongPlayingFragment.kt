package com.intershala.echo


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.hardware.*
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.*
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import com.intershala.echo.Database.database
import com.intershala.echo.SongPlayingFragment.Staticated.MY_PREFS_LOOP
import com.intershala.echo.SongPlayingFragment.Staticated.MY_PREFS_SHUFFLE
import com.intershala.echo.SongPlayingFragment.Staticated.onSongCompletion
import com.intershala.echo.SongPlayingFragment.Staticated.playNext
import com.intershala.echo.SongPlayingFragment.Staticated.processInfo
import com.intershala.echo.SongPlayingFragment.Staticated.setText
import com.intershala.echo.SongPlayingFragment.staticate.MY_PREF
import com.intershala.echo.SongPlayingFragment.staticate.audioVizualization
import com.intershala.echo.SongPlayingFragment.staticate.currentSong
import com.intershala.echo.SongPlayingFragment.staticate.currentposition
import com.intershala.echo.SongPlayingFragment.staticate.endTimerText
import com.intershala.echo.SongPlayingFragment.staticate.favbtn
import com.intershala.echo.SongPlayingFragment.staticate.favcontent
import com.intershala.echo.SongPlayingFragment.staticate.fetchsongs
import com.intershala.echo.SongPlayingFragment.staticate.glview
import com.intershala.echo.SongPlayingFragment.staticate.loopbtn
import com.intershala.echo.SongPlayingFragment.staticate.mSensorListener
import com.intershala.echo.SongPlayingFragment.staticate.mSensorManager
import com.intershala.echo.SongPlayingFragment.staticate.mediaPlayer
import com.intershala.echo.SongPlayingFragment.staticate.myActivity
import com.intershala.echo.SongPlayingFragment.staticate.nextbtn
import com.intershala.echo.SongPlayingFragment.staticate.playpauseButton
import com.intershala.echo.SongPlayingFragment.staticate.prevbtn
import com.intershala.echo.SongPlayingFragment.staticate.seekbar
import com.intershala.echo.SongPlayingFragment.staticate.shufflebtn
import com.intershala.echo.SongPlayingFragment.staticate.songArtistView
import com.intershala.echo.SongPlayingFragment.staticate.songTitleView
import com.intershala.echo.SongPlayingFragment.staticate.startTimerText
import com.intershala.echo.SongPlayingFragment.staticate.upadteTime
import kotlinx.android.synthetic.main.fragment_song_playing.view.*
import org.w3c.dom.Text
import java.util.*
import java.util.concurrent.TimeUnit


class SongPlayingFragment : Fragment() {


object staticate {
    var myActivity: Activity? = null
    var mediaPlayer: MediaPlayer?=null

    var startTimerText: TextView? = null
    var endTimerText: TextView? = null
    var playpauseButton: ImageButton? = null
    var nextbtn: ImageButton? = null
    var prevbtn: ImageButton? = null
    var shufflebtn: ImageButton? = null
    var loopbtn: ImageButton? = null
    var seekbar: SeekBar? = null
    var songTitleView: TextView? = null
    var songArtistView: TextView? = null
    var currentposition: Int = 0
    var fetchsongs: ArrayList<Songs>? = null
    var currentSong = CurrentSongHelper()
    var audioVizualization: AudioVisualization? = null
    var glview: GLAudioVisualizationView? = null
    var favbtn: ImageButton? = null
    var favcontent: database? = null
    var mSensorManager: SensorManager? = null
    var mSensorListener: SensorEventListener? = null
    var MY_PREF = "ShakeFeature"

    var upadteTime = object : Runnable {
        override fun run() {
            val getcurrent = mediaPlayer?.currentPosition
            var t = TimeUnit.MILLISECONDS.toMinutes(getcurrent?.toLong() as Long) * 60
            startTimerText?.setText(
                String.format(
                    "%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(getcurrent?.toLong() as Long),
                    (TimeUnit.MILLISECONDS.toSeconds(getcurrent?.toLong()) - t as Long)


                )
            )
            seekbar?.setProgress(getcurrent?.toInt() as Int)
            Handler().postDelayed(this, 1000)
        }

    }

}


    object Staticated {
        var MY_PREFS_SHUFFLE = "Shufflefeature"
        var MY_PREFS_LOOP = "Loopfeature"


        fun playNext(check: String) {
            if (check.equals("playNextNormal", true)) {
                currentposition = currentposition + 1
            } else if (check.equals("playnextlikenormalshuffle", true)) {
                val random = Random()
                val randompos = random.nextInt(fetchsongs?.size?.plus(1) as Int)
                currentposition = randompos

            }

            val t = fetchsongs?.size
            if (currentposition == t ) {
                currentposition = 0
            }
            currentSong.isloop = false
            val nextsong = fetchsongs?.get(currentposition)
            currentSong.songPath = nextsong?.songData
            currentSong.songId = nextsong?.songId as Long
            currentSong.songTitle = nextsong.songTitle
           currentSong.songArtist = nextsong.artist
            currentSong.trackPosition= currentposition
            setText(currentSong.songTitle as String, currentSong.songArtist as String)
           mediaPlayer?.reset()
            try {

              mediaPlayer?.setDataSource(
               myActivity as Context,
                    Uri.parse(currentSong.songPath)
                )
           mediaPlayer?.prepare()
            mediaPlayer?.start()
                processInfo(mediaPlayer as MediaPlayer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
                if (favcontent?.checkIfIdexist(currentSong.songId.toInt() as Int) as Boolean) {
                favbtn?.setImageResource(R.drawable.favorite_on)
                } else {
                   favbtn?.setImageResource(R.drawable.favorite_off)
                }


        }

        fun onSongCompletion() {
            if (staticate.currentSong.isshuffle ) {
                currentSong.isplaying = true
                playNext("playnextlikenormalshuffle")

            } else {
                if (currentSong.isloop ) {
                    currentSong.isplaying = true

                    currentSong.isplaying = true
                    val nextSong = fetchsongs?.get(currentposition)
                    currentSong.trackPosition = currentposition
                    currentSong.songPath = nextSong?.songData
                    currentSong.songTitle = nextSong?.songTitle
                    currentSong.songArtist = nextSong?.artist
                    currentSong.songId = nextSong?.songId as Long
                    mediaPlayer?.reset()
                    try {
                        setText(currentSong.songTitle as String, currentSong.songArtist as String)
                       mediaPlayer?.setDataSource(
                            myActivity as Context,
                            Uri.parse(currentSong.songPath)
                        )
                        mediaPlayer?.prepare()
                       mediaPlayer?.start()
                        processInfo(staticate.mediaPlayer as MediaPlayer)
                        if (favcontent?.checkIfIdexist(currentSong.songId.toInt() as Int) as Boolean) {
                            favbtn?.setImageResource(R.drawable.favorite_on)
                        } else {
                            favbtn?.setImageResource(R.drawable.favorite_off)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                } else {
                   currentSong.isplaying = true
                    playNext("playNextNormal")
                }
            }


        }

        fun setText(title: String, artist: String) {
            var temp:String=artist
            if(artist?.equals("<unknown", true))
            {
                temp="unknown"
            }
            var temp2:String=title
            if(title?.equals("<unknown>", true))
            {
                temp2="unknown"
            }
           songArtistView?.setText(temp)
           songTitleView?.setText(temp2)
        }


        fun processInfo(media: MediaPlayer) {
            val finalTime = media.duration
            val start = media.currentPosition
           seekbar?.max = finalTime

            val tt = TimeUnit.MILLISECONDS.toMinutes(start.toLong()) * 60
           startTimerText?.setText(
                String.format(
                    "%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(start.toLong()),
                    (TimeUnit.MILLISECONDS.toSeconds(start.toLong()) -
                            tt*60))
                )

            val t = TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()) * 60

          endTimerText?.setText(
                String.format(
                    "%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()),
                    (TimeUnit.MILLISECONDS.toSeconds(finalTime.toLong()) - t)
                )
            )

           seekbar?.setProgress(start)
            Handler().postDelayed(upadteTime, 1000)
        }

    }

    var mAccelerartion:Float=0f
    var mAccelerationCurrent:Float=0f
    var mAccelerationLast=0f



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_song_playing, container, false)
   activity?.title="Now Playing"
        setHasOptionsMenu(true)
     startTimerText = view.findViewById(R.id.startTime)
  endTimerText = view.findViewById(R.id.endTime)

   playpauseButton = view.findViewById(R.id.playpausebutton)

    nextbtn = view.findViewById(R.id.playforward)
     prevbtn = view.findViewById(R.id.playback)

      shufflebtn = view.findViewById(R.id.shuffle)
      loopbtn = view.findViewById(R.id.loop)
        seekbar = view.findViewById(R.id.seekbar)

    songTitleView = view.findViewById(R.id.songtitle)
       songArtistView = view.findViewById(R.id.songartist)
      glview = view.findViewById(R.id.visualizer_view)
        favbtn = view.findViewById(R.id.favoriteicon)
   favbtn?.alpha = 0.8f



        return view

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
      myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
    myActivity = activity
    }
    override fun onResume() {
        super.onResume()
        audioVizualization?.onResume()
        mSensorManager?.registerListener(mSensorListener ,
            mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_FASTEST)
    }

    override fun onPause() {
        super.onPause()
        audioVizualization?.onPause()
        mSensorManager?.unregisterListener(mSensorListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        audioVizualization?.release()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

  audioVizualization = glview as AudioVisualization
    }






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*Sensor service is activate when the fragment is created*/
      mSensorManager = staticate.myActivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        /*Default values*/
       mAccelerartion = 0.0f
        /*We take earth's gravitational value to be default, this will give us good results*/
        mAccelerationCurrent = SensorManager.GRAVITY_EARTH
        mAccelerationLast = SensorManager.GRAVITY_EARTH
        /*Here we call the function*/
        bindShakeList()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {

        menu?.clear()
        inflater?.inflate(R.menu.menu_song,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item:MenuItem?=menu?.findItem(R.id.action_redirect)
        item?.isVisible=true

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId)
        {
            R.id.action_redirect->{
             myActivity?.onBackPressed()
                return false
            }
        }
        return false
    }







    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    favcontent = database((myActivity))



        currentSong = CurrentSongHelper()
        currentSong.isplaying = true
      currentSong.isloop = false
       currentSong.isshuffle = false
        playpauseButton?.setBackgroundResource(R.drawable.pause_icon)


        var path: String? = null
        var songTitle: String? = null
        var songArtist: String? = null
        var songId: Long? = null


        try {
            path = arguments?.getString("path")
            songTitle = arguments?.getString("songTitle")
            songArtist = arguments?.getString("songArtist")
            songId = arguments?.getInt("SongId")?.toLong()
            currentposition = arguments!!.getInt("position")  //change
            fetchsongs = arguments?.getParcelableArrayList("songData")
            currentSong.songPath = path
            currentSong.songArtist = songArtist
            currentSong.songTitle = songTitle
            currentSong.songId = songId as Long
            currentSong.trackPosition = currentposition as Int


        } catch (e: Exception) {
            e.printStackTrace()
        }

val fromFav=arguments?.get("FavBottom") as? String
        val fromMain=arguments?.get("MainBottom") as? String

        if(fromFav!=null)
        {    setText(currentSong.songTitle as String, currentSong.songArtist as String)
            mediaPlayer=FavoriteFragment.Statified.mediaPlayer
            if(mediaPlayer?.isPlaying as Boolean)
            {
                playpauseButton?.setBackgroundResource(R.drawable.pause_icon)
            }
            else
            {
                playpauseButton?.setBackgroundResource(R.drawable.play_icon)
            }
        }
        else if( fromMain!=null)
        {  setText(currentSong.songTitle as String, currentSong.songArtist as String)
            mediaPlayer=MainScreenFragment.Statified.mediaPlayer

            if(mediaPlayer?.isPlaying as Boolean)
            {
                playpauseButton?.setBackgroundResource(R.drawable.pause_icon)
            }
            else
            {
                playpauseButton?.setBackgroundResource(R.drawable.play_icon)
            }

        }
        else
        {

            mediaPlayer= MediaPlayer()
            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)

            try {


                setText(currentSong.songTitle as String, currentSong.songArtist as String)
                mediaPlayer?.setDataSource(myActivity as Context, Uri.parse(path))
                mediaPlayer?.prepare()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            mediaPlayer?.start()
        }


        processInfo(mediaPlayer as MediaPlayer)

        if (currentSong?.isplaying as Boolean) {
            playpauseButton?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            playpauseButton?.setBackgroundResource(R.drawable.play_icon)
        }


       mediaPlayer?.setOnCompletionListener {

           onSongCompletion()
        }
        clickHandler()

        val vizHandler = DbmHandler.Factory.newVisualizerHandler(myActivity as Context, 0)
        audioVizualization?.linkTo(vizHandler)


        val preforShuffle =

            myActivity?.getSharedPreferences(MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)
        val isShflallwd = preforShuffle?.getBoolean("feature", false)
        if (isShflallwd as Boolean) {
            currentSong.isloop = false
            currentSong.isshuffle=true
            shufflebtn?.setBackgroundResource(R.drawable.shuffle_icon)
            loopbtn?.setBackgroundResource(R.drawable.loop_white_icon)
        } else {
          currentSong.isshuffle = false
            shufflebtn?.setBackgroundResource(R.drawable.shuffle_white_icon)
        }

        var preforLoop = staticate.myActivity?.getSharedPreferences(MY_PREFS_LOOP, Context.MODE_PRIVATE)
        val isLooplallwd = preforLoop?.getBoolean("feature", false)

        if (isLooplallwd as Boolean) {
            currentSong.isshuffle = false
            currentSong.isloop = true
            shufflebtn?.setBackgroundResource(R.drawable.shuffle_white_icon)
           loopbtn?.setBackgroundResource(R.drawable.loop_icon)
        } else {
            currentSong.isloop = false
           loopbtn?.setBackgroundResource(R.drawable.loop_white_icon)
        }
        if (favcontent?.checkIfIdexist(currentSong.songId.toInt() as Int ) as Boolean) {
            favbtn?.setImageResource(R.drawable.favorite_on)
        } else {
            favbtn?.setImageResource(R.drawable.favorite_off)
        }
    }





    fun clickHandler() {
       favbtn?.setOnClickListener({
            if (favcontent?.checkIfIdexist(staticate.currentSong.songId.toInt() as Int) as Boolean) {
                favbtn?.setImageResource(R.drawable.favorite_off)
                favcontent?.deleteid(currentSong.songId.toInt() as Int)
                Toast.makeText(staticate.myActivity, "Deleted from favorites", Toast.LENGTH_SHORT).show()
            } else {
               favbtn?.setImageResource(R.drawable.favorite_on)
                favcontent?.storeAsFavorite(
                    currentSong.songId.toInt(),
                  currentSong.songArtist,
                   currentSong.songTitle,
                  currentSong.songPath
                )
                Toast.makeText(staticate.myActivity, "Added to favorites", Toast.LENGTH_SHORT).show()
            }

        })


      playpauseButton?.setOnClickListener({
            if (mediaPlayer?.isPlaying as Boolean) {
                mediaPlayer?.pause()
                currentSong.isplaying = false
                playpauseButton?.setBackgroundResource(R.drawable.play_icon)
            } else {
                staticate.mediaPlayer?.start()
                staticate.currentSong.isplaying = true
                staticate.playpauseButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        });
        staticate.nextbtn?.setOnClickListener({

        currentSong.isplaying = true
           playpauseButton?.setBackgroundResource(R.drawable.pause_icon)
            if (currentSong.isshuffle as Boolean) {
                playNext("playnextlikenormalshuffle")
            } else {
                playNext("playNextNormal")
            }

        })
        prevbtn?.setOnClickListener({
           currentSong.isplaying = true
            if (currentSong.isloop as Boolean) {
                loopbtn?.setBackgroundResource(R.drawable.loop_white_icon)
            }
            playprev()
        })

        shufflebtn?.setOnClickListener({
            val editorShuffle =
               myActivity?.getSharedPreferences(MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()
            val editorLoop =
               myActivity?.getSharedPreferences(MY_PREFS_LOOP, Context.MODE_PRIVATE)?.edit()

            if (currentSong.isshuffle as Boolean) {
                currentSong.isshuffle = false
               shufflebtn?.setBackgroundResource(R.drawable.shuffle_white_icon)
                editorShuffle?.putBoolean("feature", false)
                editorShuffle?.apply()
            } else {
                currentSong.isshuffle = true
                currentSong.isloop = false
              shufflebtn?.setBackgroundResource(R.drawable.shuffle_icon)
                loopbtn?.setBackgroundResource(R.drawable.loop_white_icon)
                editorShuffle?.putBoolean("feature", true)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature", false)
                editorLoop?.apply()
            }


        })
      loopbtn?.setOnClickListener({

            val editorShuffle =
               myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()
            val editorLoop =
               myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)?.edit()
            if (currentSong.isloop as Boolean) {
                currentSong.isloop = false
                loopbtn?.setBackgroundResource(R.drawable.loop_white_icon)
                editorLoop?.putBoolean("feature", false)
                editorLoop?.apply()
            } else {
               currentSong.isloop = true
                currentSong.isshuffle = false
                loopbtn?.setBackgroundResource(R.drawable.loop_icon)
               shufflebtn?.setBackgroundResource(R.drawable.shuffle_white_icon)
                editorShuffle?.putBoolean("feature", false)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature", true)
                editorLoop?.apply()
            }
        })


    }

    fun playprev() {

  currentposition = currentposition - 1
        if (currentposition == -1) {
           currentposition = 0
        }
        if (currentSong.isplaying) {
           playpauseButton?.setBackgroundResource(R.drawable.pause_icon)
        } else {
           playpauseButton?.setBackgroundResource(R.drawable.play_icon)
        }
      currentSong.isloop = false

        val nextsong = fetchsongs?.get(currentposition as Int)
        currentSong.songPath = nextsong?.songData
        currentSong.songId = nextsong?.songId as Long
       currentSong.songTitle = nextsong?.songTitle
    currentSong.songArtist = nextsong?.artist

        mediaPlayer?.reset()
        try {
            setText(
                currentSong.songTitle as String,
                currentSong.songArtist as String
            )
            mediaPlayer?.setDataSource(
                myActivity as Context,
                Uri.parse(staticate.currentSong.songPath)
            )
            mediaPlayer?.prepare()
         mediaPlayer?.start()
           processInfo(mediaPlayer as MediaPlayer)
            if (favcontent?.checkIfIdexist(staticate.currentSong.songId.toInt() as Int) as Boolean) {
               favbtn?.setImageResource(R.drawable.favorite_on)
            } else {
                favbtn?.setImageResource(R.drawable.favorite_off)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    fun bindShakeList() {


        staticate.mSensorListener = object : SensorEventListener, SensorListener {
            override fun onSensorChanged(p0: Int, p1: FloatArray?) {

            }

            override fun onAccuracyChanged(p0: Int, p1: Int) {
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {


            }
            override fun onSensorChanged(event: SensorEvent) {


                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]


                mAccelerationLast = mAccelerationCurrent


                mAccelerationCurrent = Math.sqrt(((x * x + y * y + z * z).toDouble())).toFloat()


                val delta = mAccelerationCurrent - mAccelerationLast


                mAccelerartion = mAccelerartion * 0.9f + delta


                if (mAccelerartion > 12) {


                    val prefs = myActivity?.getSharedPreferences(MY_PREF, Context.MODE_PRIVATE)
                    val isAllowed = prefs?.getBoolean("feature", false)
                    if (isAllowed as Boolean) {
                       playNext("PlayNextNormal")
                    }
                }
            }
        }
    }
}

