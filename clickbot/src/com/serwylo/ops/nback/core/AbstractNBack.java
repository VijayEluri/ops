package com.serwylo.ops.nback.core;

/*
 * Copyright (c) 2010 Peter Serwylo
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Provides an interface for different types of NBack tasks.
 * Allows dispatching of various ACTION_* events.
 * Holds a reference to the important properties and numberSequence variables.
 * Also takes care of storing results via addResult();
 * @author Peter Serwylo
 */
public abstract class AbstractNBack 
{

	/**
	 * When the next number is chosen, we will dispatch an event with this ID.
	 */
	public static final int ACTION_TICK = 1;

	/**
	 * If the focus deelio is enabled, then each time the test focuses (for the
	 * first time, if there is more than one consecutive focus), we dispatch an
	 * event with this ID.
	 */
	public static final int ACTION_FOCUS = 2;
	
	/**
	 * When the sequence has hit the number of ticks specified in the configuration, 
	 * then we dispatch an event with this ID to whoever is listening.
	 */
	public static final int ACTION_COMPLETE = 3;

	private ArrayList<Result> results;
	protected Sequence numberSequence;
	protected NBackProperties properties;
    private ArrayList<ActionListener> listenerList = new ArrayList<ActionListener>();
	
	protected long timeStartedTest;
	protected long timeNumberShown;
	protected boolean hasResultForThisTick;
	protected boolean hasStarted;
	protected boolean isCompleted;

	public final Sequence getSequence() { return this.numberSequence; }
	public final NBackProperties getProperties() { return this.properties; }
	public final ArrayList<Result> getResults() { return this.results; }
	public final boolean hasStarted() { return this.hasStarted; }
	public final boolean isCompleted() { return this.isCompleted; }
	
	public AbstractNBack( NBackProperties properties )
	{
		this.numberSequence = new Sequence( properties );
		this.properties = properties;
		this.results = new ArrayList<Result>();
	}

	/**
	 * In this function, the most important thing to do is call addResult()
	 * appropriately to make sure the result is persisted. You may want to
	 * do certain things depending on whether the result was forced or not.
	 * For instance, if an InteractiveNBack receives a forced result, it means
	 * that it needs to tick over to the next number (receiving a forced result
	 * is the only queue it gets to tick over).
	 * @param isTarget
	 * @param wasForced
	 */
	public abstract void submitResult( boolean isTarget, boolean wasForced );
	
	public void submitResult( boolean isTarget )
	{
		this.submitResult( isTarget, false );
	}
	
	/**
	 * Adds a new Result object to the results array.
	 * In addition to the number, whether it was forced, whether it was a
	 * target, and whether the user chose correctly, it also stores timing
	 * information.
	 * @param isTarget
	 * @param wasForced
	 */
	protected void addResult( boolean isTarget, boolean wasForced )
	{
		int timeFromStart = (int)( System.currentTimeMillis() - this.timeStartedTest );
		int timeFromNumberShown = (int)( System.currentTimeMillis() - this.numberSequence.getTimeNumberShown() );
		this.results.add
		( 
			new Result
			( 
				this.numberSequence.getCurrentNumber(), 
				isTarget == this.numberSequence.isTarget(), 
				this.numberSequence.isTarget(), 
				wasForced, 
				timeFromStart, 
				timeFromNumberShown 
			) 
		);
	}
	
	public void start()
	{
		this.hasStarted = true;
	}
	
	public void stop()
	{
		this.hasStarted = false;
	}
	
	public void addActionListener( ActionListener listener ) 
	{
        this.listenerList.add( listener );
    }
    
    public void removeActionListener( ActionListener listener ) 
    {
	    this.listenerList.remove( listener );
	}

	protected final void fireAction( int actionId ) 
	{
		for ( ActionListener listener : this.listenerList )
		{
			listener.actionPerformed( new ActionEvent( this, actionId, "" ) );
		}	
	}
    
}
