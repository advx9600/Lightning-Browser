package acr.browser.lightning.database.disablejs

import acr.browser.lightning.database.databaseDelegate
import acr.browser.lightning.extensions.useMap
import android.app.Application
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.annotation.WorkerThread
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@WorkerThread
class DisableJsDatabase @Inject constructor(
        application: Application
) : SQLiteOpenHelper(application, DATABASE_NAME, null, DATABASE_VERSION),DisableJsRepository{

    private val database: SQLiteDatabase by databaseDelegate()

    override fun onCreate(db: SQLiteDatabase?) {
        val createAllowListTable = "CREATE TABLE $TABLE_WHITELIST(" +
                " $KEY_ID INTEGER PRIMARY KEY," +
                " $KEY_URL VARCHAR" +
                ")"
        db?.execSQL(createAllowListTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_WHITELIST")
        // Create tables again
        onCreate(db)
    }

    private fun Cursor.bindToDisableJsItem() = DisableJsEntry(
            url = getString(1)
    )

    override fun allDisableJsListItems(): Single<List<DisableJsEntry>> = Single.fromCallable() {
        return@fromCallable database.query(
                TABLE_WHITELIST,
                null,
                null,
                null,
                null,
                null,
                "$KEY_ID DESC"
        ).useMap { it.bindToDisableJsItem() }
    }

    override fun addDisableJsItem(item: DisableJsEntry): Completable = Completable.fromAction() {
        val values = ContentValues().apply {
            put(KEY_URL, item.url)
        }
        database.insert(TABLE_WHITELIST, null, values)
    }

    override fun removeDisableJsItem(item: DisableJsEntry): Completable = Completable.fromAction(){
        database.delete(TABLE_WHITELIST, "$KEY_URL = ?", arrayOf(item.url))
    }


    companion object {

        // Database version
        private const val DATABASE_VERSION = 1

        // Database name
        private const val DATABASE_NAME = "disableJsManager.db"

        // AllowListItems table name
        private const val TABLE_WHITELIST = "disableJs"

        // AllowListItems table columns names
        private const val KEY_ID = "id"
        private const val KEY_URL = "url"

    }
}