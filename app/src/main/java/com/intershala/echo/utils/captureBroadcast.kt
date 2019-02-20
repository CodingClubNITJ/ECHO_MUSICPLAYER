package com.intershala.echo.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.intershala.echo.*
import java.lang.Exception
import java.security.spec.ECField

class  captureBroadcast: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {

        if (p1?.action == Intent.ACTION_NEW_OUTGOING_CALL) {
            try {  MainActivity.statified.notificationManager?.cancel(1987)

            }catch (e:Exception)
            {
                e.printStackTrace()
            }
            try {
                SongPlayingFragment.staticate.mediaPlayer?.pause()
                SongPlayingFragment.staticate.playpauseButton?.setBackgroundResource(R.drawable.play_icon)
                FavoriteFragment.Statified.playbtn?.setBackgroundResource(R.drawable.play_icon)
MainScreenFragment.Statified.playbtn?.setBackgroundResource(R.drawable.play_icon)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        else
        {
            val tm:TelephonyManager=p0?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            when(tm?.callState)
            {
                TelephonyManager.CALL_STATE_RINGING->
                { try {  MainActivity.statified.notificationManager?.cancel(1987)

                }catch (e:Exception)
                {
                    e.printStackTrace()
                }


                    try {

                            SongPlayingFragment.staticate.mediaPlayer?.pause()
                            SongPlayingFragment.staticate.playpauseButton?.setBackgroundResource(R.drawable.play_icon)
FavoriteFragment.Statified.playbtn?.setBackgroundResource(R.drawable.play_icon)
                        MainScreenFragment.Statified.playbtn?.setBackgroundResource(R.drawable.play_icon)
                    }catch (e:Exception)
                    {
                        e.printStackTrace()
                    }
                }
                else->
                {

                }
            }
        }

    }
}