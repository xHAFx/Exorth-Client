public class QueueNode extends Node {

	public final void unlinkSub() {
		if (nextNodeSub == null) {
		} else {
			nextNodeSub.prevNodeSub = prevNodeSub;
			prevNodeSub.nextNodeSub = nextNodeSub;
			prevNodeSub = null;
			nextNodeSub = null;
		}
	}

	public QueueNode() {
	}

	public QueueNode prevNodeSub;
	QueueNode nextNodeSub;
	public static int nodeId;
}
