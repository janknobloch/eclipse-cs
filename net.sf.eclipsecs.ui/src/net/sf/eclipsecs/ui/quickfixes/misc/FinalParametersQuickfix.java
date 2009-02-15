//============================================================================
//
// Copyright (C) 2002-2008  David Schneider, Lars K�dderitzsch
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//
//============================================================================

package net.sf.eclipsecs.ui.quickfixes.misc;

import net.sf.eclipsecs.ui.CheckstyleUIPluginImages;
import net.sf.eclipsecs.ui.quickfixes.AbstractASTResolution;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.graphics.Image;


/**
 * Quickfix implementation which adds final modifiers to parameters in method
 * declarations.
 * 
 * @author Levon Saldamli
 * @author Lars K�dderitzsch
 */
public class FinalParametersQuickfix extends AbstractASTResolution
{

    /**
     * {@inheritDoc}
     */
    protected ASTVisitor handleGetCorrectingASTVisitor(final IRegion lineInfo,
            final int markerStartOffset)
    {
        return new ASTVisitor()
        {

            public boolean visit(SingleVariableDeclaration node)
            {
                if (containsPosition(node, markerStartOffset)
                        && !Modifier.isFinal(node.getModifiers()))
                {
                    if (!Modifier.isFinal(node.getModifiers()))
                    {
                        Modifier finalModifier = node.getAST().newModifier(
                                ModifierKeyword.FINAL_KEYWORD);
                        node.modifiers().add(finalModifier);
                    }
                }
                return true;
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription()
    {
        return Messages.FinalParametersQuickfix_description;
    }

    /**
     * {@inheritDoc}
     */
    public String getLabel()
    {
        return Messages.FinalParametersQuickfix_label;
    }

    /**
     * {@inheritDoc}
     */
    public Image getImage()
    {
        return CheckstyleUIPluginImages.getImage(CheckstyleUIPluginImages.CORRECTION_ADD);
    }

}