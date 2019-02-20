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
import com.intershala.echo.R
import com.intershala.echo.SongPlayingFragment
import com.intershala.echo.Songs

class FavSreeenAdapter(_songDetails: ArrayList<Songs>, _context: Context): RecyclerView.Adapter<FavSreeenAdapter.MyViewHolder>(){


    var songDetails : ArrayList<Songs>?=null
    var mContext: Context?=null
    init
    {
        this.songDetails=_songDetails
        this.mContext=_context
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    override fun onBindViewHolder(p0: FavSreeenAdapter.MyViewHolder, p1: Int) {
        val songObject=songDetails?.get(p1)
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



            val songplaying= SongPlayingFragment()
            val args= Bundle()
            args.putString("songArtist", songObject?.artist)
            args.putString("path", songObject?.songData)
            args.putString("songTitle", songObject?.songTitle)
            args.putInt("SongId", songObject?.songId?.toInt() as Int)
            args.putInt("position", p1)
            args.putParcelableArrayList("songData", songDetails)
            songplaying.arguments=args
            (mContext as FragmentActivity).supportFragmentManager
                .beginTransaction().replace(R.id.details_fragment, songplaying)
                .addToBackStack("SongPlayingFragmentFavorite")
                .commit()
        })
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): FavSreeenAdapter.MyViewHolder {
        val itemView= LayoutInflater.from(p0.context)
            .inflate(R.layout.row_custom_mainscreen_adapter,p0, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        if(songDetails==null) {
            return 0
        }
        else
        { return (songDetails as ArrayList<Songs>).size}
    }


    class MyViewHolder( view: View) : RecyclerView.ViewHolder(view)
    {
        var trackTitle: TextView?=null
        var trackArtist: TextView?=null
        var contentHolder: RelativeLayout?=null
        init {
            trackTitle=view.findViewById(R.id.tracktitle) as TextView
            trackArtist=view.findViewById(R.id.trackartist) as TextView
            contentHolder=view.findViewById(R.id.contentrow) as RelativeLayout

        }
    }
}