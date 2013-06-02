/*
 * DynNetwork plugin for Cytoscape 3.0 (http://www.cytoscape.org/).
 * Copyright (C) 2012 Sabina Sara Pfister
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.cytoscape.dyn.internal.io.read.xgmml;

import java.io.InputStream;

import org.cytoscape.dyn.internal.io.read.AbstractDynNetworkReaderFactory;
import org.cytoscape.io.CyFileFilter;
import org.cytoscape.work.TaskIterator;
import org.xml.sax.helpers.DefaultHandler;

/**
 * <code> XGMMLDynNetworkReaderFactory </code> extends {@link AbstractDynNetworkReaderFactory}. 
 * Is used to create instance of the file reader {@link XGMMLDynNetworkReader}.
 * 
 * @author Sabina Sara Pfister
 *
 */
public final class XGMMLDynNetworkReaderFactory extends AbstractDynNetworkReaderFactory
{
        private final DefaultHandler parser;

        /**
         * <code> XGMMLDynNetworkReaderFactory </code> constructor.
         * @param filter
         * @param parser
         */
        public XGMMLDynNetworkReaderFactory(
                        final CyFileFilter filter,
                        final DefaultHandler parser)
        {
                super(filter);
                this.parser = parser;
        }

        @Override
        public TaskIterator createTaskIterator(InputStream inputStream, String inputName)
        {
                return new TaskIterator(new XGMMLDynNetworkReader(inputStream, parser));
        }
}

