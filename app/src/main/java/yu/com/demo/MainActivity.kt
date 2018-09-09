package yu.com.demo

import android.os.Bundle
import android.support.constraint.ConstraintSet.PARENT_ID
import android.support.v7.app.AppCompatActivity
import android.view.View
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.ConstraintSetBuilder.Side.*
import org.jetbrains.anko.constraint.layout.applyConstraintSet
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.sdk25.coroutines.onClick
import yu.com.filepicker.core.FilePicker


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MyActivityUI().setContentView(this)
    }
}

class MyActivityUI : AnkoComponent<MainActivity> {

    override fun createView(ui: AnkoContext<MainActivity>) = with(ui) {
        constraintLayout {
            val name = editText() {
                id = View.generateViewId()
            }
            val button = button("Say Hello") {
                id = R.id.button
                onClick {
                    FilePicker.openFilePicker(ui.owner)
                }
            }
            applyConstraintSet {
                name {
                    connect(
                            START to START of PARENT_ID,
                            END to END of PARENT_ID,
                            TOP to TOP of PARENT_ID

                    )
                }
                button {
                    connect(
                            START to START of PARENT_ID,
                            END to END of PARENT_ID,
                            TOP to BOTTOM of name,
                            BOTTOM to BOTTOM of PARENT_ID
                    )
                }
            }
        }
    }
}
