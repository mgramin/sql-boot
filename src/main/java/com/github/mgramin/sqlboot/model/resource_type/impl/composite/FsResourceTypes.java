/**
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2016-2017 Maksim Gramin
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.mgramin.sqlboot.model.resource_type.impl.composite;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singletonList;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.commons.lang3.StringUtils.substringBetween;

import com.github.mgramin.sqlboot.exceptions.BootException;
import com.github.mgramin.sqlboot.model.resource.DbResource;
import com.github.mgramin.sqlboot.model.resource_type.ResourceType;
import com.github.mgramin.sqlboot.model.resource_type.impl.jdbc.JdbcResourceType;
import com.github.mgramin.sqlboot.model.resource_type.impl.sql.SqlResourceType;
import com.github.mgramin.sqlboot.model.resource_type.wrappers.body.TemplateBodyWrapper;
import com.github.mgramin.sqlboot.model.resource_type.wrappers.header.SelectWrapper;
import com.github.mgramin.sqlboot.model.resource_type.wrappers.list.LimitWrapper;
import com.github.mgramin.sqlboot.model.resource_type.wrappers.list.WhereWrapper;
import com.github.mgramin.sqlboot.model.uri.Uri;
import com.github.mgramin.sqlboot.sql.impl.JdbcSqlQuery;
import com.github.mgramin.sqlboot.template.generator.impl.GroovyTemplateGenerator;
import com.github.mgramin.sqlboot.tools.jdbc.JdbcDbObjectType;
import com.github.mgramin.sqlboot.tools.jdbc.impl.Column;
import com.github.mgramin.sqlboot.tools.jdbc.impl.ForeignKey;
import com.github.mgramin.sqlboot.tools.jdbc.impl.Function;
import com.github.mgramin.sqlboot.tools.jdbc.impl.Index;
import com.github.mgramin.sqlboot.tools.jdbc.impl.PrimaryKey;
import com.github.mgramin.sqlboot.tools.jdbc.impl.Procedure;
import com.github.mgramin.sqlboot.tools.jdbc.impl.Schema;
import com.github.mgramin.sqlboot.tools.jdbc.impl.Table;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/**
 * Created by MGramin on 11.07.2017.
 */
@Service
@Configuration
@ConfigurationProperties(prefix = "conf")
public class FsResourceTypes implements ResourceType {

    private DataSource dataSource;

    private List<ResourceType> resourceTypes = new ArrayList<>();
    private Resource baseFolder;
    private String url;
    private String user;
    private String password;
    private String driverClassName;

    public FsResourceTypes() {
    }

    public void setBaseFolder(Resource baseFolder) {
        this.baseFolder = baseFolder;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public FsResourceTypes(final Resource baseFolder) {
        this.baseFolder = baseFolder;
        init();
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    /**
     * Ctor.
     *
     * @param baseFolder
     */
    public FsResourceTypes(final Resource baseFolder, String url, String user, String password) {
        final DataSource dataSource = new DataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        this.dataSource = dataSource;
        this.baseFolder = baseFolder;
    }

    public void init() throws BootException {
        final DataSource dataSource = new DataSource();
        Optional.ofNullable(driverClassName).ifPresent(dataSource::setDriverClassName);
        dataSource.setUrl(url);
        Optional.ofNullable(user).ifPresent(dataSource::setUsername);
        Optional.ofNullable(password).ifPresent(dataSource::setPassword);
        this.dataSource = dataSource;

        resourceTypes.clear();
        try {
            walk(baseFolder.getFile().getPath());
        } catch (IOException e) {
            throw new BootException(e);
        }
    }

    @Deprecated
    public ResourceType type(final String name) {
        return resourceTypes.stream().filter(v -> v.name().equalsIgnoreCase(name)).findAny().get();
    }

    @Deprecated
    public List<ResourceType> resourceTypes() {
        return resourceTypes;
    }

    private List<ResourceType> walk(final String path) {
        File[] files = new File(path).listFiles();
        if (files == null) return null;
        List<ResourceType> list = new ArrayList<>();
        for (File f : files) {
            if (f.isDirectory()) {
                File sqlFile = new File(f, "README.md");
                List<ResourceType> child = walk(f.getAbsolutePath());
                final JdbcDbObjectType jdbcDbObjectType;
                switch (f.getName()) {
                    case "schema":
                        jdbcDbObjectType = new Schema(dataSource);
                        break;
                    case "table":
                        jdbcDbObjectType = new Table(dataSource);
                        break;
                    case "column":
                        jdbcDbObjectType = new Column(dataSource);
                        break;
                    case "pk":
                        jdbcDbObjectType = new PrimaryKey(dataSource);
                        break;
                    case "fk":
                        jdbcDbObjectType = new ForeignKey(dataSource);
                        break;
                    case "index":
                        jdbcDbObjectType = new Index(dataSource);
                        break;
                    case "procedure":
                        jdbcDbObjectType = new Procedure(dataSource);
                        break;
                    case "function":
                        jdbcDbObjectType = new Function(dataSource);
                        break;
                    default:
                        jdbcDbObjectType = null;
                }

                String sql = null;
                try {
                    sql = substringBetween(readFileToString(sqlFile, UTF_8), "````sql", "````");
                } catch (IOException e) {
                    // TODO catch process this exception
                }
                final ResourceType baseResourceType;
                if (sqlFile.exists() && sql != null) {
                    baseResourceType = new SqlResourceType(new JdbcSqlQuery(dataSource, sql), singletonList(f.getName()));
                } else if (jdbcDbObjectType != null) {
                    baseResourceType = new JdbcResourceType(singletonList(f.getName()), child, jdbcDbObjectType);
                } else {
                    baseResourceType = null;
                }
                final ResourceType resourceType = new SelectWrapper(
                    new TemplateBodyWrapper( // TODO move TemplateBodyWrapper on top
                        new LimitWrapper(
                            new WhereWrapper(baseResourceType)),
                        new GroovyTemplateGenerator("create objects ... ;")));
                if (baseResourceType != null) {
                    resourceTypes.add(resourceType);
                    list.add(resourceType);
                }
            }
        }
        return list;
    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public List<String> aliases() {
        return null;
    }

    @Override
    public Stream<DbResource> read(Uri uri) throws BootException {
        ResourceType type = type(uri.type());
        return type.read(uri);
    }

    @Override
    public Map<String, String> medataData() {
        throw new BootException("Not implemented!");
    }

}
