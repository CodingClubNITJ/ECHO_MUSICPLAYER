package com.intershala.echo.adapter
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.intershala.echo.*

class NavigationDrawerAdapter(_contentList:ArrayList<String>, _getImages: IntArray, _context: Context):RecyclerView.Adapter<NavigationDrawerAdapter.NavViewHolder>()
{
    var getImage:IntArray?=null
    var contentList:ArrayList<String>?=null
    var mContext:Context?=null
    init {
        this.getImage=_getImages
        this.contentList=_contentList
        this.mContext=_context
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): NavViewHolder {
        val itemView=LayoutInflater.from(p0.context).inflate(R.layout.row_custom_navigationdrawer, p0, false)
        val returnThis= NavViewHolder(itemView)
        return returnThis

    }

    override fun getItemCount(): Int {
        return 4

    }

    override fun onBindViewHolder(holder: NavViewHolder, position: Int) {
        holder.icon_GET?.setBackgroundResource(getImage?.get(position) as Int)
        holder.text_GET?.setText(contentList?.get(position))
        holder.content_holder?.setOnClickListener({
            if(position==0) {
                val mainScrenFragment=MainScreenFragment()
                (mContext as MainActivity).supportFragmentManager
                    .beginTransaction().replace(R.id.details_fragment, mainScrenFragment)
                    .commit()
            }
        else if(position==2)
            {
                val settingScrenFragment=SettingFragment()
                (mContext as MainActivity).supportFragmentManager
                    .beginTransaction().replace(R.id.details_fragment, settingScrenFragment)
                    .commit()
            }
        else if(position==1)
            {
                val favoriteFragment=FavoriteFragment()

                (mContext as MainActivity).supportFragmentManager
                    .beginTransaction().replace(R.id.details_fragment, favoriteFragment)
                    .commit()
            }
        else if(position==3)
            {
                val aboutFragment=AboutUsFragment()
                (mContext as MainActivity).supportFragmentManager
                    .beginTransaction().replace(R.id.details_fragment, aboutFragment)
                    .commit()
            }
        MainActivity.statified.drawerLayout?.closeDrawers()})

    }

    class NavViewHolder(itemView: View) : ViewHolder(itemView) {
        var icon_GET:ImageView?=null
        var text_GET:TextView?=null
        var content_holder:RelativeLayout?=null
        init{
            icon_GET= itemView.findViewById(R.id.icon_navdrawer)
            text_GET= itemView.findViewById(R.id.text_navdrawer)
            content_holder= itemView.findViewById(R.id.navdrawer_item_content_holder)
        }
    }
}
