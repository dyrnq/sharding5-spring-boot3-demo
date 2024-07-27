//package com.company.common.tools;
//
//import com.base.event.UnisicBaseEvent;
//import com.company.common.utils.ApplicationContextUtil;
//import jakarta.annotation.PostConstruct;
//import jakarta.annotation.Resource;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.shardingsphere.driver.jdbc.core.datasource.ShardingSphereDataSource;
//import org.apache.shardingsphere.infra.config.rule.RuleConfiguration;
//import org.apache.shardingsphere.sharding.algorithm.config.AlgorithmProvidedShardingRuleConfiguration;
//import org.apache.shardingsphere.sharding.api.config.rule.ShardingTableRuleConfiguration;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationContext;
//import org.springframework.stereotype.Component;
//
//import javax.sql.DataSource;
//import java.lang.reflect.Field;
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//
//
//@Slf4j
//@Component
//public class ShardingAlgorithmTool {
//
//    private static final String logicDb = "logic_db";
//    @Resource
//    private ShardingSphereDataSource shardingSphereDataSource;
//
//    @Autowired
//    private ApplicationContext applicationContext;
//    private static ApplicationContext context;
//
//    private static ShardingSphereDataSource shardingDataSource;
//    private static final HashSet<String> tableNameCache = new HashSet<>();
//    private static final List<String> ShardingTableNames = Arrays.asList("work_procedure", "work_manage", "work_procedure_file", "work_result");
//
//    // 启动时，实际表中要有值，启动后，在ShardingTablesLoadRunner中先清空在缓存
//    static {
//        ShardingTableNames.forEach(item -> {
//            String tableSuffix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy_MM"));
//            String actualTableName = item.concat("_").concat(tableSuffix);
//            tableNameCache.add(actualTableName);
//        });
//    }
//
//    @PostConstruct
//    public void init() {
//        shardingDataSource = shardingSphereDataSource;
//        context = applicationContext;
//    }
//
//    /**
//     * 获取所有表名
//     *
//     * @return 表名集合
//     */
//    public static List<String> getAllTableNameBySchema() {
//        List<String> tableNames = new ArrayList<>();
//        String sql = "SELECT tablename FROM pg_tables  WHERE schemaname = 'public' AND tablename ~ '_\\d{4}_\\d{2}$';";
//        DataSource dataSource = ApplicationContextUtil.getBean(DataSource.class);
//        try (Connection connection = dataSource.getConnection()) {
//            Statement statement = connection.createStatement();
//            try (ResultSet rs = statement.executeQuery(sql)) {
//                while (rs.next()) {
//                    String actualTableName = rs.getString(1);
//                    tableNames.add(actualTableName);
//                }
//            }
//        } catch (SQLException e) {
//            log.info("SQLException: " + e.getMessage());
//            throw new RuntimeException(e);
//        }
//        return tableNames;
//    }
//
//
//    /**
//     * 判断 分表获取的表名是否存在 不存在则自动建表
//     *
//     * @param logicTableName  逻辑表名(表头)
//     * @param actualTableName 真实表名
//     * @return 确认存在于数据库中的真实表名
//     */
//    public static String shardingTablesCheckAndCreatAndReturn(String logicTableName, String actualTableName) {
//        synchronized (logicTableName.intern()) {
//            // 缓存中有此表 返回
//            if (tableNameCache.contains(actualTableName)) {
//                return actualTableName;
//            }
//            // 建表sql
//            String createTableSql = "CREATE TABLE " + actualTableName + " (LIKE " + logicTableName + " );";
//            // 主键sql
//            String createPrimaryKeySql = "ALTER TABLE " + actualTableName + " ADD CONSTRAINT " + actualTableName + "_pk PRIMARY KEY (id);";
//            // 缓存中无此表，则建表,创建索引并添加缓存
//            DataSource dataSource = ApplicationContextUtil.getBean(DataSource.class);
//            try {
//                Connection connection = dataSource.getConnection();
//                Statement statement = connection.createStatement();
//                try {
//                    statement.executeUpdate(createTableSql);
//                } catch (SQLException e) {
//                    log.info("create-table-SQLException: " + e.getMessage());
//                    throw new RuntimeException(e);
//                }
//                try {
//                    statement.executeUpdate(createPrimaryKeySql);
//                } catch (SQLException e) {
//                    log.info("create-primaryKey-SQLException: " + e.getMessage());
//                    throw new RuntimeException(e);
//                }
//                ArrayList<String> names = new ArrayList<>(2);
//                names.add(logicTableName);
//                names.add(actualTableName);
//                context.publishEvent(new UnisicBaseEvent<>(names, ConstantInterface.CREATE_INDEX));
//            } catch (SQLException e) {
//                log.info("SQLException: " + e.getMessage());
//                throw new RuntimeException(e);
//            }
//
//            // 缓存重载
//            tableNameCacheReload(true);
//
//        }
//        return actualTableName;
//    }
//
//    /**
//     * 缓存重载方法
//     */
//    public static void tableNameCacheReload(boolean flag) {
//        // 读取数据库中所有表名
//        List<String> tableNameList = getAllTableNameBySchema();
//        // 删除旧的缓存(如果存在)
//        ShardingAlgorithmTool.tableNameCache.clear();
//        // 写入新的缓存
//        ShardingAlgorithmTool.tableNameCache.addAll(tableNameList);
//        if (flag) {
//            reloadShardRuleActualDataNodes(shardingDataSource, logicDb);
//        }
//
//    }
//
//    /**
//     * 获取缓存中的表名
//     *
//     * @return
//     */
//    public static HashSet<String> cacheTableNames() {
//        return tableNameCache;
//    }
//
//    /**
//     * 根据行表达式生成表名
//     *
//     * @param expression
//     * @return
//     */
//    public static List<String> generateTableNames(String expression) {
//        List<String> tableNames = new ArrayList<>();
//        List<String> returnTableNames = new ArrayList<>();
//        List<String> extractedValues = extractValues(expression);
//        if (expression.contains(".")) {
//            expression = expression.substring(expression.indexOf(".") + 1);
//        }
//        String replacedExpression = expression.replaceAll("\\$\\{.*?}", "#");
//
//        for (int i = 0; i < extractedValues.size(); i++) {
//            String s = extractedValues.get(i).replaceAll("\\.", "#");
//            String[] split = s.split("##");
//            int start = Integer.parseInt(split[0]);
//            int end = Integer.parseInt(split[1]);
//            if (i == 0) {
//                for (int j = start; j <= end; j++) {
//                    String replace = replacedExpression.replaceFirst("#", String.valueOf(j));
//                    tableNames.add(replace);
//                }
//            } else {
//                for (int k = 0; k < tableNames.size(); k++) {
//                    for (int j = start; j <= end; j++) {
//                        String s1 = tableNames.get(k).replaceFirst("#", String.valueOf(j));
//                        returnTableNames.add(s1);
//
//                    }
//                }
//            }
//        }
//        return returnTableNames;
//    }
//
//    /**
//     * 提取行表达式中的${}中范围值
//     *
//     * @param expression 行表达式
//     * @return
//     */
//    private static List<String> extractValues(String expression) {
//        List<String> extractedValues = new ArrayList<>();
//
//        Pattern pattern = Pattern.compile("\\$\\{([^}]*)\\}");
//        Matcher matcher = pattern.matcher(expression);
//
//        while (matcher.find()) {
//            String extractedValue = matcher.group(1);
//            extractedValues.add(extractedValue);
//        }
//
//        return extractedValues;
//    }
//
//    private static void reloadShardRuleActualDataNodes(ShardingSphereDataSource dataSource, String schemaName) {
//        // Context manager.
//        org.apache.shardingsphere.mode.manager.ContextManager contextManager = dataSource.getContextManager();
//        // Rule configuration.
//        Collection<RuleConfiguration> oldRuleConfigList = dataSource.getContextManager()
//                .getMetaDataContexts()
//                .getMetaData(schemaName)
//                .getRuleMetaData()
//                .getConfigurations();
//        for (RuleConfiguration config : oldRuleConfigList) {
//            Collection<ShardingTableRuleConfiguration> tables = ((AlgorithmProvidedShardingRuleConfiguration) config).getTables();
//            tables.forEach(logicTable -> {
//                String logicTableName = logicTable.getLogicTable();
//                if (ShardingTableNames.contains(logicTableName)) {
//                    setActualDataNodes(logicTable, "master.$->{com.unisic.base.shading.ShardingAlgorithmTool.cacheTableNames()}");
//                }
//            });
//        }
//        contextManager.alterRuleConfiguration(logicDb, oldRuleConfigList);
////        Collection<RuleConfiguration> newRuleConfigList = new LinkedList<>();
////        for (RuleConfiguration oldRuleConfig : oldRuleConfigList) {
////            if (oldRuleConfig instanceof AlgorithmProvidedShardingRuleConfiguration) {
////
////                // Algorithm provided sharding rule configuration
////                AlgorithmProvidedShardingRuleConfiguration oldAlgorithmConfig = (AlgorithmProvidedShardingRuleConfiguration) oldRuleConfig;
////                AlgorithmProvidedShardingRuleConfiguration newAlgorithmConfig = new AlgorithmProvidedShardingRuleConfiguration();
////
////                // Sharding table rule configuration Collection
////                Collection<ShardingTableRuleConfiguration> newTableRuleConfigList = new LinkedList<>();
////                Collection<ShardingTableRuleConfiguration> oldTableRuleConfigList = oldAlgorithmConfig.getTables();
////
////                oldTableRuleConfigList.forEach(oldTableRuleConfig -> {
////                    if (tableNameCache.contains(oldTableRuleConfig.getLogicTable())) {
////                        ShardingTableRuleConfiguration newTableRuleConfig = new ShardingTableRuleConfiguration(oldTableRuleConfig.getLogicTable(), "master.$->{com.unisic.base.shading.ShardingAlgorithmTool.cacheTableNames()}");
////                        newTableRuleConfig.setTableShardingStrategy(oldTableRuleConfig.getTableShardingStrategy());
////                        newTableRuleConfig.setDatabaseShardingStrategy(oldTableRuleConfig.getDatabaseShardingStrategy());
////                        newTableRuleConfig.setKeyGenerateStrategy(oldTableRuleConfig.getKeyGenerateStrategy());
////
////                        newTableRuleConfigList.add(newTableRuleConfig);
////                    } else {
////                        newTableRuleConfigList.add(oldTableRuleConfig);
////                    }
////                });
////
////                newAlgorithmConfig.setTables(newTableRuleConfigList);
////                newAlgorithmConfig.setAutoTables(oldAlgorithmConfig.getAutoTables());
////                newAlgorithmConfig.setBindingTableGroups(oldAlgorithmConfig.getBindingTableGroups());
////                newAlgorithmConfig.setBroadcastTables(oldAlgorithmConfig.getBroadcastTables());
////                newAlgorithmConfig.setDefaultDatabaseShardingStrategy(oldAlgorithmConfig.getDefaultDatabaseShardingStrategy());
////                newAlgorithmConfig.setDefaultTableShardingStrategy(oldAlgorithmConfig.getDefaultTableShardingStrategy());
////                newAlgorithmConfig.setDefaultKeyGenerateStrategy(oldAlgorithmConfig.getDefaultKeyGenerateStrategy());
////                newAlgorithmConfig.setDefaultShardingColumn(oldAlgorithmConfig.getDefaultShardingColumn());
////                newAlgorithmConfig.setShardingAlgorithms(oldAlgorithmConfig.getShardingAlgorithms());
////                newAlgorithmConfig.setKeyGenerators(oldAlgorithmConfig.getKeyGenerators());
////
////                newRuleConfigList.add(newAlgorithmConfig);
////            }
////        }
////
////        // update context
////        contextManager.alterRuleConfiguration(schemaName, newRuleConfigList);
//
//    }
//
//
//    private static void setActualDataNodes(ShardingTableRuleConfiguration ruleConfig, String actualDataNodes) {
//        try {
//            Field field = ShardingTableRuleConfiguration.class.getDeclaredField("actualDataNodes");
//            field.setAccessible(true);
//            field.set(ruleConfig, actualDataNodes);
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            log.error(e.getMessage());
//        }
//    }
//
//}
