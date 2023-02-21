/*
 * Copyright 2023-present Daleks Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.daleks.risk.utils.ip2region;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * ip 地址转换
 * https://gitee.com/lionsoul/ip2region
 *
 * @author chenxin<chenxin619315 @ gmail.com>
 */
@Slf4j
public class DbSearcher {

    private final static int DATA_BLOCK_LENGTH = 12;
    private final static String DEFAULT_DB_FILE = "ip2region.db";

    /**
     * super blocks info
     */
    private long firstIndexPtr = 0;
    private long lastIndexPtr = 0;
    private int totalIndexBlocks = 0;

    /**
     * for memory mode
     * the original db binary string
     */
    private byte[] dbBinStr = null;

    public DbSearcher() throws IOException {
        this(DEFAULT_DB_FILE);
    }

    /**
     * construct class
     *
     * @param dbFile
     * @throws FileNotFoundException
     */
    public DbSearcher(String dbFile) throws IOException {
        log.info("初始化IP信息库,目录:{}", Objects.requireNonNull(this.getClass().getClassLoader().getResource(dbFile)).getFile());
        InputStream raf = this.getClass().getClassLoader().getResourceAsStream(dbFile);
        if (dbBinStr == null) {
            dbBinStr = IOUtils.toByteArray(raf);
            //initialize the global vars
            firstIndexPtr = Util.getIntLong(dbBinStr, 0);
            lastIndexPtr = Util.getIntLong(dbBinStr, 4);
            totalIndexBlocks = (int) ((lastIndexPtr - firstIndexPtr) / DATA_BLOCK_LENGTH) + 1;
        }
        IOUtils.closeQuietly(raf, null);
    }

    /**
     * get the region with a int ip address with memory binary search algorithm
     *
     * @param ip 地址
     */
    public IpDetail memorySearch(long ip) {

        //search the index blocks to define the data
        int l = 0, h = totalIndexBlocks;
        long sip, eip, dataptr = 0;
        while (l <= h) {
            int m = (l + h) >> 1;
            int p = (int) (firstIndexPtr + m * DATA_BLOCK_LENGTH);

            sip = Util.getIntLong(dbBinStr, p);
            if (ip < sip) {
                h = m - 1;
            } else {
                eip = Util.getIntLong(dbBinStr, p + 4);
                if (ip > eip) {
                    l = m + 1;
                } else {
                    dataptr = Util.getIntLong(dbBinStr, p + 8);
                    break;
                }
            }
        }

        //not matched
        if (dataptr == 0) return null;

        //get the data
        int dataLen = (int) ((dataptr >> 24) & 0xFF);
        int dataPtr = (int) ((dataptr & 0x00FFFFFF));
        int cityId = (int) Util.getIntLong(dbBinStr, dataPtr);
        String region = new String(dbBinStr, dataPtr + 4, dataLen - 4, StandardCharsets.UTF_8);

        return new IpDetail(cityId, region);
    }

    /**
     * get the region throught the ip address with memory binary search algorithm
     *
     * @param ip 地址
     * @return IpDetail
     * @throws IOException
     */
    public IpDetail memorySearch(String ip) throws IOException {
        return memorySearch(Util.ip2long(ip));
    }

    /**
     * close the db
     */
    public void close() {
        dbBinStr = null;
    }

}
