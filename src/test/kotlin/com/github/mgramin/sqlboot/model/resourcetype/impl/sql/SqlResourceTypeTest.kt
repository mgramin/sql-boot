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

package com.github.mgramin.sqlboot.model.resourcetype.impl.sql

import com.github.mgramin.sqlboot.model.connection.SimpleDbConnection
import com.github.mgramin.sqlboot.model.uri.impl.DbUri
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.core.io.FileSystemResource
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * @author Maksim Gramin (mgramin@gmail.com)
 * @version $Id: 3d61942b737e9bf2768c93c6058e1ec55929b6f3 $
 * @since 0.1
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(locations = ["/test_config.xml"])
class SqlResourceTypeTest {

    private val db = SimpleDbConnection()

    init {
        db.setName("unit_test_db")
        db.baseFolder = FileSystemResource("conf/h2/database")
        db.url = "jdbc:h2:mem:;INIT=RUNSCRIPT FROM 'classpath:schema.sql';"
        db.paginationQueryTemplate = "${'$'}{query} offset ${'$'}{uri.pageSize()*(uri.pageNumber()-1)} limit ${'$'}{uri.pageSize()}"
    }

    @Test
    fun name() {
        val name = SqlResourceType(arrayListOf("table", "tbl", "t"), "", listOf(db)).name()
        assertEquals("table", name)
    }

    @Test
    fun aliases() {
        val aliases = SqlResourceType(arrayListOf("table", "tbl", "t"), "", listOf(db)).aliases()
        assertEquals(arrayListOf("table", "tbl", "t"), aliases)
    }

    @Test
    fun read() {
        val sql = """select *
                    |  from (select table_schema   as "@schema"
                    |             , table_name     as "@table"
                    |          from information_schema.tables)""".trimMargin()
        val type =
                SqlResourceType(
                        aliases = arrayListOf("table"),
                        sql = sql,
                        connections = listOf(db))
        assertEquals(46, type.read(DbUri("table/m.column")).count().block())
    }

    @Test
    fun read2() {
        val sql = """select *
                    |  from (select table_schema    as "@schema"
                    |             , table_name      as "@table"
                    |             , column_name     as "@column"
                    |          from information_schema.columns)""".trimMargin()
        val type =
                SqlResourceType(
                        aliases = arrayListOf("column"),
                        sql = sql,
                        connections = listOf(db))
        assertEquals(347, type.read(DbUri("column/main_schema.users")).count().block())
    }

    @Test
    fun path() {
        val sql = """select @schema
                    |     , @table
                    |     , @column
                    |  from (select table_schema    as "@schema"
                    |             , table_name      as "@table"
                    |             , column_name     as "@column"
                    |          from information_schema.columns)""".trimMargin()
        val type =
                SqlResourceType(
                        aliases = arrayListOf("column"),
                        sql = sql,
                        connections = listOf(db))
        assertEquals("[schema, table, column]", type.path().toString())
    }

    @Test
    fun metaData() {
    }

}