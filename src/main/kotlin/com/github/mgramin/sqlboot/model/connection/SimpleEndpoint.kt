/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2019, CROC Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.github.mgramin.sqlboot.model.connection

import com.fasterxml.jackson.annotation.JsonIgnore
import org.apache.tomcat.jdbc.pool.DataSource

/**
 * @author Maksim Gramin (mgramin@gmail.com)
 * @version $Id: f221782080d430e77aed80ef8446745687c350f4 $
 * @since 0.1
 */
open class SimpleEndpoint(
        var name: String = "",
        var host: String = "",
        var confDir: String = "",
        var properties: Map<String, Any> = emptyMap()
) : Endpoint {

    override fun name() = name

    override fun host() = host

    override fun confDir() = confDir

    override fun properties(): Map<String, Any> = properties

    private var dataSource: DataSource? = null

    @JsonIgnore
    override fun getDataSource(): DataSource {
        return if (dataSource != null) {
            dataSource!!
        } else {
            val dataSourceNew = DataSource()
            if (properties().containsKey("jdbc_url")) {
                dataSourceNew.url = properties()["jdbc_url"].toString()
            } else {
                val jdbcProtocol = properties()["jdbc_protocol"]
                val dbName = properties()["db_name"]
                val dbPort = properties()["db_port"]
                dataSourceNew.url = "$jdbcProtocol://$host:$dbPort/$dbName"
            }
            dataSourceNew.driverClassName = properties()["jdbc_driver_class_name"].toString()
            dataSourceNew.username = properties()["db_user"].toString()
            dataSourceNew.password = properties()["db_password"].toString()
            dataSourceNew.minIdle = 1
            dataSourceNew.maxActive = 10
            dataSourceNew.maxIdle = 10
            dataSourceNew.maxWait = 10
            dataSource = dataSourceNew
            dataSourceNew
        }
    }

}
