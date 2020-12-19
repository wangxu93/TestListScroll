package com.example.testgitproject

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var listVIew: ListView
    private val list: MutableList<ItemData> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        listVIew = findViewById<View>(R.id.listview) as ListView
        initData()
        listVIew.adapter = MyAdapter()
    }

    private fun initData() {
        for (i in 0..199) {
            val item = ItemData()
            item.name = i.toString() + ""
            item.content = i.toString() + "content"
            if (i % 5 == 0) {
                item.type = 1
            }
            list.add(item)
        }
    }

    inner class MyAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return list.size
        }

        override fun getItem(position: Int): Any {
            return list[position]!!
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            var viewHolder: ViewHolder? = null
            if (convertView == null) {
                convertView = LayoutInflater.from(this@MainActivity).inflate(R.layout.item_view, null)
                viewHolder = ViewHolder(convertView)
                (convertView as View).setTag(viewHolder)
            } else {
                viewHolder = convertView.getTag() as ViewHolder
            }

            val item = list[position]
            viewHolder.tvName.text = item.name
            viewHolder.tvContent.text = item.content
            viewHolder.pluginView.setPosition(position)
            viewHolder.pluginView.setOnItemCLickListener(object : OnItemCLickListener {
                override fun onClick(position: Int, height: Int) {
                    setSelect(position, height)
                }

            })

            if (item.type == 1) {
                viewHolder.pluginView.visibility = View.VISIBLE
            } else {
                viewHolder.pluginView.visibility = View.GONE
            }
            convertView.setOnClickListener(View.OnClickListener {
                setSelect(position, 0)
            })
            return convertView
        }

        inner class ViewHolder(var view: View) {
            var tvName: TextView
            var tvContent: TextView
            var ivLog: ImageView
            var pluginView: PluginView

            init {
                tvName = view.findViewById(R.id.tvName)
                tvContent = view.findViewById(R.id.tvContent)
                ivLog = view.findViewById(R.id.ivLog)
                var lp = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                pluginView = PluginView(view.context)

                lp.addRule(RelativeLayout.BELOW, ivLog.id)
                if (view is ViewGroup) {
                    (view as ViewGroup).addView(pluginView, lp)
                }
            }
        }
    }

    fun setSelect(position: Int, dexHeight: Int) {
        val firstVisiblePosition = listVIew.firstVisiblePosition
        if (firstVisiblePosition >= position) {
            return
        }
        var totalHeight = 0
        for (index in firstVisiblePosition until position) {
            var currentIndex = index - firstVisiblePosition
            var itemVIew = listVIew.getChildAt(currentIndex)
            var h = itemVIew.measuredHeight
            if (currentIndex == 0) {
                var bottom = itemVIew.bottom
                Log.i(TAG, "bottom: " + bottom)
                if (h - bottom > (h * 0.05)) {
                    totalHeight += bottom
                } else {
                    totalHeight += h
                }
            } else {
                totalHeight += h
            }
            Log.i(TAG, "getViewM: " + h)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (listVIew.canScrollList(totalHeight + dexHeight)) {
                listVIew.scrollListBy(totalHeight + dexHeight)
            }
        } else {
            var finalHeight = totalHeight + dexHeight
            try {
                if (listVIew != null) {
                    var result: Boolean = ReflectUtil.invokeMethod(listVIew, "trackMotionScroll", arrayOf(-finalHeight, -finalHeight), arrayOf<Class<*>?>(Int::class.java, Int::class.java)) as Boolean
                    Log.i(TAG, "setSelect: " + result)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    inner class ItemData {
        var name: String? = null
        var content: String? = null
        var type = 0
    }

    inner class PluginView : LinearLayout {
        private var onitemClickListener: OnItemCLickListener? = null
        private var mContext: Context? = null
        private var position = 0

        constructor(context: Context?) : super(context) {
            mContext = context
            init()
        }

        private fun init() {
            LayoutInflater.from(context).inflate(R.layout.plugin_item, this)
            (findViewById<View>(R.id.tvTag) as TextView).text = "这个就是plugm吗？"
            setOnClickListener {
                val parent = parent
                if (parent is ViewGroup) {
                    var dexHeight = parent.height - height
//                    listVIew.scrollListBy(dexHeight)
                    onitemClickListener?.onClick(position, dexHeight)
                }
            }
        }

        fun setPosition(pos: Int) {
            position = pos
        }

        fun setOnItemCLickListener(listener: OnItemCLickListener) {
            this.onitemClickListener = listener
        }

        constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
        constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}
    }

    companion object {
        private const val TAG = "MainActivity"
    }

    interface OnItemCLickListener {
        fun onClick(position: Int, height: Int)
    }

}