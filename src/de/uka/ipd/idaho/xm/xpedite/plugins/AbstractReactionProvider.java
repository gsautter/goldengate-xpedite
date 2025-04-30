/*
 * Copyright (c) 2006-, IPD Boehm, Universitaet Karlsruhe (TH) / KIT, by Guido Sautter
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Universitaet Karlsruhe (TH) / KIT nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY UNIVERSITAET KARLSRUHE (TH) / KIT AND CONTRIBUTORS 
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.uka.ipd.idaho.xm.xpedite.plugins;

import de.uka.ipd.idaho.xm.XmAnnotation;
import de.uka.ipd.idaho.xm.XmObject;
import de.uka.ipd.idaho.xm.util.XmDocumentMarkupPanel;

/**
 * Convenience abstract super class for reactive plug-ins, inheriting the life
 * cycle methods from abstract GoldenGATE plug-in. This class further includes
 * default implementations of all the notification methods, sub classes need
 * to overwrite only those they require for providing meaningful functionality.
 * 
 * @author sautter
 */
public class AbstractReactionProvider extends AbstractGoldenGateXpeditePlugin implements ReactionProvider {
	
	/** we need a zero-argument constructor for class loading */
	protected AbstractReactionProvider() {}
	
	/**
	 * This default implementation does nothing; sub classes are welcome to
	 * overwrite it as needed.
	 * @see de.uka.ipd.idaho.xm.xpedite.plugins.ReactionProvider#typeChanged(de.uka.ipd.idaho.xm.XmObject, java.lang.String, de.uka.ipd.idaho.xm.util.XmDocumentMarkupPanel, boolean)
	 */
	public void typeChanged(XmObject object, String oldType, XmDocumentMarkupPanel xdmp, boolean allowPrompt) {}
	
	/**
	 * This default implementation does nothing; sub classes are welcome to
	 * overwrite it as needed.
	 * @see de.uka.ipd.idaho.xm.xpedite.plugins.ReactionProvider#attributeChanged(de.uka.ipd.idaho.xm.XmObject, java.lang.String, java.lang.Object, de.uka.ipd.idaho.xm.util.XmDocumentMarkupPanel, boolean)
	 */
	public void attributeChanged(XmObject object, String attributeName, Object oldValue, XmDocumentMarkupPanel xdmp, boolean allowPrompt) {}
	
	/**
	 * This default implementation does nothing; sub classes are welcome to
	 * overwrite it as needed.
	 * @see de.uka.ipd.idaho.xm.xpedite.plugins.ReactionProvider#annotationAdded(de.uka.ipd.idaho.xm.XmAnnotation, de.uka.ipd.idaho.xm.util.XmDocumentMarkupPanel, boolean)
	 */
	public void annotationAdded(XmAnnotation annotation, XmDocumentMarkupPanel xdmp, boolean allowPrompt) {}
	
	/**
	 * This default implementation does nothing; sub classes are welcome to
	 * overwrite it as needed.
	 * @see de.uka.ipd.idaho.xm.xpedite.plugins.ReactionProvider#annotationRemoved(de.uka.ipd.idaho.xm.XmAnnotation, de.uka.ipd.idaho.xm.util.XmDocumentMarkupPanel, boolean)
	 */
	public void annotationRemoved(XmAnnotation annotation, XmDocumentMarkupPanel xdmp, boolean allowPrompt) {}
}