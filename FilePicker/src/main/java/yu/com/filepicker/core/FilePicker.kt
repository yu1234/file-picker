package yu.com.filepicker.core

import android.app.Activity
import android.app.Application
import android.content.Intent
import com.blankj.utilcode.util.Utils
import yu.com.filepicker.activity.MainActivity

object FilePicker {
    internal var context: Application? = null

    /**
     * 初始化
     */
    fun init(builder: Builder) {
        this.context = builder.context
        Utils.init(builder.context)
    }

    /**
     * 检查初始化
     */
   private fun checkInit(): Boolean {
        if(context==null){

            throw Exception("file picker need init")
        }
        return context != null
    }

    /**
     * 请求打开文件选择界面
     */
    fun openFilePicker(activity: Activity) {
        if(checkInit()){
            val intent = Intent(activity, MainActivity::class.java)
            activity.startActivityForResult(intent, 1000)
        }

    }

    class Builder private constructor(val context: Application) {
        companion object {
            fun create(context: Application): Builder {
                return Builder(context)
            }
        }
    }


}