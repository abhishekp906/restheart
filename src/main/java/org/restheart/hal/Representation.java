/*
 * RESTHeart - the data REST API server
 * Copyright (C) 2014 - 2015 SoftInstigate Srl
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.restheart.hal;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.bson.BSONObject;

/**
 *
 * @author Andrea Di Cesare
 */
public class Representation {

    /**
     *
     */
    public static final String HAL_JSON_MEDIA_TYPE = "application/hal+json";

    private final BasicDBObject properties;
    private final BasicDBObject embedded;
    private final BasicDBObject links;

    /**
     *
     * @param href
     */
    public Representation(String href) {
        properties = new BasicDBObject();
        embedded = new BasicDBObject();
        links = new BasicDBObject();

        links.put("self", new BasicDBObject("href", href));
    }

    BasicDBObject getDBObject() {
        BasicDBObject ret = new BasicDBObject(properties);

        if (!embedded.isEmpty()) {
            ret.append("_embedded", embedded);
        }

        if (!links.isEmpty()) {
            ret.append("_links", links);
        }

        return ret;
    }

    /**
     *
     * @param link
     */
    public void addLink(Link link) {
        links.putAll((BSONObject) ((Link) link).getDBObject());
    }

    /**
     *
     * @param link
     * @param inArray
     */
    public void addLink(Link link, boolean inArray) {
        BasicDBList linkArray = (BasicDBList) links.get(link.getRef());

        if (linkArray == null) {
            linkArray = new BasicDBList();
            links.append(link.getRef(), linkArray);
        }

        linkArray.add(link.getDBObject().get(link.getRef()));

        links.put(link.getRef(), linkArray);
    }

    /**
     *
     * @param key
     * @param value
     */
    public void addProperty(String key, Object value) {
        properties.append(key, value);
    }

    /**
     *
     * @param props
     */
    public void addProperties(DBObject props) {
        if (props == null) {
            return;
        }

        properties.putAll(props);
    }

    /**
     *
     * @param rel
     * @param rep
     */
    public void addRepresentation(String rel, Representation rep) {
        BasicDBList repArray = (BasicDBList) embedded.get(rel);

        if (repArray == null) {
            repArray = new BasicDBList();

            embedded.append(rel, repArray);
        }

        repArray.add(rep.getDBObject());
    }

    @Override
    public String toString() {
        return getDBObject().toString();
    }
}
