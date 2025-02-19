package tk.zwander.rootactivitylauncher.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

val Context.prefs: PrefManager
    get() = PrefManager.getInstance(this)

class PrefManager private constructor(context: Context) : ContextWrapper(context) {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: PrefManager? = null

        fun getInstance(context: Context): PrefManager {
            return instance ?: PrefManager(context.applicationContext).also {
                instance = it
            }
        }

        const val KEY_EXTRAS = "ACTIVITY_EXTRAS"
        const val KEY_ACTIONS = "COMPONENT_ACTIONS"
        const val KEY_DATAS = "COMPONENT_DATAS"
        const val KEY_CATEGORIES = "COMPONENT_CATEGORIES"
    }

    private val prefs = PreferenceManager.getDefaultSharedPreferences(this)
    private val gson = GsonBuilder()
        .create()

    var extras: HashMap<String, List<ExtraInfo>>
        get() = gson.fromJson(
            prefs.getString(KEY_EXTRAS, ""),
            object : TypeToken<HashMap<String, List<ExtraInfo>>>() {}.type
        ) ?: HashMap()
        set(value) {
            prefs.edit {
                putString(
                    KEY_EXTRAS,
                    gson.toJson(value)
                )
            }
        }
    var actions: HashMap<String, String?>
        get() = gson.fromJson(
            prefs.getString(KEY_ACTIONS, ""),
            object : TypeToken<HashMap<String, String?>>() {}.type
        ) ?: HashMap()
        set(value) {
            prefs.edit {
                putString(
                    KEY_ACTIONS,
                    gson.toJson(value)
                )
            }
        }
    var datas: HashMap<String, String?>
        get() = gson.fromJson(
            prefs.getString(KEY_DATAS, ""),
            object : TypeToken<HashMap<String, String?>>() {}.type
        ) ?: HashMap()
        set(value) {
            prefs.edit {
                putString(
                    KEY_DATAS,
                    gson.toJson(value)
                )
            }
        }
    var categories: HashMap<String, ArrayList<String?>>
        get() = gson.fromJson(
            prefs.getString(KEY_CATEGORIES, ""),
            object : TypeToken<HashMap<String, ArrayList<String?>>>() {}.type
        ) ?: HashMap()
        set(value) {
            prefs.edit {
                putString(
                    KEY_CATEGORIES,
                    gson.toJson(value)
                )
            }
        }
}