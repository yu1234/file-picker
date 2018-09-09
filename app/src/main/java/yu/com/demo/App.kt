package yu.com.demo

import android.app.Application
import yu.com.filepicker.core.FilePicker

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        FilePicker.init(FilePicker.Builder.create(this))
    }
}