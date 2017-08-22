/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2017 Maksim Gramin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.mgramin.sqlboot.model.resource_type.impl.jdbc;

import com.github.mgramin.sqlboot.exceptions.BootException;
import com.github.mgramin.sqlboot.model.resource.DbResource;
import com.github.mgramin.sqlboot.model.resource.impl.DbResourceImpl;
import com.github.mgramin.sqlboot.model.resource_type.ResourceType;
import com.github.mgramin.sqlboot.model.uri.Uri;
import com.github.mgramin.sqlboot.model.uri.impl.DbUri;
import com.github.mgramin.sqlboot.tools.jdbc.JdbcDbObject;
import com.github.mgramin.sqlboot.tools.jdbc.JdbcDbObjectType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import lombok.ToString;

/**
 * Created by MGramin on 12.07.2017.
 */
@ToString
public class JdbcResourceType implements ResourceType {

    final private List<String> aliases;
    final private List<ResourceType> child;
    final private JdbcDbObjectType jdbcDbObjectType;

    public JdbcResourceType(List<String> aliases, JdbcDbObjectType jdbcDbObjectType) {
        this(aliases, null, jdbcDbObjectType);
    }

    public JdbcResourceType(List<String> aliases, List<ResourceType> child, JdbcDbObjectType jdbcDbObjectType) {
        this.aliases = aliases;
        this.child = child;
        this.jdbcDbObjectType = jdbcDbObjectType;
    }

    @Override
    public String name() {
        return aliases.get(0);
    }

    @Override
    public List<String> aliases() {
        return aliases;
    }

    @Override
    public List<DbResource> read(final Uri uri) throws BootException {
        List<DbResource> dbResourceList = new ArrayList<>();
        try {
            List<JdbcDbObject> list = jdbcDbObjectType.read(uri.path());
            for (JdbcDbObject l : list) {
                dbResourceList.add(new DbResourceImpl(l.name(), this, new DbUri(jdbcDbObjectType.name(), l.path()), l.properties()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dbResourceList;
    }

}
