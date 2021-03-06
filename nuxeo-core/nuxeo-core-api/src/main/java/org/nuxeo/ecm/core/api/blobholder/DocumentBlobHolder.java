/*
 * (C) Copyright 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 *
 */

package org.nuxeo.ecm.core.api.blobholder;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.utils.BlobsExtractor;

/**
 * {@link BlobHolder} implementation based on a {@link DocumentModel} and a XPath.
 *
 * @author tiry
 */
public class DocumentBlobHolder extends AbstractBlobHolder {

    protected final DocumentModel doc;

    protected final String xPath;

    protected String xPathFilename;

    protected List<Blob> blobList = null;

    /**
     * Constructor with filename property for compatibility (when filename was not stored on blob object)
     */
    public DocumentBlobHolder(DocumentModel doc, String xPath, String xPathFilename) {
        this.doc = doc;
        this.xPath = xPath;
        this.xPathFilename = xPathFilename;
    }

    public DocumentBlobHolder(DocumentModel doc, String xPath) {
        this(doc, xPath, null);
    }

    @Override
    protected String getBasePath() {
        return doc.getPathAsString();
    }

    @Override
    public Blob getBlob() {
        Blob blob = (Blob) doc.getPropertyValue(xPath);
        if (blob != null && xPathFilename != null) {
            String filename = blob.getFilename();
            if (filename == null || "".equals(filename)) {
                // compatibility when filename was not stored on blob
                blob.setFilename((String) doc.getPropertyValue(xPathFilename));
            }
        }
        return blob;
    }

    @Override
    public void setBlob(Blob blob) {
        doc.getProperty(xPath).setValue(blob);
        if (xPathFilename != null) {
            String filename = blob == null ? null : blob.getFilename();
            doc.setPropertyValue(xPathFilename, filename);
        }
    }

    @Override
    public Calendar getModificationDate() {
        return (Calendar) doc.getProperty("dublincore", "modified");
    }

    @Override
    public String getHash() {
        Blob blob = getBlob();
        if (blob != null) {
            String h = blob.getDigest();
            if (h != null) {
                return h;
            }
        }
        return doc.getId() + xPath + String.valueOf(getModificationDate());
    }

    @Override
    public Serializable getProperty(String name) {
        return null;
    }

    @Override
    public Map<String, Serializable> getProperties() {
        return null;
    }

    @Override
    public List<Blob> getBlobs() {
        if (blobList == null) {
            List<Blob> blobs = new BlobsExtractor().getBlobs(doc);
            Blob main = getBlob();
            if (main != null) {
                // be sure the "main" blob is always in first position
                Iterator<Blob> bi = blobs.iterator();
                while (bi.hasNext()) {
                    Blob blob = bi.next();
                    if (blob.getDigest() != null) {
                        if (blob.getDigest().equals(main.getDigest())) {
                            bi.remove();
                            break;
                        }
                    } else if (blob.getFilename() != null) {
                        if (blob.getFilename().equals(main.getFilename())) {
                            bi.remove();
                            break;
                        }
                    }
                }
                blobs.add(0, main);
            }
            blobList = blobs;
        }
        return blobList;
    }

    /**
     * @since 7.3
     */
    public String getXpath() {
        return xPath;
    }

    /**
     * @since 7.4
     */
    public DocumentModel getDocument() {
        return doc;
    }
}
