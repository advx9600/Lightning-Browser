package acr.browser.lightning.database.textSizeSomePage

import io.reactivex.Completable
import io.reactivex.Single

data class TextSizeSomePageEntry (
        var url:String,
        var textSize: Int
)

interface TextSizeSomePageRepository{
    fun getChanged():Boolean
    fun setChanged(change:Boolean)
    fun getTextZoomFromTextSize(textSize:Int):Int
    fun allData(): Single<List<TextSizeSomePageEntry>>
    fun findIndexOf(template:List<TextSizeSomePageEntry>,url:String):Int
    fun addData(data:TextSizeSomePageEntry):Completable
    fun delData(data:TextSizeSomePageEntry):Completable
}