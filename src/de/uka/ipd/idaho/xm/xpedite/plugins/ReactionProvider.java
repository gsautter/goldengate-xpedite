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
 * Reactive plug-ins receive notification of user edits to a document and can
 * take further measures in response.
 * 
 * @author sautter
 */
public interface ReactionProvider extends GoldenGateXpeditePlugin {
	
	/**
	 * Notify the reactive plugin that the type of an XML Markup object has
	 * changed.
	 * @param object the object whose type changed
	 * @param oldType the old type of the object
	 * @param xdmp the document editor panel the change occurred in
	 * @param allowPrompt got permission to prompt user?
	 */
	public abstract void typeChanged(XmObject object, String oldType, XmDocumentMarkupPanel xdmp, boolean allowPrompt);
	
	/**
	 * Notify the reactive plugin that an attribute has changed in an XML
	 * Markup object. The affected attribute can also be a functional pseudo
	 * attribute, like the predecessor or successor of Image Markup words.
	 * @param object the object whose attribute changed
	 * @param attributeName the name of the attribute
	 * @param oldValue the old value of the attribute, which was just
	 *        replaced
	 * @param xdmp the document editor panel the change occurred in
	 * @param allowPrompt got permission to prompt user?
	 */
	public abstract void attributeChanged(XmObject object, String attributeName, Object oldValue, XmDocumentMarkupPanel xdmp, boolean allowPrompt);
	
	/**
	 * Notify the reactive plugin that an annotation has been added.
	 * @param annotation the annotation that was just added
	 * @param xdmp the document editor panel the change occurred in
	 * @param allowPrompt got permission to prompt user?
	 */
	public abstract void annotationAdded(XmAnnotation annotation, XmDocumentMarkupPanel xdmp, boolean allowPrompt);
	
	/**
	 * Notify the reactive plugin that an annotation has been removed.
	 * @param region the annotation that was just removed
	 * @param xdmp the document editor panel the change occurred in
	 * @param allowPrompt got permission to prompt user?
	 */
	public abstract void annotationRemoved(XmAnnotation annotation, XmDocumentMarkupPanel xdmp, boolean allowPrompt);
}
