package com.lcg.mylibrary.utils

import android.app.Activity
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.annotation.Px
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver

/**
 * View的一些扩展
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2018/3/22 10:33
 */

/**
 * Performs the given action when the view tree is about to be Layout.
 */
inline fun View.doOnGlobalLayout(crossinline action: (view: View) -> Boolean) {
    val vto = viewTreeObserver
    vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (action(this@doOnGlobalLayout))
                when {
                    vto.isAlive -> vto.removeGlobalOnLayoutListener(this)
                    else -> viewTreeObserver.removeGlobalOnLayoutListener(this)
                }
        }
    })
}

/**
 * Performs the given action when the view tree is about to be drawn.
 */
inline fun View.doOnPreDraw(crossinline action: (view: View) -> Unit) {
    val vto = viewTreeObserver
    vto.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            action(this@doOnPreDraw)
            when {
                vto.isAlive -> vto.removeOnPreDrawListener(this)
                else -> viewTreeObserver.removeOnPreDrawListener(this)
            }
            return true
        }
    })
}

/**
 * Updates this view's padding. This version of the method allows using named parameters
 * to just set one or more axes.
 *
 * @see View.setPadding
 */
fun View.updatePadding(
        @Px left: Int = paddingLeft,
        @Px top: Int = paddingTop,
        @Px right: Int = paddingRight,
        @Px bottom: Int = paddingBottom
) {
    setPadding(left, top, right, bottom)
}

/**
 * Sets the view's padding. This version of the method sets all axes to the provided size.
 *
 * @see View.setPadding
 */
fun View.setPadding(@Px size: Int) {
    setPadding(size, size, size, size)
}

/**
 * Returns true when this view's visibility is [View.VISIBLE], false otherwise.
 *
 * ```
 * if (view.isVisible) {
 *     // Behavior...
 * }
 * ```
 *
 * Setting this property to true sets the visibility to [View.VISIBLE], false to [View.GONE].
 *
 * ```
 * view.isVisible = true
 * ```
 */
inline var View.isVisible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }

/**
 * Returns true when this view's visibility is [View.INVISIBLE], false otherwise.
 *
 * ```
 * if (view.isInvisible) {
 *     // Behavior...
 * }
 * ```
 *
 * Setting this property to true sets the visibility to [View.INVISIBLE], false to [View.VISIBLE].
 *
 * ```
 * view.isInvisible = true
 * ```
 */
inline var View.isInvisible: Boolean
    get() = visibility == View.INVISIBLE
    set(value) {
        visibility = if (value) View.INVISIBLE else View.VISIBLE
    }

/**
 * Returns true when this view's visibility is [View.GONE], false otherwise.
 *
 * ```
 * if (view.isGone) {
 *     // Behavior...
 * }
 * ```
 *
 * Setting this property to true sets the visibility to [View.GONE], false to [View.VISIBLE].
 *
 * ```
 * view.isGone = true
 * ```
 */
inline var View.isGone: Boolean
    get() = visibility == View.GONE
    set(value) {
        visibility = if (value) View.GONE else View.VISIBLE
    }

/**
 * Executes [block] with the View's layoutParams and reassigns the layoutParams with the
 * updated version.
 *
 * @see View.getLayoutParams
 * @see View.setLayoutParams
 **/
inline fun View.updateLayoutParams(block: ViewGroup.LayoutParams.() -> Unit) {
    updateLayoutParams<ViewGroup.LayoutParams>(block)
}

/**
 * Executes [block] with a typed version of the View's layoutParams and reassigns the
 * layoutParams with the updated version.
 *
 * @see View.getLayoutParams
 * @see View.setLayoutParams
 **/
@JvmName("updateLayoutParamsTyped")
inline fun <reified T : ViewGroup.LayoutParams> View.updateLayoutParams(block: T.() -> Unit) {
    val params = layoutParams as T
    block(params)
    layoutParams = params
}

/**
 * Returns the view at [index].
 *
 * @throws IndexOutOfBoundsException if index is less than 0 or greater than or equal to the count.
 */
operator fun ViewGroup.get(index: Int) =
        getChildAt(index) ?: throw IndexOutOfBoundsException("Index: $index, Size: $childCount")

/** Returns `true` if [view] is found in this view group. */
operator fun ViewGroup.contains(view: View) = indexOfChild(view) != -1

/** Adds [view] to this view group. */
operator fun ViewGroup.plusAssign(view: View) = addView(view)

/** Removes [view] from this view group. */
operator fun ViewGroup.minusAssign(view: View) = removeView(view)

/** Returns the number of views in this view group. */
inline val ViewGroup.size get() = childCount

/** Returns true if this view group contains no views. */
fun ViewGroup.isEmpty() = childCount == 0

/** Returns true if this view group contains one or more views. */
fun ViewGroup.isNotEmpty() = childCount != 0

/** Performs the given action on each view in this view group. */
inline fun ViewGroup.forEach(action: (view: View) -> Unit) {
    for (index in 0 until childCount) {
        action(getChildAt(index))
    }
}

/** Performs the given action on each view in this view group, providing its sequential index. */
inline fun ViewGroup.forEachIndexed(action: (index: Int, view: View) -> Unit) {
    for (index in 0 until childCount) {
        action(index, getChildAt(index))
    }
}

/** Returns a [MutableIterator] over the views in this view group. */
operator fun ViewGroup.iterator() = object : MutableIterator<View> {
    private var index = 0
    override fun hasNext() = index < childCount
    override fun next() = getChildAt(index++) ?: throw IndexOutOfBoundsException()
    override fun remove() = removeViewAt(--index)
}

/** Returns a [Sequence] over the child views in this view group. */
val ViewGroup.children: Sequence<View>
    get() = object : Sequence<View> {
        override fun iterator() = this@children.iterator()
    }

/**
 * Sets the margins in the ViewGroup's MarginLayoutParams. This version of the method sets all axes
 * to the provided size.
 *
 * @see ViewGroup.MarginLayoutParams.setMargins
 */
fun ViewGroup.MarginLayoutParams.setMargins(@Px size: Int) {
    setMargins(size, size, size, size)
}

/**
 * Updates the margins in the [ViewGroup]'s [ViewGroup.MarginLayoutParams].
 * This version of the method allows using named parameters to just set one or more axes.
 *
 * @see ViewGroup.MarginLayoutParams.setMargins
 */
fun ViewGroup.MarginLayoutParams.updateMargins(
        @Px left: Int = leftMargin,
        @Px top: Int = topMargin,
        @Px right: Int = rightMargin,
        @Px bottom: Int = bottomMargin
) {
    setMargins(left, top, right, bottom)
}

/**通过DataBindingUtil去setContentView
 * @see[DataBindingUtil.setContentView]*/
fun <T : ViewDataBinding> Activity.setContentViewBinding(layoutResID: Int): T {
    return DataBindingUtil.setContentView(this, layoutResID)
}

/**通过DataBindingUtil去inflate
 * @see[DataBindingUtil.inflate]*/
@JvmOverloads
fun <T : ViewDataBinding> LayoutInflater.inflateBinding(
        layoutResID: Int,
        parent: ViewGroup? = null,
        attachToParent: Boolean = false
): T {
    return DataBindingUtil.inflate(this, layoutResID, parent, attachToParent)
}