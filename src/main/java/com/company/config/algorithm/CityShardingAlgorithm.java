package com.company.config.algorithm;

import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;

import java.util.Collection;


public class CityShardingAlgorithm implements StandardShardingAlgorithm<String> {


    @Override
    public String doSharding(Collection<String> databaseNames, PreciseShardingValue<String> preciseShardingValue) {
        String city = preciseShardingValue.getValue();
        String databaseName = "ds_" + city;
        for (String each : databaseNames) {
            if (each.equals(databaseName)) {
                return each;
            }
        }
        throw new IllegalArgumentException();
    }


    @Override
    public Collection<String> doSharding(Collection<String> databaseNames, RangeShardingValue<String> rangeShardingValue) {
    
        return null;
    }
}
