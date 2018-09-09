package yu.com.filepicker.fragment

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.constraint.ConstraintSet.PARENT_ID
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SDCardUtils
import com.blankj.utilcode.util.ToastUtils
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.ConstraintSetBuilder
import org.jetbrains.anko.constraint.layout.ConstraintSetBuilder.Side.*
import org.jetbrains.anko.constraint.layout.applyConstraintSet
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.find
import yu.com.filepicker.R
import java.io.File


class MemoryCardFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var fileAdapter: FileAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = MemoryCardFragmentUI().createView(AnkoContext.create(ctx, this))

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        this.init()
    }

    private fun init() {
        recyclerView = find(R.id.yu_file_picker_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this.activity)
        fileAdapter = FileAdapter()
        recyclerView.adapter = fileAdapter
    }
}


class MemoryCardFragmentUI : AnkoComponent<MemoryCardFragment> {
    override fun createView(ui: AnkoContext<MemoryCardFragment>) = with(ui) {
        constraintLayout {
            lparams(matchParent, matchParent)
            val recyclerView = recyclerView {
                id = R.id.yu_file_picker_recycler_view
                lparams(matchParent, matchParent)
            }
            applyConstraintSet {
                recyclerView {
                    connect(
                            ConstraintSetBuilder.Side.START to ConstraintSetBuilder.Side.START of ConstraintSet.PARENT_ID,
                            ConstraintSetBuilder.Side.END to ConstraintSetBuilder.Side.END of ConstraintSet.PARENT_ID,
                            ConstraintSetBuilder.Side.TOP to ConstraintSetBuilder.Side.TOP of ConstraintSet.PARENT_ID,
                            ConstraintSetBuilder.Side.BOTTOM to ConstraintSetBuilder.Side.BOTTOM of ConstraintSet.PARENT_ID
                    )
                }
            }
        }
    }
}

class FileAdapter : RecyclerView.Adapter<FileAdapter.MyViewHolder>() {
    private val list = mutableListOf<JSONObject>()
    private val history = mutableListOf<String>()

    /**
     * 初始化
     */
    init {
        this.loadRootDir()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(FileAdapterItemUI().createView(AnkoContext.create(parent.context, parent)))
    }

    override fun getItemCount(): Int {
        return list.size
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        if (item.getBoolean("isFile")) {
            holder.dirItem.visibility = View.GONE
            holder.fileItem.visibility = View.VISIBLE
        } else {
            holder.dirItem.visibility = View.VISIBLE
            holder.fileItem.visibility = View.GONE
            holder.dirName.text = item.getString("name") ?: ""
            if ("back" == item.getString("id")) {
                holder.dirIcon.setImageResource(R.mipmap.file_icon_back_m_default)
            } else {
                holder.dirIcon.setImageResource(R.mipmap.attachment_icon_folder_l_default)
            }
        }

        holder.view.tag = item
        holder.view.onClick {
            val tag: JSONObject? = it?.tag as JSONObject
            if (tag?.getBoolean("isFile") == true) {

            } else {
                if ("back" == tag?.getString("id")) {
                    this@FileAdapter.backPreviousDir()
                } else {
                    this@FileAdapter.enterDir(tag?.getString("id") ?: "")
                }
            }

        }
    }

    /**
     * 加载根目录
     */
    private fun loadRootDir() {
        Single.create<Any> {
            val paths = SDCardUtils.getSDCardPaths() ?: mutableListOf<String>()
            list.clear()
            for (path in paths) {
                val jsonObject: JSONObject = JSONObject()
                jsonObject.put("pid", "root")
                jsonObject.put("id", path)
                jsonObject.put("name", FileUtils.getFileName(path))
                jsonObject.put("isFile", false)
                list.add(jsonObject)
            }
            it.onSuccess(1)
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    this.history.clear()
                    this.history.add("root")
                    this.notifyDataSetChanged()
                }, {
                    ToastUtils.showShort(it.message ?: "error")
                    LogUtils.eTag("FilePicker", it)
                })

    }

    /**
     * 加载文件和目录
     */
    private fun loadFiles(id: String, enter: Boolean) {
        if (id == "root") {
            this.loadRootDir()
        } else {
            Single

                    .create<Any> {
                        val files: MutableList<File> = FileUtils.listFilesInDir(id)
                                ?: mutableListOf()
                        list.clear()
                        val jsonObject: JSONObject = JSONObject()
                        jsonObject.put("id", "back")
                        jsonObject.put("name", "返回")
                        jsonObject.put("isFile", false)
                        list.add(jsonObject)
                        for (file in files) {
                            val jsonObject: JSONObject = JSONObject()
                            jsonObject.put("pid", id)
                            jsonObject.put("id", file.path)
                            jsonObject.put("name", file.name)
                            jsonObject.put("isFile", file.isFile)
                            list.add(jsonObject)
                        }
                        it.onSuccess(1)
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (enter) {
                            this.history.add(id)
                        } else {
                            this.history.removeAt(this.history.size - 1)
                        }
                        this.notifyDataSetChanged()
                    }, {
                        ToastUtils.showShort(it.message ?: "error")
                        LogUtils.eTag("FilePicker", it)
                    })
        }
    }

    /**
     * 进入文件夹
     */
    private fun enterDir(id: String) {
        loadFiles(id, true)
    }

    /**
     * 返回上一级
     */
    private fun backPreviousDir() {
        loadFiles(this.history[this.history.size-2], false)
    }


    /**
     * hode
     */
    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val dirItem: ConstraintLayout
        val dirName: TextView
        val dirIcon: ImageView

        val fileItem: ConstraintLayout

        init {
            dirItem = view.find(R.id.yu_file_picker_dir_item)
            dirName = view.find(R.id.yu_file_picker_dir_name)
            dirIcon = view.find(R.id.yu_file_picker_dir_icon)
            fileItem = view.find(R.id.yu_file_picker_file_item)
        }
    }

    /**
     * item ui
     */
    class FileAdapterItemUI : AnkoComponent<ViewGroup> {
        override fun createView(ui: AnkoContext<ViewGroup>): View {
            return with(ui) {
                constraintLayout {
                    lparams(width = matchParent, height = dip(50))
                    val fileItem = constraintLayout {
                        id = R.id.yu_file_picker_file_item
                        visibility = View.GONE
                        lparams(width = matchParent, height = matchParent)
                    }
                    val dirItem = constraintLayout {
                        id = R.id.yu_file_picker_dir_item
                        visibility = View.VISIBLE
                        lparams(width = matchParent, height = matchParent)
                        val dirIcon = imageView(R.mipmap.attachment_icon_folder_l_default) {
                            id = R.id.yu_file_picker_dir_icon
                        }.lparams(width = dip(40), height = dip(40))
                        val dirName = textView {
                            id = R.id.yu_file_picker_dir_name
                        }.lparams(width = 0, height = wrapContent)
                        val moreIcon = imageView(R.mipmap.form_icon_right_arrow_default) {
                            id = View.generateViewId()
                        }
                        applyConstraintSet {
                            dirIcon {
                                connect(
                                        START to START of PARENT_ID margin dip(8),
                                        TOP to TOP of PARENT_ID margin dip(8),
                                        END to START of dirName margin dip(8),
                                        BOTTOM to BOTTOM of PARENT_ID margin dip(8)
                                )
                            }
                            dirName {
                                connect(
                                        START to END of dirIcon,
                                        END to START of moreIcon,
                                        TOP to TOP of PARENT_ID margin dip(8),
                                        BOTTOM to BOTTOM of PARENT_ID margin dip(8)
                                )
                            }
                            moreIcon {
                                connect(
                                        START to END of dirName,
                                        END to END of PARENT_ID margin dip(8),
                                        TOP to TOP of PARENT_ID margin dip(8),
                                        BOTTOM to BOTTOM of PARENT_ID margin dip(8)
                                )
                            }
                        }
                    }
                    applyConstraintSet {
                        fileItem {
                            connect(
                                    START to START of PARENT_ID,
                                    TOP to TOP of PARENT_ID,
                                    END to END of PARENT_ID,
                                    BOTTOM to BOTTOM of PARENT_ID
                            )
                        }
                        dirItem {
                            connect(
                                    START to START of PARENT_ID,
                                    TOP to TOP of PARENT_ID,
                                    END to END of PARENT_ID,
                                    BOTTOM to BOTTOM of PARENT_ID
                            )
                        }
                    }
                }
            }
        }
    }

}