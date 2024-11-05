import java.util.Iterator;
import java.util.NoSuchElementException;

public class MyALDAList<T> implements ALDAList<T> {
	private Node<T> first;
	private Node<T> last;
	private int size = 0;

	// Iterator --------------------------------------------------
	@Override
	public Iterator<T> iterator() {

		return new Iterator<T>() {
			private Node<T> target = first;
			private Node<T> prev = first;
			private T data;

			@Override
			public boolean hasNext() {
				return target != null;
			}

			@Override
			public T next() {
				if (target == null)
					throw new NoSuchElementException();
				prev = target;
				data = target.data;
				target = target.next;
				return data;
			}

			@Override
			public void remove() {
				if (data == null)
					throw new IllegalStateException();
				else if (first.data == data) {
					first = first.next;
					size--;
				} else {
					
					if (prev.next == last)
						last = prev;

					if(prev.next != null)
						prev.next = prev.next.next;

					target = prev.next;
					size--;
					data = null;
				}
			}

		};
	}

	// Add methods -----------------------------------------------
	@Override
	public void add(T element) {
		// Check if list is empty
		if (first == null) {
			first = new Node<T>(element);
			last = first;
			size = 1;
			return;
		}
		last.next = new Node<T>(element);
		last = last.next;
		size++;
	}

	@Override
	public void add(int index, T element) {
		if (index > size || index < 0)
			throw new IndexOutOfBoundsException();

		Node<T> newNode = new Node<T>(element);

		// Adding first element to list
		if (index == 0) {
			// Check if list is empty
			if (first == null) {
				first = newNode;
				last = first;
				size = 1;
				return;
			}
			newNode.next = first;
			first = newNode;
			size++;
			return;
		}

		// Adding to last position
		if (index == size) {
			last.next = newNode;
			last = last.next;
			size++;
			return;
		}

		// Adding to the middle
		Node<T> nodeBeforeTarget = findNodeFromIndex(index);
		newNode.next = nodeBeforeTarget.next;
		nodeBeforeTarget.next = newNode;
		size++;
	}

	// Remove methods --------------------------------------------
	@Override
	public T remove(int index) {
		// size - 1 to offset index of first position being 0
		if (index > size - 1 || index < 0)
			throw new IndexOutOfBoundsException();

		T data;

		// Remove from first position
		if (index == 0) {
			data = first.data;
			first = first.next;
			size--;
			return data;
		}

		// Remove all other positions
		Node<T> nodeBeforeTarget = findNodeFromIndex(index);
		data = nodeBeforeTarget.next.data;
		remove(nodeBeforeTarget);
		return data;
	}

	@Override
	public boolean remove(T element) {

		// Removes first
		if (first.data == element) {
			first = first.next;
			size--;
			return true;
		}

		Node<T> nodeBeforeTarget = findNodeFromElement(element);
		if (nodeBeforeTarget == null)
			return false;
		remove(nodeBeforeTarget);
		return true;
	}

	private void remove(Node<T> nodeBeforeTarget) {
		// It is not possible to remove first position here
		// That is because to remove a node you need the previous nodes "next".

		// Reassigning last position
		if (nodeBeforeTarget.next == last)
			last = nodeBeforeTarget;

		// Removing middle or last position
		nodeBeforeTarget.next = nodeBeforeTarget.next.next;

		size--;
	}

	// Peeking into list methods ---------------------------------
	@Override
	public T get(int index) {
		// size - 1 to offset index of first position being 0
		if (index > size - 1 || index < 0)
			throw new IndexOutOfBoundsException();

		// Get first position
		if (index == 0)
			return first.data;

		// Get last position
		if (index == size - 1)
			return last.data;

		// Get middle positions
		return findNodeFromIndex(index).next.data;
	}

	@Override
	public boolean contains(T element) {
		if (first == null)
			return false;
		if (first.data == element)
			return true;
		return findNodeFromElement(element) != null;
	}

	@Override
	public int indexOf(T element) {
		Node<T> target = first;
		for (int i = 0; i < size; i++) {
			if (target.data == element) {
				return i;
			}
			target = target.next;
		}
		return -1;
	}

	// Other methods -----------------------------------------------
	@Override
	public void clear() {
		first = null;
		last = null;
		size = 0;
	}

	@Override
	public int size() {
		return size;
	}

	private Node<T> findNodeFromIndex(int index) {
		Node<T> nodeBeforeTarget = first;
		for (int i = 1; i < index; i++)
			nodeBeforeTarget = nodeBeforeTarget.next;
		return nodeBeforeTarget;
		// Index 0 and 1 will return same node. Have a check for fist above *every* time
	}

	private Node<T> findNodeFromElement(T element) {
		// Can not return first node. Have a check for first node above *every* time
		for (Node<T> nodeBeforeTarget = first; nodeBeforeTarget.next != null; nodeBeforeTarget = nodeBeforeTarget.next) {

			if (nodeBeforeTarget.next.data == element) {
				return nodeBeforeTarget;
				// Returns the node previous to the target node
			}

		}
		return null;
		// Failed to find element in list
	}

	@Override
	public String toString() {
		String s = "[";
		Iterator<T> iterator = iterator();
		while (iterator.hasNext())
			s += iterator.next() + ", ";
		// Removes the last ", " from the string if the string has gone through iterator
		if (s.length() > 1)
			s = s.substring(0, s.length() - 2);
		s += "]";
		return s;
	}

	// Inner Node class
	private static class Node<T> {
		T data;
		Node<T> next;

		public Node(T data) {
			this.data = data;
		}
	}

}
