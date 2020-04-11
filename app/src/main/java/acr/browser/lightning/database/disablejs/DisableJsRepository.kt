package acr.browser.lightning.database.disablejs

import io.reactivex.Completable
import io.reactivex.Single

interface DisableJsRepository{

    fun allDisableJsListItems(): Single<List<DisableJsEntry>>

    fun addDisableJsItem(item:DisableJsEntry): Completable

    fun removeDisableJsItem(item:DisableJsEntry): Completable

    fun getDataChanged() :Boolean

    fun setDataChanged(changed:Boolean)
}