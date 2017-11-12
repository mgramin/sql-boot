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

package com.github.mgramin.sqlboot.model.resource_type.wrappers.list;

import com.github.mgramin.sqlboot.exceptions.BootException;
import com.github.mgramin.sqlboot.model.resource.DbResource;
import com.github.mgramin.sqlboot.model.resource_type.ResourceType;
import com.github.mgramin.sqlboot.model.uri.Uri;
import java.util.List;
import java.util.stream.Stream;
import lombok.ToString;

/**
 * Created by MGramin on 18.07.2017.
 */
@ToString
public class WhereWrapper implements ResourceType {

    private final ResourceType origin;

    public WhereWrapper(ResourceType origin) {
        this.origin = origin;
    }

    @Override
    public String name() {
        return origin.name();
    }

    @Override
    public List<String> aliases() {
        return origin.aliases();
    }

    @Override
    public Stream<DbResource> read(Uri uri) throws BootException {
        final Stream<DbResource> resources = origin.read(uri);
        return resources
            .filter(resource -> {
                for (int i = 0; i < uri.path().size(); i++) {
                    boolean contains = resource.dbUri().path().get(i)
                            .toLowerCase().contains(uri.path().get(i)
                                    .toLowerCase()) || uri.path().get(i).equals("%");
                    if (!contains)
                        return false;
                }
                return true;
            });
    }

}
