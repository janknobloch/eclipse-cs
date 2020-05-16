//============================================================================
//
// Copyright (C) 2009 Lukas Frena
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

package net.sf.eclipsecs.core.transformer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import net.sf.eclipsecs.core.util.CheckstyleLog;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * Class for writing a new eclipse-configuration-file. Gets used by class Transformer. A new
 * eclipse-formatter-profile gets added.
 *
 * @author Lukas Frena
 * @author Lars Ködderitzsch
 */
@SuppressWarnings("restriction")
public class FormatterConfigWriter {

  private static final String JDT_UI_PLUGINID = "org.eclipse.jdt.ui";

  /** A eclipse-configuration. */
  private final FormatterConfiguration mConfiguration;

  /** Name of new createt profile. */
  private final String mNewProfileName;

  private IProject mProject;

  /**
   * Constructor to create a new instance of class FormatterConfigWriter.
   *
   * @param project
   *          the project whose formatter settings should be written
   * @param settings
   *          A eclipse-configuration.
   */
  public FormatterConfigWriter(IProject project, final FormatterConfiguration settings) {
    mConfiguration = settings;
    mProject = project;

    mNewProfileName = "eclipse-cs " + mProject.getName();
    writeSettings();
  }

  /**
   * Method for writing all settings to disc. Also activates new profile.
   */
  private void writeSettings() {
    // read the Eclipse-Preferences for manipulation
    writeCleanupSettings(mConfiguration.getCleanupSettings());
    writeFormatterSettings(mConfiguration.getFormatterSettings());
  }

  private void writeCleanupSettings(final Map<String, String> settings) {
    try(ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
      XmlProfileWriter.writeCleanupProfileToStream(bos, "CheckStyle-Generated", settings);

      IFile settingsFile = mProject.getFile("checkstype-cleanup-settings.xml");
      try (InputStream stream = new ByteArrayInputStream(bos.toByteArray())) {
        settingsFile.create(stream, true, new NullProgressMonitor());
      }
    } catch (IOException|CoreException e) {
      CheckstyleLog.log(e, "Error saving cleanup profile");
    }
  }

  /**
   * Method for writing all formatter-settings to disc.
   *
   * @param settings
   *          All the settings.
   * @throws CoreException
   */
  private void writeFormatterSettings(final Map<String, String> settings) {
    try(ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
      XmlProfileWriter.writeFormatterProfileToStream(bos, "CheckStyle-Generated", settings);

      IFile settingsFile = mProject.getFile("checkstype-formatter-settings.xml");
      try (InputStream stream = new ByteArrayInputStream(bos.toByteArray())) {
        settingsFile.create(stream, true, new NullProgressMonitor());
      }
    } catch (IOException|CoreException e) {
      CheckstyleLog.log(e, "Error saving formatter profile");
    }
  }
}
