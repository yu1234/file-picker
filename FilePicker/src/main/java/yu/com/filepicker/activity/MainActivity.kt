package yu.com.filepicker.activity

import android.Manifest
import android.graphics.Color
import android.os.Bundle
import android.support.constraint.ConstraintSet.PARENT_ID
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import com.blankj.utilcode.util.PermissionUtils
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.themedToolbar
import org.jetbrains.anko.constraint.layout.ConstraintSetBuilder.Side.*
import org.jetbrains.anko.constraint.layout.applyConstraintSet
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.design.themedTabLayout
import org.jetbrains.anko.support.v4.viewPager
import yu.com.filepicker.R
import yu.com.filepicker.fragment.MemoryCardFragment


class MainActivity : AppCompatActivity() {
    private val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
    private val tabTitles: Array<Int> = arrayOf(R.string.yu_file_picker_memory_card, R.string.yu_file_picker_device)
    private var contentView: MainActivityUI? = null
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //权限检查
        PermissionUtils.permission(*permissions).callback(object : PermissionUtils.SimpleCallback {
            override fun onGranted() {
                contentView = MainActivityUI()
                contentView?.setContentView(this@MainActivity)
                init()
            }

            override fun onDenied() {
                toast(R.string.yu_file_picker_permission_request_denied)
                this@MainActivity.onBackPressed()
            }
        }).request()
    }

    /**
     * 初始化
     */
    fun init() {
        this.supportActionBar?.hide()
        tabLayout = find(R.id.yu_file_picker_tabs)
        viewPager = find(R.id.yu_file_picker_view_pager)
        this.initViewPager()
        this.initTabLayout()
    }

    /**
     * 初始化tab
     */
    private fun initTabLayout() {
        tabLayout.addTab(tabLayout.newTab())
        tabLayout.addTab(tabLayout.newTab())
        tabLayout.setupWithViewPager(viewPager)
    }

    /**
     * initViewPager
     */
    private fun initViewPager() {
        viewPager.adapter = object : FragmentPagerAdapter(this.supportFragmentManager) {
            override fun getCount(): Int {
                return tabTitles.size
            }

            override fun getItem(position: Int): Fragment {
                var fragment = Fragment()
                when (position) {
                    0 -> fragment = MemoryCardFragment()
                }
                return fragment
            }

            // overriding getPageTitle()
            override fun getPageTitle(position: Int): CharSequence {
                return resources.getString(tabTitles[position])
            }


        }
    }

}

class MainActivityUI : AnkoComponent<MainActivity> {
    override fun createView(ui: AnkoContext<MainActivity>) = with(ui) {
        constraintLayout {
            lparams(matchParent, matchParent)
            val toolbar = themedToolbar(R.style.yu_file_picker_theme_toolbar) {
                id = R.id.yu_file_picker_toolbar
                textView(R.string.yu_file_picker_title) {
                    id = R.id.yu_file_picker_toolbar_title
                    textColor = Color.BLACK
                    textSize = px2sp(resources.getDimensionPixelSize(R.dimen.yu_file_picker_font_size_18))
                }.lparams {
                    gravity = Gravity.CENTER
                }
            }.lparams(width = matchParent, height = dimenAttr(android.R.attr.actionBarSize))
            val tabs = themedTabLayout {
                id = R.id.yu_file_picker_tabs
                tabMode = TabLayout.MODE_FIXED
            }.lparams(width = matchParent, height = wrapContent)
            val viewPager = viewPager {
                id = R.id.yu_file_picker_view_pager
            }.lparams(width = matchParent, height = 0)
            applyConstraintSet {
                toolbar {
                    connect(
                            START to START of PARENT_ID,
                            END to END of PARENT_ID,
                            TOP to TOP of PARENT_ID
                    )
                }
                tabs {
                    connect(
                            START to START of PARENT_ID,
                            END to END of PARENT_ID,
                            TOP to BOTTOM of toolbar
                    )
                }
                viewPager {
                    connect(
                            START to START of PARENT_ID,
                            END to END of PARENT_ID,
                            TOP to BOTTOM of tabs,
                            BOTTOM to BOTTOM of PARENT_ID
                    )
                }
            }
        }
    }
}