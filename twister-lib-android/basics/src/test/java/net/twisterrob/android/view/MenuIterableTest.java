package net.twisterrob.android.view;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Test;
import org.mockito.InOrder;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import android.view.Menu;
import android.view.MenuItem;

public class MenuIterableTest {

	@Test public void testIteratorDoesNotInteract() {
		Menu menu = mock(Menu.class);

		MenuIterable items = new MenuIterable(menu);

		items.iterator();
		verifyNoInteractions(menu);
	}

	@Test public void testEmptyMenuHasNoNext() {
		Menu menu = mock(Menu.class);
		when(menu.size()).thenReturn(0);

		MenuIterable items = new MenuIterable(menu);

		Iterator<MenuItem> it = items.iterator();
		assertFalse(it.hasNext());
	}

	@Test(expected = NoSuchElementException.class)
	public void testEmptyMenuHasNoFurther() {
		Menu menu = mock(Menu.class);
		when(menu.size()).thenReturn(0);

		MenuIterable items = new MenuIterable(menu);

		Iterator<MenuItem> it = items.iterator();
		it.next(); // NoSuchElementException
	}

	@Test public void testSingleItemHasNext() {
		Menu menu = mock(Menu.class);
		when(menu.size()).thenReturn(1);
		MenuItem anItem = mock(MenuItem.class);
		when(menu.getItem(0)).thenReturn(anItem);

		MenuIterable items = new MenuIterable(menu);

		Iterator<MenuItem> it = items.iterator();
		assertTrue(it.hasNext());
		assertSame(anItem, it.next());
		assertFalse(it.hasNext());
	}

	@Test(expected = NoSuchElementException.class)
	public void testSingleItemHasNoFurther() {
		Menu menu = mock(Menu.class);
		when(menu.size()).thenReturn(1);
		when(menu.getItem(0)).thenReturn(mock(MenuItem.class));

		MenuIterable items = new MenuIterable(menu);

		Iterator<MenuItem> it = items.iterator();
		it.next();
		it.next(); // NoSuchElementException
	}

	@Test public void testTwoItemsIterable() {
		Menu menu = mock(Menu.class);
		when(menu.size()).thenReturn(2);
		MenuItem anItem = mock(MenuItem.class);
		MenuItem otherItem = mock(MenuItem.class);
		when(menu.getItem(0)).thenReturn(anItem);
		when(menu.getItem(1)).thenReturn(otherItem);

		MenuIterable items = new MenuIterable(menu);

		Iterator<MenuItem> it = items.iterator();
		assertTrue(it.hasNext());
		assertSame(anItem, it.next());
		assertTrue(it.hasNext());
		assertSame(otherItem, it.next());
		assertFalse(it.hasNext());
	}

	@Test(expected = NoSuchElementException.class)
	public void testTwoItemsHasNoFurther() {
		Menu menu = mock(Menu.class);
		when(menu.size()).thenReturn(2);
		when(menu.getItem(0)).thenReturn(mock(MenuItem.class));
		when(menu.getItem(1)).thenReturn(mock(MenuItem.class));

		MenuIterable items = new MenuIterable(menu);

		Iterator<MenuItem> it = items.iterator();
		it.next();
		it.next();
		it.next(); // NoSuchElementException
	}

	@Test(expected = IllegalStateException.class)
	public void testCannotRemoveBeforeMoving() {
		Menu menu = mock(Menu.class);
		when(menu.size()).thenReturn(0);

		MenuIterable items = new MenuIterable(menu);

		Iterator<MenuItem> it = items.iterator();
		it.remove();
	}

	@Test(expected = IllegalStateException.class)
	public void testCannotRemoveNoId() {
		Menu menu = mock(Menu.class);
		when(menu.size()).thenReturn(1);
		MenuItem anItem = mock(MenuItem.class);
		when(anItem.getItemId()).thenReturn(Menu.NONE);
		when(menu.getItem(0)).thenReturn(anItem);

		MenuIterable items = new MenuIterable(menu);

		Iterator<MenuItem> it = items.iterator();
		it.next();
		it.remove();
	}

	@Test(expected = IllegalStateException.class)
	public void testCannotRemoveTwice() {
		Menu menu = mock(Menu.class);
		when(menu.size()).thenReturn(1);
		MenuItem anItem = mock(MenuItem.class);
		when(anItem.getItemId()).thenReturn(123);
		when(menu.getItem(0)).thenReturn(anItem);

		MenuIterable items = new MenuIterable(menu);

		Iterator<MenuItem> it = items.iterator();
		it.next();
		it.remove();
		it.remove();
	}

	@Test
	public void testCanRemove() {
		Menu menu = mock(Menu.class);
		when(menu.size()).thenReturn(1);
		MenuItem anItem = mock(MenuItem.class);
		when(anItem.getItemId()).thenReturn(123);
		when(menu.getItem(0)).thenReturn(anItem);

		MenuIterable items = new MenuIterable(menu);

		Iterator<MenuItem> it = items.iterator();
		it.next();
		it.remove();

		verify(menu).removeItem(123);
	}

	@Test
	public void testCanRemoveTwo() {
		Menu menu = mock(Menu.class);
		when(menu.size()).thenReturn(2);
		MenuItem anItem = mock(MenuItem.class);
		when(anItem.getItemId()).thenReturn(123);
		MenuItem otherItem = mock(MenuItem.class);
		when(otherItem.getItemId()).thenReturn(456);
		when(menu.getItem(0)).thenReturn(anItem);
		when(menu.getItem(1)).thenReturn(otherItem);

		MenuIterable items = new MenuIterable(menu);

		Iterator<MenuItem> it = items.iterator();
		it.next();
		it.remove();
		it.next();
		it.remove();

		InOrder order = inOrder(menu);
		order.verify(menu).removeItem(123);
		order.verify(menu).removeItem(456);
	}

	@Test
	public void testCanRemoveMiddleOfThree() {
		Menu menu = mock(Menu.class);
		when(menu.size()).thenReturn(3);
		MenuItem anItem = mock(MenuItem.class);
		when(anItem.getItemId()).thenReturn(123);
		MenuItem otherItem = mock(MenuItem.class);
		when(otherItem.getItemId()).thenReturn(456);
		MenuItem anotherItem = mock(MenuItem.class);
		when(anotherItem.getItemId()).thenReturn(789);
		when(menu.getItem(0)).thenReturn(anItem);
		when(menu.getItem(1)).thenReturn(otherItem);
		when(menu.getItem(2)).thenReturn(anotherItem);

		MenuIterable items = new MenuIterable(menu);

		Iterator<MenuItem> it = items.iterator();
		assertSame(anItem, it.next());
		assertSame(otherItem, it.next());
		it.remove();
		assertSame(anotherItem, it.next());

		verify(menu).removeItem(456);
	}
}
