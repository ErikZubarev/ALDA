import java.util.Random;


// @Autor Erik Zubarev, erzu6003
public class SkipList<T extends Comparable<T>> {

	// Variables -----------------------------------------------------------
	private static final Random RANDOMGEN = new Random();
	private int size;
	private Node<T> head;
	private Node<T> tail;

	// Constructor -----------------------------------------------------------
	SkipList() {
		this.head = new Node<T>(null, 1);
		this.tail = new Node<T>(null, 1);
		head.next = tail;
		tail.prev = head;
		size = 0;

		head.isHead = true;
		tail.isTail = true;
	}

	// Main Methods -----------------------------------------------------------
	public void add(T item) {
		validateItem(item);
		Node<T> newNode = new Node<>(item, 1);
		insertInList(newNode);
		size++;
	}

	public boolean remove(T item) {
		validateItem(item);
		Node<T> node = get(item);

		// Check if node exists
		if (node == null)
			return false;

		// Check if previous and next nodes are sentinels
		Node<T> prev = node.prev;
		Node<T> next = node.next;
		if (prev.isHead && next.isTail && head.level != 1) {
			// Remove top levels from sentinel
			Node<T> newHead = head.below;
			Node<T> newTail = tail.below;

			newHead.above = null;
			newTail.above = null;	

			head = newHead;
			tail = newTail;
		}

		while (node != null) {
			// Cut horizontal link
			prev = node.prev;
			next = node.next;
			prev.next = next;
			next.prev = prev;

			node = node.below;
		}
		size--;
		return true;
	}

	public boolean contains(T item) {
		return get(item) != null;
	}

	public int size() {
		return size;
	}

	// Helper Methods -----------------------------------------------------------
	private void insertInList(Node<T> newNode) {
		Node<T> node = head;
		Node<T> next = null;
		
		while (true) {
			next = node.next;
			while (next != null && !next.isTail && next.item.compareTo(newNode.item) <= 0) {
				node = next;
				next = node.next;
			}

			if (node.below != null) {
				node = node.below;
			} else {
				break;
			}
		}
		horizontalInsert(node, newNode);
		calculateLevel(newNode);
	}

	private void calculateLevel(Node<T> node) {
		Node<T> prev = null;

		// 50% chance to increase level for new node
		while (RANDOMGEN.nextDouble() < 0.5) {

			// Make sure sentinels are highest
			if (node.level >= head.level) {
				adjustHeadAndTail();
			}
			
			// Find previous node with level above current node
			prev = node;
			while (prev.above == null) {
				prev = prev.prev;
			}
			prev = prev.above;

			int newNodeLevel = node.level + 1;

			Node<T> newAboveNode = new Node<T>(node.item, newNodeLevel);
			verticalInsert(node, newAboveNode);
			horizontalInsert(prev, newAboveNode);
			node = newAboveNode;
		}
	}
	
	
	private void adjustHeadAndTail() {
		int newSentinelLevel = head.level + 1;
		Node<T> newHead = new Node<T>(null, newSentinelLevel);
		Node<T> newTail = new Node<T>(null, newSentinelLevel);
		
		newHead.next = newTail;
		newTail.prev = newHead;
		verticalInsert(head, newHead);
		verticalInsert(tail, newTail);

		head = newHead;
		tail = newTail;
		head.isHead = true;
		tail.isTail = true;
	}

	private void horizontalInsert(Node<T> prev, Node<T> newNode) {
		Node<T> next = prev.next;
		
		newNode.next = next;
		newNode.prev = prev;

		if(next != null) {
			next.prev = newNode;
			prev.next = newNode;
		}
	}

	private void verticalInsert(Node<T> node, Node<T> newNode) {
		node.above = newNode;
		newNode.below = node;
	}
	
    public Node<T> get(T item) {
        validateItem(item);
        Node<T> node = head;
        Node<T> next;

        while (node != null) {
        	next = node.next;
            if (next == null || next.isTail || next.item.compareTo(item) > 0) {
                if (node != null && !node.isHead && node.item.compareTo(item) == 0) {
                    return node;
                }
                node = node.below;
            } else {
                node = node.next;
            }
        }
        return null;
    }

	private void validateItem(T item) {
		if (item == null)
			throw new IllegalArgumentException();
	}

	/**
	 * Denna metod är enbart till för testning. Den ska returnera antalet nivåer för
	 * nod nr i. Skyddsnivån är avsiktlig för att möjliggöra för testfallen att
	 * komma åt metoden utan att behöva bryta sig in.
	 */
	int levelOfNode(int i) {
		Node<T> node = head;
		while (node.below != null) {
			node = node.below;
		}
		for (int x = 0; x < i; x++)
			node = node.next;
		while (node.above != null) {
			node = node.above;
		}
		return node.level;
	}

	@Override
	public String toString() {
		Node<T> v = head;
		Node<T> h = head;
		int level;
		String s = "";
		
//		while (true) {
//			level = v.level;
//			s += "Level " + level + ": Head -> ";
//			h = v;
//			while (h.next != null) {
//				h = h.next;
//				s += h.item +  " -> ";
//			}
//
//			s = s.substring(0, s.length() - 4);
//			s += "\n";
//
//			if (v.below == null) {
//				break;
//			} else
//				v = v.below;
//		}
		while(v.below != null)
			v = v.below;
		while(v.next != null) {
			s += " "+v.item;
			v = v.next;
		}
		return s;
	}

	// Node Class -----------------------------------------------------------
	private class Node<T extends Comparable<T>> {
		T item;
		int level;
		boolean isHead;
		boolean isTail;
		Node<T> above;
		Node<T> below;
		Node<T> next;
		Node<T> prev;

		Node(T item, int level) {
			this.item = item;
			this.level = level;
		}

	}

}
