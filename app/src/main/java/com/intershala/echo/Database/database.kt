package com.intershala.echo.Database


import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.intershala.echo.Database.database.staticated.COL_ARTIST
import com.intershala.echo.Database.database.staticated.COL_ID
import com.intershala.echo.Database.database.staticated.COL_PATH
import com.intershala.echo.Database.database.staticated.COL_TITLE
import com.intershala.echo.Database.database.staticated.DB_NAME
import com.intershala.echo.Database.database.staticated.TABLE_NAME
import com.intershala.echo.Database.database.staticated.VAL
import com.intershala.echo.Database.database.staticated.songlist
import com.intershala.echo.Songs

class database: SQLiteOpenHelper {


    object staticated {
        var songlist=  ArrayList<Songs>()
        val DB_NAME = "FavouriteDatabase"
        val TABLE_NAME = "FavouriteTable"
        val COL_ID = "SongId"
        val COL_TITLE = "SongTitle"
        val COL_ARTIST = "SongArtist"
        val COL_PATH = "SongPath"
        var VAL = 1

    }

    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL("CREATE TABLE "+ TABLE_NAME +"("+ COL_ID + " INTEGER,"+ COL_ARTIST+ " STRING," +COL_TITLE+ " STRING," + COL_PATH +" STRING);"
        )


    }
    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }

    constructor(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : super(
        context,
        name,
        factory,
        version
    ) {}
    constructor(context: Context?) : super(
        context,
        DB_NAME,
        null,
        VAL

    )







    fun storeAsFavorite(id:Int?, artist:String?, songTitle:String?, path:String?)
    {
        val db=this.writableDatabase
        val contentvalues=ContentValues()
        contentvalues.put(COL_ID,id)
        contentvalues.put(COL_ARTIST, artist)
        contentvalues.put(COL_TITLE, songTitle)
        contentvalues.put(COL_PATH, path)
        db.insert(TABLE_NAME, null, contentvalues)
        db.close()

    }
    fun queryDBlist(): ArrayList<Songs>?
    {
        try{
            val db=this.readableDatabase

            val query= "SELECT * FROM "+ TABLE_NAME
            val csor=db.rawQuery(query, null)


            if(csor.moveToFirst())
            {

                do {

                    var id=csor.getInt(csor.getColumnIndexOrThrow(COL_ID))
                    var artist=csor.getString(csor.getColumnIndexOrThrow(COL_ARTIST))
                    var title=csor.getString(csor.getColumnIndexOrThrow(COL_TITLE))
                    var path= csor.getString(csor.getColumnIndexOrThrow(COL_PATH))
                    songlist.add(Songs(id.toLong(), title, artist, path,0))
                }while(csor.moveToNext())
            }
            else
            {
                return  null
            }

        }catch (e:Exception)
        {
            e.printStackTrace()
        }
        return songlist

    }

    fun checkIfIdexist(id:Int):Boolean
    {
        var stid=-1090
        val db=this.readableDatabase
        val query="SELECT * FROM "+ TABLE_NAME+ " WHERE SongId ='$id'"
        val csor=db.rawQuery(query, null)
        if(csor.moveToFirst())
        {
            do{
                stid=csor.getInt(csor.getColumnIndexOrThrow(COL_ID))
            }
            while (csor.moveToNext())

        }else
        {
            return false
        }

        return stid!=-1090
    }



    fun deleteid(id :Int)
    {
        val db=this.writableDatabase
        db.delete(TABLE_NAME, COL_ID+ "="+id, null)
        db.close()
    }


    fun checkSize():Int
    {
        var counter:Int=0

        val db=this.readableDatabase
        val query="SELECT * FROM "+TABLE_NAME
        val csor=db.rawQuery(query, null)
        if(csor.moveToFirst())
        {
            do{

                counter= counter+1
            }
            while (csor.moveToNext())

        }else
        {
            return 0
        }
        csor.close()
        return counter
    }

}
