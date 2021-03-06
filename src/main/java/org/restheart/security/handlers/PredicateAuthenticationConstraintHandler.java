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
package org.restheart.security.handlers;

import org.restheart.security.AccessManager;
import io.undertow.predicate.Predicate;
import io.undertow.security.handlers.AuthenticationConstraintHandler;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import java.util.Set;

/**
 *
 * @author Andrea Di Cesare
 */
public class PredicateAuthenticationConstraintHandler extends AuthenticationConstraintHandler {

    AccessManager am;

    /**
     *
     * @param next
     * @param am
     */
    public PredicateAuthenticationConstraintHandler(HttpHandler next, AccessManager am) {
        super(next);
        this.am = am;
    }

    @Override
    protected boolean isAuthenticationRequired(final HttpServerExchange exchange) {
        Set<Predicate> ps = am.getAcl().get("$unauthenticated");
        return ps == null ? true : !ps.stream().anyMatch(p -> p.resolve(exchange));
    }
}
