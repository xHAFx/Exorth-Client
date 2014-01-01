public final class Resource extends QueueNode {

	public Resource() {
		incomplete = true;
	}

	int dataType;
	byte buffer[];
	int ID;
	boolean incomplete;
	int loopCycle;
}
