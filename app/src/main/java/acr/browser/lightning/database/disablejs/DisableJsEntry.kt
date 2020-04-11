package acr.browser.lightning.database.disablejs

import javax.inject.Inject

/**
 * A model object representing a domain on the allow list.
 *
 * @param url The url should be disable js
 * @param timeCreated The time this entry was created in milliseconds.
 */

data class DisableJsEntry(
        var url:String
)