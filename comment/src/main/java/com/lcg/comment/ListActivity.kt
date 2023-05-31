package com.lcg.comment

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.recyclerview.widget.LinearLayoutManager
import com.lcg.comment.databinding.ActivityListBinding
import com.lcg.mylibrary.BaseActivity
import com.lcg.mylibrary.BaseObservableMe
import com.lcg.mylibrary.adapter.CommentAdapter
import com.lcg.mylibrary.utils.setContentViewBinding

/**列表*/
class ListActivity : BaseActivity() {
    private lateinit var binding: ActivityListBinding
    private lateinit var viewModel: ListViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //初始化viewModel
        val className = intent.getStringExtra("clazz")
        val clazz = Class.forName(className) as Class<ListViewModel>
        viewModel = clazz.getConstructor(BaseActivity::class.java).newInstance(this)
        //
        binding = setContentViewBinding(R.layout.activity_list)
        binding.title = viewModel.title
        binding.rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rv.adapter = viewModel.adapter
        //
        lifecycle.addObserver(viewModel)
    }

    companion object {
        /**
         * 开启
         * @param viewModelClass ListViewModel实现类
         * @param bundle 需要传递的数据bundle
         */
        @JvmStatic
        fun <T : ListViewModel> start(activity: BaseActivity, viewModelClass: Class<T>, bundle: Bundle? = null) {
            activity.startActivity(
                    Intent(activity, ListActivity::class.java)
                            .putExtra("clazz", viewModelClass.name)
                            .putExtra("bundle", bundle)
            )
        }
    }

    abstract class ListViewModel(val activity: BaseActivity) : DefaultLifecycleObserver {
        protected var bundle: Bundle? = activity.intent.getBundleExtra("bundle")
        abstract var title: BaseObservableMe
        abstract var adapter: CommentAdapter
    }
}
