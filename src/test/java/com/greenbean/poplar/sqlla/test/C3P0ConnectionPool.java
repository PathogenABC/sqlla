package com.greenbean.poplar.sqlla.test;

import com.greenbean.poplar.sqlla.Sqlla;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by chrisding on 2017/5/10.
 * Function: NULL
 */
public class C3P0ConnectionPool implements Sqlla.ConnectionPool {

    private ComboPooledDataSource mSource = new ComboPooledDataSource(true);

    public C3P0ConnectionPool(String confPropsFile) {
        File file = new File(confPropsFile);
        if (file.exists()) {
            try {
                init(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                throw new RuntimeException("init connection pool failed", e);
            }
        } else {
            throw new RuntimeException("init connection pool failed: config properties file " + String.valueOf(confPropsFile) + "not exist");
        }
    }

    public C3P0ConnectionPool(InputStream confResource) {
        init(confResource);
    }

    private void init(InputStream propertiesFileIs) {
        Properties p = new Properties();
        try {
            p.load(propertiesFileIs);

            ComboPooledDataSource source = this.mSource;
            source.setDataSourceName(p.getProperty("c3p0.sourceName"));
            source.setJdbcUrl(p.getProperty("c3p0.jdbcUrl"));
            source.setDriverClass(p.getProperty("c3p0.driverClass"));
            source.setUser(p.getProperty("c3p0.username"));
            source.setPassword(p.getProperty("c3p0.password"));
            source.setMaxPoolSize(Integer.valueOf(p.getProperty("c3p0.maxPoolSize")));
            source.setMinPoolSize(Integer.valueOf(p.getProperty("c3p0.minPoolSize")));
            source.setAcquireIncrement(Integer.valueOf(p.getProperty("c3p0.acquireIncrement")));
            source.setInitialPoolSize(Integer.valueOf(p.getProperty("c3p0.initialPoolSize")));
            source.setMaxIdleTime(Integer.valueOf(p.getProperty("c3p0.maxIdleTime")));
        } catch (Exception e) {
            throw new RuntimeException("init connection pool failed", e);
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return mSource.getConnection();
    }
}
