package acr.browser.lightning.database.textSizeSomePage

import acr.browser.lightning.database.databaseDelegate
import acr.browser.lightning.di.MainScheduler
import acr.browser.lightning.extensions.useMap
import android.app.Application
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.text.TextUtils
import android.util.Log
import androidx.annotation.MainThread
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@MainThread
class TextSizeSomePageDatabase @Inject constructor(app:Application):
        SQLiteOpenHelper(app, DATABASE_NAME, null, DATABASE_VERSION),TextSizeSomePageRepository{
    private var changed :Boolean = false
    private val database: SQLiteDatabase by databaseDelegate()

    override fun getChanged(): Boolean {
        return  changed
    }

    override fun setChanged(target: Boolean) {
        this.changed = target
    }

    override fun getTextZoomFromTextSize(textSize: Int): Int {
        var zoom = 100
        when(textSize){
            1->zoom= 120
            2->zoom = 140
            3->zoom = 160
            4->zoom = 180
            5->zoom = 200
        }
        return  zoom
    }

    private fun Cursor.bindToTextSizeItem() = TextSizeSomePageEntry(
        url = getString(1),
        textSize = getInt(2)
    )
    override fun allData(): Single<List<TextSizeSomePageEntry>> = Single.fromCallable(){
        database.query(TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null)
                .useMap { it.bindToTextSizeItem() }
    }

    override fun findIndexOf(list:List<TextSizeSomePageEntry>,target:String):Int{
        var ret = -1
        if (list.isNotEmpty() && !TextUtils.isEmpty(target)){
            for(item in list){
                if (TextUtils.isEmpty(item.url) || !item.url.contains('/') || item.url.endsWith("/")) continue

                val url1 = item.url.substring(0,item.url.lastIndexOf('/'))
                val url2 = item.url.substring(item.url.lastIndexOf('/')+1)
                if (!TextUtils.isEmpty(url1)
                        && !TextUtils.isEmpty(url2)
                        && target.startsWith(url1)
                        && target.length > url1.length+1){
                    val remain = target.substring(url1.length+1)
                    if (!TextUtils.isEmpty(remain)){
                        var dotNum =0
                        for(c in remain)
                            if (c == '.') dotNum++

                        var dotNum2 = 0
                        for (c in url2)
                            if (c == '.') dotNum2++
                        if (dotNum == dotNum2){
                            ret = list.indexOf(item)
                            break
                        }
                    }
                }
            }
        }
        return ret
    }

    override fun addData(data: TextSizeSomePageEntry): Completable = Completable.fromAction(){
        val values = ContentValues().apply {
            put(KEY_URL,data.url)
            put(KEY_TEXT_SIZE,data.textSize)
        }
        database.insert(TABLE_NAME,null,values)
    }

    override fun delData(data: TextSizeSomePageEntry): Completable = Completable.fromAction(){
        database.delete(TABLE_NAME,"$KEY_URL = ?", arrayOf(data.url))
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("create table $TABLE_NAME (" +
                "$KEY_ID INTEGER PRIMARY KEY," +
                "$KEY_URL VARCHAR," +
                "$KEY_TEXT_SIZE INTEGER"+
                ")")
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL("drop table if exists $TABLE_NAME")
        onCreate(db)
    }

    companion object{
        private const val DATABASE_NAME = "textSizeSomePage.db"
        private const val TABLE_NAME = "textSizeSomePage"
        private const val DATABASE_VERSION = 1
        private const val KEY_ID = "ID"
        private const val KEY_URL = "url"
        private const val KEY_TEXT_SIZE = "text_size"
    }
}