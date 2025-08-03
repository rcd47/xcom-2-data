package com.github.rcd47.x2data.explorer.file.data;

public abstract class X2VersionedDataContainer<T> extends X2VersionedDatum<T> {

	public X2VersionedDataContainer(int frame) {
		frames[0] = frame;
		changes[0] = FieldChangeType.ADDED;
		numFrames = 1;
	}
	
	protected abstract Iterable<X2VersionedDatum<?>> getChildren();
	
	protected abstract void createFrameValue(int frame);
	
	public void frameFinished(int frame, boolean deltaDisabled) {
		if (deltaDisabled) {
			var anyChildRemoved = false;
			for (var child : getChildren()) {
				if (child.lastFrameTouched != frame && child.changes[child.numFrames - 1] != FieldChangeType.REMOVED) {
					child.markRemoved(frame);
					anyChildRemoved = true;
				}
			}
			if (anyChildRemoved) {
				descendantValueSet(frame);
			}
		}
		if (frames[numFrames - 1] == frame && changes[numFrames - 1] != FieldChangeType.REMOVED) {
			createFrameValue(frame);
		}
	}
	
	@Override
	public void markRemoved(int frame) {
		super.markRemoved(frame);
		for (var child : getChildren()) {
			child.markRemoved(frame);
		}
	}
	
}
