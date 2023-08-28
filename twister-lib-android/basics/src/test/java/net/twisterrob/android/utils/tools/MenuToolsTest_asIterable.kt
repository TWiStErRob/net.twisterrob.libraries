package net.twisterrob.android.utils.tools

import android.view.Menu
import android.view.MenuItem
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito.inOrder
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class MenuToolsTest_asIterable {

	@Test fun testIteratorDoesNotInteract() {
		val menu: Menu = mock()

		val items = menu.asIterable()

		items.iterator()
		verifyNoInteractions(menu)
	}

	@Test fun testEmptyMenuHasNoNext() {
		val menu: Menu = mock()
		whenever(menu.size()).thenReturn(0)

		val items = menu.asIterable()

		val it: Iterator<MenuItem> = items.iterator()
		assertFalse(it.hasNext())
	}

	@Test(expected = NoSuchElementException::class)
	fun testEmptyMenuHasNoFurther() {
		val menu: Menu = mock()
		whenever(menu.size()).thenReturn(0)

		val items = menu.asIterable()

		val it: Iterator<MenuItem> = items.iterator()
		it.next() // NoSuchElementException
	}

	@Test fun testSingleItemHasNext() {
		val menu: Menu = mock()
		whenever(menu.size()).thenReturn(1)
		val anItem: MenuItem = mock()
		whenever(menu.getItem(0)).thenReturn(anItem)

		val items = menu.asIterable()

		val it: Iterator<MenuItem> = items.iterator()
		assertTrue(it.hasNext())
		assertSame(anItem, it.next())
		assertFalse(it.hasNext())
	}

	@Test(expected = NoSuchElementException::class)
	fun testSingleItemHasNoFurther() {
		val menu: Menu = mock()
		whenever(menu.size()).thenReturn(1)
		whenever(menu.getItem(0)).thenReturn(mock())

		val items = menu.asIterable()

		val it: Iterator<MenuItem> = items.iterator()
		it.next()
		it.next() // NoSuchElementException
	}

	@Test fun testTwoItemsIterable() {
		val menu: Menu = mock()
		whenever(menu.size()).thenReturn(2)
		val anItem: MenuItem = mock()
		val otherItem: MenuItem = mock()
		whenever(menu.getItem(0)).thenReturn(anItem)
		whenever(menu.getItem(1)).thenReturn(otherItem)

		val items = menu.asIterable()

		val it: Iterator<MenuItem> = items.iterator()
		assertTrue(it.hasNext())
		assertSame(anItem, it.next())
		assertTrue(it.hasNext())
		assertSame(otherItem, it.next())
		assertFalse(it.hasNext())
	}

	@Test(expected = NoSuchElementException::class)
	fun testTwoItemsHasNoFurther() {
		val menu: Menu = mock()
		whenever(menu.size()).thenReturn(2)
		whenever(menu.getItem(0)).thenReturn(mock())
		whenever(menu.getItem(1)).thenReturn(mock())

		val items = menu.asIterable()

		val it: Iterator<MenuItem> = items.iterator()
		it.next()
		it.next()
		it.next() // NoSuchElementException
	}

	@Test(expected = IllegalStateException::class)
	fun testCannotRemoveBeforeMoving() {
		val menu: Menu = mock()
		whenever(menu.size()).thenReturn(0)

		val items = menu.asIterable()

		val it = items.iterator()
		it.remove() // IllegalStateException
	}

	@Test(expected = IllegalStateException::class)
	fun testCannotRemoveNoId() {
		val menu: Menu = mock()
		whenever(menu.size()).thenReturn(1)
		val anItem: MenuItem = mock { whenever(it.itemId).thenReturn(Menu.NONE) }
		whenever(menu.getItem(0)).thenReturn(anItem)

		val items = menu.asIterable()

		val it = items.iterator()
		assertSame(anItem, it.next())
		it.remove() // IllegalStateException
	}

	@Test(expected = IllegalStateException::class)
	fun testCannotRemoveTwice() {
		val menu: Menu = mock()
		whenever(menu.size()).thenReturn(1)
		val anItem: MenuItem = mock { whenever(it.itemId).thenReturn(123) }
		whenever(menu.getItem(0)).thenReturn(anItem)

		val items = menu.asIterable()

		val it = items.iterator()
		assertSame(anItem, it.next())
		it.remove()
		it.remove() // IllegalStateException
	}

	@Test fun testCanRemove() {
		val menu: Menu = mock()
		whenever(menu.size()).thenReturn(1)
		val anItem = mock<MenuItem> { whenever(it.itemId).thenReturn(123) }
		whenever(menu.getItem(0)).thenReturn(anItem)

		val items = menu.asIterable()

		val it = items.iterator()
		assertSame(anItem, it.next())
		it.remove()
		verify(menu).removeItem(123)
	}

	@Test fun testCanRemoveTwo() {
		val menu: Menu = mock()
		whenever(menu.size()).thenReturn(2)
		val anItem = mock<MenuItem> { whenever(it.itemId).thenReturn(123) }
		whenever(menu.getItem(0)).thenReturn(anItem)
		val otherItem = mock<MenuItem> { whenever(it.itemId).thenReturn(456) }
		whenever(menu.getItem(1)).thenReturn(otherItem)

		val items = menu.asIterable()

		val it = items.iterator()
		assertSame(anItem, it.next())
		it.remove()
		assertSame(otherItem, it.next())
		it.remove()
		val order = inOrder(menu)
		order.verify(menu).removeItem(123)
		order.verify(menu).removeItem(456)
	}

	@Test fun testCanRemoveMiddleOfThree() {
		val menu: Menu = mock()
		whenever(menu.size()).thenReturn(3)
		val anItem: MenuItem = mock { whenever(it.itemId).thenReturn(123) }
		whenever(menu.getItem(0)).thenReturn(anItem)
		val otherItem: MenuItem = mock { whenever(it.itemId).thenReturn(456) }
		whenever(menu.getItem(1)).thenReturn(otherItem)
		val anotherItem: MenuItem = mock { whenever(it.itemId).thenReturn(789) }
		whenever(menu.getItem(2)).thenReturn(anotherItem)

		val items = menu.asIterable()

		val it = items.iterator()
		assertSame(anItem, it.next())
		assertSame(otherItem, it.next())
		it.remove()
		assertSame(anotherItem, it.next())
		verify(menu).removeItem(456)
	}
}
