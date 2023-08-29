@file:JvmName("MenuTools")

package net.twisterrob.android.utils.tools

import android.view.Menu
import android.view.MenuItem

fun Menu.asIterable(): MutableIterable<MenuItem> =
	MenuIterable(this)

private class MenuIterable(
	private val menu: Menu
) : MutableIterable<MenuItem> {

	override fun iterator(): MutableIterator<MenuItem> =
		object : MutableIterator<MenuItem> {
			private var index = -1
			private var currentItemRemoved = false

			override fun hasNext(): Boolean {
				return index + 1 < menu.size()
			}

			override fun next(): MenuItem {
				if (!hasNext()) {
					throw NoSuchElementException()
				}
				currentItemRemoved = false
				return menu.getItem(++index)
			}

			override fun remove() {
				check(index != -1) { "Call next() first." }
				check(!currentItemRemoved) { "Item was already removed." }
				// CONSIDER using internal MenuBuilder.removeItemAt(index: Int).
				val item = menu.getItem(index)
				check(item.itemId != Menu.NONE) { "Item has no ID, so cannot be removed." }
				menu.removeItem(item.itemId)
				currentItemRemoved = true
			}
		}
}
