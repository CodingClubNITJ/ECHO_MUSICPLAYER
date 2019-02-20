package com.intershala.echo



import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.CompoundButton
import android.widget.Switch
import kotlinx.android.synthetic.main.fragment_setting.*



class SettingFragment : Fragment() {
 var myactivity:Activity?=null
    var switch:Switch?=null
    object statified
    {
        val MY_PREF= "ShakeFeature"
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         val view=inflater!!.inflate(R.layout.fragment_setting, container, false)
activity?.title="Settings"
setHasOptionsMenu(true)
        switch=view?.findViewById(R.id.shakeswitch)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myactivity= context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myactivity=activity
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val pref=myactivity?.getSharedPreferences(statified.MY_PREF,Context.MODE_PRIVATE)
        val isallowed=pref?.getBoolean("feature", false)
        if(isallowed as Boolean)
        {
            shakeswitch?.isChecked=true
        }
        else
        {
            shakeswitch?.isChecked=false
        }
        shakeswitch?.setOnCheckedChangeListener({ CompoundButton, b->
            if(b)
            { val editor=myactivity?.getSharedPreferences(statified.MY_PREF, Context.MODE_PRIVATE)?.edit()
            editor?.putBoolean("feature", true)
                editor?.apply()
            }
            else
            {val editor=myactivity?.getSharedPreferences(statified.MY_PREF, Context.MODE_PRIVATE)?.edit()
                editor?.putBoolean("feature", false)
                editor?.apply()

            }

        })

    }





}
