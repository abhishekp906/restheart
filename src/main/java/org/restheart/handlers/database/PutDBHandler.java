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
package org.restheart.handlers.database;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.restheart.db.DBDAO;
import org.restheart.handlers.injectors.LocalCachesSingleton;
import org.restheart.handlers.PipedHttpHandler;
import org.restheart.utils.HttpStatus;
import org.restheart.handlers.RequestContext;
import org.restheart.utils.RequestHelper;
import org.restheart.utils.ResponseHelper;
import io.undertow.server.HttpServerExchange;
import org.bson.types.ObjectId;

/**
 *
 * @author Andrea Di Cesare
 */
public class PutDBHandler extends PipedHttpHandler {

    /**
     * Creates a new instance of PutDBHandler
     */
    public PutDBHandler() {
        super(null);
    }

    /**
     *
     * @param exchange
     * @param context
     * @throws Exception
     */
    @Override
    public void handleRequest(HttpServerExchange exchange, RequestContext context) throws Exception {
        if (context.getDBName().isEmpty() || context.getDBName().startsWith("_")) {
            ResponseHelper.endExchangeWithMessage(exchange, HttpStatus.SC_NOT_ACCEPTABLE, "db name cannot be empty or start with _");
            return;
        }

        DBObject content = context.getContent();

        if (content == null) {
            content = new BasicDBObject();
        }

        // cannot PUT an array
        if (content instanceof BasicDBList) {
            ResponseHelper.endExchangeWithMessage(exchange, HttpStatus.SC_NOT_ACCEPTABLE, "data cannot be an array");
            return;
        }

        ObjectId etag = RequestHelper.getWriteEtag(exchange);

        int SC = DBDAO.upsertDB(context.getDBName(), content, etag, false);

        // send the warnings if any (and in case no_content change the return code to ok
        if (context.getWarnings() != null && !context.getWarnings().isEmpty()) {
            sendWarnings(SC, exchange, context);
        } else {
            exchange.setResponseCode(SC);
        }

        exchange.endExchange();

        LocalCachesSingleton.getInstance().invalidateDb(context.getDBName());
    }
}
