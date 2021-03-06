package com.serwylo.ops

import com.serwylo.ops.gui.ProgressEvent

import javax.swing.JComponent

abstract class Phase {

	abstract boolean requiresUserInteraction()

	abstract String getName()

	abstract boolean execute()

	String getDescription() {
		""
	}

	JComponent getGui() {
		null
	}

	String toString() {
		name
	}

	ProgressListener progressListener

	protected void dispatchProgressEvent( double progress, String currentDescription ) {
		if ( progressListener ) {
			progressListener.onProgress( new ProgressEvent( progress : progress, currentDescription : currentDescription ) )
		}
	}

	protected void dispatchIndeterminateProgressEvent( String currentDescription ) {
		if ( progressListener ) {
			progressListener.onProgress( new ProgressEvent( currentDescription : currentDescription, indeterminate: true ) )
		}
	}

	interface ProgressListener {
		void onProgress( ProgressEvent event )
	}

}
