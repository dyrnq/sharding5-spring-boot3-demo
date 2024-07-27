package com.company.config.algorithm;


import com.google.common.collect.Range;
import com.google.common.collect.Sets;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;
import org.joda.time.LocalDate;

import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName TbInteractiveClassroomNewShardingAlgorithm
 * @Author zhangzheng
 * @Date 2024/07/21 11:44
 * @Version 1.0
 **/
public class CreateTimeShardingAlgorithm implements StandardShardingAlgorithm<Date> {
    
    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Date> shardingValue) {
        String tableName = shardingValue.getLogicTableName();

        tableName = tableName + "_" + LocalDate.fromDateFields(shardingValue.getValue())
                .toString("yyyyMM", Locale.CHINA);

        for (String each : availableTargetNames) {
            if (each.equals(tableName)) {
                return each;
            }
        }
        throw new IllegalArgumentException();
    }

    @Override
    public Collection<String> doSharding(Collection<String> collection, RangeShardingValue<Date> rangeShardingValue) {
        Collection<String> tableSet = Sets.newConcurrentHashSet();
        String logicTableName = rangeShardingValue.getLogicTableName();
        Range<Date> dates = rangeShardingValue.getValueRange();
        Date lowDate = dates.lowerEndpoint();
        Date upperDate = dates.upperEndpoint();
        AtomicInteger i = new AtomicInteger(0);

        while (DateUtils.addDays(lowDate, i.get()).compareTo(upperDate) <= 0) {
            String tableName = logicTableName + "_" + LocalDate.fromDateFields(DateUtils.addDays(lowDate, i.getAndAdd(1)))
                    .toString("yyyyMM", Locale.CHINA);
            tableSet.add(tableName);
        }
        return tableSet;
    }


    @Override
    public void init(Properties properties) {

    }
}
