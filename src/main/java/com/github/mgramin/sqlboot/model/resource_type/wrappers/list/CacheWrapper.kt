/*
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2016-2019 Maksim Gramin
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

package com.github.mgramin.sqlboot.model.resource_type.wrappers.list

import com.github.mgramin.sqlboot.exceptions.BootException
import com.github.mgramin.sqlboot.model.resource.DbResource
import com.github.mgramin.sqlboot.model.resource_type.ResourceType
import com.github.mgramin.sqlboot.model.uri.Uri
import java.util.Optional.ofNullable
import java.util.stream.Collectors
import java.util.stream.Stream
import javax.cache.Cache
import javax.cache.CacheManager
import javax.cache.Caching
import javax.cache.configuration.MutableConfiguration
import javax.cache.spi.CachingProvider

/**
 * @author Maksim Gramin (mgramin@gmail.com)
 * @version $Id: 822c72ab4745f06ca5b3062b4b0be1f9588596db $
 * @since 0.1
 */
class CacheWrapper(private val origin: ResourceType) : ResourceType {

    private val cachingProvider: CachingProvider = Caching.getCachingProvider()
    private val cacheManager: CacheManager
    private val config: MutableConfiguration<String, List<DbResource>>
    private var cache: Cache<String, List<DbResource>>? = null

    init {
        cacheManager = cachingProvider.cacheManager
        config = MutableConfiguration()
        cache = cacheManager.getCache("simpleCache")
        if (cache == null) {
            cache = cacheManager.createCache("simpleCache", config)
        }
    }

    override fun aliases(): List<String> {
        return origin.aliases()
    }

    override fun path(): List<String> {
        return origin.path()
    }

    @Throws(BootException::class)
    override fun read(uri: Uri): Stream<DbResource> {
        val cache = ofNullable(uri.params()["cache"]).orElse("true")
        var cachedResources: List<DbResource>? = this.cache!!.get(uri.toString())
        if (cachedResources == null || cache.equals("false", ignoreCase = true)) {
            cachedResources = origin.read(uri).collect(Collectors.toList())
            this.cache!!.put(uri.toString(), cachedResources)
        }
        return cachedResources!!.stream()
    }

    override fun metaData(): Map<String, String> {
        return origin.metaData()
    }

}