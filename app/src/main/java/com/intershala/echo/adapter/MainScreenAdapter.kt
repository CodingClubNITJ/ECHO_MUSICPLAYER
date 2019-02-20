package com.intershala.echo.adapter

import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.intershala.echo.*
import kotlinx.android.synthetic.main.row_custom_mainscreen_adapter.view.*
import java.lang.reflect.Array

class MainScreenAdapter(_songDetails: ArrayList<Songs>,  _context: Context): RecyclerView.Adapter<MainScreenAdapter.MyViewHolder>(){
    var songDetails : ArrayList<Songs>?=null
    var mContext: Context?=null
    var c:Int=0
    init
    {
        this.songDetails=_songDetails
        this.mContext=_context
    }

    override fun onBindViewHolder(p0: MainScreenAdapter.MyViewHolder, p1: Int) {
var songObject=songDetails?.get(p1)
        p0.trackTitle?.text=songObject?.songTitle
        p0.trackArtist?.text=songObject?.artist

        p0.contentHolder?.setOnClickListener({


            try{
                if(SongPlayingFragment.staticate.mediaPlayer?.isPlaying as Boolean)
                {
                    SongPlayingFragment.staticate.mediaPlayer?.pause()

                }
            }catch (e:Exception)
            { e.printStackTrace()
            }
            var args = Bundle()

            val songplaying = SongPlayingFragment()
            args.putString("songArtist", songObject?.artist)
            args.putString("path", songObject?.songData)
            args.putString("songTitle", songObject?.songTitle)
            args.putInt("SongId", songObject?.songId?.toInt() as Int)
            args.putInt("position", p1)
            args.putParcelableArrayList("songData", songDetails)
            songplaying.arguments = args


            c=c+1
            (mContext as FragmentActivity).supportFragmentManager
                .beginTransaction().replace(R.id.details_fragment, songplaying)
                .addToBackStack("SongPlayingFragment")
                .commit()
        })
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MainScreenAdapter.MyViewHolder {
        var itemView=LayoutInflater.from(p0?.context)
            .inflate(R.layout.row_custom_mainscreen_adapter,p0, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        if(songDetails==null) {
            return 0
        }
        else
            return (songDetails as ArrayList).size
    }


    class MyViewHolder( view: View) : RecyclerView.ViewHolder(view)
    {
        var trackTitle: TextView?=null
        var trackArtist:TextView?=null
    var contentHolder: RelativeLayout?=null
        init {
            trackTitle=view.findViewById<TextView>(R.id.tracktitle)
            trackArtist=view.findViewById<TextView>(R.id.trackartist)
            contentHolder=view.findViewById<RelativeLayout>(R.id.contentrow)

        }
    }
}